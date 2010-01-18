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

import java.util.Collection;
import java.util.Comparator;

import org.exoplatform.gwt.commons.smartgwt.component.CheckboxItem;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;
import org.exoplatform.ideall.client.model.File;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.Layout;

public abstract class PropertyEditor extends Layout
{

   public PropertyEditor(File file)
   {
      DynamicForm propertiesForm = getPropertiesForm(file.getProperties());

      propertiesForm.setPadding(10);
      propertiesForm.setTitleWidth(200);
      propertiesForm.setLayoutAlign(VerticalAlignment.TOP);
      propertiesForm.setLayoutAlign(Alignment.LEFT);

      addMember(propertiesForm);
   }

   public abstract DynamicForm getPropertiesForm(Collection<Property> properties);

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
