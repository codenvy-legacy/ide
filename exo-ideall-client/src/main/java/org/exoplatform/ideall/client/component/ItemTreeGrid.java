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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.TreeGrid;
import org.exoplatform.ideall.client.module.vfs.api.Folder;
import org.exoplatform.ideall.client.module.vfs.api.Item;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemTreeGrid<T extends Item> extends TreeGrid<T>
{

   private Tree tree;

   private TreeNode rootNode;

   private boolean allowSameNames = false;
   
   private final String ID = "ideItemTreeGrid";

   public ItemTreeGrid()
   {
      setID(ID);
      setShowRoot(false);
      
      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      tree.setRoot(new TreeNode("root"));      
      setData(tree);

      setSelectionType(SelectionStyle.SINGLE);

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
      if (getValue() == null) {
         if (rootNode != null) {
            tree.remove(rootNode);
         }
         rootNode = null;
         return;
      }
      
      if (rootNode == null)
      {
//         tree.setRoot(new TreeNode("root"));

         String nodeName = getValue().getHref();
         if (nodeName.endsWith("/"))
         {
            nodeName = nodeName.substring(0, nodeName.length() - 1);
         }

         nodeName = nodeName.substring(nodeName.lastIndexOf("/") + 1);

         
//         rootNode = new TreeNode("<span>" + iconPrefix + "</span>&nbsp;" + nodeName);
         rootNode = new TreeNode(nodeName);
         rootNode.setAttribute(getValuePropertyName(), getValue());

         String iconPrefix = "";
         if (getValue().getIcon() != null)
         {
            rootNode.setAttribute("icon", getValue().getIcon());
//            Image i = new Image(IDEImageBundle.INSTANCE.search());
//            iconPrefix = ImageUtil.getHTML(i);
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
      if (!getValue().getHref().startsWith(rootFolder.getHref()))
      {
         return;
      }

      TreeNode parent = getNodeByHref(getValue().getHref());
      
      setItems(parent, ((Folder)getValue()).getChildren());
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

   private TreeNode getNodeByHref(String href)
   {
      Folder rootFolder = (Folder)rootNode.getAttributeAsObject(getValuePropertyName());
      String path = href.substring(rootFolder.getHref().length());
      
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
         if (item.getName().equals(name))
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
      
      /*
       * check differences in name of tree node and item href
       */
      for (TreeNode childNode : tree.getChildren(parentNode)) {
         Item item = ((Item)childNode.getAttributeAsObject(getValuePropertyName()));
         if (!childNode.getName().equals(item.getName())) {
            tree.remove(childNode);
         }
      }

      // remove not existed items in response from tree
      for (TreeNode childNode : tree.getChildren(parentNode))
      {
         String name = ((Item)childNode.getAttributeAsObject(getValuePropertyName())).getName();
         if (!isItemExist(children, name))
         {
            tree.remove(childNode);
         }
         tree.closeAll(childNode);
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

   public void selectItem(String href)
   {
      TreeNode node = getNodeByHref(href);
      if (node == null)
      {
         return;
      }
      
      deselectAllRecords();
      selectRecord(node);
   }

   public List<Item> getSelectedItems()
   {
      List<Item> selectedItems = new ArrayList<Item>();

      for (ListGridRecord record : getSelection())
      {
         selectedItems.add((Item)record.getAttributeAsObject(getValuePropertyName()));
      }

      return selectedItems;
   }

}
