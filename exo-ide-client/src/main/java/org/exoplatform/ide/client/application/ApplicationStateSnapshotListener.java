/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class ApplicationStateSnapshotListener implements EditorFileOpenedHandler, EditorFileClosedHandler,
   EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private ApplicationSettings applicationSettings;

   public ApplicationStateSnapshotListener(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
      System.out.println("LISTENER > application settings received");
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      System.out.println("LISTENER > file opened");
      storeOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      System.out.println("LISTENER > file closed");
      storeOpenedFiles();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      System.out.println("LISTENER > active file changed");
      storeOpenedFiles();
      storeActiveFile(event.getFile());
   }

   private void storeOpenedFiles()
   {
      List<String> files = new ArrayList<String>();

      Iterator<String> openedFilesIter = openedFiles.keySet().iterator();
      while (openedFilesIter.hasNext())
      {
         String fileName = openedFilesIter.next();

         File file = openedFiles.get(fileName);
         if (file.isNewFile())
         {
            continue;
         }

         files.add(fileName);
      }

      applicationSettings.setValue("opened-files", files, Store.COOKIES);
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
   }

   private void storeActiveFile(File file)
   {
      String activeFile = "";
      if (file != null)
      {
         activeFile = file.getHref();
      }

      applicationSettings.setValue("active-file", activeFile, Store.COOKIES);
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
   }

}
