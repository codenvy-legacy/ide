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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: IdeTree Mar 14, 2011 4:00:06 PM evgen $
 *
 */
public class ItemTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item>
{
   private Map<String, String> locktokens;

   private String id;

   private String prefixId;

   private boolean expandProjects = true;

   public ItemTree()
   {
   }

   /**
    * @param id of UI component
    * @param prefixId prefix for child element ID
    */
   public ItemTree(String id, String prefixId)
   {
      getElement().setId(id);
      this.prefixId = prefixId;
   }

   public boolean isExpandProjects()
   {
      return expandProjects;
   }

   public void setExpandProjects(boolean expandProjects)
   {
      this.expandProjects = expandProjects;
   }

   protected Widget createItemWidget(ImageResource icon, String text)
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
   private TreeItem getNodeById(String id)
   {
      TreeItem node = tree.getItem(0);
      if (((Item)node.getUserObject()).getId().equals(id))
      {
         node.setState(true, false);
         return node;
      }

      node = getChild(node, id);
      return node;
   }

   /**
    * Work throught children on tree item and compare with pathSplit.
    * <p/>
    * If last part of child path (substring after last /) equals to pathSplit,
    * return tree item.
    * @param parent
    * @param id
    * @return
    */
   private TreeItem getChild(TreeItem parent, String id)
   {
      for (int i = 0; i < parent.getChildCount(); i++)
      {
         TreeItem child = parent.getChild(i);
         if (child.getUserObject() == null)
         {
            continue;
         }

         Item userObject = (Item)child.getUserObject();
         if (userObject.getId().equals(id))
         {
            parent.setState(true, false);
            return child;
         }

         if (userObject instanceof FolderModel || userObject instanceof ProjectModel)
         {
            TreeItem child2 = getChild(child, id);
            if (child2 != null)
            {
               parent.setState(true, false);
               child.setState(true, false);
               return child2;
            }
         }
      }

      return null;
   }

   private boolean updateValue = false;

   public void setUpdateValue(boolean updateValue)
   {
      this.updateValue = updateValue;
   }

   public boolean isUpdateValue()
   {
      return updateValue;
   }

