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
package org.exoplatform.ideall.client.statusbar;

import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.event.EditorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorActivityHandler;
import org.exoplatform.gwtframework.ui.client.component.command.StatusTextCommand;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.event.edit.GoToLineEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class EditorCursorPositionControl extends StatusTextCommand implements EditorActivityHandler,
   EditorActiveFileChangedHandler
{
   public static final String ID = "__editor_cursor_position";

   private ApplicationContext context;

   public EditorCursorPositionControl(HandlerManager eventBus, ApplicationContext context)
   {
      super(ID);

      this.context = context;

      setVisible(true);
      setEnabled(true);
      setSize(80);

      setText("&nbsp;");
      
      setEvent(new GoToLineEvent());

      eventBus.addHandler(EditorActivityEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   private void setCursorPosition(int row, int col)
   {
      String html =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height: 16px;\"><tr>"
            + "</td><td style=\"border: none; font-family:Verdana,Bitstream Vera Sans,sans-serif; font-size:11px; font-style:normal; \">&nbsp;"
            + row + " : " + col + "</td></tr></table>";
      setText(html);
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   public void onEditorActivity(EditorActivityEvent event)
   {
      if (event.getRow() > 0 && event.getColumn() > 0)
      {
         setCursorPosition(event.getRow(), event.getColumn());
      }
      else
      {
         setText("&nbsp;");
      }
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      TextEditor editor = event.getEditor();

      if (editor == null)
      {
         setText("&nbsp;");
         return;
      }
            
      if(editor.getCursorRow() > 0 && editor.getCursorCol() > 0)
      {
          setCursorPosition(editor.getCursorRow(), editor.getCursorCol());
      }
      else
      {
         setText("&nbsp;");
      }
   }

}
