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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.ssh.client.SshKeyProvider;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.security.oauth.OAuthStatus.LOGGED_IN;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Provides SSH keys for github.com and deploys it.
 *
 * @author Ann Shumilova
 */
@Singleton
public class GitHubSshKeyProvider implements SshKeyProvider, OAuthCallback {

    private GitHubClientService gitHubService;

    private EventBus eventBus;

    private NotificationManager notificationManager;

    private String baseUrl;

    private GitHubLocalizationConstant constant;

    private AsyncRequestCallback<Void> callback;

    @Inject
    public GitHubSshKeyProvider(GitHubClientService gitHubService, EventBus eventBus, @Named("restContext") String baseUrl,
                                GitHubLocalizationConstant constant, NotificationManager notificationManager) {
        this.gitHubService = gitHubService;
        this.eventBus = eventBus;
        this.notificationManager = notificationManager;
        this.baseUrl = baseUrl;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void generateKey(String userId, AsyncRequestCallback<Void> callback) {
        this.callback = callback;
        getToken(userId);
    }

    private void getToken(final String user) {
        try {
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
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
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
        try {
            String url = baseUrl + "/github/" + Utils.getWorkspaceId() + "/ssh/generate";
            AsyncRequest.build(POST, url).loader(new EmptyLoader()).send(callback);
        } catch (RequestException e) {
            Window.alert("Upload key to github failed.");
        }
    }
}
