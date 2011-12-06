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
package org.exoplatform.ide.client.operation.gotoline;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.component.TextButton.TextAlignment;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class CursorPositionControl extends StatusTextControl implements IDEControl, EditorCursorActivityHandler,
   EditorActiveFileChangedHandler
{

   public static final String ID = "__editor_cursor_position";

   private FileModel file;

   private Editor editor;

   /**
    * 
    */
   public CursorPositionControl()
   {
      super(ID);

      setSize(70);
      setFireEventOnSingleClick(true);
      setText("&nbsp;");
      setTextAlignment(TextAlignment.CENTER);
      setEvent(new GoToLineEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorCursorActivityEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      setEnabled(true);
   }

   /**
    * @param row
    * @param column
    */
   private void setCursorPosition(int row, int column)
   {
      if (row > 0 && column > 0)
      {
         setText("<nobr>" + row + " : " + column + "</nobr>");
      }
      else
      {
         setText("");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   @Override
   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (editor == null || !editor.getEditorId().equals(event.getEditorId())) {
         return;
      }
      
      setCursorPosition(event.getRow(), event.getColumn());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      file = event.getFile();
      editor = event.getEditor();      
      Scheduler.get().scheduleDeferred(updateCursorPositionCommand);
   }

   ScheduledCommand updateCursorPositionCommand = new ScheduledCommand()
   {
      @Override
      public void execute()
      {
         try
         {
            if (file == null || editor == null)
            {
               setText("");
               setVisible(false);
               return;
            }

            setVisible(true);
            setCursorPosition(editor.getCursorRow(), editor.getCursorCol());
         }
         catch (Throwable e)
         {
            e.printStackTrace();
         }
      }
   };

}
