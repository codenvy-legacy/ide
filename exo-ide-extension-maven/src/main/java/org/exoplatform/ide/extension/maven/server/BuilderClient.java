/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.maven.server;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.json.JsonHelper;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Client to remote build server.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BuilderClient {
    public static final String                     BUILD_SERVER_BASE_URL     = "exo.ide.builder.build-server-base-url";

    private final Timer                            checkStatusTimer          = new Timer();

    private final ConcurrentMap<String, TimerTask> checkStatusTasks          = new ConcurrentHashMap<String, TimerTask>();

    private static final long                      CHECK_BUILD_STATUS_PERIOD = 2000;

    private static final String                    BUILD_STATUS_CHANNEL      = "maven:buildStatus:";

    private static final Log                       LOG                       = ExoLogger.getLogger(BuilderClient.class);

    private final String                           baseURL;

    public BuilderClient(InitParams initParams) {
        this(readValueParam(initParams, "build-server-base-url", System.getProperty(BUILD_SERVER_BASE_URL)));
    }

    private static String readValueParam(InitParams initParams, String paramName, String defaultValue) {
        if (initParams != null) {
            ValueParam vp = initParams.getValueParam(paramName);
            if (vp != null) {
                return vp.getValue();
            }
        }
        return defaultValue;
    }

    protected BuilderClient(String baseURL) {
        if (baseURL == null || baseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL of build server may not be null or empty string. ");
        }
        this.baseURL = baseURL;
    }

    /**
     * Send request to start collect list of dependencies. Process may be started immediately or add in queue.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of project we want to send for collect dependencies
     * @return ID of build task. It may be used as parameter for method {@link #status(String)} .
     * @throws IOException if any i/o errors occur
     * @throws BuilderException if build request was rejected by remote build server
     * @throws VirtualFileSystemException if any error in VFS
     */
    public String dependenciesList(VirtualFileSystem vfs, String projectId) throws IOException, BuilderException,
                                                                           VirtualFileSystemException {
        URL url = new URL(baseURL + "/builder/maven/dependencies/list");
        return run(url, vfs.exportZip(projectId));
    }

    /**
     * Send request to start collect project dependencies and add them in zip archive. Process may be started immediately or add in queue.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of project we want to send for collect dependencies
     * @param classifier classifier to look for, e.g. : sources. May be <code>null</code>.
     * @return ID of build task. It may be used as parameter for method {@link #status(String)} .
     * @throws IOException if any i/o errors occur
     * @throws BuilderException if build request was rejected by remote build server
     * @throws VirtualFileSystemException if any error in VFS
     */
    public String dependenciesCopy(VirtualFileSystem vfs, String projectId, String classifier) throws IOException,
                                                                                              BuilderException,
                                                                                              VirtualFileSystemException {
        String url = baseURL + "/builder/maven/dependencies/copy";
        if (!(classifier == null || classifier.isEmpty())) {
            url += "?classifier=" + classifier;
        }
        return run(new URL(url), vfs.exportZip(projectId));
    }

    /**
     * Send request to start new build at remote build server. Build may be started immediately or add in queue.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of project we want to send for build
     * @return ID of build task. It may be used as parameter for method {@link #status(String)}.
     * @throws IOException if any i/o errors occur
     * @throws BuilderException if build request was rejected by remote build server
     * @throws VirtualFileSystemException if any error in VFS
     */
    public String build(VirtualFileSystem vfs, String projectId, String projectName, String projectType) throws IOException,
                                                                                                        BuilderException,
                                                                                                        VirtualFileSystemException {
        URL url = new URL(baseURL + "/builder/maven/build");
        String buildId = run(url, vfs.exportZip(projectId));
        LOG.info("EVENT#build-started# PROJECT#" + projectName + "# TYPE#" + projectType + "#");
        LOG.info("EVENT#project-built# PROJECT#" + projectName + "# TYPE#" + projectType + "#");
        startCheckingBuildStatus(buildId, projectName, projectType);
        return buildId;
    }

    /**
     * Send request to start new build and deploy artifact. Build may be started immediately or add in queue.
     * 
     * @param vfs virtual file system
     * @param projectId identifier of project we want to send for build
     * @return ID of build task. It may be used as parameter for method {@link #status(String)} .
     * @throws IOException if any i/o errors occur
     * @throws BuilderException if build request was rejected by remote build server
     * @throws VirtualFileSystemException if any error in VFS
     */
    public String deploy(VirtualFileSystem vfs, String projectId, String projectName, String projectType)
                                                                                                         throws IOException,
                                                                                                         BuilderException,
                                                                                                         VirtualFileSystemException {
        URL url = new URL(baseURL + "/builder/maven/deploy");
        String buildId = run(url, vfs.exportZip(projectId));
        LOG.info("EVENT#build-started# PROJECT#" + projectName + "# TYPE#" + projectType + "#");
        LOG.info("EVENT#project-deployed# PROJECT#" + projectName + "# TYPE#" + projectType + "# PAAS#LOCAL#");
        startCheckingBuildStatus(buildId, projectName, projectType);
        return buildId;
    }

    private String run(URL url, ContentStream zippedProject) throws IOException, BuilderException,
                                                            VirtualFileSystemException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", zippedProject.getMimeType());
            authenticate(http);
            http.setDoOutput(true);
            byte[] buff = new byte[8192];
            InputStream data = null;
            OutputStream out = null;
            try {
                data = zippedProject.getStream();
                out = http.getOutputStream();
                int r;
                while ((r = data.read(buff)) != -1) {
                    out.write(buff, 0, r);
                }
            } finally {
                if (data != null) {
                    data.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            int responseCode = http.getResponseCode();
            if (responseCode != 202) // 202 (Accepted) response is expected.
            {
                fail(http);
            }
            String location = http.getHeaderField("location");
            return location.substring(location.lastIndexOf('/') + 1);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Get result of build.
     * 
     * @param buildID ID of build need to check
     * @return string that contains description of current status of build in JSON format. Do nothing with such string just re-send result
     *         to client
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     */
    public String status(String buildID) throws IOException, BuilderException {
        URL url = new URL(baseURL + "/builder/maven/status/" + buildID);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            authenticate(http);
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                fail(http);
            }

            InputStream data = http.getInputStream();
            try {
                return readBody(data, http.getContentLength());
            } finally {
                data.close();
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Check status of build.
     * 
     * @param buildID ID of build need to check
     * @return string that contains description of current status of build in JSON format. Do nothing with such string just re-send result
     *         to client
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     */
    public String result(String buildID) throws IOException, BuilderException {
        URL url = new URL(baseURL + "/builder/maven/result/" + buildID);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            authenticate(http);
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                fail(http);
            }

            InputStream data = http.getInputStream();
            try {
                return readBody(data, http.getContentLength());
            } finally {
                data.close();
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Cancel build.
     * 
     * @param buildID ID of build to be canceled
     * @param projectName name of project which build will be interrupted
     * @param projectType type of project which build will be interrupted
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     */
    public void cancel(String buildID, String projectName, String projectType) throws IOException, BuilderException {
        URL url = new URL(baseURL + "/builder/maven/cancel/" + buildID);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            authenticate(http);
            int responseCode = http.getResponseCode();
            if (responseCode != 204) {
                fail(http);
            } else {
                LOG.info("EVENT#build-interrupted# PROJECT#" + projectName + "# TYPE#" + projectType + "#");
            }

        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Read log of build.
     * 
     * @param buildID ID of build
     * @return stream that contains build log
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     */
    public InputStream log(String buildID) throws IOException, BuilderException {
        // Download build output.
        return doDownload(baseURL + "/builder/maven/log/" + buildID);
    }

    private InputStream doDownload(String downloadURL) throws IOException, BuilderException {
        URL url = new URL(downloadURL);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            authenticate(http);
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                fail(http);
            }
            // Connection closed automatically when input stream closed.
            // If IOException or BuilderException occurs then connection closed immediately.
            return new HttpStream(http);
        } catch (IOException ioe) {
            if (http != null) {
                http.disconnect();
            }
            throw ioe;
        } catch (BuilderException be) {
            http.disconnect();
            throw be;
        }
    }

    /**
     * Check is URL for download artifact is valid. Artifact may be removed by timeout but GWT client not able to check it because to
     * cross-domain restriction for ajax requests.
     * 
     * @param url URL for checking
     * @throws IOException if any i/o errors occur
     * @throws BuilderException URL is not valid or any other errors related to build server internal state
     */
    public void checkDownloadURL(String url) throws IOException, BuilderException {
        URL checkURL = new URL(url);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)checkURL.openConnection();
            http.setRequestMethod("HEAD"); // don't want to download artifact
            authenticate(http);
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                // no response body.
                throw new BuilderException(responseCode, "", "text/plain");
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Add authentication info to the request. By default do nothing. May be reimplemented for particular authentication scheme.
     * 
     * @param http HTTP connection to add authentication info, e.g. Basic authentication headers.
     * @throws IOException if any i/o errors occur
     */
    protected void authenticate(HttpURLConnection http) throws IOException {
    }

    private void fail(HttpURLConnection http) throws IOException, BuilderException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new BuilderException(responseCode, body, body != null ? http.getContentType() : null);
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }
    }

    private String readBody(InputStream input, int contentLength) throws IOException {
        String body = null;
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int off = 0;
            int i;
            while ((i = input.read(b, off, contentLength - off)) > 0) {
                off += i;
            }
            body = new String(b);
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int i;
            while ((i = input.read(buf)) != -1) {
                bout.write(buf, 0, i);
            }
            body = bout.toString();
        }
        return body;
    }

    /**
     * Periodically checks status of the previously launched job and sends the status to WebSocket connection when job status will be
     * changed.
     * 
     * @param buildId identifier of the build job to check status
     */
    private void startCheckingBuildStatus(final String buildId, final String projectName, final String projectType) {
        final String ws = EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString();
        final String userId = ConversationState.getCurrent().getIdentity().getUserId();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    String status = status(buildId);
                    if (!status.contains("\"status\":\"IN_PROGRESS\"")) {
                        checkStatusTasks.remove(buildId);
                        cancel();
                        LOG.info("EVENT#build-finished# WS#" + ws + "# USER#" + userId + "# PROJECT#" + projectName + "# TYPE#"
                                 + projectType + "#");
                        publishWebSocketMessage(status, BUILD_STATUS_CHANNEL + buildId, null);
                    }
                } catch (Exception e) {
                    checkStatusTasks.remove(buildId);
                    cancel();
                    publishWebSocketMessage(null, BUILD_STATUS_CHANNEL + buildId, e);
                }
            }
        };
        checkStatusTasks.put(buildId, task);
        checkStatusTimer.schedule(task, CHECK_BUILD_STATUS_PERIOD, CHECK_BUILD_STATUS_PERIOD);
    }

    /**
     * Publishes the message over WebSocket connection.
     * 
     * @param data the data to be sent to the client
     * @param channelID channel identifier
     * @param e exception which has occurred or <code>null</code> if no exception
     */
    private static void publishWebSocketMessage(Object data, String channelID, Exception e) {
        ChannelBroadcastMessage message = new ChannelBroadcastMessage();
        message.setChannel(channelID);
        if (e == null) {
            message.setType(ChannelBroadcastMessage.Type.NONE);
            if (data instanceof String) {
                message.setBody((String)data);
            } else if (data != null) {
                message.setBody(JsonHelper.toJson(data));
            }
        } else {
            message.setType(ChannelBroadcastMessage.Type.ERROR);
            if (e instanceof BuilderException)
                message.setBody(e.getMessage());
        }

        try {
            WSConnectionContext.sendMessage(message);
        } catch (Exception ex) {
            LOG.error("Failed to send message over WebSocket.", ex);
        }
    }

    /** Stream that automatically close HTTP connection when all data ends. */
    private static class HttpStream extends FilterInputStream {
        private final HttpURLConnection http;
        private boolean                 closed;

        private HttpStream(HttpURLConnection http) throws IOException {
            super(http.getInputStream());
            this.http = http;
        }

        @Override
        public int read() throws IOException {
            int r = super.read();
            if (r == -1) {
                close();
            }
            return r;
        }

        @Override
        public int read(byte[] b) throws IOException {
            int r = super.read(b);
            if (r == -1) {
                close();
            }
            return r;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int r = super.read(b, off, len);
            if (r == -1) {
                close();
            }
            return r;
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            try {
                super.close();
            } finally {
                http.disconnect();
                closed = true;
            }
        }
    }
}
