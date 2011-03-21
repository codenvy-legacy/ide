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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.vfs.Version;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

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

   Column<Version, String> nameColumn;
   
   Column<Version, String> dateColumn;

   Column<Version, String> sizeColumn;
   
   public VersionsGrid()
   {
      setID(ID);
      
      initColumns();
   }

   private void initColumns()
   {
      CellTable<Version> cellTable = getCellTable();
      
      //name column
      nameColumn = new Column<Version, String>(new TextCell())
      {
         @Override
         public String getValue(final Version item)
         {
            return item.getDisplayName();
         }
      };
      
      nameColumn.setSortable(true);    
      nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      cellTable.addColumn(nameColumn, NAME);
      cellTable.setColumnWidth(nameColumn, 35, Unit.PCT);
      
      //date column
      dateColumn = new Column<Version, String>(new TextCell())
      {
         @Override
         public String getValue(final Version item)
         {
            return item.getCreationDate();
         }
      };
      dateColumn.setSortable(true);
      cellTable.addColumn(dateColumn, DATE);
      cellTable.setColumnWidth(dateColumn, 40, Unit.PCT);
      
      // content size column
      sizeColumn = new Column<Version, String>(new TextCell())
      {
         @Override
         public String getValue(final Version item)
         {
            return String.valueOf(item.getContentLength());
         }
      };
      sizeColumn.setSortable(true);
      sizeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
      cellTable.addColumn(sizeColumn, LENGTH);
      cellTable.setColumnWidth(sizeColumn, 25, Unit.PCT);
   }
   
   @Override
   public void setValue(List<Version> versions)
   {
      super.setValue(versions);
      
      ListHandler<Version> columnSortHandler = getColumnSortHandler();
      
      // Add comparators
      columnSortHandler.setComparator(nameColumn, new Comparator<Version>()
      {
         public int compare(Version item1, Version item2)
         {
            return Integer.valueOf(item1.getDisplayName()) > Integer.valueOf(item2.getDisplayName()) ? 1 : -1;
//            return item1.getDisplayName().compareTo(item2.getDisplayName());
         }
      });
      
      columnSortHandler.setComparator(dateColumn, new Comparator<Version>()
      {
         public int compare(Version item1, Version item2)
         {
            return item1.getCreationDate().compareTo(item2.getCreationDate());
         }
      });
      
      columnSortHandler.setComparator(sizeColumn, new Comparator<Version>()
         {
            public int compare(Version item1, Version item2)
            {
               return (item1.getContentLength() > item2.getContentLength()) ? 1 : -1;     
            }
      });       
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
