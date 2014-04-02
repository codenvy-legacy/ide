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
package com.codenvy.ide.part.projectexplorer;

import elemental.html.DragEvent;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.tree.FileTreeNodeRenderer;
import com.codenvy.ide.tree.ResourceTreeNodeDataAdapter;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
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
    protected Tree<Resource> tree;
    private IconRegistry     iconRegistry;
    private Resources        resources;
    private SVGImage         projectVisibilityImage;
    private InlineLabel      projectTitle;
    private FlowPanel        projectHeader;
    
    
    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    public ProjectExplorerViewImpl(Resources resources, IconRegistry iconRegistry) {
        super(resources);
        this.iconRegistry = iconRegistry;
        this.resources = resources;
        
        projectHeader = new FlowPanel();
        projectHeader.setStyleName(resources.partStackCss().idePartStackToolbarBottom());
        
        FlowPanel delimeter = new FlowPanel();
        delimeter.setStyleName(resources.partStackCss().idePartStackToolbarSeparator());
        projectHeader.add(delimeter);
        
        projectVisibilityImage = new SVGImage();
        projectVisibilityImage.getElement().getStyle().setMarginRight(8, Unit.PX);
        projectVisibilityImage.getElement().getStyle().setMarginLeft(10, Unit.PX);
        projectVisibilityImage.setHeight("16px");
        projectVisibilityImage.setWidth("16px");
        projectHeader.add(projectVisibilityImage);
        
        projectTitle = new InlineLabel();
        projectHeader.add(projectTitle);
        projectHeader.setVisible(false);
        
        toolBar.addSouth(projectHeader, 28);

        tree = Tree.create(resources, new ResourceTreeNodeDataAdapter(), FileTreeNodeRenderer.create(resources, iconRegistry));
        container.add(tree.asWidget());
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
            public void onNodeDragStart(TreeNodeElement<Resource> node, DragEvent event) {
            }

            @Override
            public void onNodeDragDrop(TreeNodeElement<Resource> node, DragEvent event) {
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
            public void onRootDragDrop(DragEvent event) {
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
        if (!projectHeader.isVisible()) {
            projectHeader.setVisible(true);
            container.setWidgetSize(toolBar, 48);
        }
        projectVisibilityImage.setResource(resources.privateProject());
        projectTitle.setText(project.getName());
    }

    /** {@inheritDoc} */
    @Override
    public void hideProjectHeader() {
        projectHeader.setVisible(false);
        container.setWidgetSize(toolBar, 20);
    }
}