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
package org.exoplatform.ide.client.permissions;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.vfs.acl.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.acl.Permissions;

/**
 * This class extends {@link ListGrid} with {@link AccessControlEntry}.<br>
 *
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 20, 2010 $
 *
 */
public class PermissionsListGrid extends ListGrid<AccessControlEntry>
{

   private final String IDENTITY = "Identity";

   private final String READ = "Read";

   private final String WRITE = "Write";
   
   public PermissionsListGrid()
   {
      super();
      initColumns();
   }
   
   private void initColumns()
   {
      Column<AccessControlEntry, String> identityColumn = new Column<AccessControlEntry, String>(new EditTextCell())
      {

         @Override
         public String getValue(final AccessControlEntry item)
         {
            return item.getIdentity();
         }

      };
      
      identityColumn.setFieldUpdater(new FieldUpdater<AccessControlEntry, String>()
      {
         
         @Override
         public void update(int index, AccessControlEntry object, String value)
         {
            object.setIdentity(value);
         }
      });
      
      getCellTable().addColumn(identityColumn, IDENTITY);
      getCellTable().setColumnWidth(identityColumn, 100, Unit.PCT);
      
      
      Column<AccessControlEntry, Boolean> readColumn =
         new Column<AccessControlEntry, Boolean>(new CheckboxCell(true, false))
         {
            @Override
            public Boolean getValue(AccessControlEntry item)
            {
               for (Permissions p : item.getPermissionsList())
               {
                  switch (p)
                  {
                     case WRITE :
                        return false;
                     case READ :
                        return true;
                  }
               }
               return false;
            }
         };

      readColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      getCellTable().addColumn(readColumn, READ);
      getCellTable().setColumnWidth(readColumn, 33, Unit.PX);
      
      Column<AccessControlEntry, Boolean> writeColumn =
         new Column<AccessControlEntry, Boolean>(new CheckboxCell(true, false))
         {
            @Override
            public Boolean getValue(AccessControlEntry item)
            {
               for (Permissions p : item.getPermissionsList())
               {
                  switch (p)
                  {
                     case WRITE :
                        return true;
                     case READ :
                        return false;
                  }
               }
               return false;
            }
         };

      readColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      getCellTable().addColumn(writeColumn, WRITE);
      getCellTable().setColumnWidth(writeColumn, 33, Unit.PX);
   }

}
