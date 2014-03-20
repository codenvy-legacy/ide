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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.TreeElement;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.marshal.FolderTreeUnmarshaller;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.resources.model.ResourceNameValidator;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.NewResourceAgentImpl;
import com.codenvy.ide.wizard.newresource.page.NewResourcePageView.ActionDelegate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.NEW_RESOURCE_PROVIDER;
import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.PARENT;
import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.RESOURCE_NAME;

/**
 * Provides selecting kind of file which user wish to create and create resource of the chosen type with given name.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewResourcePagePresenter extends AbstractWizardPage implements ActionDelegate {
    private final AsyncRequestFactory      asyncRequestFactory;
    private final ProjectServiceClient     projectServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private       NewResourcePageView      view;
    private       EditorAgent              editorAgent;
    private       CoreLocalizationConstant constant;
    private       NewResourceProvider      selectedResourceType;
    private       boolean                  isResourceNameValid;
    private       boolean                  hasSameResource;
    private       Project                  project;
    private       Folder                   parent;
    private       Project                  treeStructure;
    private       EventBus                 eventBus;

    /** Create presenter. */
    @Inject
    public NewResourcePagePresenter(Resources resources,
                                    CoreLocalizationConstant constant,
                                    NewResourcePageView view,
                                    NewResourceAgentImpl newResourceAgent,
                                    com.codenvy.ide.api.resources.ResourceProvider resourceProvider,
                                    SelectionAgent selectionAgent,
                                    EditorAgent editorAgent, EventBus eventBus,
                                    AsyncRequestFactory asyncRequestFactory,
                                    ProjectServiceClient projectServiceClient,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super("Create a new resource", resources.newResourceIcon());

        this.view = view;
        this.asyncRequestFactory = asyncRequestFactory;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.view.setResourceName("");
        this.editorAgent = editorAgent;
        this.constant = constant;
        this.eventBus = eventBus;

        Array<NewResourceProvider> newResources = newResourceAgent.getResources();
        if (!newResources.isEmpty()) {
            Array<NewResourceProvider> availableResources = Collections.createArray();
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
        wizardContext.putData(NEW_RESOURCE_PROVIDER, selectedResourceType);
        wizardContext.putData(PROJECT, project);
        wizardContext.putData(PARENT, parent);
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        wizardContext.removeData(NEW_RESOURCE_PROVIDER);
        wizardContext.removeData(PROJECT);
        wizardContext.removeData(PARENT);
        wizardContext.removeData(RESOURCE_NAME);
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
        getProjectStructure();
    }

    private void getProjectStructure() {
        treeStructure = new Project(eventBus, asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
        treeStructure.setId(project.getId());
        treeStructure.setName(project.getName());
        treeStructure.setParent(project.getParent());
        treeStructure.setMimeType(project.getMimeType());

        projectServiceClient.getTree(project.getPath(), -1,
                                     new AsyncRequestCallback<TreeElement>(dtoUnmarshallerFactory.newUnmarshaller(TreeElement.class)) {
                                         @Override
                                         protected void onSuccess(TreeElement result) {
                                             FolderTreeUnmarshaller unmarshaller = new FolderTreeUnmarshaller(treeStructure, treeStructure);
                                             unmarshaller.unmarshal(result);
                                             Array<String> paths = Collections.createArray();
                                             view.setPackages(getPackages(paths, unmarshaller.getPayload().getChildren()));
                                             view.selectPackage(paths.indexOf(getDisplayPath(parent.getPath())));
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             Log.error(NewResourcePagePresenter.class, exception);
                                         }
                                     });
    }

    private Array<String> getPackages(Array<String> paths, Array<Resource> children) {
        for (Resource resource : children.asIterable()) {
            if (resource instanceof Folder) {
                paths.add(getDisplayPath(resource.getPath()));
                getPackages(paths, ((Folder)resource).getChildren());
            }
        }
        return paths;
    }

    /**
     * Get path to display.
     *
     * @param path
     *         item's path
     * @return {@link String} path to display
     */
    private String getDisplayPath(String path) {
        String displayPath = path.replaceFirst(project.getPath(), "");
        displayPath = displayPath.startsWith("/") ? displayPath.replaceFirst("/", "") : displayPath;
        return displayPath;
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceTypeSelected(@NotNull NewResourceProvider resourceType) {
        selectedResourceType = resourceType;
        wizardContext.putData(NEW_RESOURCE_PROVIDER, selectedResourceType);
        view.selectResourceType(resourceType);
        onResourceNameChanged();
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceNameChanged() {
        checkEnteredData();
    }

    /** Check the data on the view. */
    private void checkEnteredData() {
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
            Array<Resource> children = parent.getChildren();
            for (int i = 0; i < children.size() && !hasSameResource; i++) {
                Resource child = children.get(i);
                hasSameResource = child.getName().equals(resourceName);
            }
        }

        wizardContext.putData(RESOURCE_NAME, resourceName);

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull final CommitCallback callback) {
        parent.setProject(project);
        selectedResourceType.create(view.getResourceName(), parent, project, new AsyncCallback<Resource>() {
            @Override
            public void onSuccess(Resource result) {
                if (result.isFile()) {
                    editorAgent.openEditor((File)result);
                }
                callback.onSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceParentChanged() {
        String[] names = view.getPackageName().split("/");
        parent = treeStructure;
        for (String name : names) {
            for (Resource child : parent.getChildren().asIterable()) {
                if (child instanceof Folder && child.getName().equals(name)) {
                    parent = (Folder)child;
                    break;
                }
            }
        }
        checkEnteredData();
    }
}
