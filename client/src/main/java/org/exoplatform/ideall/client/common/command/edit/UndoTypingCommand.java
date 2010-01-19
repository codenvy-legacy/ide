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
package org.exoplatform.ideall.client.common.command.edit;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.SimpleCommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.FileContentChangedEvent;
import org.exoplatform.ideall.client.editor.event.FileContentChangedHandler;
import org.exoplatform.ideall.client.event.edit.UndoEditingEvent;
import org.exoplatform.ideall.client.event.file.FileCreatedEvent;
import org.exoplatform.ideall.client.event.file.FileCreatedHandler;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UndoTypingCommand extends SimpleCommand implements EditorActiveFileChangedHandler,
   FileContentChangedHandler, FileCreatedHandler, FileContentReceivedHandler
{

   public UndoTypingCommand()
   {
      super("Edit/Undo Typing", "Undo Typing", Images.MainMenu.UNDO, new UndoEditingEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(FileContentChangedEvent.TYPE, this);
      addHandler(FileCreatedEvent.TYPE, this);
      addHandler(FileContentReceivedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      setVisible(true);
      setEnabled(event.hasUndoChanges());
   }

   public void onFileContentChanged(FileContentChangedEvent event)
   {
      setEnabled(event.hasUndoChanges());
   }

   public void onFileCreated(FileCreatedEvent event)
   {
      setVisible(true);
      setEnabled(false);
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      setVisible(true);
      setEnabled(false);
   }

}
