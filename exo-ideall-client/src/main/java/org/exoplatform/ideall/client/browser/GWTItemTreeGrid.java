/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.browser;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.tree.GWTTree;
import org.exoplatform.gwtframework.ui.client.component.tree.TreeRecord;
import org.exoplatform.gwtframework.ui.client.component.tree.bean.TreeNode;
import org.exoplatform.ideall.client.module.vfs.api.Folder;
import org.exoplatform.ideall.client.module.vfs.api.Item;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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

   private Tree tree = new Tree();

   public GWTItemTreeGrid()
   {
      setOverflow(Overflow.HIDDEN);
      System.out.println("GWTItemTreeGrid.GWTItemTreeGrid()");

      tree.setAllowMultiSelect(true);
      
      addMember(tree);

      addResizedHandler(resizedHandler);
   }
   
   private ResizedHandler resizedHandler = new ResizedHandler() {
      public void onResized(ResizedEvent event)
      {
         System.out.println("resized to width: " + getWidth() + " height: " + getHeight());
         
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

   private Item value;

   public void setValue(Item value)
   {

      try
      {

         System.out.println("GWTItemTreeGrid.setValue()");
         System.out.println("value " + value);

         if (value == null)
         {
            tree.setRoot(null);
            return;
         }

         if (tree.getRoot() == null)
         {
            System.out.println("init root!");
            this.value = value;

            System.out.println("value > " + value);

            TreeNode rootNode = new TreeNode(value.getName(), value.getIcon(), value);
            //TreeNode rootNode = new TreeNode(value.getName(), value);
            tree.setRoot(rootNode);

         }
         else
         {
            System.out.println("adding subtree");

            System.out.println("value href > " + value.getHref());

            TreeRecord parentRecord = getRecord(tree.getRootRecord(), value.getHref());
            //System.out.println("using parent record > " + parentRecord);
            if (parentRecord == null)
            {
               return;
            }

            System.out.println("parent record node > " + parentRecord.getNode());

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

      System.out.println("checking children...");
      for (TreeRecord child : parent.getChildren())
      {
         //System.out.println("chlid > " + child);
         TreeRecord r = getRecord(child, href);
         if (r != null)
         {
            return r;
         }
      }

      return null;
   }

   private TreeNode getParentNode(TreeNode node, String href)
   {
      Item entry = (Item)node.getEntry();
      if (entry.getHref().equals(href))
      {
         System.out.println("equals. returning... " + entry.getHref());
         return node;
      }
      else
      {
         System.out.println("not equals..........");
         return null;
      }
   }

   public void setValue(Item arg0, boolean fireEvent)
   {
   }

   public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Item> arg0)
   {
      System.out.println("GWTItemTreeGrid.addValueChangeHandler()");
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
      System.out.println("GWTItemTreeGrid.addClickHandler()");
      return null;
   }

   public HandlerRegistration addDoubleClickHandler(DoubleClickHandler arg0)
   {
      System.out.println("GWTItemTreeGrid.addDoubleClickHandler()");
      return null;
   }

   public void setEmptyMessage(String emptyMessage)
   {
      //setEmptyMessage(emptyMessage);
   }

   private void onItemClicked()
   {
      System.out.println("GWTItemTreeGrid.onItemClicked()");

      //      if (getSelectedRecord() == null) {
      //         return;
      //      }
      //      
      //      Item selectedItem = (Item)getSelectedRecord().getNode().getEntry();
      //      for (SelectionHandler<Item> selectionHandler : selectionHandlers) {
      //         selectionHandler.onSelection(new SelectionEventImpl(selectedItem));
      //      }

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

      private Item selectedItem;

      public SelectionEventImpl(Item selectedItem)
      {
         super(selectedItem);
      }

   }

   private class OpenEventImpl extends OpenEvent<Item>
   {

      private Item item;

      protected OpenEventImpl(Item item)
      {
         super(item);
      }

   }

   private class Tree extends GWTTree
   {
      @Override
      public void onClick(TreeRecord treerecord)
      {
         super.onClick(treerecord);

         System.out.println("item selected");

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
      System.out.println("GWTItemTreeGrid.getSelectedItems()");

      List<Item> selectedItems = new ArrayList<Item>();

      for (TreeRecord record : tree.getSelectedRecords())
      {
         Item item = (Item)record.getNode().getEntry();
         selectedItems.add(item);
      }

      return selectedItems;
   }

}
