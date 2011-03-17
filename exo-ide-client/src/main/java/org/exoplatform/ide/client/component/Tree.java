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
package org.exoplatform.ide.client.component;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.event.CloseEventImpl;
import org.exoplatform.gwtframework.ui.client.component.event.OpenEventImpl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Tree Mar 16, 2011 12:13:24 PM evgen $
 *
 */
public abstract class Tree<T> extends Composite implements TreeGridItem<T>, DoubleClickHandler
{

   protected com.google.gwt.user.client.ui.Tree tree;

   protected T value;
   
   /**
    * 
    */
   public Tree()
   {
      tree = new com.google.gwt.user.client.ui.Tree();
      initWidget(tree);
      tree.addDomHandler(this, DoubleClickEvent.getType());
   }
   
   /**
    * @see com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.google.gwt.event.dom.client.DoubleClickEvent)
    */
   @Override
   public void onDoubleClick(DoubleClickEvent event)
   {
      TreeItem selectedItem = tree.getSelectedItem();
      if(selectedItem == null)
         return;
      selectedItem.setState(!selectedItem.getState(), true);
   }

   /**
    * @see com.google.gwt.user.client.ui.HasValue#getValue()
    */
   @Override
   public T getValue()
   {
      return value;
   }

   /**
    * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
    */
   @Override
   public void setValue(T value)
   {
      this.value = value;
      doUpdateValue();
   }
   
   public abstract void doUpdateValue();

   /**
    * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
    */
   @Override
   public void setValue(T value, boolean fireEvents)
   {
      setValue(value);
   }

   /**
    * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
    */
   @Override
   public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler)
   {
      return null;
   }

   /**
    * @see com.google.gwt.event.logical.shared.HasOpenHandlers#addOpenHandler(com.google.gwt.event.logical.shared.OpenHandler)
    */
   @Override
   public HandlerRegistration addOpenHandler(final OpenHandler<T> handler)
   {
      HandlerRegistration openHandler = tree.addOpenHandler(new OpenHandler<TreeItem>()
      {
         
         @Override
         public void onOpen(OpenEvent<TreeItem> event)
         {
            @SuppressWarnings("unchecked")
            OpenEvent<T> openEvent = new OpenEventImpl<T>((T)event.getTarget().getUserObject());
            handler.onOpen(openEvent);
         }
      });
      
      return openHandler;
    
   }

   /**
    * @see com.google.gwt.event.logical.shared.HasCloseHandlers#addCloseHandler(com.google.gwt.event.logical.shared.CloseHandler)
    */
   @Override
   public HandlerRegistration addCloseHandler(final CloseHandler<T> handler)
   {
      HandlerRegistration closeHadler = tree.addCloseHandler(new CloseHandler<TreeItem>()
      {
         
         @Override
         public void onClose(CloseEvent<TreeItem> event)
         {
            @SuppressWarnings("unchecked")
            CloseEvent<T> closeEvent = new CloseEventImpl<T>((T)event.getTarget().getUserObject());
            handler.onClose(closeEvent);
         }
      });
      return closeHadler;
   }

   /**
    * @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public HandlerRegistration addSelectionHandler(final SelectionHandler<T> handler)
   {
      HandlerRegistration selectionHandler = tree.addSelectionHandler(new SelectionHandler<TreeItem>()
      {
         
         @Override
         public void onSelection(SelectionEvent<TreeItem> event)
         {
            @SuppressWarnings("unchecked")
            SelectionEvent<T> selectionEvent = new SelectionEventImpl<T>((T)event.getSelectedItem().getUserObject());
            handler.onSelection(selectionEvent);
         }
      });
      return selectionHandler;
   }

   /**
    * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
    */
   @Override
   public HandlerRegistration addClickHandler(ClickHandler handler)
   {
      return tree.addHandler(handler, ClickEvent.getType());
   }

   /**
    * @see com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler(com.google.gwt.event.dom.client.DoubleClickHandler)
    */
   @Override
   public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
   {
      return tree.addHandler(handler, DoubleClickEvent.getType());
   }

   /**
    * @see com.google.gwt.event.dom.client.HasKeyPressHandlers#addKeyPressHandler(com.google.gwt.event.dom.client.KeyPressHandler)
    */
   @Override
   public HandlerRegistration addKeyPressHandler(KeyPressHandler handler)
   {
      return tree.addKeyPressHandler(handler);
   }
   
   
   private class SelectionEventImpl<E> extends SelectionEvent<E>
   {

      /**
       * @param selectedItem
       */
      public SelectionEventImpl(E selectedItem)
      {
         super(selectedItem);
      }
      
   }

}
