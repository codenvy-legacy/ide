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
package com.codenvy.ide.tree;

import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.tree.AbstractTreeNode;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.ui.tree.TreeNodeMutator;
import com.codenvy.ide.util.TextUtils;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * {@link NodeRenderer} to renderer {@code AbstractTreeNode}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTreeNodeRenderer implements NodeRenderer<AbstractTreeNode<?>> {
    private final Css          css;
    private       IconRegistry iconRegistry;

    @Inject
    public ProjectTreeNodeRenderer(Resources resources, IconRegistry iconRegistry) {
        this.iconRegistry = iconRegistry;
        this.css = resources.workspaceNavigationFileTreeNodeRendererCss();
    }

    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    @Override
    public SpanElement renderNodeContents(AbstractTreeNode data) {
        return renderNodeContents(css, data, true);
    }

    /** Renders the given information as a node. */
    private SpanElement renderNodeContents(Css css, AbstractTreeNode item, boolean renderIcon) {
        SpanElement root = Elements.createSpanElement(css.root());

//        if (renderIcon) {
//            SVGImage icon = detectIcon(item);
//            if (icon != null) {
//                icon.getElement().setAttribute("class", css.icon());
//                root.appendChild((Element)icon.getElement());
//            }
//        }

        Elements.addClassName(css.label(), root);

        if (item.getData() instanceof ItemReference) {
            switch (((ItemReference)item.getData()).getType()) {
                case "file":
                    Elements.addClassName(css.fileFont(), root);
                case "folder":
                    Elements.addClassName(css.folderFont(), root);
                default:
                    Elements.addClassName(css.defaultFont(), root);
            }
        }

        root.setInnerHTML(root.getInnerHTML() + "&nbsp;" + item.getName());

        // set 'id' property for rendered element (it's need for testing purpose)
        setIdProperty((com.google.gwt.dom.client.Element)root, item);
        return root;
    }

    private SVGImage detectIcon(Resource item) {
        Project project = item.getProject();
        Icon icon = null;

        if (project == null) {
            return null;
        }

        final String projectTypeId = project.getDescription().getProjectTypeId();
        if (item instanceof Project) {
            icon = iconRegistry.getIconIfExist(projectTypeId + ".projecttype.small.icon");
        } else if (item instanceof Folder) {
            icon = iconRegistry.getIcon(projectTypeId + ".folder.small.icon");
        } else if (item instanceof File) {
            String filename = item.getName();

            // search exact match first
            icon = iconRegistry.getIconIfExist(projectTypeId + "/" + filename + ".file.small.icon");

            // not found, try with extension
            if (icon == null) {
                String[] split = item.getName().split("\\.");
                String ext = split[split.length - 1];
                icon = iconRegistry.getIcon(projectTypeId + "/" + ext + ".file.small.icon");
            }
        }
        if (icon == null) {
            return null;
        }
        return icon.getSVGImage();
    }

    @Override
    public void updateNodeContents(TreeNodeElement<AbstractTreeNode<?>> treeNode) {
//        if (treeNode.getData() instanceof ProjectRootTreeNode) {
//            // Update project icon based on it's state.
//            Element icon = treeNode.getNodeLabel();
//            icon.setClassName(css.icon());
//            if (treeNode.isOpen()) {
//                icon.setClassName(css.projectOpen());
//            } else {
//                icon.setClassName(css.project());
//            }
//        } else if (treeNode.getData() instanceof ItemTreeNode) {
//            // Update folder icon based on it's state.
//            Element icon = treeNode.getNodeLabel();
//            icon.setClassName(css.icon());
//            if (treeNode.getData().isLoading()) {
//                icon.setClassName(css.folderLoading());
//            } else if (treeNode.isOpen()) {
//                icon.setClassName(css.folderOpen());
//            } else {
//                icon.setClassName(css.folder());
//            }
//        }
    }

    /**
     * Set an ID property for the specified element.
     *
     * @param element
     *         the target {@link com.google.gwt.dom.client.Element}
     * @param node
     *         node for which the specified element is rendered
     */
    private void setIdProperty(com.google.gwt.dom.client.Element element, AbstractTreeNode node) {
        String id = node.getName();
        if (node.getParent() != null) {
            id = node.getParent().getName() + node.getName();
        }
        UIObject.ensureDebugId(element, "projectTree-" + TextUtils.md5(id));
    }

    public interface Css extends TreeNodeMutator.Css {
        String file();

        String folder();

        String folderOpen();

        String folderLoading();

        String project();

        String projectOpen();

        String icon();

        String label();

        String folderFont();

        String fileFont();

        String defaultFont();

        String root();

        @Override
        String nodeNameInput();

        String treeFileIcon();
    }

    public interface Resources extends Tree.Resources {
        @Source({"FileTreeNodeRenderer.css", "com/codenvy/ide/common/constants.css", "com/codenvy/ide/api/ui/style.css"})
        Css workspaceNavigationFileTreeNodeRendererCss();

        @Source("file.png")
        ImageResource file();

        @Source("folder_loading.gif")
        ImageResource folderLoading();

        @Source("folder_open.png")
        ImageResource folderOpen();

        @Source("folder.png")
        ImageResource folder();

        @Source("project_open.png")
        ImageResource projectOpen();

        @Source("project.png")
        ImageResource project();
    }

}
