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
import org.exoplatform.ideall.client.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ideall.client.event.file.SaveAllFilesEvent;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommand extends IDECommand implements EditorFileContentChangedHandler,
   FileContentSavedHandler, EditorActiveFileChangedHandler
{

   public static final String ID = "File/Save All Files";

   public static final String TITLE = "Save All Files";

   public SaveAllFilesCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setIcon(Images.MainMenu.SAVE_ALL);
      setEvent(new SaveAllFilesEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      setVisible(true);

      addHandler(EditorFileContentChangedEvent.TYPE, this);
      addHandler(FileContentSavedEvent.TYPE, this);
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   private void checkItemEnabling()
   {
      boolean enable = false;
      for (File file : context.getOpenedFiles().values())
      {
         if (!file.isNewFile() && file.isContentChanged())
         {
            enable = true;
            break;
         }
      }

      setEnabled(enable);
   }

   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      checkItemEnabling();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      checkItemEnabling();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      checkItemEnabling();
   }

}
