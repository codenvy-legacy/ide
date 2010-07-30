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
package org.exoplatform.ideall.client.module.edit.control;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ideall.client.module.edit.event.UndoEditingEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.FileContentReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UndoTypingCommand extends IDEControl implements EditorActiveFileChangedHandler,
   EditorFileContentChangedHandler, FileContentReceivedHandler
{

   public static final String ID = "Edit/Undo Typing";

   public static final String TITLE = "Undo Typing";

   public UndoTypingCommand(HandlerManager eventBus)
   {
      super(ID, eventBus);
      setTitle(TITLE);
      setPrompt(TITLE);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.undo(), IDEImageBundle.INSTANCE.undoDisabled());
      setEvent(new UndoEditingEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(EditorActiveFileChangedEvent.TYPE, this);
      addHandler(EditorFileContentChangedEvent.TYPE, this);
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
      if (event.getEditor() != null)
      {
         setEnabled(event.getEditor().hasUndoChanges());
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onEditorFileContentChanged(EditorFileContentChangedEvent event)
   {
      setEnabled(event.hasUndoChanges());
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      setVisible(true);
      setEnabled(false);
   }

}
