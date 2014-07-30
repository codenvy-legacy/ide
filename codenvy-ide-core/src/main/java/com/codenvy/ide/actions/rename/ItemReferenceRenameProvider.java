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
package com.codenvy.ide.actions.rename;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.gwt.client.QueryExpression;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Rename provider for renaming {@link ItemReference}.
 *
 * @author Artem Zatsarynnyy
 */
public class ItemReferenceRenameProvider implements RenameProvider<ItemReference> {
    private ProjectServiceClient     projectServiceClient;
    private NotificationManager      notificationManager;
    private EventBus                 eventBus;
    private EditorAgent              editorAgent;
    private CoreLocalizationConstant localizationConstant;
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;

    @Inject
    public ItemReferenceRenameProvider(ProjectServiceClient projectServiceClient, NotificationManager notificationManager,
                                       EventBus eventBus, EditorAgent editorAgent, CoreLocalizationConstant localizationConstant,
                                       DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.projectServiceClient = projectServiceClient;
        this.notificationManager = notificationManager;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.localizationConstant = localizationConstant;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void renameItem(final ItemReference item) {
        final String dialogTitle = "file".equals(item.getType()) ? localizationConstant.renameFileDialogTitle()
                                                                 : localizationConstant.renameFolderDialogTitle();
        new AskValueDialog(dialogTitle, localizationConstant.renameDialogNewNameLabel(), new AskValueCallback() {
            @Override
            public void onOk(final String value) {
                projectServiceClient.rename(item.getPath(), value, null, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent());
                        checkOpenedFiles(item, value);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Notification notification = new Notification(exception.getMessage(), Notification.Type.ERROR);
                        notificationManager.showNotification(notification);
                        Log.error(ItemReferenceRenameProvider.class, exception);
                    }
                });
            }
        }).show();
    }

    private void checkOpenedFiles(final ItemReference itemBeforeRenaming, String newName) {
        final String itemPathBeforeRenaming = itemBeforeRenaming.getPath();
        final String parentPathBeforeRenaming =
                itemPathBeforeRenaming.substring(0, itemPathBeforeRenaming.length() - itemBeforeRenaming.getName().length());
        final String itemPathAfterRenaming = parentPathBeforeRenaming + newName;

        QueryExpression query = null;
        if ("file".equals(itemBeforeRenaming.getType())) {
            query = new QueryExpression().setPath(parentPathBeforeRenaming).setName(newName);
        } else if ("folder".equals(itemBeforeRenaming.getType())) {
            query = new QueryExpression().setPath(itemPathAfterRenaming);
        }

        if (query != null) {
            Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
            projectServiceClient.search(query, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ItemReference> result) {
                    if ("file".equals(itemBeforeRenaming.getType())) {
                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                            if (itemPathBeforeRenaming.equals(editor.getEditorInput().getFile().getPath())) {
                                // result array should contain one item only
                                ItemReference renamedItem = result.get(0);
                                replaceFileInEditor(editor, renamedItem);
                                break;
                            }
                        }
                    } else if ("folder".equals(itemBeforeRenaming.getType())) {
                        StringMap<ItemReference> children = Collections.createStringMap();
                        for (ItemReference itemReference : result.asIterable()) {
                            children.put(itemReference.getPath(), itemReference);
                        }

                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                            ItemReference openedFile = editor.getEditorInput().getFile();
                            if (openedFile.getPath().startsWith(itemPathBeforeRenaming)) {
                                String childFileNewPath = openedFile.getPath().replaceFirst(itemPathBeforeRenaming, itemPathAfterRenaming);
                                ItemReference renamedItem = children.get(childFileNewPath);
                                if (renamedItem != null) {
                                    replaceFileInEditor(editor, renamedItem);
                                }
                            }
                        }
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(ItemReferenceRenameProvider.class, exception);
                }
            });
        }
    }

    private void replaceFileInEditor(EditorPartPresenter editor, ItemReference renamedItem) {
        editorAgent.getOpenedEditors().remove(editor.getEditorInput().getFile().getPath());
        editorAgent.getOpenedEditors().put(renamedItem.getPath(), editor);
        editor.getEditorInput().setFile(renamedItem);
        editor.onFileChanged();
    }

    /** {@inheritDoc} */
    @Override
    public boolean canRename(Object item) {
        return item instanceof ItemReference;
    }
}
