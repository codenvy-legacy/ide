/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.part.projectexplorer;

import org.eclipse.che.ide.CoreLocalizationConstant;

import elemental.dom.Element;
import elemental.html.SpanElement;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.FolderNode;
import org.eclipse.che.ide.api.project.tree.generic.ProjectNode;
import org.eclipse.che.ide.ui.tree.NodeRenderer;
import org.eclipse.che.ide.ui.tree.Tree;
import org.eclipse.che.ide.ui.tree.TreeNodeElement;
import org.eclipse.che.ide.ui.tree.TreeNodeMutator;
import org.eclipse.che.ide.util.TextUtils;
import org.eclipse.che.ide.util.dom.Elements;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;


/**
 * {@link NodeRenderer} to renderer {@code TreeNode}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectTreeNodeRenderer implements NodeRenderer<TreeNode<?>> {
    private final Css                      css;
    private final Resources                resources;
    private       IconRegistry             iconRegistry;
    private       FileTypeRegistry         fileTypeRegistry;
    private       CoreLocalizationConstant constant;
    private       AppContext               appContext;

    @Inject
    public ProjectTreeNodeRenderer(Resources resources,
                                   IconRegistry iconRegistry,
                                   FileTypeRegistry fileTypeRegistry,
                                   CoreLocalizationConstant constant,
                                   AppContext appContext) {
        this.resources = resources;
        this.iconRegistry = iconRegistry;
        this.fileTypeRegistry = fileTypeRegistry;
        this.constant = constant;
        this.appContext = appContext;
        this.css = resources.workspaceNavigationFileTreeNodeRendererCss();
    }

    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    @Override
    public SpanElement renderNodeContents(TreeNode<?> data) {
        return renderNodeContents(css, data, true);
    }

    /** Renders the given information as a node. */
    private SpanElement renderNodeContents(Css css, TreeNode<?> node, boolean renderIcon) {
        SpanElement root = Elements.createSpanElement(css.root());

        if (renderIcon) {
            SVGImage icon = detectIcon(node);
            if (icon != null) {
                icon.getElement().setAttribute("class", css.icon());
                root.appendChild((Element)icon.getElement());
            }
        }

        Elements.addClassName(css.label(), root);

        if (node instanceof ProjectListStructure.ProjectNode) {
            if (hasProblems((ProjectListStructure.ProjectNode)node)) {
                root.setTitle(constant.projectExplorerProblemProjetTitle());
            }
        } else if (node instanceof FileNode) {
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

    private SVGImage detectIcon(TreeNode<?> node) {
        SVGImage nodeIcon = node.getDisplayIcon();
        if (nodeIcon != null) {
            return nodeIcon;
        }

        if (node instanceof ProjectListStructure.ProjectNode) {
            if (hasProblems((ProjectListStructure.ProjectNode)node)) {
                return new SVGImage(resources.projectProblem());
            }
        }

        CurrentProject project = appContext.getCurrentProject();
        if (project == null) {
            return null;
        }

        Icon icon = null;
        final String projectTypeId = project.getRootProject().getType();
        if (node instanceof ProjectNode) {
            icon = iconRegistry.getIconIfExist(projectTypeId + ".projecttype.small.icon");
        } else if (node instanceof FolderNode) {
            icon = iconRegistry.getIcon(projectTypeId + ".folder.small.icon");
        } else if (node instanceof FileNode) {

            final String fileName = ((FileNode)node).getName();

            // try to get icon for file name
            icon = iconRegistry.getIconIfExist(projectTypeId + "/" + fileName + ".file.small.icon");
            // try to get icon for file extension
            if (icon == null) {
                String[] split = fileName.split("\\.");
                String ext = split[split.length - 1];
                icon = iconRegistry.getIconIfExist(projectTypeId + "/" + ext + ".file.small.icon");
            }
            //use default icons from file type
            if (icon == null) {
                FileType fileType = fileTypeRegistry.getFileTypeByFile((FileNode)node);
                if (fileType != null && fileType.getSVGImage() != null) {
                    return new SVGImage(fileType.getSVGImage());
                }
            }
        }
        if (icon == null) {
            return null;
        }
        return icon.getSVGImage();
    }

    private boolean hasProblems(ProjectListStructure.ProjectNode project) {
        return !project.getData().getProblems().isEmpty();
    }

    @Override
    public void updateNodeContents(TreeNodeElement<TreeNode<?>> treeNode) {
//        if (treeNode.getData() instanceof ProjectNode) {
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
    private void setIdProperty(com.google.gwt.dom.client.Element element, TreeNode<?> node) {
        String id = "/" + node.getId();
        TreeNode<?> parent = node.getParent();
        while (parent != null && !parent.getId().equals("ROOT")) {
            id = "/" + parent.getId() + id;
            parent = parent.getParent();
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
        @Source({"FileTreeNodeRenderer.css", "org/eclipse/che/ide/common/constants.css", "org/eclipse/che/ide/api/ui/style.css"})
        Css workspaceNavigationFileTreeNodeRendererCss();

        @Source("toConfigure.svg")
        SVGResource projectProblem();

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
