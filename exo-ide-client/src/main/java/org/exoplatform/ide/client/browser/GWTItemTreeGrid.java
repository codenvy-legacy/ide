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
package org.exoplatform.ide.client.browser;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.tree.Tree;
import org.exoplatform.gwtframework.ui.client.tree.TreeNode;
import org.exoplatform.gwtframework.ui.client.tree.TreeRecord;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTItemTreeGrid extends Layout implements TreeGridItem<Item>
{

   private ItemsTree tree = new ItemsTree();

   public GWTItemTreeGrid()
   {
      setOverflow(Overflow.HIDDEN);

      tree.setAllowMultiSelect(true);
      
      addMember(tree);

      addResizedHandler(resizedHandler);
   }
   
   private ResizedHandler resizedHandler = new ResizedHandler() {
      public void onResized(ResizedEvent event)
      {
         tree.setWidth(getWidth() + "px");
         tree.setHeight(getHeight() + "px");

         simpleHack3rdParentWidth(tree.getElement(), getWidth() - 2);

         tree.updateRowsWidth(getWidth());
      }
   };

   /**
    * In case using Smartgwt over GWT there is some bug.
    * Layout has many nodes in his DOM structure. After updating of the width of layout not all his children will update their width.
    * The simple way is search third parent of menu and update his width. 
    * 
    * @param menuElement
    * @param width
    */
   protected void simpleHack3rdParentWidth(Element menuElement, int width)
   {
      try
      {
         Element element = menuElement;
         Element p1 = DOM.getParent(element);
         Element p2 = DOM.getParent(p1);
         Element p3 = DOM.getParent(p2);
         DOM.setStyleAttribute(p3, "width", "" + width + "px");
      }
      catch (Exception exc)
      {
      }
   }

   public Item getValue()
   {
      return null;
   }

   public void setValue(Item value)
   {
      try
      {
         if (value == null)
         {
            tree.setRoot(null);
            return;
         }

         if (tree.getRoot() == null)
         {
            TreeNode rootNode = new TreeNode(value.getName(), value.getIcon(), value);
            tree.setRoot(rootNode);

         }
         else
         {
            TreeRecord parentRecord = getRecord(tree.getRootRecord(), value.getHref());
            if (parentRecord == null)
            {
               return;
            }

            parentRecord.getNode().getChildren().clear();

            Folder folder = (Folder)value;
            if (folder.getChildren() != null)
            {
               for (Item i : folder.getChildren())
               {
                  TreeNode node = new TreeNode(i.getName(), i.getIcon(), i);
                  if (!(i instanceof Folder))
                  {
                     node.setIsFolder(false);
                  }
                  parentRecord.getNode().getChildren().add(node);
               }
            }

            parentRecord.refreshSubtree();
         }

      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }

   }

   private TreeRecord getRecord(TreeRecord parent, String href)
   {
      Item item = (Item)parent.getNode().getEntry();

      if (item.getHref().equals(href))
      {
         return parent;
      }

      for (TreeRecord child : parent.getChildren())
      {
         TreeRecord r = getRecord(child, href);
         if (r != null)
         {
            return r;
         }
      }

      return null;
   }

//   private TreeNode getParentNode(TreeNode node, String href)
//   {
//      Item entry = (Item)node.getEntry();
//      if (entry.getHref().equals(href))
//      {
//         return node;
//      }
//      else
//      {
//         return null;
//      }
//   }

   public void setValue(Item value, boolean fireEvent)
   {
      setValue(value);
   }

   public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Item> arg0)
   {
      return null;
   }

   private List<OpenHandler<Item>> openHandlers = new ArrayList<OpenHandler<Item>>();

   private List<SelectionHandler<Item>> selectionHandlers = new ArrayList<SelectionHandler<Item>>();

   public HandlerRegistration addOpenHandler(OpenHandler<Item> openHandler)
   {
      openHandlers.add(openHandler);
      return new OpenHandlerRegistration(openHandler);
   }

   public HandlerRegistration addSelectionHandler(SelectionHandler<Item> selectionHandler)
   {
      selectionHandlers.add(selectionHandler);
      return new SelectionHandlerRegistration(selectionHandler);
   }

   public HandlerRegistration addClickHandler(ClickHandler arg0)
   {
      return null;
   }

   public HandlerRegistration addDoubleClickHandler(DoubleClickHandler arg0)
   {
      return null;
   }

   public void setEmptyMessage(String emptyMessage)
   {
      //setEmptyMessage(emptyMessage);
   }

   private class OpenHandlerRegistration implements HandlerRegistration
   {

      private OpenHandler<Item> openHandler;

      public OpenHandlerRegistration(OpenHandler<Item> openHandler)
      {
         this.openHandler = openHandler;
      }

      public void removeHandler()
      {
         openHandlers.remove(openHandler);
      }
   }

   private class SelectionHandlerRegistration implements HandlerRegistration
   {

      private SelectionHandler<Item> selectionHandler;

      public SelectionHandlerRegistration(SelectionHandler<Item> selectionHandler)
      {
         this.selectionHandler = selectionHandler;
      }

      public void removeHandler()
      {
         selectionHandlers.remove(selectionHandler);
      }

   }

   private class SelectionEventImpl extends SelectionEvent<Item>
   {

      public SelectionEventImpl(Item selectedItem)
      {
         super(selectedItem);
      }

   }

   private class OpenEventImpl extends OpenEvent<Item>
   {

      protected OpenEventImpl(Item item)
      {
         super(item);
      }

   }

   private class ItemsTree extends Tree
   {
      @Override
      public void onClick(TreeRecord treerecord)
      {
         super.onClick(treerecord);

         Item item = (Item)treerecord.getNode().getEntry();

         for (SelectionHandler<Item> h : selectionHandlers)
         {
            h.onSelection(new SelectionEventImpl(item));
         }

      }

      @Override
      public void onExpand(TreeRecord treeRecord)
      {
         treeRecord.getChildren().clear();
         treeRecord.getNode().getChildren().clear();

         super.onExpand(treeRecord);

         for (OpenHandler<Item> openHandler : openHandlers)
         {
            openHandler.onOpen(new OpenEventImpl((Item)treeRecord.getNode().getEntry()));
         }

      }
   }

   public List<Item> getSelectedItems()
   {
      List<Item> selectedItems = new ArrayList<Item>();

      for (TreeRecord record : tree.getSelectedRecords())
      {
         Item item = (Item)record.getNode().getEntry();
         selectedItems.add(item);
      }

      return selectedItems;
   }

   /**
    * @see com.google.gwt.event.dom.client.HasKeyPressHandlers#addKeyPressHandler(com.google.gwt.event.dom.client.KeyPressHandler)
    */
   public HandlerRegistration addKeyPressHandler(KeyPressHandler handler)
   {
      return null;
   }

}
