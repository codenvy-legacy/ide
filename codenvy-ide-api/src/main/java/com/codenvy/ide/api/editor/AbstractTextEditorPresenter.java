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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Abstract implementation of TextEditorPresenter
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public abstract class AbstractTextEditorPresenter extends AbstractEditorPresenter implements CodenvyTextEditor {
    protected TextEditorConfiguration configuration;
    protected DocumentProvider        documentProvider;
    protected Document                document;
    protected NotificationManager     notificationManager;

    /** {@inheritDoc} */
    @Override
    public void initialize(@NotNull TextEditorConfiguration configuration, @NotNull DocumentProvider documentProvider,
                           @NotNull NotificationManager notificationManager) {
        this.configuration = configuration;
        this.documentProvider = documentProvider;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public DocumentProvider getDocumentProvider() {
        return documentProvider;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return input.getImageResource();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        if (isDirty()) {
            return "*" + input.getName();
        } else {
            return input.getName();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void doSave() {
        documentProvider.saveDocument(getEditorInput(), document, false, new AsyncCallback<EditorInput>() {
            @Override
            public void onSuccess(EditorInput result) {
                updateDirtyState(false);
                afterSave();
            }

            @Override
            public void onFailure(Throwable caught) {
                Notification notification = new Notification(caught.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /**
     * Override this method for handling after save actions
     */
    protected void afterSave() {
        //default nothing to do
    }

    /** {@inheritDoc} */
    @Override
    public void doSaveAs() {
        // TODO not implemented. need to add save as dialog and etc
    }
}
