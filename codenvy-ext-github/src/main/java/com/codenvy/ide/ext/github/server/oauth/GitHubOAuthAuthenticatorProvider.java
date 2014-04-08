/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.ext.github.server.oauth;

import com.codenvy.ide.security.oauth.server.OAuthAuthenticator;
import com.codenvy.ide.security.oauth.server.OAuthAuthenticatorProvider;
import com.google.api.client.auth.oauth2.MemoryCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * @author Vitaly Parfonov
 */
@Singleton
public class GitHubOAuthAuthenticatorProvider implements OAuthAuthenticatorProvider {

    private final GitHubOAuthAuthenticator gitHubOAuthAuthenticator;

    @Inject
    public GitHubOAuthAuthenticatorProvider(@Nullable @Named("security.local.oauth.github-client-id") String clientId,
                                            @Nullable @Named("security.local.oauth.github-client-secret") String clientSecret,
                                            @Named("security.local.oauth.github-auth-uri") String authUri,
                                            @Named("security.local.oauth.github-token-uri") String tokenUri,
                                            @Named("security.local.oauth.github-redirect-uris") String redirectUris
                                           ) {

        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setAuthUri(authUri);
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        web.setRedirectUris(Arrays.asList(redirectUris));
        web.setTokenUri(tokenUri);
        GoogleClientSecrets secrets = new GoogleClientSecrets();
        secrets.setWeb(web);
        gitHubOAuthAuthenticator = new GitHubOAuthAuthenticator(new MemoryCredentialStore(),secrets);
    }

    @Override
    public OAuthAuthenticator getAuthenticator() {
        return gitHubOAuthAuthenticator;
    }

    @Override
    public String getId() {
        return "github";
    }
}
