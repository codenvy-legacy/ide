/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server.builder;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Client for remote Maven build server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
class MavenBuilderClient {
    /** Base URL of Maven build server. */
    private final String buildServerBaseURL;
    /** Relative path to start a new build. */
    private final String BUILD_PATH        = "/builder/builder/build";
    /** Relative path to deploy project. */
    private final String DEPLOY_PATH       = "/builder/builder/deploy";
    /** Relative path to check status of a build task. */
    private final String CHECK_STATUS_PATH = "/builder/builder/status";

    /**
     * Creates new {@link MavenBuilderClient} instance.
     *
     * @param buildServerBaseURL
     *         base URL of Maven build server
     */
    MavenBuilderClient(String buildServerBaseURL) {
        if (buildServerBaseURL == null || buildServerBaseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL of Maven build server may not be null or empty string.");
        }
        this.buildServerBaseURL = buildServerBaseURL;
    }

    /**
     * Send request to start a new build at the remote Maven build server.
     *
     * @param zippedProjectFile
     *         zipped Maven project to build
     * @return ID of build task. It may be used as parameter for method {@link #checkStatus(String)}.
     * @throws IOException
     *         if an I/O error occurs
     * @throws VirtualFileSystemException
     *         if any error in VFS
     * @throws BuilderException
     *         if build request was rejected by remote Maven build server
     */
    String build(File zippedProjectFile) throws IOException, VirtualFileSystemException, BuilderException {
        return run(new URL(buildServerBaseURL + BUILD_PATH), new FileInputStream(zippedProjectFile));
    }

    /**
     * Send request to start a new build at the remote Maven build server and deploy result to the local repository.
     *
     * @param zippedProjectFile
     *         {@link java.io.File} that represents zipped project to send for build
     * @return ID of build task. It may be used as parameter for method {@link #checkStatus(String)}.
     * @throws IOException
     *         if an I/O error occurs
     * @throws VirtualFileSystemException
     *         if any error in VFS
     * @throws BuilderException
     *         if deploy request was rejected by remote Maven build server
     */
    String deploy(File zippedProjectFile) throws IOException, VirtualFileSystemException, BuilderException {
        return run(new URL(buildServerBaseURL + DEPLOY_PATH), new FileInputStream(zippedProjectFile));
    }

    /**
     * Check the status of a build task at the remote Maven build server.
     *
     * @param buildId
     *         ID of build task to check status
     * @return string that contains description of status of build task in JSON format
     * @throws IOException
     *         if an I/O error occurs
     * @throws BuilderException
     *         if checking status request was rejected by remote Maven build server
     */
    String checkStatus(String buildId) throws IOException, BuilderException {
        String status;
        for (; ; ) {
            status = status(buildId);
            if (!status.contains("\"status\":\"IN_PROGRESS\"")) {
                return status;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Do nothing. Continue executing.
            }
        }
    }

    private String status(String buildID) throws IOException, BuilderException {
        URL url = new URL(buildServerBaseURL + CHECK_STATUS_PATH + "/" + buildID);
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                responseFail(http);
            }

            try (InputStream data = http.getInputStream()) {
                return readBody(data, http.getContentLength());
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private String run(URL url, InputStream zippedProject) throws IOException, VirtualFileSystemException,
                                                                  BuilderException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("content-type", "application/zip");
            http.setDoOutput(true);
            byte[] buff = new byte[8192];
            InputStream data = null;
            OutputStream out = null;
            try {
                data = zippedProject;
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
                responseFail(http);
            }
            String location = http.getHeaderField("location");
            return location.substring(location.lastIndexOf('/') + 1);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private void responseFail(HttpURLConnection http) throws IOException, BuilderException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new BuilderException(responseCode, "Unable to build project. " + body == null ? "" : body);
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
}
