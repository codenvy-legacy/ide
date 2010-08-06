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
package org.exoplatform.ide.client.outline;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperPresenter implements EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler
{
   interface Display
   {
      void show();

      void hide();

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private ApplicationSettings applicationSettings;

   public CodeHelperPresenter(HandlerManager bus)
   {
      eventBus = bus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      File file = event.getFile();
      if (file == null || file.getContentType() == null)
      {
         display.hide();
         return;
      }

      if (OutlineTreeGrid.haveOutline(file))
      {
         boolean show =
            applicationSettings.getValue("outline") == null ? false : (Boolean)applicationSettings.getValue("outline");
         if (show)
         {
            display.show();
         }
         else
         {
            display.hide();
         }
      }
      else
      {
         display.hide();
      }
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

}
