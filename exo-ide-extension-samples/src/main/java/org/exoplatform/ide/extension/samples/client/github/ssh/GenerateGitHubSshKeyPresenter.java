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
package org.exoplatform.ide.extension.samples.client.github.ssh;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.samples.client.oauth.GithubLoginEvent;
import org.exoplatform.ide.extension.samples.client.oauth.GithubLoginFinishedEvent;
import org.exoplatform.ide.extension.samples.client.oauth.GithubLoginFinishedHandler;
import org.exoplatform.ide.extension.ssh.client.JsonpAsyncCallback;
import org.exoplatform.ide.extension.ssh.client.SshKeyService;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GitHubKeyGeneratedEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysEvent;
import org.exoplatform.ide.extension.ssh.client.marshaller.SshKeysUnmarshaller;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.git.client.github.GitHubClientService;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class GenerateGitHubSshKeyPresenter implements UserInfoReceivedHandler, ViewClosedHandler, GenerateGitHubKeyHandler,
                                          GithubLoginFinishedHandler {
    private UserInfo userInfo;

    EmptyLoader      loader;


    interface Display extends IsView {
        HasClickHandlers getOkButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getLabel();
    }

    private Display display;

    public GenerateGitHubSshKeyPresenter() {
        IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
        IDE.addHandler(GenerateGitHubKeyEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(GithubLoginFinishedEvent.TYPE, this);
        loader = new EmptyLoader();
    }

    /** Open view. */
    private void openView() {
        if (display == null) {
            Display d = GWT.create(Display.class);
            display = d;
            bindDisplay();
            IDE.getInstance().openView(d.asView());
            return;
        }
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
                loader.show();
                getToken(userInfo.getName());
            }
        });
    }

    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        this.userInfo = event.getUserInfo();
    }

    @Override
    public void onGenerateGitHubSshKey(GenerateGitHubKeyEvent event) {
        SshKeyService.get().getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {
            @Override
            public void onSuccess(JavaScriptObject result) {
                try {
                    boolean githubKeyExists = false;
                    List<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);
                    for (KeyItem key : keys) {
                        if (key.getHost().contains("github.com")) {
                            githubKeyExists = true;
                        }
                    }
                    if (!githubKeyExists) {
                        getLoader().hide();
                        openView();
                    } else {
                        getLoader().hide();
                        IDE.fireEvent(new GitHubKeyGeneratedEvent());
                    }
                } catch (UnmarshallerException e) {
                    getLoader().hide();
                    Dialogs.getInstance().showError("Getting ssh keys failed.");
                }

            }

            @Override
            public void onFailure(Throwable caught) {
                getLoader().hide();
                Dialogs.getInstance().showError("Getting ssh keys failed.");
            }
        });
    }

    private void generateGitHubKey() {
        try {
            AsyncRequestCallback<Void> callback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    loader.hide();
                    IDE.fireEvent(new RefreshKeysEvent());
                    IDE.fireEvent(new GitHubKeyGeneratedEvent());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    loader.hide();
                    getFailedKey();
                }
            };

            String url = Utils.getRestContext() + Utils.getWorkspaceName() + "/github/ssh/generate";
            AsyncRequest.build(RequestBuilder.POST, url).loader(new EmptyLoader()).send(callback);
        } catch (RequestException e) {
            loader.hide();
            Dialogs.getInstance().showError("Upload key to github failed.");
        }
    }

    /**
     * Need to remove failed uploaded keys from local storage if they can't be uploaded to github
     */
    private void getFailedKey() {
        SshKeyService.get().getAllKeys(new JsonpAsyncCallback<JavaScriptObject>() {

            @Override
            public void onSuccess(JavaScriptObject result) {
                getLoader().hide();
                try {
                    List<KeyItem> keys = SshKeysUnmarshaller.unmarshal(result);
                    for (KeyItem key : keys) {
                        if (key.getHost().equals("github.com")) {
                            removeFailedKey(key);
                            return;
                        }
                    }
                    IDE.fireEvent(new RefreshKeysEvent());
                } catch (UnmarshallerException e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                getLoader().hide();
                IDE.fireEvent(new RefreshKeysEvent());
                IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    private void removeFailedKey(KeyItem key) {
        SshKeyService.get().deleteKey(key, new JsonpAsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                IDE.fireEvent(new RefreshKeysEvent());
            }

            @Override
            public void onSuccess(Void result) {
                Dialogs.getInstance().showError("Failed to delete invalid ssh key.");
                IDE.fireEvent(new RefreshKeysEvent());
            }
        });
    }

    private void getToken(String user) {
        try {
            GitHubClientService.getInstance()
                               .getUserToken(user,
                                             new AsyncRequestCallback<StringBuilder>(
                                                                                     new StringUnmarshaller(new StringBuilder())) {

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

    public void oAuthLoginStart() {
        IDE.fireEvent(new GithubLoginEvent());
    }

    public void onGithubLoginFinished(GithubLoginFinishedEvent event) {
        generateGitHubKey();
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
