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
package com.codenvy.ide.extension.android.server;

import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonNameConventions;
import com.codenvy.commons.lang.IoUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
@Path("{ws-name}/android")
public class AndroidRunnerService {
    // TODO : add abstraction to be able run android application in more than one environment.
    private static final byte[] NEW_LINE                = "\r\n".getBytes();
    private static final byte[] HYPHENS                 = "--".getBytes();
    private static final byte[] CONTENT_DISPOSITION_APK = "Content-Disposition: form-data; name=\"app[apk]\"; filename=\"".getBytes();
//    private static final String OAUTH_TOKEN             = "e6TnJdyN5vs4xsGUmwODeiq6iHO3lOR6ch1Q6PKe";

    @GET
    @Path("run")
    @Produces(MediaType.APPLICATION_JSON)
    public String run(@QueryParam("apk") URL apk, @QueryParam("oauth_token") String oauthToken) throws Exception {
        final ManymoApplication manymo = uploadApplication(apk, oauthToken);
        return "{\"applicationUrl\":\"" + "https://www.manymo.com/apps/" + manymo.getId() + "/emulators/83/connect?secret=" +
               manymo.getSecret() + "\"}";
    }

    private ManymoApplication uploadApplication(URL apk, String oauthToken) throws Exception {
        HttpURLConnection http = null;
        java.io.File path = null;
        try {
            path = IoUtil.downloadFile(null, "app-", ".apk", apk);
            IoUtil.downloadFile(null, "app-", ".apk", apk);
            http = (HttpURLConnection)new URL("https://www.manymo.com/api/v1/apps").openConnection();
            http.setInstanceFollowRedirects(false);
            http.setRequestMethod("POST");
            http.setRequestProperty("Authorization", "OAuth token=" + oauthToken);
            http.setRequestProperty("Accept", "application/json");
            final String boundary = "----------" + System.currentTimeMillis();
            http.setRequestProperty("Content-type", "multipart/form-data; boundary=" + boundary);
            http.setDoOutput(true);
            final OutputStream output = http.getOutputStream();
            try {
                final byte[] boundaryBytes = boundary.getBytes();
                output.write(HYPHENS);
                output.write(boundaryBytes);
                output.write(NEW_LINE);
                output.write(CONTENT_DISPOSITION_APK);
                output.write(path.getName().getBytes());
                output.write('"');
                output.write(NEW_LINE);
                output.write(NEW_LINE);
                FileInputStream apkInput = new FileInputStream(path);
                try {
                    byte[] b = new byte[8192];
                    int r;
                    while ((r = apkInput.read(b)) != -1) {
                        output.write(b, 0, r);
                    }
                } finally {
                    apkInput.close();
                }
                output.write(NEW_LINE);
                output.write(HYPHENS);
                output.write(boundaryBytes);
                output.write(HYPHENS);
                output.write(NEW_LINE);
            } finally {
                output.close();
            }
            final int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Invalid response code: " + responseCode);
            }
            InputStream input = http.getInputStream();
            try {
                return JsonHelper.fromJson(input, ManymoApplication.class, null, JsonNameConventions.CAMEL_UNDERSCORE);
            } finally {
                input.close();
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
            if (path != null) {
                path.delete();
            }
        }
    }

    public static class ManymoApplication {
        // manymo.com provides large set of application attributes but
        // at the moment we need just 'id' and 'secret' of application.
        private long   id;
        private String secret;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        @Override
        public String toString() {
            return "ManymoApplication{" +
                   "id=" + id +
                   ", secret='" + secret + '\'' +
                   '}';
        }
    }
}
