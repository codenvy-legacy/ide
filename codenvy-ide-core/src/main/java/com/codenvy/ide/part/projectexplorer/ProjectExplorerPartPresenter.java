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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.event.RefreshProjectTreeHandler;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.ui.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.ui.projecttree.TreeStructure;
import com.codenvy.ide.api.ui.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.contexmenu.ContextMenuPresenter;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.validation.constraints.NotNull;

/**
 * Project Explorer displays project's tree in a dedicated part (view).
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
    private   TreeStructure                 currentTreeStructure;
    private   AbstractTreeNode<?>           selectedTreeNode;

    /** Instantiates the Project Explorer presenter. */
    @Inject
    public ProjectExplorerPartPresenter(ProjectExplorerView view, EventBus eventBus, ProjectServiceClient projectServiceClient,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory, ContextMenuPresenter contextMenuPresenter,
                                        CoreLocalizationConstant coreLocalizationConstant, AppContext appContext,
                                        TreeStructureProviderRegistry treeStructureProviderRegistry) {
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
                final String projectTypeId = event.getProject().getProjectTypeId();
                TreeStructure treeStructure = treeStructureProviderRegistry.getTreeStructureProvider(projectTypeId).getTreeStructure();
                setContent(treeStructure);
                view.setProjectHeader(event.getProject());
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                // if no previously opened project
                if (appContext.getCurrentProject() == null) {
                    setContent(new ProjectsListStructure(projectServiceClient, dtoUnmarshallerFactory, eventBus));
                    view.hideProjectHeader();
                }
            }
        });

        eventBus.addHandler(RefreshProjectTreeEvent.TYPE, new RefreshProjectTreeHandler() {
            @Override
            public void onRefresh(RefreshProjectTreeEvent event) {
                updateTree();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceSelected(@NotNull AbstractTreeNode<?> node) {
        selectedTreeNode = node;
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
                    Log.error(ProjectExplorerPartPresenter.class, caught);
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceAction(@NotNull AbstractTreeNode node) {
        // delegate handling an action to current tree structure
        currentTreeStructure.processNodeAction(node);
    }

    /** {@inheritDoc} */
    @Override
    public void onContextMenu(int mouseX, int mouseY) {
        contextMenuPresenter.show(mouseX, mouseY);
    }

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

    private void updateTree() {
        final AbstractTreeNode parent = selectedTreeNode.getParent();
        if (parent.getParent() == null) {
            setContent(currentTreeStructure); // refresh entire tree
        } else {
            currentTreeStructure.refreshChildren(parent, new AsyncCallback<AbstractTreeNode<?>>() {
                @Override
                public void onSuccess(AbstractTreeNode<?> result) {
                    view.updateItem(parent, result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error(ProjectExplorerPartPresenter.class, caught);
                }
            });
        }
    }
}