   private void setItems(TreeItem parentNode, List<Item> children)
   {
      parentNode.removeItems();

      if (children.size() == 0)
      {
         parentNode.setState(false);
         return;
      }

      for (int position = 0; position < children.size(); position++)
      {
         Item item = children.get(position);

         if (DirectoryFilter.get().matchWithPattern(item.getName()))
         {
            continue;
         }

         //         if (item.getName() != null && item.getName().startsWith("."))
         //         {
         //            continue;
         //         }

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

   private void updateItems(TreeItem parentNode, List<Item> children)
   {
      if (children.size() == 0)
      {
         parentNode.removeItems();
         parentNode.setState(false);
         return;
      }

      removeEmptyTreeNodes(parentNode);

      // check for new items in new list children
      int position = 0;
      for (Item item : children)
      {
         if (item.getName() != null && item.getName().startsWith("."))
         {
            continue;
         }
         if (!hasChild(parentNode, item.getId()))
         {
            TreeItem node = createTreeNode(item);
            parentNode.insertItem(position, node);
            position++;
         }
      }

      // check for items which are presents in tree and not presents in list of children
      /*
       * will be in future
       */

      //to avoid send open event (thus extra refresh folder is not done)  
      parentNode.setState(true, false);
   }

   private void removeEmptyTreeNodes(TreeItem parent)
   {
      for (int i = 0; i < parent.getChildCount();)
      {
         TreeItem item = parent.getChild(i);
         if (item.getText() == null || item.getText().isEmpty())
         {
            item.remove();
         }
         else
         {
            i++;
         }
      }
   }

   private boolean hasChild(TreeItem parentNode, String childrenId)
   {
      for (int i = 0; i < parentNode.getChildCount(); i++)
      {
         TreeItem item = parentNode.getChild(i);
         if (item.getUserObject() != null)
         {
            Item object = (Item)item.getUserObject();
            if (childrenId.equals(object.getId()))
            {
               return true;
            }
         }
      }

      return false;
   }

   private TreeItem createTreeNode(Item item)
   {
      TreeItem node = new TreeItem(createItemWidget(getItemIcon(item), getTitle(item)));
      node.setUserObject(item);

      if (item instanceof FolderModel)
      {
         node.addItem("");
      }
      else if (item instanceof ProjectModel && expandProjects)
      {
         node.addItem("");
      }

      node.getElement().setId(prefixId + Utils.md5(item.getPath()));
      return node;
   }

   /**
    * Select icon for item
    * @param item
    * @return {@link ImageResource} of item icon
    */
   public ImageResource getItemIcon(Item item)
   {
      if (item instanceof ProjectModel)
      {
         return ProjectResolver.getImageForProject(((ProjectModel)item).getProjectType());
      }
      else
         return ImageUtil.getIcon(item.getMimeType());
   }

   private String getTitle(Item item)
   {
      String title = "";

      if (locktokens == null || locktokens.isEmpty())
      {
         return (item.getName() == null || item.getName().isEmpty()) ? "/" : item.getName();
      }

      if (item instanceof FileModel && ((FileModel)item).isLocked())
      {
         if (!locktokens.containsKey(item.getId()))
         {
            title +=
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-11px; margin-top:3px;\"  border=\"0\" suppress=\"TRUE\" src=\""
                  + UIHelper.getGadgetImagesURL() + "navigation/lock.png" + "\" />&nbsp;&nbsp;";
         }
      }
      title += item.getName().isEmpty() ? "/" : item.getName();

      return title;
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.Tree#doUpdateValue()
    */
   @Override
   public void doUpdateValue()
   {
      /*
       * If value == null - clear tree.
       */
      if (value == null)
      {
         if (tree.getItemCount() > 0)
         {
            tree.removeItems();
         }

         return;
      }

      /*
       * Create root node if tree has not it. 
       */
      if (tree.getItemCount() == 0)
      {
         TreeItem addItem = createTreeNode(value);
         tree.addItem(addItem);
      }

      ItemList<Item> children =
         (value instanceof ProjectModel) ? ((ProjectModel)value).getChildren() : ((FolderModel)value).getChildren();

      /*
       * Return if children are not defined.
       */
      if (children == null)
      {
         return;
      }

      /*
       * Return if new value is not a children of root node.
       */
      Folder rootFolder = (Folder)tree.getItem(0).getUserObject();
      String rootFolderHref = rootFolder.getPath();
      if (!value.getPath().startsWith(rootFolderHref))
      {
         return;
      }

      TreeItem parent = getNodeById(value.getId());
      try
      {
         List<Item> filteredItems = DirectoryFilter.get().filter(children.getItems());

         if (updateValue)
         {
            updateItems(parent, filteredItems);
         }
         else
         {
            setItems(parent, filteredItems);
         }

         if (tree.getSelectedItem() != null)
         {
            moveHighlight(tree.getSelectedItem());
         }
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Select item by itemId
    * 
    * @param itemId
    * @return <b>true</b> if item was found and selected, <b>false</b> otherwise
    */
   public boolean selectItem(String itemId)
   {
      TreeItem item = getNodeById(itemId);
      if (item != null)
      {
         tree.setSelectedItem(item, true);
         return true;
      }

      return false;
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
   public void updateFileState(FileModel file)
   {
      TreeItem fileNode = getNodeById(file.getId());
      if (fileNode == null)
      {
         return;
      }
      TreeItem parentItem = fileNode.getParentItem();
      parentItem.removeItem(fileNode);
      fileNode = createTreeNode(file);
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
      TreeItem item = getNodeById(path);
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
   public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons)
   {
      for (Item item : itemsIcons.keySet())
      {
         TreeItem node = getNodeById(item.getId());
         if (node == null)
         {
            continue;
         }
         Grid grid = (Grid)node.getWidget();
         TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
         Map<TreeIconPosition, ImageResource> map = itemsIcons.get(item);
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
         TreeItem node = getNodeById(item.getId());
         Grid grid = (Grid)node.getWidget();
         TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
         treeIcon.removeIcon(itemsIcons.get(item));
      }
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
      getElement().setId(id);
   }

   public void setTreeGridId(String id)
   {
      this.id = id;
      getElement().setId(id);
   }

   public String getPrefixId()
   {
      return prefixId;
   }

   public void setPrefixId(String prefixId)
   {
      this.prefixId = prefixId;
   }

}
