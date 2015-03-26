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
package org.eclipse.che.ide.upload.folder;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.FileContentUpdateEvent;
import org.eclipse.che.ide.api.event.RefreshProjectTreeEvent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;

/**
 * The purpose of this class is upload file
 *
 * @author Roman Nikitenko.
 */
public class UploadFolderFromZipPresenter implements UploadFolderFromZipView.ActionDelegate {

    private UploadFolderFromZipView view;
    private EditorAgent             editorAgent;
    private SelectionAgent          selectionAgent;
    private String                  restContext;
    private String                  workspaceId;
    private EventBus                eventBus;
    private NotificationManager     notificationManager;

    @Inject
    public UploadFolderFromZipPresenter(UploadFolderFromZipView view,
                                        @Named("restContext") String restContext,
                                        @Named("workspaceId") String workspaceId,
                                        SelectionAgent selectionAgent,
                                        EditorAgent editorAgent,
                                        EventBus eventBus,
                                        NotificationManager notificationManager) {
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.editorAgent = editorAgent;
        this.selectionAgent = selectionAgent;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setDelegate(this);
        this.view.setEnabledUploadButton(false);
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.closeDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onSubmitComplete(String result) {
        view.setLoaderVisibility(false);
        eventBus.fireEvent(new RefreshProjectTreeEvent(getParent()));

        if (result != null && !result.isEmpty()) {
            view.closeDialog();
            notificationManager.showError(parseMessage(result));
            return;
        }

        if (view.isOverwriteFileSelected()) {
            updateOpenedEditors();
        }
        view.closeDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        view.setLoaderVisibility(true);
        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        view.setAction(restContext + "/project/" + workspaceId + "/upload/zipfolder/" + getParent().getPath());
        view.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onFileNameChanged() {
        String fileName = view.getFileName();
        boolean enabled = !fileName.isEmpty() && fileName.contains(".zip");
        view.setEnabledUploadButton(enabled);
    }

    private StorableNode getParent() {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selection.getFirstElement() instanceof StorableNode) {
                final StorableNode selectedNode = (StorableNode)selection.getFirstElement();
                if (selectedNode instanceof FileNode) {
                    return (StorableNode)selectedNode.getParent();
                } else {
                    return selectedNode;
                }
            }
        }
        return null;
    }

    private String parseMessage(String message) {
        int startIndex = 0;
        int endIndex = -1;

        if (message.contains("<pre>message:")) {
            startIndex = message.indexOf("<pre>message:") + "<pre>message:".length();
        } else if (message.contains("<pre>")) {
            startIndex = message.indexOf("<pre>") + "<pre>".length();
        }

        if (message.contains("</pre>")) {
            endIndex = message.indexOf("</pre>");
        }
        return (endIndex != -1) ? message.substring(startIndex, endIndex) : message.substring(startIndex);
    }

    private void updateOpenedEditors() {
        for (EditorPartPresenter partPresenter : editorAgent.getOpenedEditors().getValues().asIterable()) {
            String filePath = partPresenter.getEditorInput().getFile().getPath();
            if (filePath.contains(getParent().getPath())) {
                eventBus.fireEvent(new FileContentUpdateEvent(filePath));
            }
        }
    }
}
