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
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.NodeChangedEvent;
import com.codenvy.ide.api.event.NodeChangedHandler;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.event.RefreshProjectTreeHandler;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
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
    private ProjectExplorerView           view;
    private EventBus                      eventBus;
    private ContextMenuPresenter          contextMenuPresenter;
    private ProjectServiceClient          projectServiceClient;
    private DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private CoreLocalizationConstant      coreLocalizationConstant;
    private AppContext                    appContext;
    private TreeStructureProviderRegistry treeStructureProviderRegistry;
    private AbstractTreeStructure         currentTreeStructure;
    private AbstractTreeNode<?>           selectedTreeNode;
    private DeleteItemHandler             deleteItemHandler;

    /** Instantiates the Project Explorer presenter. */
    @Inject
    public ProjectExplorerPartPresenter(ProjectExplorerView view, EventBus eventBus, ProjectServiceClient projectServiceClient,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory, ContextMenuPresenter contextMenuPresenter,
                                        CoreLocalizationConstant coreLocalizationConstant, AppContext appContext,
                                        TreeStructureProviderRegistry treeStructureProviderRegistry, DeleteItemHandler deleteItemHandler) {
        this.view = view;
        this.eventBus = eventBus;
        this.contextMenuPresenter = contextMenuPresenter;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.coreLocalizationConstant = coreLocalizationConstant;
        this.appContext = appContext;
        this.treeStructureProviderRegistry = treeStructureProviderRegistry;
        this.view.setTitle(coreLocalizationConstant.projectExplorerTitleBarText());
        this.deleteItemHandler = deleteItemHandler;

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
        setTree(new ProjectListStructure(TreeSettings.DEFAULT, eventBus, projectServiceClient, dtoUnmarshallerFactory));
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
                final ProjectDescriptor project = event.getProject();
                setTree(treeStructureProviderRegistry.getTreeStructureProvider(project.getProjectTypeId()).newTreeStructure(project));
                view.setProjectHeader(event.getProject());
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                if (appContext.getCurrentProject() == null) {
                    // this is case when some project is opening and previously opened project is closing
                    setTree(new ProjectListStructure(TreeSettings.DEFAULT, eventBus, projectServiceClient, dtoUnmarshallerFactory));
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

        eventBus.addHandler(NodeChangedEvent.TYPE, new NodeChangedHandler() {
            @Override
            public void onNodeRenamed(NodeChangedEvent event) {
                if (appContext.getCurrentProject() != null) {
                    updateNode(event.getNode().getParent());
                    view.selectNode(event.getNode());
                } else {
                    setTree(currentTreeStructure);
                }
            }

            @Override
            public void onNodeChildrenChanged(NodeChangedEvent event) {
                final AbstractTreeNode<?> node = event.getNode();
                node.refreshChildren(new AsyncCallback<AbstractTreeNode<?>>() {
                    @Override
                    public void onSuccess(AbstractTreeNode<?> result) {
                        updateNode(node);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                });
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onNodeSelected(@NotNull AbstractTreeNode<?> node) {
        selectedTreeNode = node;
        setSelection(new Selection<>(node));

        updateAppContext(node);
    }

    private void updateAppContext(AbstractTreeNode<?> node) {
        if (node instanceof StorableNode && appContext.getCurrentProject() != null) {
            appContext.getCurrentProject().setProjectDescription(node.getProject().getData());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onNodeExpanded(final AbstractTreeNode<?> node) {
        if (node.getChildren().isEmpty()) {
            // If children is empty then may be it doesn't refreshed yet?
            node.refreshChildren(new AsyncCallback<AbstractTreeNode<?>>() {
                @Override
                public void onSuccess(AbstractTreeNode<?> result) {
                    if (!result.getChildren().isEmpty()) {
                        view.updateNode(node, result);
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
    public void onNodeAction(@NotNull AbstractTreeNode<?> node) {
        node.processNodeAction();
    }

    /** {@inheritDoc} */
    @Override
    public void onContextMenu(int mouseX, int mouseY) {
        contextMenuPresenter.show(mouseX, mouseY);
    }

    private void setTree(@NotNull final AbstractTreeStructure treeStructure) {
        currentTreeStructure = treeStructure;
        treeStructure.getRoots(new AsyncCallback<Array<AbstractTreeNode<?>>>() {
            @Override
            public void onSuccess(Array<AbstractTreeNode<?>> result) {
                view.setRootNodes(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ProjectExplorerPartPresenter.class, caught.getMessage());
            }
        });
    }

    private void updateNode(final AbstractTreeNode<?> node) {
        view.updateNode(node, node);
    }

    private void updateTree() {
        final AbstractTreeNode<?> parent = selectedTreeNode.getParent();
        if (parent.getParent() == null) {
            setTree(currentTreeStructure); // refresh entire tree
        } else {
            parent.refreshChildren(new AsyncCallback<AbstractTreeNode<?>>() {
                @Override
                public void onSuccess(AbstractTreeNode<?> result) {
                    view.updateNode(parent, result);
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
    public void onDeleteKey() {
        if (selectedTreeNode != null && selectedTreeNode instanceof StorableNode) {
            deleteItemHandler.delete((StorableNode)selectedTreeNode);
        }
    }
}
