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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.security.oauth.server.OAuthAuthenticationException;
import com.codenvy.ide.security.oauth.shared.User;

import org.everrest.core.impl.provider.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Used to store credentials when given url is WSO2.
 *
 * @author Eugene Voevodin
 */
@Singleton
public class WSO2OAuthCredentialsProvider implements CredentialsProvider {
    private static      String  OAUTH_PROVIDER_NAME = "wso2";
    public static final String  WSO_2_URL_STRING    =
            "(http|https)://((([0-9a-fA-F]{32}(:x-oauth-basic)?)|([0-9a-zA-Z-_.]+))@)?git\\.cloudpreview\\.wso2\\.com" +
            "(:[0-9]{1,5})?/.+\\.git";
    public static final Pattern WSO_2_URL_PATTERN   = Pattern.compile(WSO_2_URL_STRING);

    private static final Logger LOG   = LoggerFactory.getLogger(WSO2OAuthCredentialsProvider.class);
    private static final String SCOPE = "openid";
    private final OAuthTokenProvider tokenProvider;
    private final String             userUri;

    @Inject
    public WSO2OAuthCredentialsProvider(OAuthTokenProvider tokenProvider, @Named("oauth.wso2.useruri") String userUri) {
        this.tokenProvider = tokenProvider;
        this.userUri = userUri;
    }

    @Override
    public boolean get(String url, CredentialItem... items) throws GitException {
        if (!WSO_2_URL_PATTERN.matcher(url).matches()) {
            return false;
        }
        OAuthToken token;
        try {
            token = tokenProvider.getToken(OAUTH_PROVIDER_NAME, EnvironmentContext.getCurrent().getUser().getName());
        } catch (IOException e) {
            LOG.error("Can't get token", e);
            return false;
        }
        if (token != null) {
            for (CredentialItem item : items) {
                if (item instanceof CredentialItem.Password) {
                    ((CredentialItem.Password)item).setValue("x-oauth-basic");
                    continue;
                }
                if (item instanceof CredentialItem.Username) {
                    ((CredentialItem.Username)item).setValue(token.getToken());
                }
            }
        } else {
            LOG.error("Token is null");
            return false;
        }
        return true;
    }

    @Override
    public boolean getUser(String oauthProviderName, CredentialItem... items) throws GitException {
        if (!oauthProviderName.equals(OAUTH_PROVIDER_NAME)) {
            return false;
        }

        OAuthToken token;
        try {
            token = tokenProvider.getToken(OAUTH_PROVIDER_NAME, EnvironmentContext.getCurrent().getUser().getId());
        } catch (IOException e) {
            return false;
        }

        User user;
        try {
            user = getUser(token);
        } catch (OAuthAuthenticationException e) {
            throw new GitException(e);
        }

        if (user != null) {
            for (CredentialItem item : items) {
                if (item instanceof CredentialItem.AuthenticatedUserName) {
                    ((CredentialItem.AuthenticatedUserName)item).setValue(user.getName());
                    continue;
                }
                if (item instanceof CredentialItem.AuthenticatedUserEmail) {
                    ((CredentialItem.AuthenticatedUserEmail)item).setValue(user.getEmail());
                }
            }
        } else {
            return false;
        }

        return true;
    }

    private User getUser(OAuthToken accessToken) throws OAuthAuthenticationException {
        URL getUserUrL;
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", "Bearer " + accessToken.getToken());
        try {
            getUserUrL = new URL(String.format("%s?schema=%s", userUri, SCOPE));
            JsonValue userValue = doRequest(getUserUrL, params);
            User user = new Wso2User();
            user.setEmail(userValue.getElement("http://wso2.org/claims/emailaddress").getStringValue());
            user.setName(userValue.getElement("http://wso2.org/claims/fullname").getStringValue());
            return user;
        } catch (JsonParseException | IOException e) {
            throw new OAuthAuthenticationException(e.getMessage(), e);
        }
    }

    private JsonValue doRequest(URL tokenInfoUrl, Map<String, String> params) throws IOException, JsonParseException {
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)tokenInfoUrl.openConnection();
            http.setRequestMethod("GET");
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    http.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            int responseCode = http.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                LOG.warn("Can not receive wso2 token by path: {}. Response status: {}. Error message: {}",
                         tokenInfoUrl.toString(), responseCode, IoUtil.readStream(http.getErrorStream()));
                return null;
            }

            JsonValue result;
            try (InputStream input = http.getInputStream()) {
                result = JsonHelper.parseJson(input);
            }
            return result;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private class Wso2User implements User {
        private String email;
        private String name;

        @Override
        public final String getId() {
            return email;
        }

        @Override
        public final void setId(String id) {
            // JSON response from Google API contains key 'id' but it has different purpose.
            // Ignore calls of this method. Email address is used as user identifier.
        }

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public void setEmail(String email) {
            setId(email);
            this.email = email;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Wso2User{" +
                   "id='" + getId() + '\'' +
                   ", email='" + email + '\'' +
                   ", name='" + name + '\'' +
                   '}';
        }
    }
}
