/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.template;

import org.exoplatform.gwtframework.ui.smartgwt.component.ListGrid;
import org.exoplatform.ideall.client.model.template.Template;
import org.exoplatform.ideall.client.model.util.ImageUtil;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateListGrid extends ListGrid<Template>
{

   public TemplateListGrid()
   {
      ListGridField iconField = new ListGridField("icon", "&nbsp;");
      iconField.setType(ListGridFieldType.IMAGE);
      iconField.setWidth(28);
      iconField.setAlign(Alignment.CENTER);
      iconField.setCanSort(false);

      ListGridField fName = new ListGridField("name", "Name");
      ListGridField fDescription = new ListGridField("description", "Description");

      setFields(iconField, fName, fDescription);
   }

   @Override
   protected String getValuePropertyName()
   {
      return "templateItem";
   }

   @Override
   protected void setRecordFields(ListGridRecord record, Template item)
   {
      String icon = ImageUtil.getIcon(item.getMimeType());
      record.setAttribute("icon", icon);
      record.setAttribute("name", "<span title=\"" + item.getName() + "\">" + item.getName() + "</span>");
      record.setAttribute("description", "<span title=\"" + item.getDescription() + "\">" + item.getDescription()
         + "</span>");
   }

}
