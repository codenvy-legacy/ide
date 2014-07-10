// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.tree;

import elemental.dom.Element;
import elemental.html.SpanElement;

import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
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

/** {@link NodeRenderer} for the project explorer tree. */
public class FileTreeNodeRenderer implements NodeRenderer<Resource> {
    private final Css          css;
    private       IconRegistry iconRegistry;

    @Inject
    public FileTreeNodeRenderer(Resources resources, IconRegistry iconRegistry) {
        this.iconRegistry = iconRegistry;
        this.css = resources.workspaceNavigationFileTreeNodeRendererCss();
    }

    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    @Override
    public SpanElement renderNodeContents(Resource data) {
        return renderNodeContents(css, data, true);
    }

    /** Renders the given information as a node. */
    private SpanElement renderNodeContents(Css css, Resource item, boolean renderIcon) {
        SpanElement root = Elements.createSpanElement(css.root());

        if (renderIcon) {
            SVGImage icon = detectIcon(item);
            if (icon != null) {
                icon.getElement().setAttribute("class", css.icon());
                root.appendChild((Element)icon.getElement());
            }
        }

        Elements.addClassName(css.label(), root);

        if (item.isFolder()) {
            Elements.addClassName(css.folderFont(), root);
        } else if (item.isFile()) {
            Elements.addClassName(css.fileFont(), root);
        } else {
            Elements.addClassName(css.defaultFont(), root);
        }

        root.setInnerHTML(root.getInnerHTML() + "&nbsp;" + item.getName());

        UIObject.ensureDebugId((com.google.gwt.dom.client.Element)root, "projectTree-" + TextUtils.md5(item.getPath()));

        return root;
    }

    private SVGImage detectIcon(Resource item) {
        Project project = item.getProject();
        Icon icon = null;

        if (project == null) return null;
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
    public void updateNodeContents(TreeNodeElement<Resource> treeNode) {
//        if (treeNode.getData() instanceof Project) {
//            // Update folder icon based on icon state.
//            Element icon = treeNode.getNodeLabel().getFirstChildElement();
//            icon.setClassName(css.icon());
//            if (treeNode.isOpen()) {
//                icon.addClassName(css.projectOpen());
//            } else {
//                icon.addClassName(css.project());
//            }
//        } else if (treeNode.getData() instanceof Folder) {
//            // Update folder icon based on icon state.
//            Element icon = treeNode.getNodeLabel().getFirstChildElement();
//            icon.setClassName(css.icon());
        //      if (treeNode.getData().isLoading()) {
        //        icon.addClassName(css.folderLoading());
        //      } else
//            if (treeNode.isOpen()) {
//                icon.addClassName(css.folderOpen());
//            } else {
//                icon.addClassName(css.folder());
//            }

//        }
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
