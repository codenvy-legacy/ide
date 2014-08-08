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
package com.codenvy.ide.upload;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

/**
 * The purpose of this class is upload file
 *
 * @author Roman Nikitenko.
 */
public class UploadFilePresenter implements UploadFileView.ActionDelegate {

    private UploadFileView view;
    private SelectionAgent selectionAgent;
    private AppContext     appContext;
    private String         restContext;
    private String         workspaceId;
    private EventBus       eventBus;

    @Inject
    public UploadFilePresenter(UploadFileView view,
                               @Named("restContext") String restContext,
                               @Named("workspaceId") String workspaceId,
                               SelectionAgent selectionAgent,
                               AppContext appContext,
                               EventBus eventBus) {

        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.selectionAgent = selectionAgent;
        this.appContext = appContext;
        this.eventBus = eventBus;
        this.view = view;
        this.view.setDelegate(this);
        this.view.setEnabledUploadButton(false);
    }

    /** Show dialog. */
    public void showDialog() {
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onSubmitComplete(@NotNull String result) {
        view.close();
        eventBus.fireEvent(new RefreshProjectTreeEvent());
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        view.setAction(restContext + "/project/" + workspaceId + "/uploadFile" + getParentPath());
        view.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onFileNameChanged() {
        String fileName = view.getFileName();
        boolean enabled = !fileName.isEmpty();
        view.setEnabledUploadButton(enabled);
    }

    private String getParentPath() {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selection.getFirstElement() instanceof ItemReference) {
                ItemReference item = (ItemReference)selection.getFirstElement();
                final String path = item.getPath();
                if ("file".equals(item.getType())) {
                    return path.substring(0, path.length() - item.getName().length());
                } else if ("folder".equals(item.getType())) {
                    return item.getPath();
                }
            }
        }
        return appContext.getCurrentProject().getProjectDescription().getPath();
    }
}
