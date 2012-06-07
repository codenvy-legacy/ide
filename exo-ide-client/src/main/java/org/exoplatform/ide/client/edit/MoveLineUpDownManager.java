/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.edit;

import org.exoplatform.ide.client.edit.control.MoveLineDownControl;
import org.exoplatform.ide.client.edit.control.MoveLineUpControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.SelectionRange;

/**
 * 
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class MoveLineUpDownManager implements EditorMoveLineUpHandler, EditorMoveLineDownHandler,
   EditorActiveFileChangedHandler
{

   /**
    * Currenlty active editor
    */
   private Editor editor;

   public MoveLineUpDownManager()
   {
      IDE.getInstance().addControl(new MoveLineUpControl());
      IDE.getInstance().addControl(new MoveLineDownControl());

      IDE.addHandler(EditorMoveLineUpEvent.TYPE, this);
      IDE.addHandler(EditorMoveLineDownEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * Moves selection up
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownHandler#onEditorMoveLineDown(org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownEvent)
    */
   @Override
   public void onEditorMoveLineDown(EditorMoveLineDownEvent event)
   {
      if (editor == null || !editor.isCapable(EditorCapability.GO_TO_POSITION))
      {
         return;
      }

      int lines = editor.getNumberOfLines();
      int row = editor.getCursorRow();
      if (row == lines || row == lines - 1)
      {
         return;
      }

      SelectionRange selectionRange = editor.getSelectionRange();
      int start = selectionRange.getStartLine();
      int end = selectionRange.getEndLine();
      if (selectionRange.getEndSymbol() == 0 && selectionRange.getStartLine() != selectionRange.getEndLine())
      {
         end--;
      }

      if (start >= lines - 1 || end >= lines - 1)
      {
         return;
      }

      String nextLineText = editor.getLineText(end + 1);
      for (int i = end; i >= start; i--)
      {
         String lineText = editor.getLineText(i);
         editor.setLineText(i + 1, lineText);
      }

      editor.setLineText(start, nextLineText);
      editor.selectRange(start + 1, 0, end + 2, 0);
      editor.goToPosition(row + 1, 0);
   }

   /**
    * Moves selection down
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpHandler#onEditorMoveLineUp(org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpEvent)
    */
   @Override
   public void onEditorMoveLineUp(EditorMoveLineUpEvent event)
   {
      if (editor == null || !editor.isCapable(EditorCapability.GO_TO_POSITION))
      {
         return;
      }

      SelectionRange selectionRange = editor.getSelectionRange();
      int row = editor.getCursorRow();

      if (selectionRange.getStartLine() == 1)
      {
         return;
      }

      int start = selectionRange.getStartLine();
      int end = selectionRange.getEndLine();
      if (selectionRange.getEndSymbol() == 0 && selectionRange.getStartLine() != selectionRange.getEndLine())
      {
         end--;
      }

      String prevLineText = editor.getLineText(start - 1);
      for (int i = start; i <= end; i++)
      {
         String lineText = editor.getLineText(i);
         editor.setLineText(i - 1, lineText);
      }

      editor.setLineText(end, prevLineText);
      editor.selectRange(start - 1, 0, end, 0);
      editor.goToPosition(row - 1, 0);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      editor = event.getEditor();
   }

}
