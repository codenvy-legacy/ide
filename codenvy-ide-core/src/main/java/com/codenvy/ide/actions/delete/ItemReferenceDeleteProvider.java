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
package com.codenvy.ide.actions.delete;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Delete provider for deleting {@link ItemReference}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ItemReferenceDeleteProvider implements DeleteProvider<ItemReference> {
    private CoreLocalizationConstant localizationConstant;
    private ProjectServiceClient     projectServiceClient;
    private EventBus                 eventBus;
    private NotificationManager      notificationManager;
    private EditorAgent              editorAgent;

    @Inject
    public ItemReferenceDeleteProvider(CoreLocalizationConstant localizationConstant, ProjectServiceClient projectServiceClient,
                                       EventBus eventBus, NotificationManager notificationManager, EditorAgent editorAgent) {
        this.localizationConstant = localizationConstant;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.notificationManager = notificationManager;
        this.editorAgent = editorAgent;
    }

    /** {@inheritDoc} */
    @Override
    public void deleteItem(final ItemReference item) {
        final String dialogTitle = "file".equals(item.getType()) ? localizationConstant.deleteFileDialogTitle()
                                                                 : localizationConstant.deleteFolderDialogTitle();
        final String dialogQuestion = "file".equals(item.getType()) ? localizationConstant.deleteFileDialogQuestion(item.getName())
                                                                    : localizationConstant.deleteFolderDialogQuestion(item.getName());

        new Ask(dialogTitle, dialogQuestion, new AskHandler() {
            @Override
            public void onOk() {
                projectServiceClient.delete(item.getPath(), new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent());
                        if ("file".equals(item.getType())) {
                            eventBus.fireEvent(new FileEvent(item, FileEvent.FileOperation.CLOSE));
                        } else if ("folder".equals(item.getType())) {
                            // close all opened child files since its has been deleted
                            for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                                if (editor.getEditorInput().getFile().getPath().startsWith(item.getPath())) {
                                    eventBus.fireEvent(new FileEvent(editor.getEditorInput().getFile(), FileEvent.FileOperation.CLOSE));
                                }
                            }
                        }
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
                    }
                });
            }
        }).show();
    }

    /** {@inheritDoc} */
    @Override
    public boolean canDelete(Object item) {
        return item instanceof ItemReference;
    }

}
