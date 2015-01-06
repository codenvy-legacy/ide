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
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.api.projecttree.generic.Openable;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.menu.ContextMenu;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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
    private ContextMenu                   contextMenu;
    private ProjectServiceClient          projectServiceClient;
    private DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private CoreLocalizationConstant      coreLocalizationConstant;
    private AppContext                    appContext;
    private TreeStructureProviderRegistry treeStructureProviderRegistry;
    private AbstractTreeStructure         currentTreeStructure;
    private DeleteNodeHandler             deleteNodeHandler;

    /** Instantiates the Project Explorer presenter. */
    @Inject
    public ProjectExplorerPartPresenter(ProjectExplorerView view, EventBus eventBus, ProjectServiceClient projectServiceClient,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory, ContextMenu contextMenu,
                                        CoreLocalizationConstant coreLocalizationConstant, AppContext appContext,
                                        TreeStructureProviderRegistry treeStructureProviderRegistry, DeleteNodeHandler deleteNodeHandler) {
        this.view = view;
        this.eventBus = eventBus;
        this.contextMenu = contextMenu;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.coreLocalizationConstant = coreLocalizationConstant;
        this.appContext = appContext;
        this.treeStructureProviderRegistry = treeStructureProviderRegistry;
        this.deleteNodeHandler = deleteNodeHandler;
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
        if (Config.getProjectName() == null) {
            setTree(new ProjectListStructure(TreeSettings.DEFAULT, eventBus, projectServiceClient, dtoUnmarshallerFactory));
        } else {
            projectServiceClient.getProject(Config.getProjectName(), new AsyncRequestCallback<ProjectDescriptor>() {
                @Override
                protected void onSuccess(ProjectDescriptor result) {
                }

                @Override
                protected void onFailure(Throwable exception) {
                    setTree(new ProjectListStructure(TreeSettings.DEFAULT, eventBus, projectServiceClient, dtoUnmarshallerFactory));
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Nonnull
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

    /** {@inheritDoc} */
    @Override
    public int getSize() {
        return 230;
    }

    /** Adds behavior to view's components. */
    protected void bind() {
        view.setDelegate(this);

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                final ProjectDescriptor project = event.getProject();
                setTree(treeStructureProviderRegistry.getTreeStructureProvider(project.getType()).newTreeStructure(project));
                view.setProjectHeader(event.getProject());
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                // this isn't case when some project going to open while previously opened project is closing
                if (!event.isCloseBeforeOpening()) {
                    setTree(new ProjectListStructure(TreeSettings.DEFAULT, eventBus, projectServiceClient, dtoUnmarshallerFactory));
                    view.hideProjectHeader();
                }
            }
        });

        eventBus.addHandler(RefreshProjectTreeEvent.TYPE, new RefreshProjectTreeHandler() {
            @Override
            public void onRefresh(RefreshProjectTreeEvent event) {
                final TreeNode<?> node = event.getNode();
                if (node == null) {
                    // refresh tree's root
                    setTree(currentTreeStructure);
                } else {
                    node.refreshChildren(new AsyncCallback<TreeNode<?>>() {
                        @Override
                        public void onSuccess(TreeNode<?> result) {
                            updateNode(node);
                            view.selectNode(node);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(ProjectExplorerPartPresenter.class, caught);
                        }
                    });
                }
            }
        });

        eventBus.addHandler(NodeChangedEvent.TYPE, new NodeChangedHandler() {
            @Override
            public void onNodeRenamed(NodeChangedEvent event) {
                if (appContext.getCurrentProject() == null) {
                    // any opened project - all projects list is shown
                    setTree(currentTreeStructure);
                } else {
                    updateNode(event.getNode().getParent());
                    view.selectNode(event.getNode());
                }
            }

            @Override
            public void onNodeChildrenChanged(NodeChangedEvent event) {
                if (appContext.getCurrentProject() == null) {
                    // any opened project - all projects list is shown
                    setTree(currentTreeStructure);
                } else {
                    final TreeNode<?> node = event.getNode();

                    final List<Object> oldNodes = new ArrayList<Object>();
                    for (TreeNode item : node.getChildren().asIterable()) {
                        oldNodes.add(item.getData());
                    }

                    node.refreshChildren(new AsyncCallback<TreeNode<?>>() {
                        @Override
                        public void onSuccess(TreeNode<?> result) {
                            updateNode(node);

                            Array<TreeNode<?>> newNodes = node.getChildren();
                            for (TreeNode item : newNodes.asIterable()) {
                                if (!oldNodes.contains(item.getData())) {
                                    view.selectAndExpandNode(item);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(ProjectExplorerPartPresenter.class, caught);
                        }
                    });
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onNodeSelected(TreeNode<?> node) {
        setSelection(new Selection<>(node));

        if (node != null && node instanceof StorableNode && appContext.getCurrentProject() != null && node.getProject() != null) {
            appContext.getCurrentProject().setProjectDescription(node.getProject().getData());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onNodeExpanded(@Nonnull final TreeNode<?> node) {
        if (node.getChildren().isEmpty()) {
            // If children is empty then may be it doesn't refreshed yet?
            node.refreshChildren(new AsyncCallback<TreeNode<?>>() {
                @Override
                public void onSuccess(TreeNode<?> result) {
                    if (node instanceof Openable) {
                        ((Openable)node).open();
                    }
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
    public void onNodeAction(@Nonnull TreeNode<?> node) {
        node.processNodeAction();
    }

    /** {@inheritDoc} */
    @Override
    public void onContextMenu(int mouseX, int mouseY) {
        contextMenu.show(mouseX, mouseY);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteKey() {
        final TreeNode<?> selectedNode = view.getSelectedNode();
        if (selectedNode instanceof StorableNode) {
            deleteNodeHandler.delete((StorableNode)selectedNode);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEnterKey() {
        view.getSelectedNode().processNodeAction();
    }

    private void setTree(@Nonnull final AbstractTreeStructure treeStructure) {
        currentTreeStructure = treeStructure;
        if (appContext.getCurrentProject() != null) {
            appContext.getCurrentProject().setCurrentTree(currentTreeStructure);
        }
        treeStructure.getRoots(new AsyncCallback<Array<TreeNode<?>>>() {
            @Override
            public void onSuccess(Array<TreeNode<?>> result) {
                view.setRootNodes(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ProjectExplorerPartPresenter.class, caught.getMessage());
            }
        });
    }

    private void updateNode(TreeNode<?> node) {
        view.updateNode(node, node);
    }
}
