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

package org.exoplatform.ideall.client.operation.properties.propertyeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwt.commons.xml.QName;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Properties;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class DefaultPropertyEditor extends PropertyEditor
{

   public DefaultPropertyEditor(File file)
   {
      super(file);
   }

   @Override
   public DynamicForm getPropertiesForm(Collection<Property> properties)
   {
      DynamicForm propertiesForm = new DynamicForm();

      if (properties == null)
      {
         return propertiesForm;
      }

      ArrayList<FormItem> formItems = new ArrayList<FormItem>();

      for (Property property : properties)
      {
         QName propertyName = property.getName();
         if (Properties.isSkip(propertyName))
         {
            continue;
         }

         String propertyTitle = Properties.getPropertyTitle(propertyName);
         formItems.add(getStaticTextItem(propertyTitle, property.getValue()));
      }

      Collections.sort(formItems, itemsComparator);
      propertiesForm.setFields(formItems.toArray(new FormItem[formItems.size()]));

      return propertiesForm;
   }

}
