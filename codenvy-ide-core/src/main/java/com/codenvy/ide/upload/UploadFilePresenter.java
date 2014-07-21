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

import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
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

    private UploadFileView   view;
    private SelectionAgent   selectionAgent;
    private ResourceProvider resourceProvider;
    private String           restContext;
    private String           workspaceId;
    private EventBus         eventBus;

    @Inject
    public UploadFilePresenter(UploadFileView view,
                               @Named("restContext") String restContext,
                               @Named("workspaceId") String workspaceId,
                               SelectionAgent selectionAgent,
                               ResourceProvider resourceProvider,
                               EventBus eventBus) {

        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.selectionAgent = selectionAgent;
        this.resourceProvider = resourceProvider;
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
        Folder folder = getParent();
        resourceProvider.getActiveProject().refreshChildren(folder, new AsyncCallback<Folder>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error(UploadFilePresenter.class, caught);
            }

            @Override
            public void onSuccess(Folder result) {
                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
            }
        });

    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        view.setEncoding(FormPanel.ENCODING_MULTIPART);

        //TODO Temporarily used vfs service. Should be changed to api service.
        view.setAction(restContext + "/project/" + workspaceId + "/uploadFile" + getParent().getPath());
        view.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onFileNameChanged() {
        String fileName = view.getFileName();
        boolean enabled = !fileName.isEmpty();
        view.setEnabledUploadButton(enabled);
    }

    /**
     * Gets the selected resource or
     * the parent folder if the file has been allocated.
     *
     * @return the selected resource or
     * the parent folder if the file has been allocated
     */
    private Folder getParent() {
        Selection<?> select = selectionAgent.getSelection();
        Folder parent = null;

        if (select != null && select.getFirstElement() instanceof Resource) {
            Selection<Resource> selection = (Selection<Resource>)select;
            Resource resource = selection.getFirstElement();

            if (resource.isFile()) {
                parent = resource.getParent();
            } else parent = (Folder)resource;
        }
        return parent;
    }
}
