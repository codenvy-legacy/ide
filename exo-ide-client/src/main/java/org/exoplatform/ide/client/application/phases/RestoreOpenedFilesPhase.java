/**
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
 *
 */

package org.exoplatform.ide.client.application.phases;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.editor.EditorUtil;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RestoreOpenedFilesPhase extends Phase implements ItemPropertiesReceivedHandler,
   FileContentReceivedHandler, ExceptionThrownHandler, EditorFileOpenedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationSettings applicationSettings;

   private List<String> filesToLoad;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private File fileToLoad;

   private Map<String, String> defaultEditors;

   private List<String> filesToOpen;

   private String activeFileURL;

   public RestoreOpenedFilesPhase(HandlerManager eventBus, ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.applicationSettings = applicationSettings;
      handlers = new Handlers(eventBus);

      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
   }

   @Override
   protected void execute()
   {
      filesToLoad = applicationSettings.getValueAsList("opened-files");
      if (filesToLoad == null)
      {
         filesToLoad = new ArrayList<String>();
         applicationSettings.setValue("opened-files", filesToLoad, Store.REGISTRY);
      }

      defaultEditors = applicationSettings.getValueAsMap("default-editors");
      if (defaultEditors == null)
      {
         defaultEditors = new LinkedHashMap<String, String>();
      }

      Window.alert("Files to preload: " + filesToLoad.size());

      preloadNextFile();
   }

   protected void preloadNextFile()
   {
      try
      {
         if (filesToLoad.size() == 0)
         {
            fileToLoad = null;
            handlers.removeHandlers();
            eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
            openFilesInEditor();
            return;
         }

         String href = filesToLoad.get(0);

         fileToLoad = new File(href);
         filesToLoad.remove(0);
         VirtualFileSystem.getInstance().getProperties(fileToLoad);
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      fileToLoad.setNewFile(false);
      fileToLoad.setContentChanged(false);
      VirtualFileSystem.getInstance().getContent(fileToLoad);
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      openedFiles.put(fileToLoad.getHref(), fileToLoad);
      preloadNextFile();
   }

   public void onError(ExceptionThrownEvent event)
   {
      openedFiles.remove(fileToLoad.getHref());
      preloadNextFile();
   }

   private void openFilesInEditor()
   {
      activeFileURL = applicationSettings.getValueAsString("active-file");
      filesToOpen = new ArrayList<String>(openedFiles.keySet());
      handlers.addHandler(EditorFileOpenedEvent.TYPE, this);
      openNextFileInEditor();
   }

   private void openNextFileInEditor()
   {
      if (filesToOpen.size() == 0)
      {
         handlers.removeHandlers();

         if (activeFileURL != null)
         {
            File activeFile = openedFiles.get(activeFileURL);
            if (activeFile != null)
            {
               eventBus.fireEvent(new EditorChangeActiveFileEvent(activeFile));
            }
         }

         return;
      }

      String fileURL = filesToOpen.get(0);
      filesToOpen.remove(0);

      File file = openedFiles.get(fileURL);

      try
      {
         String editorDescription = defaultEditors.get(file.getContentType());
         Editor editor = EditorUtil.getEditor(file.getContentType(), editorDescription);
         eventBus.fireEvent(new EditorOpenFileEvent(file, editor));
      }
      catch (EditorNotFoundException e)
      {
         e.printStackTrace();
      }

   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openNextFileInEditor();
   }

}
