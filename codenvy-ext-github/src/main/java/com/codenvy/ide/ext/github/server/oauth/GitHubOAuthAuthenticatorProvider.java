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
