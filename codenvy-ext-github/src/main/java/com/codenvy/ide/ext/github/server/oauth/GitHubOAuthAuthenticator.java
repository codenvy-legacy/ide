/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.github.server.oauth;

import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.ide.security.oauth.server.OAuthAuthenticationException;
import com.codenvy.ide.security.oauth.server.OAuthAuthenticator;
import com.codenvy.ide.security.oauth.shared.User;
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;

/** OAuth authentication  for github account. */
public class GitHubOAuthAuthenticator extends OAuthAuthenticator {
    public GitHubOAuthAuthenticator(CredentialStore credentialStore, GoogleClientSecrets clientSecrets) {
        super(
                new AuthorizationCodeFlow.Builder(BearerToken.authorizationHeaderAccessMethod(),
                                                  new NetHttpTransport(),
                                                  new JacksonFactory(),
                                                  new GenericUrl(clientSecrets.getDetails().getTokenUri()),
                                                  new ClientParametersAuthentication(
                                                          clientSecrets.getDetails().getClientId(),
                                                          clientSecrets.getDetails().getClientSecret()),
                                                  clientSecrets.getDetails().getClientId(),
                                                  clientSecrets.getDetails().getAuthUri())
                        .setScopes(Collections.<String>emptyList())
                        .setCredentialStore(credentialStore).build(),
                new HashSet<String>(clientSecrets.getDetails().getRedirectUris()));
    }

    @Override
    public User getUser(OAuthToken accessToken) throws OAuthAuthenticationException {
        GitHubUser user = getJson("https://api.github.com/user?access_token=" + accessToken.getToken(), GitHubUser.class);


        GithubEmail[] result = getJson2("https://api.github.com/user/emails?access_token=" + accessToken.getToken(), GithubEmail[].class, null);

        GithubEmail verifiedEmail = null;
        for (GithubEmail email : result) {
            if (email.isPrimary() && email.isVerified()) {
                verifiedEmail = email;
                break;
            }
        }
        if (verifiedEmail == null || verifiedEmail.getEmail() == null || verifiedEmail.getEmail().isEmpty()) {
            throw new OAuthAuthenticationException(
                    "Sorry, we failed to find any verified emails associated with your GitHub account." +
                    " Please, verify at least one email in your GitHub account and try to connect with GitHub again.");

        }
        user.setEmail(verifiedEmail.getEmail());
        final String email = user.getEmail();
        try {
            new InternetAddress(email).validate();
        } catch (AddressException e) {
            throw new OAuthAuthenticationException(e.getMessage());
        }
        return user;
    }

    protected <O> O getJson2(String getUserUrl, Class<O> userClass, Type type) throws OAuthAuthenticationException {
        HttpURLConnection urlConnection = null;
        InputStream urlInputStream = null;

        try {
            urlConnection = (HttpURLConnection)new URL(getUserUrl).openConnection();
            urlConnection.setRequestProperty("Accept", "application/vnd.github.v3.html+json");
            urlInputStream = urlConnection.getInputStream();
            return JsonHelper.fromJson(urlInputStream, userClass, type);
        } catch (JsonParseException e) {
            throw new OAuthAuthenticationException(e.getMessage(), e);
        } catch (IOException e) {
            throw new OAuthAuthenticationException(e.getMessage(), e);
        } finally {
            if (urlInputStream != null) {
                try {
                    urlInputStream.close();
                } catch (IOException ignored) {
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    @Override
    public final String getOAuthProvider() {
        return "github";
    }

    @Override
    public OAuthToken getToken(String userId) throws IOException {
        final OAuthToken token = super.getToken(userId);
        if (!(token == null || token.getToken() == null || token.getToken().isEmpty())) {
            // Need to check if token which stored is valid for requests, then if valid - we returns it to caller
            String tokenVerifyUrl = "https://api.github.com/?access_token=" + token.getToken();
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection)new URL(tokenVerifyUrl).openConnection();
                http.setInstanceFollowRedirects(false);
                http.setRequestMethod("GET");
                http.setRequestProperty("Accept", "application/json");

                if (http.getResponseCode() == 401) {
                    return null;
                }
            } finally {
                if (http != null) {
                    http.disconnect();
                }
            }

            return token;
        }
        return null;
    }

    /**
     * information for each email address indicating if the address
     * has been verified and if it’s the user’s primary email address for GitHub.
     */
    public static class GithubEmail {
        private boolean primary;
        private boolean verified;
        private String  email;

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
