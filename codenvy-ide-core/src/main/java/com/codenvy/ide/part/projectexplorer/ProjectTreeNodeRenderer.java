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

import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.icon.Icon;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.projecttree.generic.ProjectRootNode;
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
    private       FileTypeRegistry fileTypeRegistry;
    private       AppContext   appContext;

    @Inject
    public ProjectTreeNodeRenderer(Resources resources, IconRegistry iconRegistry, FileTypeRegistry fileTypeRegistry, AppContext appContext) {
        this.iconRegistry = iconRegistry;
        this.fileTypeRegistry = fileTypeRegistry;
        this.appContext = appContext;
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
    private SpanElement renderNodeContents(Css css, AbstractTreeNode node, boolean renderIcon) {
        SpanElement root = Elements.createSpanElement(css.root());

        if (renderIcon) {
            SVGImage icon = detectIcon(node);
            if (icon != null) {
                icon.getElement().setAttribute("class", css.icon());
                root.appendChild((Element)icon.getElement());
            }
        }

        Elements.addClassName(css.label(), root);

        if (node instanceof FileNode) {
            Elements.addClassName(css.fileFont(), root);
        } else if (node instanceof FolderNode) {
            Elements.addClassName(css.folderFont(), root);
        } else {
            Elements.addClassName(css.defaultFont(), root);
        }

        root.setInnerHTML(root.getInnerHTML() + "&nbsp;" + node.getDisplayName());

        // set 'id' property for rendered element (it's need for testing purpose)
        setIdProperty((com.google.gwt.dom.client.Element)root, node);
        return root;
    }

    private SVGImage detectIcon(AbstractTreeNode<?> node) {
        SVGImage nodeIcon = node.getPresentation().getSvgIcon();
        if (nodeIcon != null) {
            return nodeIcon;
        }

        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return null;
        }

        Icon icon = null;
        final String projectTypeId = project.getRootProject().getProjectTypeId();
        if (node instanceof ProjectRootNode) {
            icon = iconRegistry.getIconIfExist(projectTypeId + ".projecttype.small.icon");
        } else if (node instanceof FolderNode) {
            icon = iconRegistry.getIcon(projectTypeId + ".folder.small.icon");
        } else if (node instanceof FileNode) {
            FileType fileType = fileTypeRegistry.getFileTypeByFile((FileNode)node);
            if (fileType != null && fileType.getSVGImage() != null) {
                return new SVGImage(fileType.getSVGImage());
            }

            final String fileName = ((FileNode)node).getName();
            // try to get icon for file name
            icon = iconRegistry.getIconIfExist(projectTypeId + "/" + fileName + ".file.small.icon");
            // try to get icon for file extension
            if (icon == null) {
                String[] split = fileName.split("\\.");
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
//        if (treeNode.getData() instanceof ProjectRootNode) {
//            // Update project icon based on it's state.
//            Element icon = treeNode.getNodeLabel();
//            icon.setClassName(css.icon());
//            if (treeNode.isOpen()) {
//                icon.setClassName(css.projectOpen());
//            } else {
//                icon.setClassName(css.project());
//            }
//        } else if (treeNode.getData() instanceof FileNode) {
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
        String id = node.getDisplayName();
        if (node.getParent() != null) {
            id = node.getParent().getDisplayName() + node.getDisplayName();
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
