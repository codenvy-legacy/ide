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
package org.exoplatform.ide.extension.cloudfoundry.server;

import com.codenvy.ide.commons.server.ParsingResponseException;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.ide.security.paas.Credential;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryAuthenticator {
    private static final String defaultTarget = "http://api.cloudfoundry.com";

    /**
     * Obtain cloudfoundry API token and store it somewhere (it is dependent to implementation) for next usage. Token
     * should be used instead of username/password for any request to cloudfoundry service.
     *
     * @param target
     *         location of Cloud Foundry REST API, e.g. http://api.cloudfoundry.com
     * @param email
     *         email address that used when signup to cloudfoundry.com
     * @param password
     *         password
     * @param credential
     *         use it to sore credential after successful authentication.
     * @throws CloudfoundryException
     *         if cloudfoundry server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         if any i/o errors occurs
     */
    public final void login(String target, String email, String password, Credential credential)
            throws CloudfoundryException, ParsingResponseException, IOException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(target + "/users/" + email + "/tokens");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/json, */*");
            http.setRequestProperty("Content-type", "application/json");
            http.setDoOutput(true);
            OutputStream output = http.getOutputStream();
            try {
                output.write(("{\"password\":\"" + password + "\"}").getBytes());
                output.flush();
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw Cloudfoundry.fault(http);
            }

            InputStream input = http.getInputStream();
            JsonValue jsonValue;
            try {
                JsonParser jsonParser = new JsonParser();
                jsonParser.parse(input);
                jsonValue = jsonParser.getJsonObject();
            } finally {
                input.close();
            }

            credential.setAttribute(target, jsonValue.getElement("token").getStringValue());
            credential.setAttribute("current_target", target);
        } catch (JsonException jsonExc) {
            throw new ParsingResponseException(jsonExc.getMessage(), jsonExc);
        } catch (UnknownHostException exc) {
            throw new CloudfoundryException(500, "Can't access target.\n", "text/plain");
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    public final void login(Credential credential) throws CloudfoundryException, ParsingResponseException, IOException {
        login(getTarget(), getEmail(), getPassword(), credential);
    }

    // For test

    public String getEmail() {
        return null;
    }

    public String getPassword() {
        return null;
    }

    public String getTarget() {
        return defaultTarget;
    }
}
