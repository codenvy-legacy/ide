/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.navigation.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: IdeTree Mar 14, 2011 4:00:06 PM evgen $
 *
 */
public class ItemTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item>
{
   private Map<String, String> locktokens;

   private String prefixId;

   /**
    * @param id of UI component
    * @param prefixId prefix for child element ID
    */
   public ItemTree(String id, String prefixId)
   {
      getElement().setId(id);
      this.prefixId = prefixId + "-";
      tree.addCloseHandler(new CloseHandler<TreeItem>()
      {

         @Override
         public void onClose(CloseEvent<TreeItem> event)
         {
            // Close event don't remove child TtreeItem, just set display:node CSS property
            // so for Selenium test remove id attribute in invisible TreeItem, and remove all child TreeItem
            TreeItem treeItem = event.getTarget();
            for (int i = 0; i < treeItem.getChildCount(); i++)
            {
               treeItem.getChild(i).getElement().removeAttribute("id");
               treeItem.getChild(i).removeItems();
            }
         }
      });
   }

   @Override
   protected Widget createItemWidget(String icon, String text)
   {
      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      //      Image i = new Image(icon);
      TreeIcon i = new TreeIcon(icon);
      i.setHeight("16px");
      grid.setWidget(0, 0, i);
      //      Label l = new Label(text, false);
      HTMLPanel l = new HTMLPanel("div", text);
      l.setStyleName("ide-Tree-label");
      grid.setWidget(0, 1, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");
      //      grid.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
      DOM.setStyleAttribute(grid.getElement(), "display", "block");
      return grid;
   }

   /**
    * @param item
    */
   private TreeItem getNodeByHref(String href)
   {
      Folder rootFolder = (Folder)tree.getItem(0).getUserObject();
      String path = href.substring(rootFolder.getHref().length());

      if (path.length() > 0 && path.charAt(0) == '/')
      {
         path = path.substring(1);
      }

      if ("".equals(path))
      {
         return tree.getItem(0);
      }

      String[] names = path.split("/");
      TreeItem node = tree.getItem(0);
      for (String folderName : names)
      {
         node = getChild(node, folderName);
         if (node == null)
         {
            return node;
         }
      }
      return node;
   }

   private TreeItem getChild(TreeItem parent, String name)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem child = parent.getChild(i);
         if (child.getUserObject() == null)
         {
            continue;
         }

         Item userObject = (Item)child.getUserObject();
         if (userObject.getName().equals(name))
         {
            return child;
         }
      }

      return null;
   }

   private void setItems(TreeItem parentNode, List<Item> children)
   {
      if (children.size() == 0)
      {
         parentNode.removeItems();
         parentNode.setState(false);
         return;
      }

      parentNode.removeItems();
      for (int position = 0; position < children.size(); position++)
      {
         Item item = children.get(position);

         TreeItem node = createTreeNode(item);
         parentNode.addItem(node);

         if (parentNode.getChild(0).getUserObject() == null)
         {
            parentNode.getChild(0).remove();
         }
      }
      
       //to avoid send open event (thus extra refresh folder is not done)  
      parentNode.setState(true, false);
   }

   private TreeItem createTreeNode(Item item)
   {
      String text = "";
      if (item.getIcon() != null)
      {
         text = item.getIcon();
      }
      else
      {
         text = Images.FileTypes.FOLDER;
      }

      TreeItem node = new TreeItem(createItemWidget(text, getTitle(item)));

      node.setUserObject(item);
      if (item instanceof Folder)
      {
         // TODO fix this 
         node.addItem("");
      }

      node.getElement().setId(prefixId + Utils.md5(item.getHref()));

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
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-11px; margin-top:3px;\"  border=\"0\" suppress=\"TRUE\" src=\""
                  + UIHelper.getGadgetImagesURL() + "navigation/lock.png" + "\" />&nbsp;&nbsp;";
         }
      }
      title += item.getName();

      return title;
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      if (value == null)
      {
         if (tree.getItemCount() > 0)
            tree.removeItems();
         return;
      }

      if (tree.getItemCount() == 0)
      {
         TreeItem addItem = createTreeNode(value);
         tree.addItem(addItem);
         if (((Folder)value).getChildren() == null)
            return;
      }

      if (((Folder)value).getChildren() == null)
      {
         return;
      }

      Folder rootFolder = (Folder)tree.getItem(0).getUserObject();

      String rootFolderHref = rootFolder.getHref();
      if (!value.getHref().startsWith(rootFolderHref))
      {
         return;
      }

      TreeItem parent = getNodeByHref(value.getHref());
      try
      {
         setItems(parent, ((Folder)value).getChildren());
         if (tree.getSelectedItem() != null)
         {
            moveHighlight(tree.getSelectedItem());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   /**
    * Select item by path
    * @param path
    */
   public void selectItem(String path)
   {
      TreeItem item = getNodeByHref(path);
      if (item != null)
      {
         tree.setSelectedItem(item, true);
      }
   }

   /**
    * Get all selected items
    * @return  List of selected items
    */
   public List<Item> getSelectedItems()
   {
      List<Item> items = new ArrayList<Item>();
      if (tree.getSelectedItem() != null)
      {
         items.add((Item)tree.getSelectedItem().getUserObject());
      }
      return items;
   }

   /**
    * @param file
    */
   public void updateFileState(File file)
   {
      TreeItem fileNode = getNodeByHref(file.getHref());
      if (fileNode == null)
      {
         return;
      }
      TreeItem parentItem = fileNode.getParentItem();
      parentItem.removeItem(fileNode);
      fileNode = new TreeItem(createItemWidget(file.getIcon(), getTitle(file)));
      parentItem.addItem(fileNode);

      fileNode.setState(true);
   }

   /**
    * Set lock token map
    * @param locktokens
    */
   public void setLocktokens(Map<String, String> locktokens)
   {
      this.locktokens = locktokens;
   }

   /**
    * Remove selection by Item path
    * @param path Item path
    */
   public void deselectItem(String path)
   {
      TreeItem item = getNodeByHref(path);
      if (item != null)
      {
         tree.setSelectedItem(item, false);
      }
   }

   /**
    * Remove selection from all selected items
    */
   public void deselectAllRecords()
   {
      if (tree.getSelectedItem() != null)
         tree.getSelectedItem().setSelected(false);
   }

   /**
    * Add info icons to Item main icon
    * @param itemsIcons Map of Item, info icon position and info icon URL
    */
   public void addItemsIcons(Map<Item, Map<TreeIconPosition, String>> itemsIcons)
   {
      for (Item item : itemsIcons.keySet())
      {
         TreeItem node = getNodeByHref(item.getHref());
         Grid grid = (Grid)node.getWidget();
         TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
         Map<TreeIconPosition, String> map = itemsIcons.get(item);
         for (TreeIconPosition position : map.keySet())
         {
            treeIcon.addIcon(position, map.get(position));
         }

      }
   }

   /**
    * Remove info icon from Item main icon
    * @param itemsIcons Map of item and position of info icon
    */
   public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons)
   {
      for (Item item : itemsIcons.keySet())
      {
         TreeItem node = getNodeByHref(item.getHref());
         Grid grid = (Grid)node.getWidget();
         TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
         treeIcon.removeIcon(itemsIcons.get(item));
      }
   }

}
