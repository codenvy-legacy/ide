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
package org.exoplatform.ide.extension.heroku.server;

import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonNameConventions;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.ide.commons.server.ParsingResponseException;

import org.exoplatform.ide.security.paas.Credential;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Heroku API authenticator.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class HerokuAuthenticator {
    /**
     * Obtain heroku API key and store it somewhere (it is dependent to implementation) for next usage. Key should be
     * used by {@link Heroku#authenticate(HerokuCredential, HttpURLConnection)} instead of password for any request to
     * heroku service.
     *
     * @param email
     *         email address that used when create account at heroku.com
     * @param password
     *         password
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         if any i/o errors occurs
     */
    public void login(String email, String password, Credential credential)
            throws HerokuException, ParsingResponseException, IOException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(Heroku.HEROKU_API + "/login");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/json, */*");
            http.setDoOutput(true);
            OutputStream output = http.getOutputStream();
            try {
                output.write(("username=" + email + "&password=" + password).getBytes());
                output.flush();
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw Heroku.fault(http);
            }

            final HerokuCredential herokuCredential;
            InputStream input = http.getInputStream();
            try {
                herokuCredential =
                        JsonHelper.fromJson(input, HerokuCredential.class, null, JsonNameConventions.CAMEL_UNDERSCORE);
            } finally {
                input.close();
            }

            credential.setAttribute("email", herokuCredential.getEmail());
            credential.setAttribute("api_key", herokuCredential.getApiKey());
        } catch (JsonParseException e) {
            // Parsing error.
            throw new ParsingResponseException(e.getMessage(), e);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    public void login(Credential credential) throws HerokuException, ParsingResponseException, IOException {
        login(getEmail(), getPassword(), credential);
    }

    // For test.

    public String getEmail() {
        return null;
    }

    public String getPassword() {
        return null;
    }
}
