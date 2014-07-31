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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.UndoableEditor;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Abstract implementation of TextEditorPresenter
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public abstract class AbstractTextEditorPresenter extends AbstractEditorPresenter implements CodenvyTextEditor, UndoableEditor {
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

    @Override
    public TextEditorConfiguration getConfiguration() {
        return configuration;
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
    public SVGResource getTitleSVGImage() {
        return input.getSVGResource();
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

    /** {@inheritDoc} */
    @Override
    public void doSave(final AsyncCallback<EditorInput> callback) {
        documentProvider.saveDocument(getEditorInput(), document, false, new AsyncCallback<EditorInput>() {
            @Override
            public void onFailure(Throwable caught) {
                Notification notification = new Notification(caught.getMessage(), ERROR);
                notificationManager.showNotification(notification);
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(EditorInput result) {
                updateDirtyState(false);
                callback.onSuccess(result);
                afterSave();
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
