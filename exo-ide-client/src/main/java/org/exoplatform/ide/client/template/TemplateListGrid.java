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
package org.exoplatform.ide.client.template;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ListGrid;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateListGrid extends ListGrid<Template>
{
   private static final String ID = "ideCreateFileFromTemplateFormTemplateListGrid";
   
   public TemplateListGrid()
   {
      setID(ID);
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
      if (item.getNodeName() == null)
      {
         record.setAttribute("icon", getItemIcon(item));
         record.setAttribute("name", "<span title=\"" + item.getName() + "\"><font color=\"#FF0000\">" + item.getName() + "</font></span>");
         record.setAttribute("description", "<span title=\"" + item.getDescription() + "\"><font color=\"#FF0000\">" + item.getDescription()
            + "</font></span>");
         return;
      }
      
      record.setAttribute("icon", getItemIcon(item));
      record.setAttribute("name", "<span title=\"" + item.getName() + "\">" + item.getName() + "</span>");
      record.setAttribute("description", "<span title=\"" + item.getDescription() + "\">" + item.getDescription()
         + "</span>");
   }
   
   /**
    * Get selected templates.
    * 
    * @return selected templates
    */
   public List<Template> getSelectedItems()
   {
      List<Template> selectedItems = new ArrayList<Template>();

      for (ListGridRecord record : getSelection())
      {
         selectedItems.add((Template)record.getAttributeAsObject(getValuePropertyName()));
      }

      return selectedItems;
   }
   
   /**
    * Return URL to icon of template according to type of template:
    * FileTemplate or ProjectTemplate and according to mime type if FileTemplate.
    * 
    * @param template
    * @return String
    */
   private String getItemIcon(Template template)
   {
      if (template instanceof FileTemplate)
      {
         System.out.println(">>>>>>>>> " + template.getName() + " " + String.valueOf(template instanceof FileTemplate));
         return ImageUtil.getIcon(((FileTemplate)template).getMimeType());
      }
      
      return null;
   }

}
