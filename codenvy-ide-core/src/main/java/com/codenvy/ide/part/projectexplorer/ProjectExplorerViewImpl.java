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

import elemental.events.MouseEvent;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.tree.FileTreeNodeRenderer;
import com.codenvy.ide.tree.ResourceTreeNodeDataAdapter;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.dom.client.Document;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.validation.constraints.NotNull;


/**
 * Tree-based Project Explorer view.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class ProjectExplorerViewImpl extends BaseView<ProjectExplorerView.ActionDelegate> implements ProjectExplorerView {
    protected Tree<Resource>               tree;
    private Resources                      resources;
    private SVGImage                       projectVisibilityImage;
    private InlineLabel                    projectTitle;
    private FlowPanel                      projectHeader;
    private final CoreLocalizationConstant locale;

    /**
     * Create view.
     *
     * @param resources
     * @param iconRegistry
     */
    @Inject
    public ProjectExplorerViewImpl(Resources resources, IconRegistry iconRegistry, CoreLocalizationConstant locale) {
        super(resources);
        this.resources = resources;
        this.locale = locale;

        projectHeader = new FlowPanel();
        projectHeader.setStyleName(resources.partStackCss().idePartStackToolbarBottom());

        tree = Tree.create(resources, new ResourceTreeNodeDataAdapter(), FileTreeNodeRenderer.create(resources, iconRegistry));
        container.add(tree.asWidget());
        tree.asWidget().ensureDebugId("projectExplorerTree-panel");
        minimizeButton.ensureDebugId("projectExplorer-minimizeBut");
    }

    /** {@inheritDoc} */
    @Override
    public void setItems(Resource resource) {
        tree.getModel().setRoot(resource);
        tree.renderTree(1);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(final ActionDelegate delegate) {
        this.delegate = delegate;
        tree.setTreeEventHandler(new Tree.Listener<Resource>() {

            @Override
            public void onNodeAction(TreeNodeElement<Resource> node) {
                delegate.onResourceAction(node.getData());
            }

            @Override
            public void onNodeClosed(TreeNodeElement<Resource> node) {
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onNodeContextMenu(int mouseX, int mouseY, TreeNodeElement<Resource> node) {
                delegate.onResourceSelected(node.getData());
                delegate.onContextMenu(mouseX, mouseY);
            }

            @Override
            public void onNodeDragStart(TreeNodeElement<Resource> node, MouseEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Resource> node, MouseEvent event) {
            }

            @Override
            public void onNodeExpanded(TreeNodeElement<Resource> node) {
                delegate.onResourceOpened(node.getData());
            }

            @Override
            public void onNodeSelected(TreeNodeElement<Resource> node, SignalEvent event) {
                delegate.onResourceSelected(node.getData());
            }

            @Override
            public void onRootContextMenu(int mouseX, int mouseY) {
                delegate.onContextMenu(mouseX, mouseY);
            }

            @Override
            public void onRootDragDrop(MouseEvent event) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void updateItem(Resource oldResource, Resource newResource) {
        Array<Array<String>> paths = tree.replaceSubtree(oldResource, newResource, true);

        TreeNodeElement<Resource> nodeElement = tree.getNode(newResource);
        if (nodeElement != null) {
            tree.closeNode(nodeElement);
            tree.expandNode(nodeElement);
        }
        tree.expandPaths(paths, false);
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectHeader(@NotNull Project project) {
        if (toolBar.getWidgetIndex(projectHeader) < 0) {
            toolBar.addSouth(projectHeader, 28);
            container.setWidgetSize(toolBar, 50);
        }
        projectHeader.clear();

        FlowPanel delimeter = new FlowPanel();
        delimeter.setStyleName(resources.partStackCss().idePartStackToolbarSeparator());
        projectHeader.add(delimeter);

        projectVisibilityImage = new SVGImage("private".equals(project.getVisibility()) ? resources.privateProject()
                                                                                        : resources.publicProject());
        projectVisibilityImage.getElement().setAttribute("class", resources.partStackCss().idePartStackToolbarBottomIcon());
        projectHeader.add(projectVisibilityImage);

        projectTitle = new InlineLabel(project.getName());
        projectHeader.add(projectTitle);
        Document.get().setTitle(locale.projectOpenedTitle(project.getName()));
    }

    /** {@inheritDoc} */
    @Override
    public void hideProjectHeader() {
        toolBar.remove(projectHeader);
        container.setWidgetSize(toolBar, 22);
        Document.get().setTitle(locale.projectClosedTitle());
    }
}