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
package com.codenvy.ide.part.projectexplorer;

import elemental.client.Browser;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.event.ResourceChangedHandler;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.ui.tree.AbstractTreeNode;
import com.codenvy.ide.api.ui.tree.TreeStructure;
import com.codenvy.ide.api.ui.tree.TreeStructureProviderRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.contexmenu.ContextMenuPresenter;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

/**
 * Project Explorer displays project's tree in a dedicated Part (view).
 *
 * @author Nikolay Zamosenchuk
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectExplorerPartPresenter extends BasePresenter implements ProjectExplorerView.ActionDelegate, ProjectExplorerPart {
    protected ProjectExplorerView           view;
    protected EventBus                      eventBus;
    private   ContextMenuPresenter          contextMenuPresenter;
    private   TreeStructureProviderRegistry treeStructureProviderRegistry;
    private   AppContext                    appContext;
    private   ProjectServiceClient          projectServiceClient;
    private   DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private   CoreLocalizationConstant      coreLocalizationConstant;
    /** Tree that is currently showing. */
    private   TreeStructure                 currentTreeStructure;

    /** Instantiates the ProjectExplorer Presenter. */
    @Inject
    public ProjectExplorerPartPresenter(ProjectExplorerView view,
                                        EventBus eventBus,
                                        ProjectServiceClient projectServiceClient,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                        ContextMenuPresenter contextMenuPresenter,
                                        CoreLocalizationConstant coreLocalizationConstant,
                                        TreeStructureProviderRegistry treeStructureProviderRegistry,
                                        AppContext appContext) {
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.coreLocalizationConstant = coreLocalizationConstant;
        this.eventBus = eventBus;
        this.contextMenuPresenter = contextMenuPresenter;
        this.treeStructureProviderRegistry = treeStructureProviderRegistry;
        this.appContext = appContext;
        this.view.setTitle(coreLocalizationConstant.projectExplorerTitleBarText());

        bind();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpen() {
        // show list of all projects
        setContent(new ProjectsListStructure(projectServiceClient, dtoUnmarshallerFactory, eventBus));
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return coreLocalizationConstant.projectExplorerButtonTitle();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public SVGResource getTitleSVGImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "This View helps you to do basic operation with your projects.";
    }

    /** Adds behavior to view's components. */
    protected void bind() {
        view.setDelegate(this);

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                TreeStructure treeStructure =
                        treeStructureProviderRegistry.getTreeStructureProvider(event.getProject().getProjectTypeId()).getTreeStructure();
                setContent(treeStructure);
                view.setProjectHeader(event.getProject());
                Browser.getWindow().getHistory().replaceState(null, Window.getTitle(),
                                                              Config.getContext() +
                                                              "/" + Config.getWorkspaceName() +
                                                              "/" + event.getProject().getName());
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                // if no previously opened project
                if (appContext.getCurrentProject() == null) {
                    setContent(new ProjectsListStructure(projectServiceClient, dtoUnmarshallerFactory, eventBus));
                    view.hideProjectHeader();
                    Browser.getWindow().getHistory().replaceState(null, Window.getTitle(),
                                                                  Config.getContext() + "/" + Config.getWorkspaceName());
                }
            }
        });

        eventBus.addHandler(ResourceChangedEvent.TYPE, new ResourceChangedHandler() {
            @Override
            public void onResourceRenamed(ResourceChangedEvent event) {
//                if (event.getResource() instanceof Project &&
//                    event.getResource().getParent().getId().equals(resourceProvider.getRootId())) {
//                    setContent(event.getResource().getParent());
//                } else {
//                    updateItem(event.getResource().getParent());
//                }
            }

            @Override
            public void onResourceMoved(ResourceChangedEvent event) {
            }

            @Override
            public void onResourceDeleted(ResourceChangedEvent event) {
//                if (!(event.getResource() instanceof Project)) {
//                    updateItem(event.getResource().getParent());
//                }
            }

            @Override
            public void onResourceCreated(ResourceChangedEvent event) {
                updateItem(event.getResource().getParent());
            }

            @Override
            public void onResourceTreeRefreshed(ResourceChangedEvent event) {
//                final Resource resource = event.getResource();
//
//                if (resource.getProject() == null) {
//                    if (resource.getId().equals(resourceProvider.getRootId())) {
//                        setContent(resource);
//                        view.hideProjectHeader();
//                    }
//                    return;
//                }
//
//                if (resource instanceof Project && resource.getProject() != null) {
//                    view.updateItem(resource.getProject(), resource);
//                } else if (resource instanceof Folder && ((Folder)resource).getChildren().isEmpty()) {
//                    return;
//                } else if (resource.getProject() != null) {
//                    Resource oldResource = resource.getProject().findResourceById(resource.getId());
//                    if (oldResource != null) {
//                        view.updateItem(oldResource, resource);
//                    }
//                }
            }
        });
    }

    /**
     * Set tree structure to show.
     *
     * @param treeStructure
     *         tree structure to show
     */
    private void setContent(@NotNull final TreeStructure treeStructure) {
        treeStructure.getRoots(new AsyncCallback<Array<AbstractTreeNode<?>>>() {
            @Override
            public void onSuccess(Array<AbstractTreeNode<?>> result) {
                currentTreeStructure = treeStructure;
                view.setItems(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ProjectExplorerPartPresenter.class, caught.getMessage());
            }
        });

        onResourceSelected(null);
    }

    /**
     * Update item in the project explorer.
     *
     * @param resource
     *         the resource that need to be updated
     */
    private void updateItem(@NotNull final Resource resource) {
        Project project = resource.getProject();
//        if (resource.getParent().getId().equals(resourceProvider.getRootId())) {
//            view.updateItem(project, resource);
//        } else {
//            project.findResourceByPath(resource.getPath(), new AsyncCallback<Resource>() {
//                @Override
//                public void onSuccess(Resource result) {
//                    view.updateItem(result, resource);
//                }
//
//                @Override
//                public void onFailure(Throwable caught) {
//                }
//            });
//        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceSelected(@NotNull AbstractTreeNode<?> node) {
        if (node == null) {
            setSelection(null);
        } else {
            setSelection(new Selection<>(node.getData()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceOpened(final AbstractTreeNode<?> node) {
        // if children is empty then may be it doesn't refreshed yet
        if (node.getChildren().isEmpty()) {
            currentTreeStructure.refreshChildren(node, new AsyncCallback<AbstractTreeNode<?>>() {
                @Override
                public void onSuccess(AbstractTreeNode<?> result) {
                    if (!result.getChildren().isEmpty()) {
                        view.updateItem(node, result);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error(ProjectExplorerPartPresenter.class, caught.getMessage());
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceAction(@NotNull AbstractTreeNode node) {
        // delegate processing an action
        currentTreeStructure.processNodeAction(node);
    }

    /** {@inheritDoc} */
    @Override
    public void onContextMenu(int mouseX, int mouseY) {
        contextMenuPresenter.show(mouseX, mouseY);
    }
}
