/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * Grid for displaying provisioned services.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 5:48:28 PM anya $
 * 
 */
public class BoundedServicesGrid extends ListGrid<String> implements HasUnbindServiceHandler
{
   private Column<String, String> nameColumn;

   private Column<String, String> unbindColumn;

   public BoundedServicesGrid()
   {
      setID("eXoBoundedServicesGrid");

      nameColumn = new Column<String, String>(new TextCell())
      {

         @Override
         public String getValue(String name)
         {
            return name;
         }
      };

      unbindColumn = new Column<String, String>(new ButtonCell())
      {

         @Override
         public String getValue(String object)
         {
            return CloudFoundryExtension.LOCALIZATION_CONSTANT.unBindButton();
         }
      };

      getCellTable().addColumn(nameColumn);
      getCellTable().addColumn(unbindColumn);
      getCellTable().setColumnWidth(unbindColumn, "60px");
   }

   private class SelectionEventImpl extends SelectionEvent<String>
   {
      /**
       * @param selectedItem
       */
      protected SelectionEventImpl(String selectedItem)
      {
         super(selectedItem);
      }
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.services.HasUnbindServiceHandler#addUnbindServiceHandler(com.google.gwt.event.logical.shared.SelectionHandler)
    */
   @Override
   public void addUnbindServiceHandler(final SelectionHandler<String> handler)
   {
      unbindColumn.setFieldUpdater(new FieldUpdater<String, String>()
      {

         @Override
         public void update(int index, String object, String value)
         {
            handler.onSelection(new SelectionEventImpl(object));
         }
      });
   }
}
