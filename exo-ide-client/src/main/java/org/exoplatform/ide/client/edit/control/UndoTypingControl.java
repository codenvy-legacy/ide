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
package org.exoplatform.ide.client.edit.control;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorUndoTypingEvent;
import org.exoplatform.ide.client.framework.vfs.Version;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class UndoTypingControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   EditorFileContentChangedHandler
{

   public static final String ID = "Edit/Undo Typing";

   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.undoTypingControl();

   public UndoTypingControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.undo(), IDEImageBundle.INSTANCE.undoDisabled());
      setEvent(new EditorUndoTypingEvent());
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(EditorFileContentChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || (event.getFile() instanceof Version))
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

}
