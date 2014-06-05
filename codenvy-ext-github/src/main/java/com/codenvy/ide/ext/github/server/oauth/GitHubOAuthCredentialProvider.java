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

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.CredentialItem;
import com.codenvy.ide.ext.git.server.nativegit.CredentialsProvider;
import com.codenvy.ide.security.oauth.server.OAuthAuthenticationException;
import com.codenvy.ide.security.oauth.shared.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitHubOAuthCredentialProvider implements CredentialsProvider {

    private static String OAUTH_PROVIDER_NAME = "github";
    private OAuthTokenProvider tokenProvider;

    @Inject
    public GitHubOAuthCredentialProvider(OAuthTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public boolean get(String url, CredentialItem... items) throws GitException {
        if (!url.contains("github.com")) {
            return false;
        }

        OAuthToken token;
        try {
            token = tokenProvider.getToken(OAUTH_PROVIDER_NAME, EnvironmentContext.getCurrent().getUser().getId());
        } catch (IOException e) {
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
            return false;
        }

        return true;
    }

    @Override
    public boolean getUser(String url, CredentialItem... items) throws GitException {
        if (!url.contains("github.com")) {
            return false;
        }

        OAuthToken token;
        try {
            token = tokenProvider.getToken(OAUTH_PROVIDER_NAME, EnvironmentContext.getCurrent().getUser().getId());
        } catch (IOException e) {
            return false;
        }
        if (token == null) {
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
        GitHubUser user = getJson("https://api.github.com/user?access_token=" + accessToken.getToken(), GitHubUser.class);

        GithubEmail[] result =
                getJson2("https://api.github.com/user/emails?access_token=" + accessToken.getToken(), GithubEmail[].class, null);

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

    private <O> O getJson(String getUserUrl, Class<O> userClass) throws OAuthAuthenticationException {
        HttpURLConnection urlConnection = null;
        InputStream urlInputStream = null;

        try {
            urlConnection = (HttpURLConnection)new URL(getUserUrl).openConnection();
            urlInputStream = urlConnection.getInputStream();
            return JsonHelper.fromJson(urlInputStream, userClass, null);
        } catch (JsonParseException | IOException e) {
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

    protected <O> O getJson2(String getUserUrl, Class<O> userClass, Type type) throws OAuthAuthenticationException {
        HttpURLConnection urlConnection = null;
        InputStream urlInputStream = null;

        try {
            urlConnection = (HttpURLConnection)new URL(getUserUrl).openConnection();
            urlConnection.setRequestProperty("Accept", "application/vnd.github.v3.html+json");
            urlInputStream = urlConnection.getInputStream();
            return JsonHelper.fromJson(urlInputStream, userClass, type);
        } catch (JsonParseException | IOException e) {
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
