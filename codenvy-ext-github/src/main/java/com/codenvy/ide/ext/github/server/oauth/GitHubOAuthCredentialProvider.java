package com.codenvy.ide.ext.github.server.oauth;

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.CredentialItem;
import com.codenvy.ide.ext.git.server.nativegit.CredentialsProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class GitHubOAuthCredentialProvider implements CredentialsProvider {

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
            token = tokenProvider.getToken("github", EnvironmentContext.getCurrent().getUser().getId());
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
}
