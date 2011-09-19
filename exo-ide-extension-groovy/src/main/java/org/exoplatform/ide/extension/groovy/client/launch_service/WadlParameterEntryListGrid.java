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
package org.exoplatform.ide.extension.groovy.client.launch_service;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlParameterEntryListGrid extends ListGrid<WadlParameterEntry>
{
   
   private static final String SEND = "Send";

   private static final String NAME = "Name";

   private static final String TYPE = "Type";

   private static final String DEFAULT = "By default";

   private static final String VALUE = "Value";

   public WadlParameterEntryListGrid()
   {
      initColumns();
   }

   private void initColumns()
   {
      //isSend column
      Column<WadlParameterEntry, Boolean> sendColumn =
         new Column<WadlParameterEntry, Boolean>(new CheckboxCell(true, false))
         {
            @Override
            public Boolean getValue(WadlParameterEntry object)
            {
               return object.isSend();
            }
         };

      sendColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      sendColumn.setFieldUpdater(new FieldUpdater<WadlParameterEntry, Boolean>()
      {

         @Override
         public void update(int index, WadlParameterEntry object, Boolean value)
         {
            object.setSend(value);
         }
      });

      getCellTable().addColumn(sendColumn, SEND);
      getCellTable().setColumnWidth(sendColumn, 33, Unit.PX);

      //name column
      Column<WadlParameterEntry, String> nameColumn = new Column<WadlParameterEntry, String>(new TextCell())
      {

         @Override
         public String getValue(final WadlParameterEntry item)
         {
            return item.getName();
         }

      };
      getCellTable().addColumn(nameColumn, NAME);

      //type column
      Column<WadlParameterEntry, String> typeColumn = new Column<WadlParameterEntry, String>(new TextCell())
      {

         @Override
         public String getValue(final WadlParameterEntry item)
         {
            return item.getType();
         }

      };
      getCellTable().addColumn(typeColumn, TYPE);

      //default column
      Column<WadlParameterEntry, String> defaultColumn = new Column<WadlParameterEntry, String>(new TextCell())
      {
         @Override
         public String getValue(final WadlParameterEntry item)
         {
            return item.getDefaultValue();
         }

      };
      getCellTable().addColumn(defaultColumn, DEFAULT);

      //value column
      Column<WadlParameterEntry, String> valueColumn = new Column<WadlParameterEntry, String>(new EditTextCell())
      {

         @Override
         public String getValue(final WadlParameterEntry item)
         {
            return item.getValue();
         }

      };
      valueColumn.setFieldUpdater(new FieldUpdater<WadlParameterEntry, String>()
      {
         @Override
         public void update(int index, WadlParameterEntry item, String value)
         {
            item.setValue(value);
         }
      });
      getCellTable().addColumn(valueColumn, VALUE);
   }

}
