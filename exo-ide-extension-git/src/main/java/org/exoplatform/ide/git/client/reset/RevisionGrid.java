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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.git.shared.Revision;

import java.util.Date;
import java.util.List;

/**
 * Grid for displaying revisions' info (date, commiter, comment.)
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 15, 2011 11:26:49 AM anya $
 *
 */
public class RevisionGrid extends ListGrid<Revision>
{
   /**
    * Grid's ID.
    */
   private static final String ID = "ideRevisionGrid";

   /**
    * Date column's name.
    */
   private final String DATE = "Date";

   /**
    * Commiter column's name.
    */
   private final String COMMITER = "Commiter";

   /**
    * Comment column's name.
    */
   private final String COMMENT = "Comment";

   /**
    * Date column.
    */
   Column<Revision, Date> dateColumn;

   /**
    * Commiter column.
    */
   Column<Revision, String> commiterColumn;

   /**
    * Comment column.
    */
   Column<Revision, String> commentColumn;

   public RevisionGrid()
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
      CellTable<Revision> cellTable = getCellTable();
      
      dateColumn = new Column<Revision, Date>(new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)))
      {

         @Override
         public Date getValue(Revision revision)
         {
            return new Date(revision.getCommitTime());
         }
      };

      commiterColumn = new Column<Revision, String>(new TextCell())
      {

         @Override
         public String getValue(Revision revision)
         {
            if (revision.getCommitter() == null)
            {
               return "";
            }
            return revision.getCommitter().getName();
         }

      };

      commentColumn = new Column<Revision, String>(new TextCell())
      {

         @Override
         public String getValue(Revision revision)
         {
            return revision.getMessage();
         }

      };

      cellTable.addColumn(dateColumn, DATE);
      cellTable.setColumnWidth(dateColumn, "15%");
      cellTable.addColumn(commiterColumn, COMMITER);
      cellTable.setColumnWidth(commiterColumn, "20%");
      cellTable.addColumn(commentColumn, COMMENT);
      cellTable.setColumnWidth(commentColumn, "65%");
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List)
    */
   @Override
   public void setValue(List<Revision> value)
   {
      super.setValue(value);
      if (value != null && value.size() > 0)
      {
         selectItem(value.get(0));
      }
   }

   /**
    * Returns selected revision in grid.
    * 
    * @return {@link Revision} selected revision
    */
   public Revision getSelectedRevision()
   {
      return super.getSelectedItems().get(0);
   }
}
