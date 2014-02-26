/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.github.client;

import com.codenvy.ide.ext.ssh.client.SshKeyProvider;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.security.oauth.OAuthStatus.LOGGED_IN;

/**
 * Provides SSH keys for github.com and deploys it.
 *
 * @author Ann Shumilova
 */
@Singleton
public class GitHubSshKeyProvider implements SshKeyProvider, OAuthCallback {

    private GitHubClientService        gitHubService;
    private String                     baseUrl;
    private String                     workspaceId;
    private GitHubLocalizationConstant constant;
    private AsyncRequestCallback<Void> callback;
    private AsyncRequestFactory        asyncRequestFactory;

    @Inject
    public GitHubSshKeyProvider(GitHubClientService gitHubService,
                                @Named("restContext") String baseUrl,
                                @Named("workspaceId") String workspaceId,
                                GitHubLocalizationConstant constant, AsyncRequestFactory asyncRequestFactory) {
        this.gitHubService = gitHubService;
        this.baseUrl = baseUrl;
        this.workspaceId = workspaceId;
        this.constant = constant;
        this.asyncRequestFactory = asyncRequestFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void generateKey(String userId, AsyncRequestCallback<Void> callback) {
        this.callback = callback;
        getToken(userId);
    }

    private void getToken(final String user) {
        gitHubService.getUserToken(user, new AsyncRequestCallback<String>(new com.codenvy.ide.rest.StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                if (result == null || result.isEmpty()) {
                    oAuthLoginStart(user);
                } else {
                    generateGitHubKey();
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                oAuthLoginStart(user);
            }
        });
    }

    /** Log in  github */
    private void oAuthLoginStart(@NotNull String user) {
        boolean permitToRedirect = Window.confirm(constant.loginOAuthLabel());
        if (permitToRedirect) {
            String authUrl = baseUrl + "/oauth/authenticate?oauth_provider=github"
                             + "&scope=user&userId=" + user + "&scope=repo&redirect_after_login=/ide/" + Utils.getWorkspaceName();
            JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
            authWindow.loginWithOAuth();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        if (LOGGED_IN.equals(authStatus)) {
            generateGitHubKey();
        }
    }

    /** Generate github key. */
    public void generateGitHubKey() {
        final String url = baseUrl + "/github/" + workspaceId + "/ssh/generate";
        asyncRequestFactory.createPostRequest(url, null).loader(new EmptyLoader()).send(callback);
    }
}
