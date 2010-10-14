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

package org.exoplatform.ide.client.operation.properties.propertyeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.CheckboxItem;
import org.exoplatform.ide.client.framework.module.vfs.api.File;
import org.exoplatform.ide.client.framework.module.vfs.property.ItemProperty;
import org.exoplatform.ide.client.framework.module.vfs.property.PropertyTitle;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.Layout;

public class PropertyEditor extends Layout
{
   
   private final String ID = "ideDynamicPropertiesForm";

   public PropertyEditor(File file)
   {
      DynamicForm propertiesForm = getPropertiesForm(file.getProperties());

      propertiesForm.setID(ID);
      propertiesForm.setPadding(10);
      propertiesForm.setTitleWidth(200);
      propertiesForm.setLayoutAlign(VerticalAlignment.TOP);
      propertiesForm.setLayoutAlign(Alignment.LEFT);

      addMember(propertiesForm);
   }

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

         if (ItemProperty.JCR_CONTENT.equals(propertyName))
         {
            for (Property p : property.getChildProperties())
            {
               if (!PropertyTitle.containsTitleFor(p.getName()))
               {
                  continue;
               }

               String propertyTitle = PropertyTitle.getPropertyTitle(p.getName());
               formItems.add(getStaticTextItem(propertyTitle, p.getValue()));
            }

         }
         else
         {
            if (!PropertyTitle.containsTitleFor(propertyName))
            {
               continue;
            }

            String propertyTitle = PropertyTitle.getPropertyTitle(propertyName);
            formItems.add(getStaticTextItem(propertyTitle, property.getValue()));
         }
      }

      Collections.sort(formItems, itemsComparator);
      propertiesForm.setFields(formItems.toArray(new FormItem[formItems.size()]));

      return propertiesForm;
   }

   protected CheckboxItem getBooleanItem(String name, boolean value, boolean isReadOnly)
   {
      CheckboxItem booleanItem = new CheckboxItem();
      booleanItem.setWrapTitle(false);
      booleanItem.setTitle("<b>" + name + "</b>");
      booleanItem.setValue(value);
      booleanItem.setDisabled(isReadOnly);
      booleanItem.setLabelAsTitle(true);
      return booleanItem;
   }

   protected StaticTextItem getStaticTextItem(String name, String value)
   {
      StaticTextItem staticTextItem = new StaticTextItem();
      staticTextItem.setName("idePropertiesText" + name.replaceAll(" ", ""));
      staticTextItem.setWrapTitle(false);
      staticTextItem.setTitle("<b>" + name + "</b>");
      staticTextItem.setValue(value);
      staticTextItem.setTitleAlign(Alignment.RIGHT);

      staticTextItem.setWrap(false);

      return staticTextItem;
   }

   protected static Comparator<FormItem> itemsComparator = new Comparator<FormItem>()
   {
      public int compare(FormItem item1, FormItem item2)
      {
         return item1.getTitle().compareToIgnoreCase(item2.getTitle());
      }
   };

}
