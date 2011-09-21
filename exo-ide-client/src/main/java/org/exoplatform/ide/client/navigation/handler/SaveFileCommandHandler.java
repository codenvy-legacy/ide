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
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.event.SaveFileHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileCommandHandler implements SaveFileHandler, EditorActiveFileChangedHandler,
   ApplicationSettingsReceivedHandler
{

   private HandlerManager eventBus;

   private FileModel activeFile;

   private Map<String, String> lockTokens;

   public SaveFileCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(SaveFileEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onSaveFile(SaveFileEvent event)
   {
      final FileModel file = event.getFile() != null ? event.getFile() : activeFile;

      if (!file.isPersisted())
      {
         eventBus.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.YES_CANCEL, null, null));
         return;
      }

      String lockToken = lockTokens.get(file.getId());

      if (file.isContentChanged())
      {
         try
         {
            VirtualFileSystem.getInstance().updateContent(file, new AsyncRequestCallback<FileModel>()
            {

               @Override
               protected void onSuccess(FileModel result)
               {
                  getProperties(file);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception,
                     "Service is not deployed.<br>Resource not found."));
               }
            });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            eventBus.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Resource not found."));
         }
         return;
      }
      else
      {
         //TODO 
         //         if (file.isPropertiesChanged())
         //         {
         //            VirtualFileSystem.getInstance().saveProperties(file, lockToken, new ItemPropertiesCallback()
         //            {
         //               @Override
         //               protected void onSuccess(Item result)
         //               {
         //                  eventBus.fireEvent(new FileSavedEvent((FileModel)result, null));
         //               }
         //            });
         //            return;
         //         }
      }
      eventBus.fireEvent(new FileSavedEvent(file, null));
   }

   private void getProperties(final FileModel file)
   {
      //TODO
      try
      {
         VirtualFileSystem.getInstance().getItemByLocation(file.getLinkByRelation(Link.REL_SELF).getHref(),
            new AsyncRequestCallback<FileModel>(new FileUnmarshaller(file))
            {
               @Override
               protected void onSuccess(FileModel result)
               {
                  eventBus.fireEvent(new FileSavedEvent(result, null));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
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

      lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
   }

}
