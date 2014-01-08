/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.java.jdi.server;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.commons.server.ParsingResponseException;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.DebugMode;
import org.exoplatform.ide.extension.cloudfoundry.server.ext.CloudfoundryPool;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;
import org.exoplatform.ide.extension.java.jdi.server.model.ApplicationInstanceImpl;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.picocontainer.Startable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codenvy.commons.json.JsonHelper.toJson;
import static com.codenvy.ide.commons.server.ContainerUtils.readValueParam;
import static com.codenvy.commons.lang.IoUtil.countFileHash;
import static com.codenvy.commons.lang.IoUtil.createTempDirectory;
import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static com.codenvy.commons.lang.IoUtil.downloadFile;
import static com.codenvy.commons.lang.IoUtil.list;
import static com.codenvy.commons.lang.NameGenerator.generate;
import static com.codenvy.commons.lang.ZipUtils.listEntries;
import static com.codenvy.commons.lang.ZipUtils.unzip;
import static com.codenvy.commons.lang.ZipUtils.zipDir;

/**
 * ApplicationRunner for deploy Java applications at Cloud Foundry PaaS.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplicationRunner implements ApplicationRunner, Startable {
    /** Default application lifetime (in minutes). After this time application may be stopped automatically. */
    private static final int DEFAULT_APPLICATION_LIFETIME = 10;

    /** Expiration time (in milliseconds) which is left to notify user about this. */
    private static final long EXPIRATION_TIME_LEFT_TO_NOTIFY = 2 * 60 * 1000;

    private static final Log LOG = ExoLogger.getLogger(CloudfoundryApplicationRunner.class);

    private final int  applicationLifetime;
    private final long applicationLifetimeMillis;

    private final CloudfoundryPool cfServers;

    private final Map<String, Application> applications;
    private final ScheduledExecutorService applicationTerminator;


    public CloudfoundryApplicationRunner(CloudfoundryPool cfServers, InitParams initParams) {
        this(cfServers, parseApplicationLifeTime(readValueParam(initParams, "cloudfoundry-application-lifetime")));
    }

    private static int parseApplicationLifeTime(String str) {
        if (str != null) {
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {
            }
        }
        return DEFAULT_APPLICATION_LIFETIME;
    }

    protected CloudfoundryApplicationRunner(CloudfoundryPool cfServers, int applicationLifetime) {
        if (applicationLifetime < 1) {
            throw new IllegalArgumentException("Invalid application lifetime: " + 1);
        }
        this.applicationLifetime = applicationLifetime;
        this.applicationLifetimeMillis = applicationLifetime * 60 * 1000;
        this.cfServers = cfServers;

        this.applications = new ConcurrentHashMap<String, Application>();
        this.applicationTerminator = Executors.newSingleThreadScheduledExecutor();
        this.applicationTerminator.scheduleAtFixedRate(new TerminateApplicationTask(), 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public ApplicationInstance runApplication(URL war, Map<String, String> params) throws ApplicationRunnerException {
        return startApplication(cfServers.next(), generate("app-", 16), war, null, params);
    }

    @Override
    public ApplicationInstance debugApplication(URL war, boolean suspend, Map<String, String> params)
            throws ApplicationRunnerException {
        return startApplication(cfServers.next(), generate("app-", 16), war,
                                suspend ? new DebugMode("suspend") : new DebugMode(), params);
    }

    private enum APPLICATION_TYPE {
        JAVA_WEB,
        JAVA_WEB_APP_ENGINE
    }

    private APPLICATION_TYPE determineApplicationType(java.io.File war) throws IOException {
        for (String f : listEntries(war)) {
            if (f.endsWith("WEB-INF/appengine-web.xml")) {
                return APPLICATION_TYPE.JAVA_WEB_APP_ENGINE;
            }
        }
        return APPLICATION_TYPE.JAVA_WEB;
    }

    private ApplicationInstance startApplication(Cloudfoundry cloudfoundry,
                                                 String name,
                                                 URL war,
                                                 DebugMode debugMode,
                                                 Map<String, String> params) throws ApplicationRunnerException {
        final java.io.File path;
        final APPLICATION_TYPE type;
        try {
            path = downloadFile(null, "app-", ".war", war);
            type = determineApplicationType(path);
        } catch (IOException e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }

        try {
            if (debugMode != null) {
                return doDebugApplication(cloudfoundry, name, path, type, debugMode, params);
            }
            return doRunApplication(cloudfoundry, name, path, type, params);
        } catch (ApplicationRunnerException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CloudfoundryException) {
                if (200 == ((CloudfoundryException)cause).getExitCode()) {
                    // login and try one more time.
                    login(cloudfoundry);
                    if (debugMode != null) {
                        return doDebugApplication(cloudfoundry, name, path, type, debugMode, params);
                    }
                    return doRunApplication(cloudfoundry, name, path, type, params);
                }
            }
            throw e;
        } finally {
            if (path.exists()) {
                path.delete();
            }
        }
    }

    private ApplicationInstance doRunApplication(Cloudfoundry cloudfoundry,
                                                 String name,
                                                 java.io.File path,
                                                 APPLICATION_TYPE type,
                                                 Map<String, String> params) throws ApplicationRunnerException {
        try {
            final String target = cloudfoundry.getTarget();
            final CloudFoundryApplication cfApp = createApplication(cloudfoundry, target, name, path, type, null, params);
            final long expired = System.currentTimeMillis() + applicationLifetimeMillis;

            String wsName = EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString();
            String userId = ConversationState.getCurrent().getIdentity().getUserId();

            Application application = new Application(name, target, expired, params.get("projectName"), wsName, userId, 0);
            applications.put(name, application);
            LOG.debug("Start application {} at CF server {}", name, target);
            LOG.info("EVENT#run-started# WS#" + wsName + "# USER#" + userId + "# PROJECT#" + params.get("projectName") + "# TYPE#War# ID#" +
                     application.getSessionId() + "#");
            LOG.info("EVENT#project-deployed# WS#" + wsName + "# USER#" + userId + "# PROJECT#" + params.get("projectName")
                     + "# TYPE#War# PAAS#LOCAL#");
            return new ApplicationInstanceImpl(name, cfApp.getUris().get(0), null, applicationLifetime);
        } catch (Exception e) {
            String logs = safeGetLogs(cloudfoundry, name);

            // try to remove application.
            try {
                LOG.warn("Application {} failed to start, cause: {}", name, e.getMessage());
                cloudfoundry.deleteApplication(cloudfoundry.getTarget(), name, null, null, "cloudfoundry", true);
            } catch (Exception e1) {
                LOG.warn("Unable delete failed application {}, cause: {}", name, e.getMessage());
            }

            throw new ApplicationRunnerException(e.getMessage(), e, logs);
        }
    }

    private ApplicationInstance doDebugApplication(Cloudfoundry cloudfoundry,
                                                   String name,
                                                   java.io.File path,
                                                   APPLICATION_TYPE type,
                                                   DebugMode debugMode,
                                                   Map<String, String> params) throws ApplicationRunnerException {
        try {
            final String target = cloudfoundry.getTarget();
            final CloudFoundryApplication cfApp = createApplication(cloudfoundry, target, name, path, type, debugMode, params);
            final long expired = System.currentTimeMillis() + applicationLifetimeMillis;

            Instance[] instances = cloudfoundry.applicationInstances(target, name, null, null);
            if (instances.length != 1) {
                throw new ApplicationRunnerException("Unable run application in debug mode. ");
            }

            String wsName = EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString();
            String userId = ConversationState.getCurrent().getIdentity().getUserId();

            Application application = new Application(name, target, expired, params.get("projectName"), wsName, userId, 1);
            applications.put(name, application);
            LOG.debug("Start application {} under debug at CF server {}", name, target);
            LOG.info("EVENT#debug-started# WS#" + wsName + "# USER#" + userId + "# PROJECT#" + params.get("projectName") +
                     "# TYPE#War# ID#" + application.getSessionId() + "#");
            LOG.info("EVENT#project-deployed# WS#" + wsName + "# USER#" + userId + "# PROJECT#" + params.get("projectName")
                     + "# TYPE#War# PAAS#LOCAL#");
            return new ApplicationInstanceImpl(name, cfApp.getUris().get(0), null, applicationLifetime,
                                               instances[0].getDebugHost(), instances[0].getDebugPort());
        } catch (Exception e) {
            String logs = safeGetLogs(cloudfoundry, name);

            // try to remove application.
            try {
                LOG.warn("Application {} failed to start, cause: {}", name, e.getMessage());
                cloudfoundry.deleteApplication(cloudfoundry.getTarget(), name, null, null, "cloudfoundry", true);
            } catch (Exception e1) {
                LOG.warn("Unable delete failed application {}, cause: {}", name, e.getMessage());
            }

            throw new ApplicationRunnerException(e.getMessage(), e, logs);
        }
    }

    @Override
    public String getLogs(String name) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            Cloudfoundry cloudfoundry = cfServers.byTargetName(application.server);
            if (cloudfoundry != null) {
                try {
                    return doGetLogs(cloudfoundry, name);
                } catch (ApplicationRunnerException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof CloudfoundryException) {
                        if (200 == ((CloudfoundryException)cause).getExitCode()) {
                            login(cloudfoundry);
                            return doGetLogs(cloudfoundry, name);
                        }
                    }
                    throw e;
                }
            } else {
                throw new ApplicationRunnerException("Unable get logs. Server not available. ");
            }
        } else {
            throw new ApplicationRunnerException("Unable get logs. Application '" + name + "' not found. ");
        }
    }

    private String doGetLogs(Cloudfoundry cloudfoundry, String name) throws ApplicationRunnerException {
        try {
            return cloudfoundry.getLogs(cloudfoundry.getTarget(), name, "0", null, null);
        } catch (Exception e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }
    }

    /**
     * Get applications logs and hide any errors. This method is used for getting logs of failed application to help user understand what
     * is
     * going wrong.
     */
    private String safeGetLogs(Cloudfoundry cloudfoundry, String name) {
        try {
            return cloudfoundry.getLogs(cloudfoundry.getTarget(), name, "0", null, null);
        } catch (Exception e) {
            // Not able show log if any errors occurs.
            return null;
        }
    }

    @Override
    public void stopApplication(String name) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            Cloudfoundry cloudfoundry = cfServers.byTargetName(application.server);
            if (cloudfoundry != null) {
                try {
                    doStopApplication(cloudfoundry, name);
                } catch (ApplicationRunnerException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof CloudfoundryException) {
                        if (200 == ((CloudfoundryException)cause).getExitCode()) {
                            login(cloudfoundry);
                            doStopApplication(cloudfoundry, name);
                        }
                    }
                    throw e;
                }
            } else {
                throw new ApplicationRunnerException("Unable stop application. Server not available. ");
            }
        } else {
            throw new ApplicationRunnerException("Unable stop application. Application '" + name + "' not found. ");
        }
    }

    private void doStopApplication(Cloudfoundry cloudfoundry, String name) throws ApplicationRunnerException {
        try {
            String target = cloudfoundry.getTarget();
            Application app = applications.get(name);
            cloudfoundry.stopApplication(target, name, null, null, "cloudfoundry");
            cloudfoundry.deleteApplication(target, name, null, null, "cloudfoundry", true);
            publishWebSocketMessage(null, "runner:application-stopped:" + name);
            LOG.debug("Stop application {}.", name);
            if (app.type == 0) {
                LOG.info("EVENT#run-finished# WS#" + app.wsName + "# USER#" + app.userId + "# PROJECT#" + app.projectName
                         + "# TYPE#War# ID#" + app.getSessionId() + "#");
            } else if (app.type == 1) {
                LOG.info("EVENT#debug-finished# WS#" + app.wsName + "# USER#" + app.userId + "# PROJECT#" + app.projectName
                         + "# TYPE#War# ID#" + app.getSessionId() + "#");
            }
            applications.remove(name);
        } catch (Exception e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        applicationTerminator.shutdownNow();
        for (Application app : applications.values()) {
            try {
                stopApplication(app.name);
            } catch (ApplicationRunnerException e) {
                LOG.error("Failed to stop application {}.", app.name, e);
            }
        }
        applications.clear();
    }

    private CloudFoundryApplication createApplication(Cloudfoundry cloudfoundry,
                                                      String target,
                                                      String name,
                                                      java.io.File path,
                                                      APPLICATION_TYPE type,
                                                      DebugMode debug,
                                                      Map<String, String> params)
            throws CloudfoundryException,
                   IOException,
                   ParsingResponseException,
                   VirtualFileSystemException,
                   CredentialStoreException {
        if (APPLICATION_TYPE.JAVA_WEB_APP_ENGINE == type) {
            return cloudfoundry.createApplication(target, name, "java_gae", null, 1, 256, false, "java", null, debug, null,
                                                  null, path.toURI().toURL(), null, params);
        }
        return cloudfoundry.createApplication(target, name, "spring", null, 1, 256, false, "java", null, debug, null,
                                              null, path.toURI().toURL(), null, params);
    }

    @Override
    public void prolongExpirationTime(String name, long time) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            application.expirationTime += time;
            return;
        }
        throw new ApplicationRunnerException("Unable to prolong expiration time of application. Application '" + name + "' not found. ");
    }

    /**
     * Pattern to get CF server name. Normally target URL of CF server is http://api.server.com and we need to get server.com.
     */
    private static final Pattern serverNameGetter = Pattern.compile("(http(s)?://)?([^\\.]+)(.*)");

    @Override
    public void updateApplication(String name, URL war) throws ApplicationRunnerException {
        Application application = applications.get(name);
        if (application != null) {
            Cloudfoundry cloudfoundry = cfServers.byTargetName(application.server);
            if (cloudfoundry != null) {
                java.io.File sourceWar = null;
                java.io.File uploadZip = null;
                java.io.File appDir = null;
                try {
                    sourceWar = downloadFile(null, "app-", ".war", war);

                    Matcher m = serverNameGetter.matcher(application.server);
                    m.matches();
                    final URL url = new URL(application.server.substring(0, m.start(3)) + name + application.server.substring(m.end(3))
                                            + "/update_jrebel");

                    // Get md5 hashes for remote files
                    Map<String, String> remoteClassesHashes = new HashMap<String, String>();
                    Map<String, String> remoteLibHashes = new HashMap<String, String>();
                    Map<String, String> remoteWebHashes = new HashMap<String, String>();
                    getRemoteFileHashes(url, remoteClassesHashes, remoteLibHashes, remoteWebHashes);

                    appDir = createTempDirectory(name + "-update");
                    unzip(sourceWar, appDir);

                    // Separate application files:
                    // 1. Files from WEB-INF/classes
                    // 2. Files from WEB-INF/lib
                    // 3. Other files. NOTE: Always skip maven files from META-INF/maven
                    java.io.File classesDir = new java.io.File(appDir, "WEB-INF/classes");
                    List<java.io.File> classes =
                            classesDir.exists() ? list(classesDir, null) : Collections.<java.io.File>emptyList();
                    java.io.File libDir = new java.io.File(appDir, "WEB-INF/lib");
                    List<java.io.File> libs = libDir.exists() ? list(libDir, null) : Collections.<java.io.File>emptyList();
                    List<java.io.File> web = list(appDir, new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return !(dir.getAbsolutePath().endsWith("WEB-INF/classes")
                                     || dir.getAbsolutePath().endsWith("WEB-INF/lib")
                                     || dir.getAbsolutePath().endsWith("META-INF/maven"));
                        }
                    });

                    // Prepare digest for counting md5 hashes for local files.
                    MessageDigest digest;
                    try {
                        digest = MessageDigest.getInstance("MD5");
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }

                    // Check file hashes and remove all files that are the same to remote files.
                    checkFiles(classesDir, classes, remoteClassesHashes, digest);
                    checkFiles(libDir, libs, remoteLibHashes, digest);
                    checkFiles(appDir, web, remoteWebHashes, digest);

                    // Pack to zip files that must be upload to remote server.
                    uploadZip = new java.io.File(System.getProperty("java.io.tmpdir"), appDir.getName() + ".zip");
                    zipDir(appDir.getAbsolutePath(), appDir, uploadZip, null);
                    doUpdateApplication(url, uploadZip, remoteClassesHashes, remoteLibHashes, remoteWebHashes);
                } catch (IOException e) {
                    throw new ApplicationRunnerException(e.getMessage(), e);
                } finally {
                    // Cleanup create files and directories.
                    if (sourceWar != null && sourceWar.exists()) {
                        sourceWar.delete();
                    }
                    if (appDir != null && appDir.exists()) {
                        deleteRecursive(appDir);
                    }
                    if (uploadZip != null && uploadZip.exists()) {
                        uploadZip.delete();
                    }
                }
            } else {
                throw new ApplicationRunnerException("Unable update application. Server not available. ");
            }
        } else {
            throw new ApplicationRunnerException("Unable update application. Application '" + name + "' not found. ");
        }
    }

    private void getRemoteFileHashes(URL url,
                                     Map<String, String> remoteClassesHashes,
                                     Map<String, String> remoteLibHashes,
                                     Map<String, String> remoteWebHashes) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            InputStream input = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            Map<String, String> hashes = remoteClassesHashes;
            int i = 0;

            // Read response line by line. Expected response format is: md5_hash_sum relative_file_path
            // Empty line separate list for three groups:
            // 1. Files from WEB-INF/classes
            // 2. Files from WEB-INF/lib
            // 3. Other files
            //
            // Here is example of remote server response:
            // 83d230901f5f18eb8804aa029e1094df helloworld/GreetingController.class
            // ...
            // [blank line]
            // c49fbf1401117f2a7de32a0e29309600 spring-web-3.0.5.RELEASE.jar
            // ...
            // [blank line]
            // 23d647f59023c61b67df7d086e75bd39 index.jsp
            // ...

            while ((line = reader.readLine()) != null && i < 3) {
                if (line.isEmpty()) {
                    i++;
                    switch (i) {
                        case 1:
                            hashes = remoteLibHashes;
                            break;
                        case 2:
                            hashes = remoteWebHashes;
                            break;
                    }
                    continue;
                }
                String hash = line.substring(0, 32); // Length of MD-5 hash sum
                String relPath = line.substring(33);
                hashes.put(relPath, hash);
            }
            input.close();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void checkFiles(java.io.File baseDir,
                            List<java.io.File> files,
                            Map<String, String> remoteFilesHashes,
                            MessageDigest digest) throws IOException {
        int relPathOffset = baseDir.getAbsolutePath().length() + 1;
        for (java.io.File f : files) {
            String relPath = f.getAbsolutePath().substring(relPathOffset);
            if (remoteFilesHashes.containsKey(relPath)) {
                digest.reset();
                if (remoteFilesHashes.get(relPath).equals(countFileHash(f, digest))) {
                    // Delete file in hashes are the same.
                    f.delete();
                }
                remoteFilesHashes.remove(relPath);
            }
        }
    }

    private void doUpdateApplication(URL url, java.io.File zip,
                                     Map<String, String> remoteClassesHashes,
                                     Map<String, String> remoteLibHashes,
                                     Map<String, String> remoteWebHashes) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("content-type", "application/zip");
            conn.setRequestProperty("content-length", Long.toString(zip.length()));
            // Send lists of files that should be removed in request headers.
            conn.setRequestProperty("x-exo-ide-classes-delete", remoteClassesHashes.keySet().toString());
            conn.setRequestProperty("x-exo-ide-lib-delete", remoteLibHashes.keySet().toString());
            conn.setRequestProperty("x-exo-ide-web-delete", remoteWebHashes.keySet().toString());
            //
            conn.setDoOutput(true);
            byte[] buf = new byte[8192];
            int r;
            InputStream zipIn = new FileInputStream(zip);
            OutputStream out = conn.getOutputStream();
            try {
                while ((r = zipIn.read(buf)) != -1) {
                    out.write(buf, 0, r);
                }
            } finally {
                zipIn.close();
                out.close();
            }
            int responseCode = conn.getResponseCode();
            if ((responseCode / 100) != 2) {
                throw new IOException("Update application failed. ");
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private void login(Cloudfoundry cloudfoundry) throws ApplicationRunnerException {
        try {
            cloudfoundry.login();
        } catch (Exception e) {
            throw new ApplicationRunnerException(e.getMessage(), e);
        }
    }

    private class TerminateApplicationTask implements Runnable {
        @Override
        public void run() {
            List<String> stopped = new ArrayList<String>();
            for (Application app : applications.values()) {
                if (app.isExpired()) {
                    try {
                        stopApplication(app.name);
                    } catch (ApplicationRunnerException e) {
                        LOG.error("Failed to stop application {}.", app.name, e);
                    }
                    // Do not try to stop application twice.
                    stopped.add(app.name);
                } else if (app.isExpiresAfter(EXPIRATION_TIME_LEFT_TO_NOTIFY)) {
                    publishWebSocketMessage(null, "debugger:expireSoonApp:" + app.name);
                }
            }
            applications.keySet().removeAll(stopped);
            LOG.debug("{} applications removed. ", stopped.size());
        }
    }

    private static class Application {
        final String name;
        final String server;
        final String projectName;
        long expirationTime;
        final String wsName;
        final String userId;
        // 0 - normal run, 1- debug mode
        final int    type;
        final String sessionId = UUID.randomUUID().toString();

        Application(String name, String server, long expirationTime, String projectName, String wsName, String userId, int type) {
            this.name = name;
            this.server = server;
            this.expirationTime = expirationTime;
            this.projectName = projectName;
            this.type = type;
            this.wsName = wsName;
            this.userId = userId;
        }

        boolean isExpired() {
            return expirationTime < System.currentTimeMillis();
        }

        boolean isExpiresAfter(long delay) {
            return expirationTime - System.currentTimeMillis() <= delay;
        }

        String getSessionId() {
            return sessionId;
        }
    }

    /**
     * Publish the message over WebSocket connection.
     *
     * @param data
     *         the data to be sent to the client
     * @param channelID
     *         channel identifier
     */
    private static void publishWebSocketMessage(Object data, String channelID) {
        ChannelBroadcastMessage message = new ChannelBroadcastMessage();
        message.setChannel(channelID);
        message.setType(ChannelBroadcastMessage.Type.NONE);
        if (data instanceof String) {
            message.setBody((String)data);
        } else if (data != null) {
            message.setBody(toJson(data));
        }

        try {
            WSConnectionContext.sendMessage(message);
        } catch (Exception e) {
            LOG.error("Failed to send message over WebSocket.", e);
        }
    }
}
