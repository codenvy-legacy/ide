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
package com.codenvy.ide.wizard.newresource.page;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.*;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.NewResourceAgentImpl;
import com.codenvy.ide.wizard.newresource.page.NewResourcePageView.ActionDelegate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Provides selecting kind of file which user wish to create and create resource of the chosen type with given name.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewResourcePagePresenter extends AbstractWizardPage implements ActionDelegate {
    private NewResourcePageView      view;
    private EditorAgent              editorAgent;
    private CoreLocalizationConstant constant;
    private NewResourceProvider      selectedResourceType;
    private boolean                  isResourceNameValid;
    private boolean                  hasSameResource;
    private Project                  project;
    private Folder                   parent;

    /**
     * Create presenter.
     *
     * @param resources
     * @param view
     */
    @Inject
    public NewResourcePagePresenter(Resources resources,
                                    CoreLocalizationConstant constant,
                                    NewResourcePageView view,
                                    NewResourceAgentImpl newResourceAgent,
                                    com.codenvy.ide.api.resources.ResourceProvider resourceProvider,
                                    SelectionAgent selectionAgent,
                                    EditorAgent editorAgent) {
        super("Create a new resource", resources.newResourceIcon());

        this.view = view;
        this.view.setDelegate(this);
        this.view.setResourceName("");
        this.editorAgent = editorAgent;
        this.constant = constant;

        JsonArray<NewResourceProvider> newResources = newResourceAgent.getResources();
        if (!newResources.isEmpty()) {
            JsonArray<NewResourceProvider> availableResources = JsonCollections.createArray();
            for (NewResourceProvider resourceData : newResources.asIterable()) {
                if (resourceData.inContext()) {
                    availableResources.add(resourceData);
                }
            }

            this.view.setResourceWizard(availableResources);
            selectedResourceType = availableResources.get(0);
            this.view.selectResourceType(selectedResourceType);
        }

        project = resourceProvider.getActiveProject();

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            if (selectionAgent.getSelection().getFirstElement() instanceof Resource) {
                Resource resource = (Resource)selectionAgent.getSelection().getFirstElement();
                if (resource.isFile()) {
                    parent = resource.getParent();
                } else {
                    parent = (Folder)resource;
                }
            }
        } else {
            parent = project;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return selectedResourceType != null && isResourceNameValid && !hasSameResource && !view.getResourceName().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        view.focusResourceName();
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getResourceName().isEmpty()) {
            return constant.enteringResourceName();
        } else if (!isResourceNameValid) {
            return constant.noIncorrectResourceName();
        } else if (selectedResourceType == null) {
            return constant.chooseResourceType();
        } else if (hasSameResource) {
            return constant.resourceExists(view.getResourceName());
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceTypeSelected(@NotNull NewResourceProvider resourceType) {
        selectedResourceType = resourceType;
        view.selectResourceType(resourceType);
        onResourceNameChanged();
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceNameChanged() {
        String resourceName = view.getResourceName();
        isResourceNameValid = ResourceNameValidator.isFolderNameValid(resourceName);

        if (selectedResourceType != null) {
            String extension = selectedResourceType.getExtension();
            if (extension == null) {
                extension = "";
            } else {
                extension = '.' + extension;
            }
            resourceName = resourceName + extension;

            hasSameResource = false;
            JsonArray<Resource> children = parent.getChildren();
            for (int i = 0; i < children.size() && !hasSameResource; i++) {
                Resource child = children.get(i);
                hasSameResource = child.getName().equals(resourceName);
            }
        }

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull CommitCallback callback) {
        selectedResourceType.create(view.getResourceName(), parent, project, new AsyncCallback<Resource>() {
            @Override
            public void onSuccess(Resource result) {
                if (result.isFile()) {
                    editorAgent.openEditor((File)result);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(NewResourcePagePresenter.class, caught);
            }
        });
    }
}