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

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MouseEvent;
import elemental.html.AnchorElement;
import elemental.html.Element;
import elemental.html.ImageElement;
import elemental.html.SpanElement;

import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.ui.tree.TreeNodeMutator;
import com.codenvy.ide.util.CssUtils;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;


/** Renderer for nodes in the file tree. */
public class FileTreeNodeRenderer implements NodeRenderer<Resource> {

    private static IconRegistry iconRegistry;

    public static FileTreeNodeRenderer create(Resources res, IconRegistry iconRegistry) {
        FileTreeNodeRenderer.iconRegistry = iconRegistry;
        return new FileTreeNodeRenderer(res, iconRegistry);
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

    /**
     * Renders the given information as a node.
     *
     * @param mouseDownListener
     *         an optional listener to be attached to the anchor. If not given, the
     *         label will not be an anchor.
     */
    public static SpanElement renderNodeContents(Css css, String name, Resource item, EventListener mouseDownListener,
                                                 boolean renderIcon) {

        SpanElement root = Elements.createSpanElement(css.root());
        Image image = detectIcon(item);

        if ((renderIcon) & (image != null)) {
            ImageElement icon = Elements.createImageElement();
            icon.setSrc(image.getUrl());
            icon.addClassName(css.icon());
            root.appendChild(icon);
        }

        final Element label;
        if (mouseDownListener != null) {
            label = Elements.createAnchorElement(css.label());
            ((AnchorElement)label).setHref("javascript:;");
            label.addEventListener(Event.MOUSEDOWN, mouseDownListener, false);
        } else {
            label = Elements.createSpanElement(css.label());
        }

        if (item.isFolder()) {
            label.addClassName(css.folderFont());
        } else if (item.isFile()) {
            label.addClassName(css.fileFont());
        } else {
            label.addClassName(css.defaultFont());
        }
        label.setTextContent(name);
        UIObject.ensureDebugId((com.google.gwt.dom.client.Element)label, "fileTreeNodeRenderer-resource-" + name + label.hashCode());

        root.appendChild(label);

        return root;
    }

    private static Image detectIcon(Resource item) {
        Project project = item.getProject();
        Image icon = null;

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
        return icon;
    }

    private final EventListener mouseDownListener = new EventListener() {
        @Override
        public void handleEvent(Event evt) {
            MouseEvent event = (MouseEvent)evt;
            AnchorElement anchor = (AnchorElement)evt.getTarget();

            if (event.getButton() == MouseEvent.Button.AUXILIARY) {
                Element parent = CssUtils.getAncestorOrSelfWithClassName(anchor, res.treeCss().treeNode());

                if (parent != null) {
                    @SuppressWarnings({"unchecked", "unused"}) TreeNodeElement<Resource> fileNode = (TreeNodeElement<Resource>)parent;
                    // TODO ????
                    //          anchor.setHref(
                    //              WorkspaceUtils.createDeepLinkToFile(fileNode.getData().getNodePath()));
                }
            }
        }
    };

    private final Css css;

    private final Resources res;

    private FileTreeNodeRenderer(Resources resources, IconRegistry iconRegistry) {
        this.iconRegistry = iconRegistry;
        this.res = resources;
        this.css = res.workspaceNavigationFileTreeNodeRendererCss();
    }

    @Override
    public Element getNodeKeyTextContainer(SpanElement treeNodeLabel) {
        return (Element)treeNodeLabel.getChildNodes().item(1);
    }

    @Override
    public SpanElement renderNodeContents(Resource data) {
        return renderNodeContents(css, data.getName(), data, mouseDownListener, true);
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
}
