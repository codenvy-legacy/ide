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
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.ext.ssh.dto.PublicKey;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestLoader;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The presenter for showing ssh key.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class SshKeyPresenter implements SshKeyView.ActionDelegate {
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private       SshKeyView             view;
    private       SshKeyService          service;
    private       EventBus               eventBus;
    private       NotificationManager    notificationManager;
    private       AsyncRequestLoader     loader;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param notificationManager
     */
    @Inject
    public SshKeyPresenter(SshKeyView view,
                           SshKeyService service,
                           EventBus eventBus,
                           AsyncRequestLoader loader,
                           NotificationManager notificationManager,
                           DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.eventBus = eventBus;
        this.loader = loader;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog(@NotNull KeyItem keyItem) {
        view.addHostToTitle(keyItem.getHost());

        service.getPublicKey(keyItem, new AsyncRequestCallback<PublicKey>(dtoUnmarshallerFactory.newUnmarshaller(PublicKey.class)) {
            @Override
            public void onSuccess(PublicKey result) {
                loader.hide();
                view.setKey(result.getKey());
                view.showDialog();
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

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}