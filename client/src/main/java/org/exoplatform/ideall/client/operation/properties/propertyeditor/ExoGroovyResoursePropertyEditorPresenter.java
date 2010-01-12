/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.operation.properties.propertyeditor;

import java.util.Collection;

import org.exoplatform.gwt.commons.smartgwt.component.CheckboxItem;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwt.commons.xml.QName;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.ItemProperty;
import org.exoplatform.ideall.client.model.Properties;
import org.exoplatform.ideall.client.operation.properties.event.FilePropertiesChangedEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class ExoGroovyResoursePropertyEditorPresenter
{

   interface Display
   {

      CheckboxItem getAutoloadField();

   }

   protected Display display;

   private File file;

   private HandlerManager eventBus;

   public ExoGroovyResoursePropertyEditorPresenter(HandlerManager eventBus, File file)
   {
      this.eventBus = eventBus;
      this.file = file;
   }

   public void bindDisplay(Display d)
   {
      display = d;

      if (display.getAutoloadField() != null)
      {
         display.getAutoloadField().addChangedHandler(new ChangedHandler()
         {
            public void onChanged(ChangedEvent event)
            {
               doPropertyValueChanged(ItemProperty.AUTOLOAD, String.valueOf(event.getValue()));
            }
         });
      }

   }

   private Property getProperty(Collection<Property> properties, QName name)
   {
      for (Property property : properties)
      {
         if (property.getName().equals(name))
         {
            return property;
         }
      }

      return null;
   }

   protected void doPropertyValueChanged(ItemProperty property, String propertyValue)
   {
      Property jcrContentProperty = getProperty(file.getProperties(), Properties.JCRProperties.JCR_CONTENT);
      Property autoloadProperty =
         getProperty(jcrContentProperty.getChildProperties(), Properties.ExoProperties.EXO_AUTOLOAD);
      autoloadProperty.setValue(propertyValue);

      file.setPropertiesChanged(true);
      eventBus.fireEvent(new FilePropertiesChangedEvent(file));
   }

}
