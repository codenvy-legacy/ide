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

import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.groovy.client.Images;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree grid to display structure of repositories for
 * Choose source dialog window.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemTreeGrid<T extends Item> extends org.exoplatform.gwtframework.ui.client.component.Tree<T>
{
   /**
    * @param id id of the tree
    */
   public ItemTreeGrid(String id)
   {
      getElement().setId(id);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      TreeItem node = findTreeNode(getValue().getHref());
      if (node != null && getValue() instanceof Folder)
      {
         fillTreeItems(node, ((Folder)getValue()).getChildren());
         node.setState(true, true);
      }
      else
      {
         fillTreeItems(((Folder)getValue()).getChildren());
      }

   }

   /**
    * Find tree node by href.
    * 
    * @param href
    * @return {@link TreeItem}
    */
   public TreeItem findTreeNode(String href)
   {
      for (int i = 0; i < tree.getItemCount(); i++)
      {
         TreeItem child = tree.getItem(i);
         if (child.getUserObject() == null)
            continue;
         if (href.equals(((Item)child.getUserObject()).getHref()))
         {
            return child;
         }
         TreeItem item = getChild(child, href);
         if (item != null)
            return item;
      }
      return null;
   }

   /**
    * Get child tree node of pointed parent, that represents the pointed token.
    * 
    * @param parent parent
    * @param token token
    * @return {@link TreeItem}
    */
   private TreeItem getChild(TreeItem parent, String href)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem child = parent.getChild(i);
         if (child.getUserObject() == null)
            continue;
         if (href.equals(((Item)child.getUserObject()).getHref()))
         {
            return child;
         }
         TreeItem item = getChild(child, href);
         if (item != null)
            return item;
      }
      return null;
   }

   /**
    * Fill the pointed node with child items.
    * 
    * @param parentNode
    * @param children
    */
   private void fillTreeItems(TreeItem parentNode, List<Item> children)
   {
      parentNode.removeItems();
      if (children == null)
      {
         return;
      }
      for (Item item : children)
      {
         TreeItem newNode = getNode(item);
         newNode.setUserObject(item);
         parentNode.addItem(newNode);
         if (item instanceof Folder && ((Folder)item).getChildren() != null)
         {
            fillTreeItems(newNode, ((Folder)item).getChildren());
         }
      }
   }

   /**
    * Fill root of the tree with pointed items.
    * 
    * @param children 
    */
   private void fillTreeItems(List<Item> children)
   {
      tree.removeItems();
      if (children == null)
      {
         return;
      }
      for (Item item : children)
      {
         TreeItem newNode = getNode(item);
         newNode.setUserObject(item);
         tree.addItem(newNode);
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
    * @return {@link TreeItem}
    */
   private TreeItem getNode(Item item)
   {
      String icon = (item instanceof Folder && item.getIcon() == null) ? Images.ClassPath.FOLDER : item.getIcon();

      TreeItem node = new TreeItem(createItemWidget(icon, item.getName()));
      node.setUserObject(item);
      if (item instanceof Folder)
      {
         // TODO fix this 
         node.addItem("");
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
      List<Item> items = new ArrayList<Item>();
      if (tree.getSelectedItem() != null)
         items.add((Item)tree.getSelectedItem().getUserObject());
      return items;
   }
}
