/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.component;

import java.util.List;

import org.exoplatform.gwt.commons.smartgwt.component.TreeGrid;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.Item;

import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemTreeGrid<T extends Item> extends TreeGrid<T> implements HasOpenHandlers<T>
{

   private Tree tree;

   private TreeNode rootNode;

   private boolean allowSameNames = false;

   public ItemTreeGrid()
   {
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      setData(tree);

      setCanFocus(false);
      setShowConnectors(false);
      setCanSort(false);
   }

   public ItemTreeGrid(boolean allowSameNames)
   {
      this();
      this.allowSameNames = allowSameNames;
   }

   @Override
   protected void doUpdateValue()
   {
      boolean switchWorkspace = !checkSwitchingWorkspace();

      if (rootNode == null || switchWorkspace)
      {
         tree.setRoot(new TreeNode("root"));

         String nodeName = getValue().getPath();
         if (nodeName.startsWith("/"))
         {
            nodeName = nodeName.substring(1);
         }

         rootNode = new TreeNode(nodeName);
         rootNode.setAttribute(getValuePropertyName(), getValue());
         if (getValue().getIcon() != null)
         {
            rootNode.setAttribute("icon", getValue().getIcon());
         }

         rootNode.setIsFolder(true);
         tree.add(rootNode, tree.getRoot());

         selectRecord(rootNode);
      }

      if (((Folder)getValue()).getChildren() == null)
      {
         return;
      }

      Folder rootFolder = (Folder)rootNode.getAttributeAsObject(getValuePropertyName());
      if (getValue().getPath().startsWith(rootFolder.getPath()))
      {
         TreeNode parent = getNodeByPath(getValue().getPath());
         setItems(parent, ((Folder)getValue()).getChildren());
      }
   }

   private boolean checkSwitchingWorkspace()
   {
      try
      {
         Item rootItem = (Item)rootNode.getAttributeAsObject(getValuePropertyName());
         String[] pathes1 = rootItem.getPath().split("/");
         String[] pathes2 = getValue().getPath().split("/");

         if (!pathes1[1].equals(pathes2[1]))
         {
            return false;
         }

         if (!pathes1[2].equals(pathes2[2]))
         {
            return false;
         }

      }
      catch (Exception exc)
      {
         //exc.printStackTrace();
      }
      return true;
   }

   private TreeNode getChild(TreeNode parent, String name)
   {
      for (TreeNode node : tree.getChildren(parent))
      {
         if (node.getName().equals(name))
         {
            return node;
         }
      }

      return null;
   }

   private TreeNode getNodeByPath(String path)
   {
      Folder rootFolder = (Folder)rootNode.getAttributeAsObject(getValuePropertyName());
      path = path.substring(rootFolder.getPath().length());

      if (path.startsWith("/"))
      {
         path = path.substring(1);
      }

      if ("".equals(path))
      {
         return rootNode;
      }

      String[] names = path.split("/");
      TreeNode node = rootNode;
      for (String folderName : names)
      {
         node = getChild(node, folderName);
      }

      return node;
   }

   /**
    * Returns true if item with name "name" is exist in items
    * 
    * @param items
    * @param name
    * @return
    */
   private boolean isItemExist(List<Item> items, String name)
   {
      for (Item item : items)
      {
         if (item.getPath().endsWith("/" + name))
         {
            return true;
         }
      }
      return false;
   }

   private void setItems(TreeNode parentNode, List<Item> children)
   {
      if (children.size() == 0)
      {
         // needs here to delete all children from parentNode

         for (TreeNode childNode : tree.getChildren(parentNode))
         {
            tree.remove(childNode);
         }
         tree.closeAll(parentNode);
         return;
      }

      // remove not existed items in response from tree
      for (TreeNode childNode : tree.getChildren(parentNode))
      {
         String name = ((Item)childNode.getAttributeAsObject(getValuePropertyName())).getName();
         if (!isItemExist(children, name))
         {
            tree.remove(childNode);
         }
      }

      for (Item child : children)
      {
         // see if parentNode already has child with this name
         TreeNode existedNode = getChild(parentNode, child.getName());

         if (!allowSameNames)
         {

            if (existedNode != null)
            {
               existedNode.setAttribute(getValuePropertyName(), child);
               continue;
            }

         }

         TreeNode node = new TreeNode(child.getName());
         node.setAttribute(getValuePropertyName(), child);
         if (child instanceof Folder)
         {
            node.setIsFolder(true);
         }

         if (child.getIcon() != null)
         {
            node.setAttribute("icon", child.getIcon());
         }

         //tree.add(node, parentNode);
         tree.add(node, parentNode, children.indexOf(child));
      }

      tree.openFolder(parentNode);
   }

   @Override
   protected String getValuePropertyName()
   {
      return "itemBean";
   }

   public HandlerRegistration addOpenHandler(OpenHandler<T> openHandler)
   {
      FolderOpenedHandlerImpl<T> openedHandler = new FolderOpenedHandlerImpl<T>(openHandler, getValuePropertyName());
      addFolderOpenedHandler(openedHandler);
      return null;
   }

}
