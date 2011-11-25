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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.AllFilesSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesHandler;
import org.exoplatform.ide.client.navigation.control.SaveAllFilesControl;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommandHandler implements SaveAllFilesHandler, EditorFileOpenedHandler,
   EditorFileClosedHandler
{

   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   public SaveAllFilesCommandHandler()
   {
      IDE.getInstance().addControl(new SaveAllFilesControl());
      
      IDE.addHandler(SaveAllFilesEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
   }

   private List<FileModel> savedFiles = new ArrayList<FileModel>();

   public void onSaveAllFiles(SaveAllFilesEvent event)
   {
      savedFiles.clear();
      saveNextFile();
   }

   private void saveNextFile()
   {
      final FileModel fileToSave = getUnsavedFile();
      if (fileToSave == null)
      {
         IDE.fireEvent(new AllFilesSavedEvent());
         return;
      }

      try
      {
         VirtualFileSystem.getInstance().updateContent(fileToSave, new AsyncRequestCallback<FileModel>()
         {
            @Override
            protected void onSuccess(FileModel result)
            {
               savedFiles.add(fileToSave);
               fileToSave.setContentChanged(false);
               IDE.fireEvent(new FileSavedEvent(fileToSave, null));
               saveNextFile();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               exception.printStackTrace();
               IDE.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Resource not found."));
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         IDE.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Resource not found."));
      }

   }

   private FileModel getUnsavedFile()
   {
      for (FileModel file : openedFiles.values())
      {
         if (file.isContentChanged() && file.isPersisted())
         {
            return file;
         }
      }

      return null;
   }

   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }


}
