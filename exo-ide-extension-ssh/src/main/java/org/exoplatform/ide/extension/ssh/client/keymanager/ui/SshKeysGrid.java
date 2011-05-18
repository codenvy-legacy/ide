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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.ssh.client.keymanager.HasSshGrid;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeysGrid May 18, 2011 12:20:34 PM evgen $
 *
 */
public class SshKeysGrid extends ListGrid<KeyItem> implements HasSshGrid<KeyItem>
{

   private Column<KeyItem, String> hostColumn;
   
   private Column<KeyItem, String> publicKeyColumn;
   
   private Column<KeyItem, String> deleteKeyColumn;

   /**
    * 
    */
   public SshKeysGrid()
   {
      TextCell hostCell = new TextCell();
      hostColumn = new Column<KeyItem, String>(hostCell)
      {
         @Override
         public String getValue(KeyItem object)
         {
            return object.getHost();
         }
      };
      publicKeyColumn = new Column<KeyItem, String>(new ButtonCell())
      {
         
         @Override
         public String getValue(KeyItem object)
         {
            return "View";
         }
      };
      deleteKeyColumn = new Column<KeyItem, String>(new ButtonCell())
      {
         
         @Override
         public String getValue(KeyItem object)
         {
            return "Delete";
         }
      };

      hostColumn.setSortable(true);
      publicKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>()
      {
         
         @Override
         public void update(int index, KeyItem object, String value)
         {
         }
      });
      
      getCellTable().addColumn(hostColumn, "Host");
      getCellTable().addColumn(publicKeyColumn, "Public Key");
      getCellTable().addColumn(deleteKeyColumn, "Delete");
      getCellTable().setColumnWidth(hostColumn, 50, Unit.PCT);
      getCellTable().setColumnWidth(publicKeyColumn, 30, Unit.PX);
      getCellTable().setColumnWidth(deleteKeyColumn, 30, Unit.PX);
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.HasSshGrid#addViewButtonSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public HandlerRegistration addViewButtonSelectionHandler(final SelectionHandler<KeyItem> handler)
   {
      publicKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>()
      {
         
         @Override
         public void update(int index, KeyItem object, String value)
         {
            
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.ssh.client.keymanager.HasSshGrid#addDeleteButtonSelectionHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public HandlerRegistration addDeleteButtonSelectionHandler(final SelectionHandler<KeyItem> handler)
   {
      deleteKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>()
         {
            
            @Override
            public void update(int index, KeyItem object, String value)
            {
               
               handler.onSelection(new SelectionEventImpl(object));
            }
         });
      return null;
   }

   private class SelectionEventImpl extends SelectionEvent<KeyItem>
   {
      /**
       * @param selectedItem
       */
      protected SelectionEventImpl(KeyItem selectedItem)
      {
         super(selectedItem);
      }
      
   }
}
