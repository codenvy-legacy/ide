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
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.api.user.shared.dto.UserDescriptor;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.ext.ssh.client.SshResources;
import com.codenvy.ide.ext.ssh.client.key.SshKeyPresenter;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyPresenter;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestLoader;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
    private       AsyncRequestLoader      loader;
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
                                  AsyncRequestLoader loader,
                                  UserServiceClient userService,
                                  SshKeyPresenter sshKeyPresenter,
                                  UploadSshKeyPresenter uploadSshKeyPresenter,
                                  NotificationManager notificationManager,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(constant.sshManagerTitle(), constant.sshManagerCategory(), resources.sshKeyManager());

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
    public void onDeleteClicked(@NotNull final KeyItem key) {
        Ask ask = new Ask(constant.deleteSshKeyTitle(), constant.deleteSshKeyQuestion(key.getHost()), new AskHandler() {
            @Override
            public void onOk() {
                deleteKey(key);
            }
        });
        ask.show();
    }

    private void deleteKey(final KeyItem key) {
        service.deleteKey(key, new AsyncRequestCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loader.hide(constant.loaderDeleteSshKeyMessage(key.getHost()));
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable exception) {
                loader.hide(constant.loaderDeleteSshKeyMessage(key.getHost()));
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
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
        uploadSshKeyPresenter.showDialog(new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onGenerateGithubKeyClicked() {
        loader.show();
        userService.getCurrentUser(new AsyncRequestCallback<UserDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(UserDescriptor.class)) {
            @Override
            protected void onSuccess(UserDescriptor result) {
                loader.hide();
                if (service.getSshKeyProviders().containsKey(GITHUB_HOST)) {
                    service.getSshKeyProviders().get(GITHUB_HOST)
                           .generateKey(result.getId(), new AsyncCallback<Void>() {
                               @Override
                               public void onSuccess(Void result) {
                                   refreshKeys();
                               }

                               @Override
                               public void onFailure(Throwable exception) {
                                   getFailedKey(GITHUB_HOST);
                               }
                           });
                } else {
                    Notification notification = new Notification(constant.sshKeysProviderNotFound(GITHUB_HOST), ERROR);
                    notificationManager.showNotification(notification);
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                loader.hide();
                Log.error(SshKeyManagerPresenter.class, exception);
            }
        });
    }

    /** Need to remove failed uploaded keys from local storage if they can't be uploaded to github */
    private void getFailedKey(final String host) {
        service.getAllKeys(new AsyncRequestCallback<Array<KeyItem>>(dtoUnmarshallerFactory.newArrayUnmarshaller(KeyItem.class)) {
            @Override
            public void onSuccess(Array<KeyItem> result) {
                loader.hide(constant.loaderGetSshKeysMessage());
                for (int i = 0; i < result.size(); i++) {
                    KeyItem key = result.get(i);
                    if (key.getHost().equals(host)) {
                        removeFailedKey(key);
                        return;
                    }
                }
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable exception) {
                loader.hide(constant.loaderGetSshKeysMessage());
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
    private void removeFailedKey(@NotNull final KeyItem key) {
        service.deleteKey(key, new AsyncRequestCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                loader.hide(constant.loaderDeleteSshKeyMessage(key.getHost()));
                Notification notification = new Notification(constant.deleteSshKeyFailed(), ERROR);
                notificationManager.showNotification(notification);
                refreshKeys();
            }

            @Override
            public void onSuccess(Void result) {
                loader.hide(constant.loaderDeleteSshKeyMessage(key.getHost()));
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
                loader.hide(constant.loaderGetSshKeysMessage());
                view.setKeys(result);
            }

            @Override
            public void onFailure(Throwable exception) {
                loader.hide(constant.loaderGetSshKeysMessage());
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }
}