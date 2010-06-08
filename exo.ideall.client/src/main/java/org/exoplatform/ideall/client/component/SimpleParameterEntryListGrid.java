/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.ideall.client.component;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.event.ValueChangeEventImpl;
import org.exoplatform.ideall.client.model.SimpleParameterEntry;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SimpleParameterEntryListGrid extends ListGrid<SimpleParameterEntry> implements EditCompleteHandler
{

   HandlerRegistration editCompleteHandler;

   public SimpleParameterEntryListGrid()
   {
      setHeaderHeight(22);

      ListGridField fieldName = new ListGridField("name", "Name");
      fieldName.setAlign(Alignment.LEFT);
      ListGridField fieldValue = new ListGridField("value", "Value");
      fieldValue.setAlign(Alignment.LEFT);
      setData(new ListGridRecord[0]);
      setFields(fieldName, fieldValue);

      setShowHeader(true);

      editCompleteHandler = addEditCompleteHandler(this);
   }

   @Override
   protected void setRecordFields(ListGridRecord record, SimpleParameterEntry item)
   {
      record.setAttribute("name", item.getName());
      record.setAttribute("value", item.getValue());
   }

   protected void fireOnValueChangeEvent()
   {
      for (ValueChangeHandler<List<SimpleParameterEntry>> handler : valueChangeHandlers)
      {
         handler.onValueChange(new ValueChangeEventImpl<List<SimpleParameterEntry>>(items));
      }
   }

   public void onEditComplete(EditCompleteEvent event)
   {
      
      editCompleteHandler.removeHandler();
      if (event.getOldRecord() == null)
      {
         String name = event.getNewValues().get("name") == null ? "" : "" + event.getNewValues().get("name");
         String value = event.getNewValues().get("value") == null ? "" : "" + event.getNewValues().get("value");
         
         SimpleParameterEntry newListItem = new SimpleParameterEntry(name, value);

         checkIfAlredyExist(newListItem);
         updateRecords();
      }
      else
      {
         SimpleParameterEntry listItem =
            (SimpleParameterEntry)event.getOldRecord().getAttributeAsObject(getValuePropertyName());
         if (event.getNewValues().get("name") != null)
         {
            listItem.setName("" + event.getNewValues().get("name"));
         }
         if (event.getNewValues().get("value") != null)
         {
            listItem.setValue("" + event.getNewValues().get("value"));
         }
      }
      fireOnValueChangeEvent();
      editCompleteHandler = addEditCompleteHandler(this);
   }

   private void checkIfAlredyExist(SimpleParameterEntry newentry)
   {
    SimpleParameterEntry entry;
    for (int i = 0; i < items.size(); i++)
    {
       entry = items.get(i);  
       if (entry.getName().equals(newentry.getName()))
       {
          entry.setValue(newentry.getValue());
          items.set(i, entry);
          return;
       }
    }
    items.add(newentry);
   }
   
}
