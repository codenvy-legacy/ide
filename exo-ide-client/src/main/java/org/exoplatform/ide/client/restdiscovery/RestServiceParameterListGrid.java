/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.restdiscovery;

import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class RestServiceParameterListGrid extends ListGrid<Param>
{

   private final static String GROUP = "Group";

   public RestServiceParameterListGrid()
   {
      setCanSort(true);
      setCanEdit(false);
      setSelectionType(SelectionStyle.SINGLE);

      ListGridField fieldName = new ListGridField("name", "Name");
      fieldName.setAlign(Alignment.LEFT);
      fieldName.setCanEdit(false);

      ListGridField fieldType = new ListGridField("type", "Type");
      fieldType.setAlign(Alignment.LEFT);
      fieldType.setCanEdit(false);

      ListGridField fieldDefault = new ListGridField("default", "By default");
      fieldDefault.setAlign(Alignment.LEFT);
      fieldDefault.setCanEdit(false);

      ListGridField fieldGroup = new ListGridField(GROUP, GROUP);
      fieldGroup.setHidden(true);
      setGroupStartOpen(GroupStartOpen.ALL);
      setGroupByField(GROUP);

      setFields(fieldName, fieldType, fieldDefault, fieldGroup);
      setData(new ListGridRecord[0]);
      setShowHeader(true);

   }

   @Override
   protected void setRecordFields(ListGridRecord record, Param item)
   {
      record.setAttribute("name", item.getName());
      record.setAttribute("type", item.getType().getLocalName());
      record.setAttribute("default", item.getDefault());
      String paramType = "";
      switch (item.getStyle())
      {
         case HEADER :
            paramType = "Header ";
            break;
         case QUERY :
            paramType = "Query ";
            break;
         case PLAIN :
             paramType = "Plain ";
            break;
         case TEMPLATE :
             paramType = "Template ";
            break;
         case MATRIX :
             paramType = "Matrix";
            break;
      }
      record.setAttribute(GROUP, paramType + "param");
   }

}
