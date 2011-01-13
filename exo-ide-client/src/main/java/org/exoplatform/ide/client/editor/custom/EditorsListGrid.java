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
package org.exoplatform.ide.client.editor.custom;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class EditorsListGrid extends ListGrid<EditorInfo>
{

   public EditorsListGrid()
   {
      setHeaderHeight(22);

      ListGridField fieldName = new ListGridField("name", "Name");
      fieldName.setAlign(Alignment.LEFT);

      setData(new ListGridRecord[0]);

      setFields(fieldName);

      setShowHeader(true);
   }

   @Override
   protected void setRecordFields(ListGridRecord record, EditorInfo item)
   {
      if(item.isDefault())
      {
         record.setAttribute("name", item.getEditor().getDescription()+"&nbsp;[Default]");         
      }
      else
      {
         record.setAttribute("name", item.getEditor().getDescription());
      }
   }

}
