/*
 * Copyright (C) 2013 eXo Platform SAS.
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
        final ManymoApplication manymoApplication = uploadApplication(apk, oauthToken);
        return "{\"applicationUrl\":\"" + "https://www.manymo.com/apps/" + manymoApplication.getId() + "/emulators/83/connect" + "\"}";
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
        // manymo.com provides large set of application attributes but at the moment we need just 'id' of application,
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
