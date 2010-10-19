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
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.panel.SimpleTabPanel;
import org.exoplatform.ide.client.panel.event.ClosePanelEvent;
import org.exoplatform.ide.client.panel.event.ClosePanelHandler;
import org.exoplatform.ide.client.panel.event.OpenPanelEvent;
import org.exoplatform.ide.client.panel.event.OpenPanelHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperPresenter implements EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler, OpenPanelHandler, ClosePanelHandler
{
   interface Display
   {
      void show();

      void hide();
      
      void addPanel(SimpleTabPanel panel);
      
      void closePanel(String panelId);
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
      handlers.addHandler(OpenPanelEvent.TYPE, this);
      handlers.addHandler(ClosePanelEvent.TYPE, this);
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

      if(!event.getEditor().canCreateTokenList())
      {
         display.hide();
         return;
      }
      
      if (OutlineTreeGrid.haveOutline(file))
      {
         boolean show =
            applicationSettings.getValueAsBoolean("outline") == null ? false : applicationSettings.getValueAsBoolean("outline");
//         System.out.println("CodeHelperPresenter.onEditorActiveFileChanged()"+show);
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

   /**
    * @see org.exoplatform.ide.client.panel.event.OpenPanelHandler#onOpenPanel(org.exoplatform.ide.client.panel.event.OpenPanelEvent)
    */
   public void onOpenPanel(OpenPanelEvent event)
   {
      display.show();
      display.addPanel(event.getPanel());
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.ClosePanelHandler#onClosePanel(org.exoplatform.ide.client.panel.event.ClosePanelEvent)
    */
   public void onClosePanel(ClosePanelEvent event)
   {
      display.closePanel(event.getPanelId());
   }

}
