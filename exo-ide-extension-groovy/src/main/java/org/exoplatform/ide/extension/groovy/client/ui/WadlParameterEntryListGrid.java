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
package org.exoplatform.ide.extension.groovy.client.ui;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlParameterEntryListGrid extends ListGrid<WadlParameterEntry> implements EditCompleteHandler
{

   private HandlerRegistration editCompleteHandler;

   public WadlParameterEntryListGrid()
   {
      setHeaderHeight(22);

      ListGridField fieldSend = new ListGridField("send", "Send");
      fieldSend.setAlign(Alignment.CENTER);
      fieldSend.setType(ListGridFieldType.BOOLEAN);
      fieldSend.setWidth(33);
      fieldSend.setCanEdit(true);

      ListGridField fieldName = new ListGridField("name", "Name");
      fieldName.setAlign(Alignment.LEFT);
      fieldName.setCanEdit(false);

      ListGridField fieldType = new ListGridField("type", "Type");
      fieldType.setAlign(Alignment.LEFT);
      fieldType.setCanEdit(false);

      ListGridField fieldDefault = new ListGridField("default", "By default");
      fieldDefault.setAlign(Alignment.LEFT);
      fieldDefault.setCanEdit(false);

      ListGridField fieldValue = new ListGridField("value", "Value");
      fieldValue.setAlign(Alignment.LEFT);
      fieldValue.setCanEdit(true);

      setData(new ListGridRecord[0]);
      setFields(fieldSend, fieldName, fieldType, fieldDefault, fieldValue);

      setShowHeader(true);

      editCompleteHandler = addEditCompleteHandler(this);
   }

   @Override
   protected void setRecordFields(ListGridRecord record, WadlParameterEntry item)
   {
      record.setAttribute("send", item.isSend());
      record.setAttribute("name", item.getName());
      record.setAttribute("type", item.getType());
      record.setAttribute("default", item.getDefaultValue());
      record.setAttribute("value", item.getValue());
   }

   public void onEditComplete(EditCompleteEvent event)
   {
      editCompleteHandler.removeHandler();

      WadlParameterEntry listItem =
         (WadlParameterEntry)event.getOldRecord().getAttributeAsObject(getValuePropertyName());
      if (event.getNewValues().get("name") != null)
      {
         listItem.setName("" + event.getNewValues().get("name"));
      }
      if (event.getNewValues().get("value") != null)
      {
         listItem.setValue("" + event.getNewValues().get("value"));
      }
      if (event.getNewValues().get("type") != null)
      {
         listItem.setType("" + event.getNewValues().get("type"));
      }
      if (event.getNewValues().get("send") != null)
      {
         listItem.setSend(Boolean.parseBoolean(event.getNewValues().get("send").toString()));
      }
      if (event.getNewValues().get("default") != null)
      {
         listItem.setDefaultValue("" + event.getNewValues().get("default"));
      }

      editCompleteHandler = addEditCompleteHandler(this);
   }

}
