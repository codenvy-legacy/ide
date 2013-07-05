/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.ui.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.github.GitHubClientService;
import com.codenvy.ide.ext.git.client.marshaller.AllRepositoriesUnmarshaller;
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.ext.ssh.client.JsonpAsyncCallback;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ext.ssh.client.SshResources;
import com.codenvy.ide.ext.ssh.client.marshaller.SshKeysUnmarshaller;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.marshal.StringUnmarshaller;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The presenter for managing ssh keys.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeyManagerPresenter May 18, 2011 10:16:44 AM evgen $
 */
@Singleton
public class SshKeyManagerPresenter extends AbstractPreferencesPagePresenter implements SshKeyManagerView.ActionDelegate {
    private SshKeyManagerView       view;
    private SshKeyService           service;
    private SshLocalizationConstant constant;
    private EventBus                eventBus;
    private ConsolePart             console;
    private UserClientService       userService;
    private GitHubClientService     gitHubClientService;
    private Loader                  loader;
    private String                  restContext;

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
                                  GitHubClientService gitHubClientService, @Named("restContext") String restContext) {
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
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClicked(KeyItem key) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(KeyItem key) {
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onGenerateGithubKeyClicked() {
        // TODO don't have OAuth login
//        service.getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {
//            @Override
//            public void onSuccess(JavaScriptObject result) {
//                boolean githubKeyExists = false;
//                loader = getLoader();
//                JsonArray<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);
//
//                for (int i = 0; i < keys.size(); i++) {
//                    KeyItem key = keys.get(i);
//                    if (key.getHost().contains("github.com")) {
//                        githubKeyExists = true;
//                    }
//                }
//
//                if (!githubKeyExists) {
//                    loader.hide();
//                    boolean needToCreate = Window.confirm(
//                            "Would you like to upload a public SSH key to your GitHub account to establish a secure connection?");
//                    if (needToCreate) {
//                        loader.show();
//                        try {
//                            com.codenvy.ide.client.DtoClientImpls.UserImpl user = com.codenvy.ide.client.DtoClientImpls.UserImpl.make();
//                            UserUnmarshaller unmarshaller = new UserUnmarshaller(user);
//
//                            userService.getUser(new AsyncRequestCallback<User>(unmarshaller) {
//                                @Override
//                                protected void onSuccess(User result) {
//                                    getToken(result.getUserId());
//                                }
//
//                                @Override
//                                protected void onFailure(Throwable exception) {
//                                    Log.error(SshKeyManagerPresenter.class, exception);
//                                }
//                            });
//                        } catch (RequestException e) {
//                            Log.error(SshKeyManagerPresenter.class, e);
//                        }
//                    }
//                } else {
//                    loader.hide();
//                    getUserRepos();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable caught) {
//                getLoader().hide();
//                Window.alert("Getting ssh keys failed.");
//            }
//        });
    }

    /**
     * Return token for user.
     *
     * @param user
     *         user which need token
     */
    private void getToken(String user) {
        StringUnmarshaller unmarshaller = new StringUnmarshaller();

        try {
            gitHubClientService.getUserToken(user, new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                @Override
                protected void onSuccess(StringBuilder result) {
                    if (result == null || result.toString().isEmpty()) {
                        loader.hide();
                        oAuthLoginStart();
                    } else {
                        generateGitHubKey();
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    loader.hide();
                    oAuthLoginStart();
                }
            });
        } catch (RequestException e) {
            loader.hide();
        }
    }

    /** Log in  github */
    private void oAuthLoginStart() {
        // TODO need to add support OAuth login
        // IDE.fireEvent(new GithubLoginEvent());
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

            String url = restContext + "/ide" + "/github/ssh/generate";
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
    private void removeFailedKey(KeyItem key) {
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
}