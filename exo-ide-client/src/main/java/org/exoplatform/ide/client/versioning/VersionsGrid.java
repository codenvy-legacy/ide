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
package org.exoplatform.ide.client.versioning;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.vfs.Version;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class VersionsGrid extends ListGrid<Version>
{
   private final String ID = "ideViewVersionsGrid";
   
   private final String NAME = "Name";

   private final String DATE = "Created";

   private final String LENGTH = "Size";

   public VersionsGrid()
   {
      setID(ID);

      ListGridField fieldName = new ListGridField(NAME, NAME);
      fieldName.setAlign(Alignment.LEFT);
      fieldName.setWidth("35%");

      ListGridField fieldDate = new ListGridField(DATE, DATE);
      fieldDate.setAlign(Alignment.LEFT);
      fieldDate.setWidth("40%");

      ListGridField fieldLenght = new ListGridField(LENGTH, LENGTH);
      fieldLenght.setAlign(Alignment.CENTER);
      fieldLenght.setWidth("25%");

//      setFields(fieldName, fieldDate, fieldLenght);
      initColumns();
   }

   private void initColumns()
   {
      //name column
      Column<Version, String> nameColumn = new Column<Version, String>(new TextCell())
      {

         @Override
         public String getValue(final Version item)
         {
            return item.getDisplayName();
         }

      };
      nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      getCellTable().addColumn(nameColumn, NAME);
      getCellTable().setColumnWidth(nameColumn, 35, Unit.PCT);
      
      //date column
      Column<Version, String> dateColumn = new Column<Version, String>(new TextCell())
      {

         @Override
         public String getValue(final Version item)
         {
            return item.getCreationDate();
         }

      };
      getCellTable().addColumn(dateColumn, DATE);
      getCellTable().setColumnWidth(dateColumn, 40, Unit.PCT);
      
      //length column
      Column<Version, String> lengthColumn = new Column<Version, String>(new TextCell())
      {

         @Override
         public String getValue(final Version item)
         {
            return item.getCreationDate();
         }

      };
      lengthColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      getCellTable().addColumn(lengthColumn, LENGTH);
      getCellTable().setColumnWidth(lengthColumn, 25, Unit.PCT);
      
   }
  
   /**
    * Returns selected version in version grid.
    * 
    * @return {@link Version} version
    */
   public Version getSelectedVersion()
   {
      return super.getSelectedItems().get(0);
   }

}
