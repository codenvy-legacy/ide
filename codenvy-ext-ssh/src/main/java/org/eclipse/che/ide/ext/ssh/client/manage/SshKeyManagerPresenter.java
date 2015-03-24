/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.ssh.client.manage;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentUser;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.preferences.AbstractPreferencePagePresenter;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.commons.exception.ExceptionThrownEvent;
import org.eclipse.che.ide.ext.ssh.client.SshKeyService;
import org.eclipse.che.ide.ext.ssh.client.SshLocalizationConstant;
import org.eclipse.che.ide.ext.ssh.client.SshResources;
import org.eclipse.che.ide.ext.ssh.client.upload.UploadSshKeyPresenter;
import org.eclipse.che.ide.ext.ssh.dto.KeyItem;
import org.eclipse.che.ide.ext.ssh.dto.PublicKey;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * The presenter for managing ssh keys.
 *
 * @author Evgen Vidolob
 */
@Singleton
public class SshKeyManagerPresenter extends AbstractPreferencePagePresenter implements SshKeyManagerView.ActionDelegate {
    public static final String GITHUB_HOST = "github.com";

    private AppContext              appContext;
    private DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private DialogFactory           dialogFactory;
    private SshKeyManagerView       view;
    private SshKeyService           service;
    private SshLocalizationConstant constant;
    private EventBus                eventBus;
    private AsyncRequestLoader      loader;
    private UploadSshKeyPresenter   uploadSshKeyPresenter;
    private NotificationManager     notificationManager;

    /** Create presenter. */
    @Inject
    public SshKeyManagerPresenter(SshKeyManagerView view,
                                  SshKeyService service,
                                  SshResources resources,
                                  AppContext appContext,
                                  SshLocalizationConstant constant,
                                  EventBus eventBus,
                                  AsyncRequestLoader loader,
                                  UploadSshKeyPresenter uploadSshKeyPresenter,
                                  NotificationManager notificationManager,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  DialogFactory dialogFactory) {
        super(constant.sshManagerTitle(), constant.sshManagerCategory(), resources.sshKeyManager());

        this.view = view;
        this.appContext = appContext;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.eventBus = eventBus;
        this.loader = loader;
        this.uploadSshKeyPresenter = uploadSshKeyPresenter;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClicked(@Nonnull final KeyItem key) {
        service.getPublicKey(key, new AsyncRequestCallback<PublicKey>(dtoUnmarshallerFactory.newUnmarshaller(PublicKey.class)) {
            @Override
            public void onSuccess(PublicKey result) {
                loader.hide(constant.loaderGetPublicSshKeyMessage(key.getHost()));
                dialogFactory.createMessageDialog(constant.publicSshKeyField() + key.getHost(), result.getKey(), null).show();
            }

            @Override
            public void onFailure(Throwable exception) {
                loader.hide(constant.loaderGetPublicSshKeyMessage(key.getHost()));
                notificationManager.showError(SafeHtmlUtils.fromString(exception.getMessage()).asString());
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(@Nonnull final KeyItem key) {
        dialogFactory.createConfirmDialog(constant.deleteSshKeyTitle(),
                                          constant.deleteSshKeyQuestion(key.getHost()).asString(),
                                          getConfirmCallbackForDelete(key),
                                          getCancelCallback()).show();
    }

    private ConfirmCallback getConfirmCallbackForDelete(@Nonnull final KeyItem key) {
        return new ConfirmCallback() {
            @Override
            public void accepted() {
                deleteKey(key);
            }
        };
    }

    private CancelCallback getCancelCallback() {
        return new CancelCallback() {
            @Override
            public void cancelled() {
                //for now do nothing but it need for tests
            }
        };
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
                notificationManager.showError(SafeHtmlUtils.fromString(exception.getMessage()).asString());
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
                    notificationManager.showError(SafeHtmlUtils.fromString(exception.getMessage()).asString());
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
        CurrentUser user = appContext.getCurrentUser();
        if (user != null && service.getSshKeyProviders().containsKey(GITHUB_HOST)) {
            generateGithubKey(user);
        } else {
            notificationManager.showError(constant.sshKeysProviderNotFound(GITHUB_HOST));
        }
    }

    private void generateGithubKey(CurrentUser user) {
        service.getSshKeyProviders().get(GITHUB_HOST).generateKey(user.getProfile().getId(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                refreshKeys();
            }

            @Override
            public void onFailure(Throwable exception) {
                getFailedKey(GITHUB_HOST);
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
                notificationManager.showError(exception.getMessage());
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
    private void removeFailedKey(@Nonnull final KeyItem key) {
        service.deleteKey(key, new AsyncRequestCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                loader.hide(constant.loaderDeleteSshKeyMessage(key.getHost()));
                notificationManager.showError(constant.deleteSshKeyFailed());
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
                notificationManager.showError(exception.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    @Override
    public void storeChanges() {

    }

    @Override
    public void revertChanges() {

    }
}