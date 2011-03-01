/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.groovy.client.classpath.ui;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

import org.exoplatform.gwtframework.ui.client.component.TreeGrid;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree grid to display structure of repositories for
 * Choose source dialog window.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemTreeGrid<T extends Item> extends TreeGrid<T>
{
   private Tree tree;

   private static final String NAME = "name";

   /**
    * @param id id of the tree
    */
   public ItemTreeGrid(String id)
   {
      setID(id);
      setShowRoot(false);

      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      tree.setRoot(new TreeNode("root"));
      setData(tree);

      setFixedFieldWidths(false);
      setSelectionType(SelectionStyle.MULTIPLE);
 
      setSeparateFolders(true);

      setCanFocus(true);
      setShowConnectors(true);
      setCanSort(false);
      
      TreeGridField nameField = new TreeGridField(NAME);
      //TODO
      //This field need for selenium.
      //We can't select tree node, if click on first column.
      //If you click on second column - tree item is selected.
      TreeGridField mockField = new TreeGridField("mock");
      mockField.setWidth(3);
      setFields(nameField, mockField);
   }

   @Override
   protected void doUpdateValue()
   {
      TreeNode node = findTreeNode(getValue().getHref());
      if (node != null && getValue() instanceof Folder)
      {
         fillTreeItems(node, ((Folder)getValue()).getChildren());
         tree.openFolder(node);
         redraw();
      }
      else
      {
         fillTreeItems(tree.getRoot(), ((Folder)getValue()).getChildren());
      }
      
   }
   
   /**
    * Find tree node by href.
    * 
    * @param href
    * @return {@link TreeNode}
    */
   public TreeNode findTreeNode(String href)
   {
      TreeNode[] treeNodes = tree.getDescendants();
      for (TreeNode node : treeNodes)
      {
         Item item = ((Item)node.getAttributeAsObject(getValuePropertyName()));
         if (href != null && href.equals(item.getHref()))
         {
            return node;
         }
      }
      return null;
   }
   
   /**
    * Fill the tree with items.
    * 
    * @param parentNode
    * @param children
    */
   private void fillTreeItems(TreeNode parentNode, List<Item> children)
   {
      TreeNode[] oldNodes = tree.getChildren(parentNode);
      tree.removeList(oldNodes);
      if (children == null)
      {
         return;
      }
      for (Item item : children)
      {
         TreeNode newNode = null;
         TreeNode[] nodes = tree.getChildren(parentNode);
         for (TreeNode node : nodes)
         {
            if (node.getAttributeAsObject(getValuePropertyName()) == item)
            {
               newNode = node;
               break;
            }
         }
         if (newNode == null)
         {
            newNode = getNode(item);
            newNode.setAttribute(getValuePropertyName(), item);
            tree.add(newNode, parentNode);
         }
         if (item instanceof Folder && ((Folder)item).getChildren() != null)
         {
            fillTreeItems(newNode, ((Folder)item).getChildren());
         }
      }
   }

   /**
    * Get node by pointed item.
    * 
    * @param item
    * @return {@link TreeNode}
    */
   private TreeNode getNode(Item item)
   {
      TreeNode node = new TreeNode(item.getName());
      node.setTitle(item.getName());
      node.setAttribute(getValuePropertyName(), item);
      if (item instanceof Folder)
      {
         node.setIsFolder(true);
      }

      if (item.getIcon() != null)
      {
         node.setAttribute("icon", item.getIcon());
      }
      return node;
   }

   /**
    * Get selected items in the tree.
    * 
    * @return {@link List}
    */
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
