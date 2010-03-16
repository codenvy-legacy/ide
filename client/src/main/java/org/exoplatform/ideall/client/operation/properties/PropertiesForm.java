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
package org.exoplatform.ideall.client.operation.properties;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.operation.TabPanel;
import org.exoplatform.ideall.client.operation.properties.propertyeditor.PropertyEditor;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class PropertiesForm extends TabPanel implements PropertiesPresenter.Display
{

   private Canvas content;

   private PropertiesPresenter presenter;

   public PropertiesForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, false);
      this.eventBus = eventBus;
      
      presenter = new PropertiesPresenter(eventBus, context);
      presenter.bindDisplay(this);
   }
   
//   @Override
//   public void destroy()
//   {
//      //presenter.destroy();
//      super.destroy();
//   }

   public void refreshProperties(File file)
   {
      if (content != null)
      {
         content.removeFromParent();
         content.destroy();
      }

      if (file.getProperties().size() == 0)
      {
         content = new Label("There are no properties for this file.");
         content.setWidth100();
         content.setHeight100();
         content.setAlign(Alignment.CENTER);
      }
      else
      {
         content = new PropertyEditor(file);
      }

      addMember(content);
   }

   @Override
   public String getTitle()
   {
      return "<span>" + Canvas.imgHTML(Images.PropertiesPanel.ICON) + "&nbsp;Properties</span>";
   }

   @Override
   public String getId()
   {
      return "Properties";
   }

}
