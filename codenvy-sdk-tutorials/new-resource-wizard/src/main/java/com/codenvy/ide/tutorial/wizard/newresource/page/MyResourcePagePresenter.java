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
package com.codenvy.ide.tutorial.wizard.newresource.page;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.editor.*;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.edits.MultiTextEdit;
import com.codenvy.ide.text.edits.ReplaceEdit;
import com.codenvy.ide.tutorial.wizard.newresource.provider.MyResourceProvider;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import static com.codenvy.ide.api.editor.EditorPartPresenter.PROP_INPUT;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.NEW_RESOURCE_PROVIDER;
import static com.codenvy.ide.tutorial.wizard.newresource.provider.MyResourceProvider.LOGIN_PLACE;
import static com.codenvy.ide.tutorial.wizard.newresource.provider.MyResourceProvider.PASSWORD_PLACE;

/**
 * Page for my new resource. This page provides replace 2 fields (login and password) in new file.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class MyResourcePagePresenter extends AbstractWizardPage implements MyResourcePageView.ActionDelegate {
    private MyResourcePageView  view;
    private EditorAgent         editorAgent;
    private NotificationManager notificationManager;

    @Inject
    public MyResourcePagePresenter(MyResourcePageView view, EditorAgent editorAgent, NotificationManager notificationManager) {
        super("My resource wizard page", null);
        this.view = view;
        this.view.setDelegate(this);
        this.editorAgent = editorAgent;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getLogin().isEmpty()) {
            return "Login field can't be empty";
        } else if (view.getPassword().isEmpty()) {
            return "Password field can't be empty";
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return !view.getLogin().isEmpty() && !view.getPassword().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
        NewResourceProvider resourceProvider = wizardContext.getData(NEW_RESOURCE_PROVIDER);
        return resourceProvider != null && resourceProvider instanceof MyResourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull final CommitCallback callback) {
        final EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
        if (activeEditor instanceof TextEditorPartPresenter) {
            activeEditor.addPropertyListener(new PropertyListener() {
                @Override
                public void propertyChanged(PartPresenter source, int propId) {
                    if (propId == PROP_INPUT) {
                        final DocumentProvider documentProvider = ((TextEditorPartPresenter)activeEditor).getDocumentProvider();
                        final EditorInput editorInput = activeEditor.getEditorInput();
                        documentProvider.getDocument(editorInput, new DocumentProvider.DocumentCallback() {
                            @Override
                            public void onDocument(Document document) {
                                MultiTextEdit multiTextEdit = new MultiTextEdit();

                                int indexLogin = document.get().indexOf(LOGIN_PLACE);
                                multiTextEdit.addChild(new ReplaceEdit(indexLogin, LOGIN_PLACE.length(), view.getLogin()));

                                int indexPassword = document.get().indexOf(PASSWORD_PLACE);
                                multiTextEdit.addChild(new ReplaceEdit(indexPassword, PASSWORD_PLACE.length(), view.getPassword()));

                                try {
                                    multiTextEdit.apply(document);
                                } catch (BadLocationException e) {
                                    notificationManager.showNotification(new Notification(e.getMessage(), ERROR));
                                }

                                activeEditor.doSave();
                                callback.onSuccess();
                            }
                        });
                    }
                }
            });
        } else {
            callback.onSuccess();
        }
    }
}