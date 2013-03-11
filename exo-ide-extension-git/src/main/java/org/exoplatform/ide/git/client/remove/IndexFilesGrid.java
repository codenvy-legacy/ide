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
package org.exoplatform.ide.git.client.remove;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;

/**
 * Grid for displaying git files.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 5:05:49 PM anya $
 * 
 */
public class IndexFilesGrid extends ListGrid<IndexFile>
{
   /**
    * Grid's ID.
    */
   private static final String ID = "ideIndexFilesGrid";

   private final String FILES = "Files for commit";

   /**
    * Files column.
    */
   Column<IndexFile, String> filesColumn;

   /**
    * Column with checkboxes.
    */
   Column<IndexFile, Boolean> checkColumn;

   public IndexFilesGrid()
   {
      super();
      setID(ID);
      initColumns();
   }

   /**
    * Initialize the columns of the grid.
    */
   private void initColumns()
   {
      CellTable<IndexFile> cellTable = getCellTable();

      // Create files column:
      filesColumn = new Column<IndexFile, String>(new TextCell())
      {
         @Override
         public String getValue(IndexFile file)
         {
            return file.getPath();
         }
      };

      // Create column with checkboxes:
      checkColumn = new Column<IndexFile, Boolean>(new CheckboxCell(false, true))
      {

         @Override
         public Boolean getValue(IndexFile file)
         {
            return !file.isIndexed();
         }

      };

      // Create bean value updater:
      FieldUpdater<IndexFile, Boolean> checkFieldUpdater = new FieldUpdater<IndexFile, Boolean>()
      {

         @Override
         public void update(int index, IndexFile file, Boolean value)
         {
            file.setIndexed(!value);
         }
      };

      checkColumn.setFieldUpdater(checkFieldUpdater);

      filesColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

      cellTable.addColumn(checkColumn, new SafeHtml()
      {
         private static final long serialVersionUID = 1L;

         @Override
         public String asString()
         {
            return "&nbsp;";
         }
      });
      cellTable.setColumnWidth(checkColumn, 1, Unit.PCT);

      cellTable.addColumn(filesColumn, FILES);
      cellTable.setColumnWidth(filesColumn, 35, Unit.PCT);
   }
}
