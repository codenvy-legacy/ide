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

import org.exoplatform.gwt.commons.smartgwt.component.CheckboxItem;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwt.commons.xml.QName;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Properties;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;

public class ExoGroovyResoursePropertyEditor extends PropertyEditor implements
   ExoGroovyResoursePropertyEditorPresenter.Display
{

   private CheckboxItem autoloadField;

   private ExoGroovyResoursePropertyEditorPresenter presenter;

   public ExoGroovyResoursePropertyEditor(HandlerManager eventBus, File file)
   {
      super(file);
      presenter = new ExoGroovyResoursePropertyEditorPresenter(eventBus, file);
      presenter.bindDisplay(this);
   }

   @Override
   public DynamicForm getPropertiesForm(Collection<Property> properties)
   {
      DynamicForm propertiesForm = new DynamicForm();
      propertiesForm.setCanSelectText(true);

      if (properties == null)
      {
         return propertiesForm;
      }

      ArrayList<FormItem> formItems = new ArrayList<FormItem>();

      for (Property property : properties)
      {
         QName propertyName = property.getName();
         if (propertyName.equals(Properties.JCRProperties.JCR_CONTENT))
         {
            Collection<Property> children = property.getChildProperties();
            for (Property child : children)
            {
               if (child.getName().equals(Properties.ExoProperties.EXO_AUTOLOAD))
               {
                  String propertyTitle = Properties.getPropertyTitle(child.getName());
                  autoloadField = getBooleanItem(propertyTitle, Boolean.parseBoolean(child.getValue()), false);
                  formItems.add(autoloadField);
                  continue;
               }

            }

         }
         else
         {
            if (Properties.isSkip(propertyName))
            {
               continue;
            }

            String propertyTitle = Properties.getPropertyTitle(propertyName);
            formItems.add(getStaticTextItem(propertyTitle, property.getValue()));
         }

      }

      Collections.sort(formItems, itemsComparator);
      propertiesForm.setFields(formItems.toArray(new FormItem[formItems.size()]));

      return propertiesForm;
   }

   public CheckboxItem getAutoloadField()
   {
      return this.autoloadField;
   }

}
