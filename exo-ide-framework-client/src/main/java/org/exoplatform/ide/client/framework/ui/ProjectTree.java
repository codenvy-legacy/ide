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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: IdeTree Mar 14, 2011 4:00:06 PM evgen $
 * 
 */
public class ProjectTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item> implements OpenHandler<Item>
{
   
   private CellTree.Resources RESOURCES = GWT.create(CellTree.Resources.class);
   
   private Map<String, String> locktokens = new HashMap<String, String>();

   private String id;

   private String prefixId;

//   private boolean expandProjects = true;

   public ProjectTree()
   {
      sinkEvents(Event.ONCONTEXTMENU);
//      addCloseHandler(CLOSE_HANDLER);
      addOpenHandler(this);
   }

   /**
    * @param id of UI component
    * @param prefixId prefix for child element ID
    */
   public ProjectTree(String id, String prefixId)
   {
      getElement().setId(id);
      this.prefixId = prefixId;

      sinkEvents(Event.ONCONTEXTMENU);
//      addCloseHandler(CLOSE_HANDLER);
      addOpenHandler(this);
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

   public void setTreeGridId(final String id)
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

   /**
    * @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event)
    */
   @Override
   public void onBrowserEvent(Event event)
   {
      if (Event.ONCONTEXTMENU == DOM.eventGetType(event))
      {
         NativeEvent nativeEvent =
            Document.get().createMouseDownEvent(-1, event.getScreenX(), event.getScreenY(), event.getClientX(),
               event.getClientY(), event.getCtrlKey(), event.getAltKey(), event.getShiftKey(), event.getMetaKey(),
               NativeEvent.BUTTON_LEFT);
         DOM.eventGetTarget(event).dispatchEvent(nativeEvent);
      }
      
      super.onBrowserEvent(event);
   }

   
   private ProjectModel project;
     
   private Map<String, ProjectTreeItem> treeItems = new HashMap<String, ProjectTreeItem>();
   
   @Override
   public void doUpdateValue()
   {
      if (value == null)
      {
         project = null;
         tree.removeItems();
         treeItems.clear();
         return;
      }
      
      if (project == null && !(value instanceof ProjectModel))
      {
         return;
      }
      
      if (project == null)
      {
         project = (ProjectModel)value;
         ProjectTreeItem item = new ProjectTreeItem(value, prefixId, locktokens);
         tree.addItem(item);
         treeItems.put(value.getId(), item);
      }

      if (!treeItems.containsKey(value.getId()))
      {
         return;
      }
      
      if (!(value instanceof FolderModel))
      {
         return;
      }
      
      ItemList<Item> children = ((FolderModel)value).getChildren();
      if (children.getItems().isEmpty())
      {
         return;
      }

      ProjectTreeItem parentTreeItem = treeItems.get(value.getId());
      parentTreeItem.setState(true, false);
      removeChildren(parentTreeItem);
      parentTreeItem.setUserObject(value);
      parentTreeItem.render();
      
      Collections.sort(children.getItems(), comparator);
      List<Item> filteredItems = DirectoryFilter.get().filter(children.getItems());      
      
      //parentTreeItem.setState(false, false);
      for (Item item : filteredItems)
      {
         ProjectTreeItem treeItem = new ProjectTreeItem(item, prefixId, locktokens);
         parentTreeItem.addItem(treeItem);
         treeItems.put(item.getId(), treeItem);
      }
      //parentTreeItem.setState(true, false);
      
      if (tree.getSelectedItem() != null)
      {
         moveHighlight(tree.getSelectedItem());
      }      
   }
      
   @Override
   public void onOpen(OpenEvent<Item> event)
   {
      Folder folder = (Folder)event.getTarget();
      setValue(folder);
   }
   
   
   private void removeChildren(TreeItem treeItem)
   {
      for (int i = 0; i < treeItem.getChildCount(); i++)
      {
         TreeItem child = treeItem.getChild(i);
         if (!(child instanceof ProjectTreeItem))
         {
            continue;
         }
         
         if (child.getUserObject() instanceof FolderModel)
         {
            removeChildren(child);
         }
         
         String childId = ((Item)child.getUserObject()).getId();
         treeItems.remove(childId);         
      }
      
      treeItem.removeItems();
   }
   
   
//   private CloseHandler<Item> CLOSE_HANDLER = new CloseHandler<Item>()
//   {
//      @Override
//      public void onClose(CloseEvent<Item> event)
//      {
//         System.out.println("ProjectTree -> CLOSE HANDLER -> onClose");
////         final Folder target = (Folder)event.getTarget();
////         System.out.println("folder id > " + target.getPath());
////
////         Scheduler.get().scheduleDeferred(new ScheduledCommand()
////         {
////            @Override
////            public void execute()
////            {
////               System.out.println("ProjectTree >>>>>> Clearing children......................");
////               
////               TreeItem treeItem = treeItems.get(target.getId());
////               if (treeItem != null)
////               {
////                  removeChildren(treeItem);
////                  treeItem.addItem("");
////               }
////            }
////         });
//      }
//   };
//   
   
   

   /**
    * Comparator for comparing items in received directory.
    */
   private Comparator<Item> comparator = new Comparator<Item>()
   {
      public int compare(Item item1, Item item2)
      {
         if (item1 instanceof Folder && item2 instanceof FileModel)
         {
            return -1;
         }
         else if (item1 instanceof File && item2 instanceof Folder)
         {
            return 1;
         }
         return item1.getName().compareTo(item2.getName());
      }
   };
   
   
   private Item getChild(Folder parent, String name) throws Exception
   {
      ItemList<Item> itemList = null;
      
      if (parent instanceof FolderModel)
      {
         itemList = ((FolderModel)parent).getChildren();
      }
      else if (parent instanceof ProjectModel)
      {
         itemList = ((ProjectModel)parent).getChildren();
      }
      
      if (itemList == null)
      {
         throw new Exception("Item " + parent.getPath() + " is not a folder");
      }
      
      for (Item item : itemList.getItems())
      {
         if (item.getName().equals(name))
         {
            return item;
         }
      }
      
      throw new Exception("Item " + name + " not found in folder " + parent.getPath());
   }
   
   
   
   public void navigateToItem(Item item)
   {
      if (project == null)
      {
         return;
      }
      
      try
      {
         String []parts = item.getPath().split("/");         
         String path = "/" + parts[1];

         Folder parent = project;
         List<Item> items = new ArrayList<Item>();
         
         for (int i = 2; i < parts.length; i++)
         {
            path += "/" + parts[i];
            Item child = getChild(parent, parts[i]);
            if (child instanceof Folder)
            {
               parent = (Folder)child;
               items.add(child);
            }
         }
         
         for (Item i : items)
         {
            setValue(i);
         }
         
         boolean selected = selectItem(item.getId());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   
   
   /**
   * Get all selected items
   * 
   * @return List of selected items
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
   * Select item by itemId
   * 
   * @param itemId
   * @return <b>true</b> if item was found and selected, <b>false</b> otherwise
   */
   public boolean selectItem(String itemId)
   {
      ProjectTreeItem treeItem = treeItems.get(itemId);
      if (treeItem == null) 
      {
         return false;
      }
      
      tree.setSelectedItem(treeItem, true);
      tree.ensureSelectedItemVisible();
      
      if (project != null && itemId.equals(project))
      {
         return true;
      }
      
      return treeItem.getParentItem() != null;
   }  
   
   
   /**
   * Remove selection of Item
   * 
   * @param itemId Item ID
   */
   public void deselectItem(String itemId)
   {
      ProjectTreeItem treeItem = treeItems.get(itemId);
      if (treeItem != null) 
      {
         tree.setSelectedItem(treeItem, true);      
      }
   }   
   
   /**
   * @param file
   */
   public void updateFileState(FileModel file)
   {
      
      
//      System.out.println("ProjectItemTree.updateFileState()");
      
      //   TreeItem fileNode = getNodeById(file.getId());
      //   if (fileNode == null)
      //   {
      //      return;
      //   }
      //   TreeItem parentItem = fileNode.getParentItem();
      //   parentItem.removeItem(fileNode);
      //   fileNode = createTreeNode(file);
      //   parentItem.addItem(fileNode);
      //
      //   fileNode.setState(true);
   } 
   
   /**
   * Set lock token map
   * 
   * @param lockTokens
   */
   public void setLockTokens(Map<String, String> lockTokens)
   {
//      System.out.println("ProjectItemTree.setLockTokens()");
      
      this.locktokens.clear();
      
      if (locktokens != null)
      {
         this.locktokens.putAll(lockTokens);
      }      
   } 
   
   /**
   * Add info icons to Item main icon
   * 
   * @param itemsIcons Map of Item, info icon position and info icon URL
   */
   public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons)
   {
//      System.out.println("ProjectItemTree.addItemsIcons()");
      
      //   for (Item item : itemsIcons.keySet())
      //   {
      //      TreeItem node = getNodeById(item.getId());
      //      if (node == null)
      //      {
      //         continue;
      //      }
      //      Grid grid = (Grid)node.getWidget();
      //      TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
      //      Map<TreeIconPosition, ImageResource> map = itemsIcons.get(item);
      //      for (TreeIconPosition position : map.keySet())
      //      {
      //         treeIcon.addIcon(position, map.get(position));
      //      }
      //
      //   }
   } 

   /**
   * Remove info icon from Item main icon
   * 
   * @param itemsIcons Map of item and position of info icon
   */
   public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons)
   {
//      System.out.println("ProjectItemTree.removeItemIcons()");
      
      //   for (Item item : itemsIcons.keySet())
      //   {
      //      TreeItem node = getNodeById(item.getId());
      //      Grid grid = (Grid)node.getWidget();
      //      TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
      //      treeIcon.removeIcon(itemsIcons.get(item));
      //   }
   }
   
   public void setUpdateValue(boolean updateValue)
   {
//      System.out.println("ProjectItemTree.setUpdateValue()");
      
      //    this.updateValue = updateValue;
   }

   /**
   * @param folder
   * @param isOpens
   */
   public void changeFolderIcon(Folder folder, boolean isOpens)
   {
//      System.out.println("ProjectItemTree.changeFolderIcon()");
      
      //   TreeItem node = getNodeById(folder.getId());
      //   if (node == null)
      //   {
      //      return;
      //   }
      //   Grid grid = (Grid)node.getWidget();
      //   TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
      //   if(isOpens)
      //   {
      //      treeIcon.setIcon(res.cellTreeLoading());
      //   }
      //   else
      //      treeIcon.setIcon(getItemIcon(folder));
   }

   
}
