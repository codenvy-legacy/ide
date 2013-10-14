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
package com.codenvy.ide.ext.extruntime.server;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Client to remote Maven build server.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
class MavenBuilderClient {

    /** Base URL of build server. */
    private final String buildServerBaseURL;

    MavenBuilderClient(String buildServerBaseURL) {
        if (buildServerBaseURL == null || buildServerBaseURL.isEmpty()) {
            throw new IllegalArgumentException("Base URL of build server may not be null or empty string.");
        }
        this.buildServerBaseURL = buildServerBaseURL;
    }

    String build(File zippedProjectFile) throws IOException, VirtualFileSystemException, ExtensionLauncherException {
        return run(new URL(buildServerBaseURL + "/builder/maven/build"), new FileInputStream(zippedProjectFile));
    }

    String deploy(File zippedProjectFile) throws IOException, VirtualFileSystemException, ExtensionLauncherException {
        return run(new URL(buildServerBaseURL + "/builder/maven/deploy"), new FileInputStream(zippedProjectFile));
    }

    String checkStatus(String buildId) throws IOException, ExtensionLauncherException {
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

    private String status(String buildID) throws IOException, ExtensionLauncherException {
        URL url = new URL(buildServerBaseURL + "/builder/maven/status/" + buildID);
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
                                                                  ExtensionLauncherException {
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

    private void responseFail(HttpURLConnection http) throws IOException, ExtensionLauncherException {
        InputStream errorStream = null;
        try {
            int responseCode = http.getResponseCode();
            int length = http.getContentLength();
            errorStream = http.getErrorStream();
            String body = null;
            if (errorStream != null) {
                body = readBody(errorStream, length);
            }
            throw new ExtensionLauncherException(responseCode, "Unable to build project. " + body == null ? "" : body);
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