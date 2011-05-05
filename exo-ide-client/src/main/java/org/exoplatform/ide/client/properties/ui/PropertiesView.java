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
package org.exoplatform.ide.client.properties.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.PropertyTitle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesView extends ViewImpl implements
   org.exoplatform.ide.client.properties.PropertiesPresenter.Display
{

   private static int WIDTH = 300;

   private static int HEIGHT = 200;

   private Image image;

   private Widget contentWidget;

   public PropertiesView()
   {
      super(ID, ViewType.OPERATION, "Properties", new Image(IDEImageBundle.INSTANCE.properties()), WIDTH, HEIGHT);
   }

   public void showProperties(File file)
   {
      if (file.getProperties() == null || file.getProperties().size() == 0)
      {
         VerticalPanel panel = new VerticalPanel();
         panel.add(new com.google.gwt.user.client.ui.Label("There are no properties for this file."));
         panel.setWidth("100%");
         panel.setHeight("100%");
         setContentWidget(panel);
      }
      else
      {
         Map<String, String> properties = getVisibleProperties(file.getProperties());
         Widget propertiesTable = createPropertiesTable(properties);
         setContentWidget(propertiesTable);
      }
   }

   private void setContentWidget(Widget w)
   {
      if (contentWidget != null)
      {
         contentWidget.removeFromParent();
      }

      contentWidget = w;
      add(contentWidget);
   }

   public Map<String, String> getVisibleProperties(Collection<Property> properties)
   {
      Map<String, String> propertiesMap = new LinkedHashMap<String, String>();

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

               String title = PropertyTitle.getPropertyTitle(p.getName());
               propertiesMap.put(title, p.getValue());
            }

         }
         else
         {
            if (!PropertyTitle.containsTitleFor(propertyName))
            {
               continue;
            }

            String title = PropertyTitle.getPropertyTitle(propertyName);
            propertiesMap.put(title, property.getValue());
         }
      }

      return propertiesMap;
   }

   public Widget createPropertiesTable(Map<String, String> properties)
   {
      Grid grid = new Grid(properties.size() + 1, 2);
      grid.setBorderWidth(0);
      //grid.setSize("100%", "100%");
      DOM.setStyleAttribute(grid.getElement(), "borderStyle", "solid");
      DOM.setStyleAttribute(grid.getElement(), "borderWidth", "5px");
      DOM.setStyleAttribute(grid.getElement(), "borderColor", "transparent");

      Iterator<String> iterator = properties.keySet().iterator();
      int row = 0;
      while (iterator.hasNext())
      {
         String key = iterator.next();
         String value = properties.get(key);

         grid.setHTML(row, 0, "<b>" + key + ":&nbsp;&nbsp;</b>");
         grid.setText(row, 1, value);

         DOM.setStyleAttribute(grid.getCellFormatter().getElement(row, 0), "textAlign", "right");
         DOM.setStyleAttribute(grid.getCellFormatter().getElement(row, 1), "width", "100%");
         DOM.setElementAttribute(grid.getCellFormatter().getElement(row, 0), "nowrap", "nowrap");
         DOM.setElementAttribute(grid.getCellFormatter().getElement(row, 1), "nowrap", "nowrap");

         row++;
      }

      grid.setHTML(row, 0, "&nbsp;");
      grid.setHTML(row, 1, "&nbsp;");

      DOM.setStyleAttribute(grid.getRowFormatter().getElement(row), "height", "100%");
      return grid;
   }

   /**
    * @return the image
    */
   public Image getImage()
   {
      return image;
   }

}
