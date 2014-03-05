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

import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.api.user.shared.dto.User;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ext.ssh.client.SshResources;
import com.codenvy.ide.ext.ssh.client.key.SshKeyPresenter;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyPresenter;
import com.codenvy.ide.ext.ssh.dto.GenKeyRequest;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The presenter for managing ssh keys.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 */
@Singleton
public class SshKeyManagerPresenter extends AbstractPreferencesPagePresenter implements SshKeyManagerView.ActionDelegate {
    public static final String GITHUB_HOST = "github.com";
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       SshKeyManagerView       view;
    private       SshKeyService           service;
    private       SshLocalizationConstant constant;
    private       EventBus                eventBus;
    private       UserServiceClient       userService;
    private       Loader                  loader;
    private       SshKeyPresenter         sshKeyPresenter;
    private       UploadSshKeyPresenter   uploadSshKeyPresenter;
    private       NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resources
     * @param constant
     * @param eventBus
     * @param userService
     * @param notificationManager
     */
    @Inject
    public SshKeyManagerPresenter(SshKeyManagerView view,
                                  SshKeyService service,
                                  SshResources resources,
                                  SshLocalizationConstant constant,
                                  EventBus eventBus,
                                  Loader loader,
                                  UserServiceClient userService,
                                  SshKeyPresenter sshKeyPresenter,
                                  UploadSshKeyPresenter uploadSshKeyPresenter,
                                  NotificationManager notificationManager,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(constant.sshManagerTitle(), resources.sshKeyManager());

        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.eventBus = eventBus;
        this.userService = userService;
        this.loader = loader;
        this.sshKeyPresenter = sshKeyPresenter;
        this.uploadSshKeyPresenter = uploadSshKeyPresenter;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClicked(@NotNull KeyItem key) {
        sshKeyPresenter.showDialog(key);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(@NotNull KeyItem key) {
        boolean needToDelete = Window.confirm(constant.deleteSshKeyQuestion(key.getHost()));
        if (needToDelete) {
            service.deleteKey(key, new AsyncRequestCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    loader.hide();
                    refreshKeys();
                }

                @Override
                public void onFailure(Throwable exception) {
                    loader.hide();
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onGenerateClicked() {
        String host = Window.prompt(constant.hostNameField(), "");
        if (!host.isEmpty()) {
            service.generateKey(host, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    refreshKeys();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
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
        service.getAllKeys(new AsyncRequestCallback<Array<KeyItem>>(dtoUnmarshallerFactory.newArrayUnmarshaller(KeyItem.class)) {
            @Override
            public void onSuccess(Array<KeyItem> result) {
                boolean githubKeyExists = false;
                for (int i = 0; i < result.size(); i++) {
                    KeyItem key = result.get(i);
                    if (key.getHost().contains("github.com")) {
                        githubKeyExists = true;
                        break;
                    }
                }

                if (!githubKeyExists) {
                    loader.hide();
                    boolean needToCreate = Window.confirm(constant.githubSshKeyLabel());
                    if (needToCreate) {
                        loader.show();
                        userService.getCurrentUser(new AsyncRequestCallback<User>(dtoUnmarshallerFactory.newUnmarshaller(User.class)) {
                            @Override
                            protected void onSuccess(User result) {
                                if (service.getSshKeyProviders().containsKey(GITHUB_HOST)) {
                                    service.getSshKeyProviders().get(GITHUB_HOST)
                                           .generateKey(result.getId(), new AsyncRequestCallback<Void>() {
                                               @Override
                                               public void onSuccess(Void result) {
                                                   loader.hide();
                                                   refreshKeys();
                                               }

                                               @Override
                                               public void onFailure(Throwable exception) {
                                                   loader.hide();
                                                   getFailedKey();
                                               }
                                           });
                                } else {
                                    Notification notification = new Notification(constant.sshKeysProviderNotFound(GITHUB_HOST), ERROR);
                                    notificationManager.showNotification(notification);
                                }
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                                Log.error(SshKeyManagerPresenter.class, exception);
                            }
                        });
                    }
                } else {
                    loader.hide();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                loader.hide();
                Notification notification = new Notification(constant.getSshKeyFailed(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** Need to remove failed uploaded keys from local storage if they can't be uploaded to github */
    private void getFailedKey() {
        service.getAllKeys(new AsyncRequestCallback<Array<KeyItem>>(dtoUnmarshallerFactory.newArrayUnmarshaller(KeyItem.class)) {
            @Override
            public void onSuccess(Array<KeyItem> result) {
                for (int i = 0; i < result.size(); i++) {
                    KeyItem key = result.get(i);
                    if (key.getHost().equals("github.com")) {
                        removeFailedKey(key);
                        return;
                    }
                }
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable exception) {
                loader.hide();
                refreshKeys();
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
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
        service.deleteKey(key, new AsyncRequestCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Notification notification = new Notification(constant.deleteSshKeyFailed(), ERROR);
                notificationManager.showNotification(notification);
                refreshKeys();
            }

            @Override
            public void onSuccess(Void result) {
                refreshKeys();
            }
        });
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
        service.getAllKeys(new AsyncRequestCallback<Array<KeyItem>>(dtoUnmarshallerFactory.newArrayUnmarshaller(KeyItem.class)) {
            @Override
            public void onSuccess(Array<KeyItem> result) {
                loader.hide();
                view.setKeys(result);
            }

            @Override
            public void onFailure(Throwable exception) {
                loader.hide();
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }
}