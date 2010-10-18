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
package org.exoplatform.ide.client.operation.properties;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.ImageUtil;
import org.exoplatform.ide.client.framework.ui.TabPanel;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.operation.properties.propertyeditor.PropertyEditor;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
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

   public PropertiesForm(HandlerManager eventBus)
   {
      super(eventBus, false);
      this.eventBus = eventBus;

      presenter = new PropertiesPresenter(eventBus);
      presenter.bindDisplay(this);
   }

//      @Override
//      public void destroy()
//      {
//         presenter.destroy();
//         super.destroy();
//      }

   public void refreshProperties(File file)
   {
//      clear(); //bug with many refresh, content not shown! 
      
      if (content != null)
      {
         content.hide();
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
      Image image = new Image(IDEImageBundle.INSTANCE.properties());
      String html = ImageUtil.getHTML(image);
      return "<span>" + html + "&nbsp;Properties</span>";
   }

   @Override
   public String getId()
   {
      return "Properties";
   }

}
