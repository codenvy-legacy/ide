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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Remote;

import java.util.List;

/**
 * Grid to display remote repositories info.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 18, 2011 12:09:09 PM anya $
 *
 */
public class RemoteGrid extends ListGrid<Remote>
{
   /**
    * Grid's ID.
    */
   private static final String ID = "ideRemoteGrid";

   /**
    *Name column.
    */
   Column<Remote, String> nameColumn;

   /**
    * Location column.
    */
   Column<Remote, String> urlColumn;

   public RemoteGrid()
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
      CellTable<Remote> cellTable = getCellTable();
      
    
      nameColumn = new Column<Remote, String>(new TextCell())
      {

         @Override
         public String getValue(Remote remote)
         {
            return remote.getName();
         }

      };

      urlColumn = new Column<Remote, String>(new TextCell())
      {

         @Override
         public String getValue(Remote remote)
         {
            return remote.getUrl();
         }

      };

      cellTable.addColumn(nameColumn, GitExtension.MESSAGES.remoteGridNameField());
      cellTable.setColumnWidth(nameColumn, "20%");
      cellTable.addColumn(urlColumn, GitExtension.MESSAGES.remoteGridLocationField());
      cellTable.setColumnWidth(urlColumn, "80%");
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List)
    */
   @Override
   public void setValue(List<Remote> value)
   {
      super.setValue(value);
      if (value != null && value.size() > 0)
      {
         selectItem(value.get(0));
      }
   }

   /**
    * Returns selected remote repository in grid.
    * 
    * @return {@link Remote} selected remote repository
    */
   public Remote getSelectedRemote()
   {
      return super.getSelectedItems().get(0);
   }
}
