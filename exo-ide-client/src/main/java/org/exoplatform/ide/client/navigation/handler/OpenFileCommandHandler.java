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
// $codepro.audit.disable logExceptions
/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.navigation.handler;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Link;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;

/**
 * Handlers events for opening files.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileCommandHandler implements OpenFileHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
   ApplicationSettingsReceivedHandler
{
   private HandlerManager eventBus;

   private String selectedEditor;

   private ApplicationSettings applicationSettings;

   /**
    * Need for versions. 
    * 
    * Number of errors, which we will ignore
    * or number of attempts to get versions.
    */
   private int ignoreErrorsCount = 0;

   private FileModel fileToOpenOnError;

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   public OpenFileCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(OpenFileEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.OpenFileHandler#onOpenFile(org.exoplatform.ide.client.framework.event.OpenFileEvent)
    */
   public void onOpenFile(OpenFileEvent event)
   {
      ignoreErrorsCount = event.getIgnoreErrorsCount();
      selectedEditor = event.getEditor();

      FileModel file = event.getFile();
      if (file != null)
      {
         if (!file.isPersisted())
         {
            openFile(file);
            return;
         }

         //TODO Check opened file!!!
         if (openedFiles.containsKey(file.getId()))
         {
            openFile(file);
            return;
         }
      }
      else
      {
         file = new FileModel();
         file.setId(event.getFileId());
      }

      getFileProperties(file);

   }

   private void getFileProperties(FileModel file)
   {
      try
      {
         VirtualFileSystem.getInstance().getItemByLocation(file.getLinkByRelation(Link.REL_SELF).getHref(),
            new AsyncRequestCallback<FileModel>(new FileUnmarshaller(file))
            {
               @Override
               protected void onSuccess(FileModel result)
               {
                  FileModel f = (FileModel)result;

                  if (f.getContent() != null)
                  {
                     openFile(f);
                     return;
                  }
                  getFileContent(f);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
                  eventBus.fireEvent(new ExceptionThrownEvent(exception,
                     "Service is not deployed.<br>Parent folder not found."));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Parent folder not found."));
      }
   }

   private void getFileContent(FileModel file)
   {
      fileToOpenOnError = file;
      //      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
      try
      {
         VirtualFileSystem.getInstance().getContent(
            new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file))
            {
               @Override
               protected void onSuccess(FileModel result)
               {
                  openFile(result);
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
   }

   private void openFile(FileModel file)
   {
      try
      {
         if (selectedEditor == null)
         {
            Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
            if (defaultEditors != null)
            {
               selectedEditor = defaultEditors.get(file.getMimeType());
            }
         }

         EditorProducer producer = EditorFactory.getEditorProducer(file.getMimeType(), selectedEditor);
         eventBus.fireEvent(new EditorOpenFileEvent(file, producer));
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError(
            IDE.IDE_LOCALIZATION_MESSAGES.openFileCantFindEditorForType(file.getMimeType()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

}
