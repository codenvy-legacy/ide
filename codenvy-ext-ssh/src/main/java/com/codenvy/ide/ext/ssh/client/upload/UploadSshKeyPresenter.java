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
package com.codenvy.ide.ext.ssh.client.upload;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Main appointment of this class is upload private SSH key to the server.
 *
 * @author Evgen Vidolob
 */
@Singleton
public class UploadSshKeyPresenter implements UploadSshKeyView.ActionDelegate {
    private UploadSshKeyView        view;
    private String                  workspaceId;
    private SshLocalizationConstant constant;
    private String                  restContext;
    private EventBus                eventBus;
    private ConsolePart             console;
    private NotificationManager     notificationManager;
    private AsyncCallback<Void>     callback;

    @Inject
    public UploadSshKeyPresenter(UploadSshKeyView view,
                                 SshLocalizationConstant constant,
                                 @Named("restContext") String restContext,
                                 @Named("workspaceId") String workspaceId,
                                 EventBus eventBus,
                                 ConsolePart console,
                                 NotificationManager notificationManager) {
        this.view = view;
        this.workspaceId = workspaceId;
        this.view.setDelegate(this);
        this.constant = constant;
        this.restContext = restContext;
        this.console = console;
        this.eventBus = eventBus;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog(@Nonnull AsyncCallback<Void> callback) {
        this.callback = callback;
        view.setMessage("");
        view.setHost("");
        view.setEnabledUploadButton(false);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        String host = view.getHost();
        if (host.isEmpty()) {
            view.setMessage(constant.hostValidationError());
            return;
        }
        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        view.setAction(restContext + "/ssh-keys/" + workspaceId + "/add?host=" + host);
        view.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onSubmitComplete(@Nonnull String result) {
        if (result.isEmpty()) {
            UploadSshKeyPresenter.this.view.close();
            callback.onSuccess(null);
        } else {
            if (result.startsWith("<pre>") && result.endsWith("</pre>")) {
                result = result.substring(5, (result.length() - 6));
            }
            console.print(result);
            Notification notification = new Notification(result, ERROR);
            notificationManager.showNotification(notification);
            callback.onFailure(new Throwable(result));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFileNameChanged() {
        String fileName = view.getFileName();
        view.setEnabledUploadButton(!fileName.isEmpty());
    }
}