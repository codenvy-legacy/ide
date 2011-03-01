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
package org.exoplatform.ide.client.component;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

import org.exoplatform.gwtframework.ui.client.component.TreeGrid;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

   private static final String NAME = "name";

   private static final String TITLE = "title";

   private Map<String, String> locktokens;

   public ItemTreeGrid(String id)
   {
      setID(id);
      setShowRoot(false);

      tree = new Tree();
      tree.setModelType(TreeModelType.CHILDREN);
      tree.setRoot(new TreeNode("root"));
      setData(tree);

      setFixedFieldWidths(false);
      setSelectionType(SelectionStyle.SINGLE);
 
      setSeparateFolders(true);

      // setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."  
      setCanFocus(true);
      setShowConnectors(false);
      setCanSort(false);
      
      TreeGridField nameField = new TreeGridField(TITLE);

      //TODO
      //This field need for selenium.
      //We can't select tree node, if click on first column.
      //If you click on second column - tree item is selected.
      TreeGridField mockField = new TreeGridField("mock");
      mockField.setWidth(3);
      setFields(nameField, mockField);
   }

   public ItemTreeGrid(String id, boolean allowSameNames)
   {
      this(id);
      this.allowSameNames = allowSameNames;
      setCanFocus(true);
   }

   @Override
   protected void doUpdateValue()
   {
      if (getValue() == null)
      {
         if (rootNode != null)
         {
            tree.remove(rootNode);
         }
         rootNode = null;
         return;
      }

      if (rootNode == null)
      {
         String nodeName = getValue().getName();
         rootNode = new TreeNode(nodeName);
         rootNode.setAttribute(TITLE, getTitle(getValue()));
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

      String rootFolderHref = rootFolder.getHref();
      if (!getValue().getHref().startsWith(rootFolderHref))
      {
         return;
      }

      TreeNode parent = getNodeByHref(getValue().getHref());

      try
      {
         setItems(parent, ((Folder)getValue()).getChildren());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

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

      if (path.length() > 0 && path.charAt(0) == '/')
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
         if (node == null) return node;
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
      for (TreeNode childNode : tree.getChildren(parentNode))
      {
         Item item = ((Item)childNode.getAttributeAsObject(getValuePropertyName()));
         if (!childNode.getName().equals(item.getName()))
         {
            tree.remove(childNode);
         }
      }

      /*
       * Remove deleted files on the server from tree
       */
      for (TreeNode childNode : tree.getChildren(parentNode))
      {
         String name = ((Item)childNode.getAttributeAsObject(getValuePropertyName())).getName();
         if (!isItemExist(children, name))
         {
            tree.remove(childNode);
         }
         tree.closeAll(childNode);
      }

      for (int position = 0; position < children.size(); position++)
      {
         Item item = children.get(position);

         TreeNode existedNode = getChild(parentNode, item.getName());

         if (!allowSameNames)
         {
            if (existedNode != null)
            {
               TreeNode newNode = getNode(item);
               tree.remove(existedNode);
               tree.add(newNode, parentNode, position);
               continue;
            }
         }

         TreeNode node = getNode(item);
         tree.add(node, parentNode, position);
      }

      tree.openFolder(parentNode);
   }

   private TreeNode getNode(Item item)
   {
      TreeNode node = new TreeNode(item.getName());
      node.setAttribute(TITLE, getTitle(item));
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

   private String getTitle(Item item)
   {
      String title = "";

      if (locktokens == null)
      {
         return item.getName();
      }

      if (item.getProperty(ItemProperty.LOCKDISCOVERY) != null)
      {
         if (!locktokens.containsKey(item.getHref()))
         {
            title +=
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-12px; margin-top:3px;\"  border=\"0\" suppress=\"TRUE\" src=\""
                  + UIHelper.getGadgetImagesURL() + "navigation/lock.png" + "\" />&nbsp;&nbsp;";
         }
      }
      title += item.getName();

      return title;
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
   
   /**
    * Deselect item in the tree by it's href.
    * 
    * @param href item's href
    */
   public void deselectItem(String href)
   {
      TreeNode node = getNodeByHref(href);
      if (node == null)
      {
         return;
      }
      deselectRecord(node);
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

   public void updateFileState(File file)
   {
      TreeNode fileNode = getNodeByHref(file.getHref());
      if (fileNode == null)
      {
         return;
      }

      fileNode.setAttribute(NAME, file.getName());
      fileNode.setAttribute(TITLE, getTitle(file));
      fileNode.setAttribute(getValuePropertyName(), file);

      tree.openFolder(fileNode);
   }

   /**
    * @param locktokens the locktokens to set
    */
   public void setLocktokens(Map<String, String> locktokens)
   {
      this.locktokens = locktokens;
   }

}
