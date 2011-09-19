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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.groovy.client.GroovyClientBundle;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TreeItem;

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
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      TreeItem node = findTreeNode(getValue().getId());
      if (node != null && getValue() instanceof FolderModel)
      {
         fillTreeItems(node, ((FolderModel)getValue()).getChildren().getItems());
         node.setState(true, true);
      }
      else
      {
         fillTreeItems(((FolderModel)getValue()).getChildren().getItems());
      }

   }

   /**
    * Find tree node by href.
    * 
    * @param id
    * @return {@link TreeItem}
    */
   public TreeItem findTreeNode(String id)
   {
      for (int i = 0; i < tree.getItemCount(); i++)
      {
         TreeItem child = tree.getItem(i);
         if (child.getUserObject() == null)
            continue;
         if (id.equals(((Item)child.getUserObject()).getId()))
         {
            return child;
         }
         TreeItem item = getChild(child, id);
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
   private TreeItem getChild(TreeItem parent, String id)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem child = parent.getChild(i);
         if (child.getUserObject() == null)
            continue;
         if (id.equals(((Item)child.getUserObject()).getId()))
         {
            return child;
         }
         TreeItem item = getChild(child, id);
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
         if (item instanceof FolderModel && ((FolderModel)item).getChildren() != null)
         {
            fillTreeItems(newNode, ((FolderModel)item).getChildren().getItems());
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
         if (item instanceof FolderModel && ((FolderModel)item).getChildren() != null)
         {
            fillTreeItems(newNode, ((FolderModel)item).getChildren().getItems());
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
      ImageResource icon;
      try
      {
         icon = (item instanceof FolderModel) ? GroovyClientBundle.INSTANCE.folder() : IDE.getInstance().getEditor(item.getMimeType()).getIcon();
      }
      catch (EditorNotFoundException e)
      {
         e.printStackTrace();
         icon = GroovyClientBundle.INSTANCE.folder();
      }

      TreeItem node = new TreeItem(createTreeNodeWidget(new Image(icon), item.getName()));
      node.setUserObject(item);
      if (item instanceof FolderModel)
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
