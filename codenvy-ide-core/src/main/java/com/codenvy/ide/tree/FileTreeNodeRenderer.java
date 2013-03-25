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

import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.TreeNodeElement;
import com.codenvy.ide.ui.tree.TreeNodeMutator;
import com.codenvy.ide.util.CssUtils;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ImageResource;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MouseEvent;
import elemental.html.AnchorElement;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.SpanElement;


/**
 * Renderer for nodes in the file tree.
 */
public class FileTreeNodeRenderer implements NodeRenderer<Resource>
{

   public static FileTreeNodeRenderer create(Resources res)
   {
      return new FileTreeNodeRenderer(res);
   }

   public interface Css extends TreeNodeMutator.Css
   {
      String file();

      String folder();

      String folderOpen();

      String folderLoading();

      String project();

      String projectOpen();

      String icon();

      String label();

      String root();

      @Override
      String nodeNameInput();
   }

   public interface Resources extends Tree.Resources
   {
      @Source({"FileTreeNodeRenderer.css", "com/codenvy/ide/common/constants.css"})
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
    * @param mouseDownListener an optional listener to be attached to the anchor. If not given, the
    *        label will not be an anchor.
    */
   public static SpanElement renderNodeContents(Css css, String name, boolean isFile, EventListener mouseDownListener,
      boolean renderIcon)
   {

      SpanElement root = Elements.createSpanElement(css.root());
      if (renderIcon)
      {
         DivElement icon = Elements.createDivElement(css.icon());
         icon.addClassName(isFile ? css.file() : css.folder());
         root.appendChild(icon);
      }

      final Element label;
      if (mouseDownListener != null)
      {
         label = Elements.createAnchorElement(css.label());
         ((AnchorElement)label).setHref("javascript:;");
         label.addEventListener(Event.MOUSEDOWN, mouseDownListener, false);
      }
      else
      {
         label = Elements.createSpanElement(css.label());
      }

      label.setTextContent(name);

      root.appendChild(label);

      return root;
   }

   private final EventListener mouseDownListener = new EventListener()
   {
      @Override
      public void handleEvent(Event evt)
      {
         MouseEvent event = (MouseEvent)evt;
         AnchorElement anchor = (AnchorElement)evt.getTarget();

         if (event.getButton() == MouseEvent.Button.AUXILIARY)
         {
            Element parent = CssUtils.getAncestorOrSelfWithClassName(anchor, res.treeCss().treeNode());

            if (parent != null)
            {
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

   private FileTreeNodeRenderer(Resources resources)
   {
      this.res = resources;
      this.css = res.workspaceNavigationFileTreeNodeRendererCss();
   }

   @Override
   public Element getNodeKeyTextContainer(SpanElement treeNodeLabel)
   {
      return (Element)treeNodeLabel.getChildNodes().item(1);
   }

   @Override
   public SpanElement renderNodeContents(Resource data)
   {
      return renderNodeContents(css, data.getName(), data.isFile(), mouseDownListener, true);
   }

   @Override
   public void updateNodeContents(TreeNodeElement<Resource> treeNode)
   {
      if (treeNode.getData() instanceof Project)
      {
         // Update folder icon based on icon state.
         Element icon = treeNode.getNodeLabel().getFirstChildElement();
         icon.setClassName(css.icon());
         if (treeNode.isOpen())
         {
            icon.addClassName(css.projectOpen());
         }
         else
         {
            icon.addClassName(css.project());
         }
      }
      else if (treeNode.getData() instanceof Folder)
      {
         // Update folder icon based on icon state.
         Element icon = treeNode.getNodeLabel().getFirstChildElement();
         icon.setClassName(css.icon());
         //      if (treeNode.getData().isLoading()) {
         //        icon.addClassName(css.folderLoading());
         //      } else 
         if (treeNode.isOpen())
         {
            icon.addClassName(css.folderOpen());
         }
         else
         {
            icon.addClassName(css.folder());
         }

      }
   }
}
