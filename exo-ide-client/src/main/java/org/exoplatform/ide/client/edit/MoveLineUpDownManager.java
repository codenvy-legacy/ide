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

import org.exoplatform.gwtframework.commons.util.Log;
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
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IRegion;
import org.exoplatform.ide.editor.text.edits.DeleteEdit;
import org.exoplatform.ide.editor.text.edits.InsertEdit;
import org.exoplatform.ide.editor.text.edits.MultiTextEdit;

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
    * Currenlty active editor.
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
    * Moves selection up.
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownHandler#onEditorMoveLineDown(org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownEvent)
    */
   @Override
   public void onEditorMoveLineDown(EditorMoveLineDownEvent event)
   {
      if (editor == null || !editor.isCapable(EditorCapability.SET_CURSOR_POSITION))
      {
         return;
      }

      int lines = editor.getDocument().getNumberOfLines();
      int cursorRow = editor.getCursorRow();
      int cursorColumn = editor.getCursorColumn();
      if (cursorRow == lines || cursorRow + 1 > lines)
      {
         return;
      }

      SelectionRange selectionRange = editor.getSelectionRange();

      int startLineNum = selectionRange.getStartLine();
      int endLineNum = selectionRange.getEndLine();
      int endSymbol = selectionRange.getEndSymbol();
      // when user select text from down to up in CollabEditor
      if (startLineNum > endLineNum)
      {
         startLineNum = selectionRange.getEndLine();
         endLineNum = selectionRange.getStartLine();
         endSymbol = selectionRange.getStartSymbol();
      }

      if (endSymbol == 0 && startLineNum != endLineNum)
      {
         endLineNum--;
      }
      if (startLineNum >= lines || endLineNum >= lines)
      {
         return;
      }

      IDocument document = editor.getDocument();
      MultiTextEdit multiEdit = new MultiTextEdit();
      try
      {
         IRegion nextLineInformation = document.getLineInformation(endLineNum);
         String nextLineContent = document.get(nextLineInformation.getOffset(), nextLineInformation.getLength());
         int nextLineOffset = nextLineInformation.getOffset();
         int nextLineLength = nextLineInformation.getLength();
         if (document.getLineDelimiter(endLineNum) != null)
         {
            nextLineLength++;
         }

         multiEdit.addChild(new InsertEdit(document.getLineOffset(startLineNum - 1), nextLineContent + "\n"));
         multiEdit.addChild(new DeleteEdit(nextLineOffset, nextLineLength));
         multiEdit.apply(document);

         if (editor instanceof CodeMirror)
         {
            editor.selectRange(startLineNum + 1, 0, endLineNum + 2, 0);
         }
      }
      catch (BadLocationException e)
      {
         Log.info(e.getMessage());
      }
   }

   /**
    * Moves selection down.
    * 
    * @see org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpHandler#onEditorMoveLineUp(org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpEvent)
    */
   @Override
   public void onEditorMoveLineUp(EditorMoveLineUpEvent event)
   {
      if (editor == null || !editor.isCapable(EditorCapability.SET_CURSOR_POSITION))
      {
         return;
      }

      SelectionRange selectionRange = editor.getSelectionRange();
      int cursorRow = editor.getCursorRow();
      int cursorColumn = editor.getCursorColumn();

      int startLineNum = selectionRange.getStartLine();
      int endLineNum = selectionRange.getEndLine();
      int endSymbol = selectionRange.getEndSymbol();
      // when user select text from down to up in CollabEditor
      if (startLineNum > endLineNum)
      {
         startLineNum = selectionRange.getEndLine();
         endLineNum = selectionRange.getStartLine();
         endSymbol = selectionRange.getStartSymbol();
      }

      if (startLineNum == 1)
      {
         return;
      }

      if (endSymbol == 0 && startLineNum != endLineNum)
      {
         endLineNum--;
      }

      IDocument document = editor.getDocument();
      MultiTextEdit multiEdit = new MultiTextEdit();
      try
      {
         IRegion previousLineInformation = document.getLineInformation(startLineNum - 2);
         String previousLineContent = document.get(previousLineInformation.getOffset(), previousLineInformation.getLength());
         int previousLineOffset = previousLineInformation.getOffset();
         int previousLineLength = previousLineInformation.getLength() + 1;

         multiEdit.addChild(new InsertEdit(document.getLineOffset(endLineNum), previousLineContent + "\n"));
         multiEdit.addChild(new DeleteEdit(previousLineOffset, previousLineLength));
         multiEdit.apply(document);

         if (editor instanceof CodeMirror)
         {
            editor.selectRange(startLineNum - 1, 0, endLineNum, 0);
         }
      }
      catch (BadLocationException e)
      {
         Log.info(e.getMessage());
      }
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
