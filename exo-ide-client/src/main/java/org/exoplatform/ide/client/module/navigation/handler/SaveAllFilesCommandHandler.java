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
package org.exoplatform.ide.client.module.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommandHandler implements ExceptionThrownHandler, SaveAllFilesHandler, EditorFileOpenedHandler, 
EditorFileClosedHandler, ApplicationSettingsReceivedHandler
{

   private Handlers handlers;

   private HandlerManager eventBus;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   private Map<String, String> lockTokens;

   public SaveAllFilesCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);
      this.eventBus.addHandler(SaveAllFilesEvent.TYPE, this);
      this.eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      this.eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      this.eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onSaveAllFiles(SaveAllFilesEvent event)
   {
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      saveNextUnsavedFile();
   }

   protected void saveNextUnsavedFile()
   {
      for (File file : openedFiles.values())
      {
         if (!file.isNewFile() && file.isContentChanged())
         {
            VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()), new FileContentSaveCallback(eventBus)
            {
               
               public void onResponseReceived(Request request, Response response)
               {
                  if (this.getFile().isPropertiesChanged())
                  {
                     String lockToken = lockTokens.get(this.getFile().getHref());
                     saveFileProperties(this.getFile(), lockToken);
                  }
                  else
                  {
                     eventBus.fireEvent(new FileSavedEvent(this.getFile(), null));
                     saveNextUnsavedFile();
                  }
               }
            });
            return;
         }
      }

      handlers.removeHandlers();
   }
   
   private void saveFileProperties(File file, String lockToken)
   {
      VirtualFileSystem.getInstance().saveProperties(file, lockToken, new ItemPropertiesCallback()
      {
         
         public void onResponseReceived(Request request, Response response)
         {
            eventBus.fireEvent(new FileSavedEvent((File)this.getItem(), null));
            saveNextUnsavedFile();
         }
         
         @Override
         public void fireErrorEvent()
         {
            eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed.<br>Resource not found."));
         }
      });
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      lockTokens = (Map<String, String>)event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
