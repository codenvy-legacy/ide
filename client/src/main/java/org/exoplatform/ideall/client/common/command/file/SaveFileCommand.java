/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.common.command.file;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.FileContentChangedEvent;
import org.exoplatform.ideall.client.editor.event.FileContentChangedHandler;
import org.exoplatform.ideall.client.event.file.SaveFileEvent;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedHandler;
import org.exoplatform.ideall.client.operation.properties.event.FilePropertiesChangedEvent;
import org.exoplatform.ideall.client.operation.properties.event.FilePropertiesChangedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileCommand extends IDECommand implements EditorActiveFileChangedHandler, ItemPropertiesSavedHandler,
   FileContentChangedHandler, FilePropertiesChangedHandler, FileContentSavedHandler
{

   public static final String ID = "File/Save File";

   public static final String TITLE = "Save File";

   public SaveFileCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setIcon(Images.MainMenu.SAVE);
      setEvent(new SaveFileEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      setVisible(true);

      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(ItemPropertiesSavedEvent.TYPE, this);
      addHandler(FileContentChangedEvent.TYPE, this);
      addHandler(FilePropertiesChangedEvent.TYPE, this);
      addHandler(FileContentSavedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setEnabled(false);
         return;
      }

      if (event.getFile().isNewFile())
      {
         setEnabled(false);
      }
      else
      {
         if (event.getFile().isContentChanged() || event.getFile().isPropertiesChanged())
         {
            setEnabled(true);
         }
         else
         {
            setEnabled(false);
         }
      }
   }

   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      if (!(event.getItem() instanceof File))
      {
         return;
      }

      if ((File)event.getItem() != context.getActiveFile())
      {
         return;
      }

      File file = (File)event.getItem();

      if (file.isContentChanged())
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onFileContentChanged(FileContentChangedEvent event)
   {
      if (event.getFile().isNewFile())
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   public void onFilePropertiesChanged(FilePropertiesChangedEvent event)
   {
      if (event.getFile().isNewFile())
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (event.getFile() != context.getActiveFile())
      {
         return;
      }

      if (event.getFile().isPropertiesChanged())
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }

   }

}
