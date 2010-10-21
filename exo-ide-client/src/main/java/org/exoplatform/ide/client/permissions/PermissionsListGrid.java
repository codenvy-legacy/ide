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

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ide.client.framework.vfs.ACL.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.ACL.Permissions;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SortArrow;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 20, 2010 $
 *
 */
public class PermissionsListGrid extends ListGrid<AccessControlEntry> implements EditCompleteHandler
{

   private final String IDENTITY = "Identity";

   private final String READ = "Read";

   private final String WRITE = "Write";
   
   private HandlerRegistration editCompleteHandler;

   public PermissionsListGrid()
   {
      setCanSort(true);
//      setCanGroupBy(false);
////      setCanFocus(false);
//      setSelectionType(SelectionStyle.SINGLE);
//      setCanFreezeFields(false);
//      setCanPickFields(false);
      setCanEdit(true);  
      setEditEvent(ListGridEditEvent.CLICK);  
      setHeaderHeight(22);
      
      ListGridField fieldIdentity = new ListGridField(IDENTITY, IDENTITY);
      fieldIdentity.setAlign(Alignment.LEFT);
      fieldIdentity.setCanEdit(false);
      fieldIdentity.setWidth("60%");

      ListGridField fieldRead = new ListGridField(READ, READ);
      fieldRead.setAlign(Alignment.CENTER);
      fieldRead.setCanEdit(true);
      fieldRead.setWidth("20%");
      fieldRead.setType(ListGridFieldType.BOOLEAN);
      
      ListGridField fieldWrite = new ListGridField(WRITE, WRITE);
      fieldWrite.setAlign(Alignment.CENTER);
      fieldWrite.setCanEdit(true);
      fieldWrite.setWidth("20%");
      fieldWrite.setType(ListGridFieldType.BOOLEAN);
      
      editCompleteHandler = addEditCompleteHandler(this);
      setData(new ListGridRecord[0]);
      
      setFields(fieldIdentity, fieldRead, fieldWrite);
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid#setRecordFields(com.smartgwt.client.widgets.grid.ListGridRecord, java.lang.Object)
    */
   @Override
   protected void setRecordFields(ListGridRecord record, AccessControlEntry item)
   {
      record.setAttribute(IDENTITY, item.getIdentity());
      for (Permissions p : item.getPermissionsList())
      {
         switch (p)
         {
            case WRITE :
               record.setAttribute(WRITE, p.toString());
               break;
            case READ :
               record.setAttribute(READ, p.toString());
               break;
         }
      }

   }

   /**
    * @see com.smartgwt.client.widgets.grid.events.EditCompleteHandler#onEditComplete(com.smartgwt.client.widgets.grid.events.EditCompleteEvent)
    */
   public void onEditComplete(EditCompleteEvent event)
   {
      AccessControlEntry entry = (AccessControlEntry)event.getOldRecord().getAttributeAsObject(getValuePropertyName());
      
      if(event.getNewValues().containsKey(READ))
      {
         if(Boolean.parseBoolean(event.getNewValues().get(READ).toString()))
         {
            entry.addPermission(Permissions.READ);
         }
         else
         {
            entry.removePermission(Permissions.READ);
         }
      }
      if(event.getNewValues().containsKey(WRITE))
      {
         if(Boolean.parseBoolean(event.getNewValues().get(WRITE).toString()))
         {
            entry.addPermission(Permissions.WRITE);
         }
         else
         {
            entry.removePermission(Permissions.WRITE);
         }
      }
   }

}
