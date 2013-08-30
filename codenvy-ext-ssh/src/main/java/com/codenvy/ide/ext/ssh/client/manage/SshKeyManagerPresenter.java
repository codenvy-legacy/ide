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
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.ui.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.resources.marshal.UserUnmarshaller;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.github.GitHubClientService;
import com.codenvy.ide.ext.git.client.marshaller.AllRepositoriesUnmarshaller;
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.ext.ssh.client.JsonpAsyncCallback;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ext.ssh.client.SshResources;
import com.codenvy.ide.ext.ssh.client.key.SshKeyPresenter;
import com.codenvy.ide.ext.ssh.client.marshaller.SshKeysUnmarshaller;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyPresenter;
import com.codenvy.ide.ext.ssh.shared.GenKeyRequest;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.marshal.StringUnmarshaller;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.security.oauth.OAuthStatus.LOGGED_IN;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The presenter for managing ssh keys.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeyManagerPresenter May 18, 2011 10:16:44 AM evgen $
 */
@Singleton
public class SshKeyManagerPresenter extends AbstractPreferencesPagePresenter implements SshKeyManagerView.ActionDelegate, OAuthCallback {
    private SshKeyManagerView       view;
    private SshKeyService           service;
    private SshLocalizationConstant constant;
    private EventBus                eventBus;
    private ConsolePart             console;
    private UserClientService       userService;
    private GitHubClientService     gitHubClientService;
    private Loader                  loader;
    private String                  restContext;
    private SshKeyPresenter         sshKeyPresenter;
    private UploadSshKeyPresenter   uploadSshKeyPresenter;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resources
     * @param constant
     * @param eventBus
     * @param console
     * @param userService
     * @param gitHubClientService
     * @param restContext
     */
    @Inject
    public SshKeyManagerPresenter(SshKeyManagerView view, SshKeyService service, SshResources resources, SshLocalizationConstant constant,
                                  EventBus eventBus, ConsolePart console, UserClientService userService,
                                  GitHubClientService gitHubClientService, @Named("restContext") String restContext,
                                  SshKeyPresenter sshKeyPresenter, UploadSshKeyPresenter uploadSshKeyPresenter) {
        super(constant.sshManagerTitle(), resources.sshKeyManager());

        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.eventBus = eventBus;
        this.console = console;
        this.userService = userService;
        this.gitHubClientService = gitHubClientService;
        this.restContext = restContext;
        this.sshKeyPresenter = sshKeyPresenter;
        this.uploadSshKeyPresenter = uploadSshKeyPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClicked(@NotNull KeyItem key) {
        sshKeyPresenter.showDialog(key);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(@NotNull KeyItem key) {
        boolean needToDelete = Window.confirm("Do you want to delete ssh keys for <b>" + key.getHost() + "</b> host?");
        if (needToDelete) {
            service.deleteKey(key, new JsonpAsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    getLoader().hide();
                    refreshKeys();
                }

                @Override
                public void onFailure(Throwable exception) {
                    getLoader().hide();
                    console.print(exception.getMessage());
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onGenerateClicked() {
        String host = Window.prompt("Host name (w/o port): ", "");
        if (!host.isEmpty()) {
            try {
                service.generateKey(host, new AsyncRequestCallback<GenKeyRequest>() {
                    @Override
                    protected void onSuccess(GenKeyRequest result) {
                        refreshKeys();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        console.print(exception.getMessage());
                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    }
                });
            } catch (RequestException e) {
                console.print(e.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        uploadSshKeyPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onGenerateGithubKeyClicked() {
        service.getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {
            @Override
            public void onSuccess(JavaScriptObject result) {
                boolean githubKeyExists = false;
                loader = getLoader();
                JsonArray<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);

                for (int i = 0; i < keys.size(); i++) {
                    KeyItem key = keys.get(i);
                    if (key.getHost().contains("github.com")) {
                        githubKeyExists = true;
                    }
                }

                if (!githubKeyExists) {
                    loader.hide();
                    boolean needToCreate = Window.confirm(constant.githubSshKeyLabel());
                    if (needToCreate) {
                        loader.show();
                        try {
                            UserUnmarshaller unmarshaller = new UserUnmarshaller();

                            userService.getUser(new AsyncRequestCallback<User>(unmarshaller) {
                                @Override
                                protected void onSuccess(User result) {
                                    getToken(result.getUserId());
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    Log.error(SshKeyManagerPresenter.class, exception);
                                }
                            });
                        } catch (RequestException e) {
                            Log.error(SshKeyManagerPresenter.class, e);
                        }
                    }
                } else {
                    loader.hide();
                    getUserRepos();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                getLoader().hide();
                Window.alert("Getting ssh keys failed.");
            }
        });
    }

    /**
     * Return token for user.
     *
     * @param user
     *         user which need token
     */
    private void getToken(@NotNull final String user) {
        StringUnmarshaller unmarshaller = new StringUnmarshaller();

        try {
            gitHubClientService.getUserToken(user, new AsyncRequestCallback<String>(unmarshaller) {
                @Override
                protected void onSuccess(String result) {
                    if (result == null || result.isEmpty()) {
                        loader.hide();
                        oAuthLoginStart(user);
                    } else {
                        generateGitHubKey();
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    loader.hide();
                    oAuthLoginStart(user);
                }
            });
        } catch (RequestException e) {
            loader.hide();
        }
    }

    /** Log in  github */
    private void oAuthLoginStart(@NotNull String user) {
        boolean permitToRedirect = Window.confirm(constant.loginOAuthLabel());
        if (permitToRedirect) {
            String authUrl = "rest/ide/oauth/authenticate?oauth_provider=github"
                             + "&scope=user&userId=" + user + "&scope=repo&redirect_after_login=/ide/" + Utils.getWorkspaceName();
            JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
            authWindow.loginWithOAuth();
        }
    }

    /** Generate github key. */
    private void generateGitHubKey() {
        try {
            AsyncRequestCallback<Void> callback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    loader.hide();
                    refreshKeys();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    loader.hide();
                    getFailedKey();
                }
            };

            String url = restContext + '/' + Utils.getWorkspaceName() + "/github/ssh/generate";
            AsyncRequest.build(POST, url).loader(new EmptyLoader()).send(callback);
        } catch (RequestException e) {
            loader.hide();
            Window.alert("Upload key to github failed.");
        }
    }

    /** Need to remove failed uploaded keys from local storage if they can't be uploaded to github */
    private void getFailedKey() {
        service.getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {
            @Override
            public void onSuccess(JavaScriptObject result) {
                getLoader().hide();
                JsonArray<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);
                for (int i = 0; i < keys.size(); i++) {
                    KeyItem key = keys.get(i);
                    if (key.getHost().equals("github.com")) {
                        removeFailedKey(key);
                        return;
                    }
                }
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable exception) {
                getLoader().hide();
                refreshKeys();
                console.print(exception.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    /**
     * Remove failed key.
     *
     * @param key
     *         failed key
     */
    private void removeFailedKey(@NotNull KeyItem key) {
        service.deleteKey(key, new JsonpAsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                refreshKeys();
            }

            @Override
            public void onSuccess(Void result) {
                Window.alert("Failed to delete invalid ssh key.");
                refreshKeys();
            }
        });
    }

    /** Get the list of all authorized user's repositories. */
    private void getUserRepos() {
        try {
            AllRepositoriesUnmarshaller unmarshaller = new AllRepositoriesUnmarshaller();
            gitHubClientService.getAllRepositories(new AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonStringMap<JsonArray<GitHubRepository>> result) {
                    // do nothing
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doApply() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirty() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        refreshKeys();
        container.setWidget(view);
    }

    /** Refresh ssh keys. */
    private void refreshKeys() {
        service.getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {
            @Override
            public void onSuccess(JavaScriptObject result) {
                getLoader().hide();
                JsonArray<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);
                view.setKeys(keys);
            }

            @Override
            public void onFailure(Throwable exception) {
                getLoader().hide();
                console.print(exception.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onAuthenticated(@NotNull OAuthStatus authStatus) {
        if (LOGGED_IN.equals(authStatus)) {
            generateGitHubKey();
        }
    }
}