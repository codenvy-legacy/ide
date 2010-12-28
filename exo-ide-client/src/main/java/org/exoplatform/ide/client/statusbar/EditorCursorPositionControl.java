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
package org.exoplatform.ide.client.statusbar;

import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.event.EditorCursorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorCursorActivityHandler;
import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.text.TextButton.TextAlignment;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.edit.event.GoToLineEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class EditorCursorPositionControl extends StatusTextControl implements IDEControl, EditorCursorActivityHandler,
   EditorActiveFileChangedHandler
{
   public static final String ID = "__editor_cursor_position";

   public EditorCursorPositionControl()
   {
      super(ID);

      setVisible(false);
      setEnabled(true);
      setSize(70);
      setFireEventOnSingleClick(true);
      //setText("&nbsp;");
      setTextAlignment(TextAlignment.CENTER);
      //setEvent(new GoToLineEvent());
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorCursorActivityEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   private void setCursorPosition(int row, int column)
   {
      setText("<nobr>" + row + " : " + column + "</nobr>");
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (event.getRow() > 0 && event.getColumn() > 0)
      {
         setEvent(new GoToLineEvent());
         setCursorPosition(event.getRow(), event.getColumn());
      }
      else
      {
         setEvent(null);
         setText("&nbsp;");
      }
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null || event.getEditor() == null)
      {
         setText("&nbsp;");
         setEvent(null);
         setVisible(false);
         return;
      }
      
      TextEditor editor = event.getEditor();
      if (editor.getCursorRow() > 0 && editor.getCursorCol() > 0)
      {
         setEvent(new GoToLineEvent());
         setCursorPosition(editor.getCursorRow(), editor.getCursorCol());
      }
      else
      {
         setEvent(null);
         setText("&nbsp;");
      }
      
      setVisible(true);      
   }
}
