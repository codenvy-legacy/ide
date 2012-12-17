// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.texteditor;

import org.exoplatform.ide.text.store.Line;
import org.exoplatform.ide.text.store.LineInfo;
import org.exoplatform.ide.text.store.Position;
import org.exoplatform.ide.text.store.util.LineUtils;
import org.exoplatform.ide.texteditor.input.CommonActions;
import org.exoplatform.ide.texteditor.input.DefaultActionExecutor;
import org.exoplatform.ide.texteditor.input.InputScheme;
import org.exoplatform.ide.texteditor.input.Shortcut;
import org.exoplatform.ide.texteditor.selection.SelectionModel;
import org.exoplatform.ide.util.input.SignalEvent;

/**
 * Implementation of some common textual actions.
 */
public class TextActions extends DefaultActionExecutor
{

   public static final TextActions INSTANCE = new TextActions();

   private TextActions()
   {
      addAction(CommonActions.SPLIT_LINE, new Shortcut()
      {
         @Override
         public boolean event(InputScheme scheme, SignalEvent event)
         {
            splitLine(scheme.getInputController().getEditor());
            return true;
         }
      });

      addAction(CommonActions.START_NEW_LINE, new Shortcut()
      {
         @Override
         public boolean event(InputScheme scheme, SignalEvent event)
         {
            startNewLine(scheme.getInputController().getEditor());
            return true;
         }
      });
   }

   private void startNewLine(TextEditorViewImpl editor)
   {
      SelectionModel selection = editor.getSelection();
      selection.deselect();
      Line line = selection.getCursorLine();
      int lineNumber = selection.getCursorLineNumber();
      int lastCursorColumn = LineUtils.getLastCursorColumn(line);
      selection.setCursorPosition(new LineInfo(line, lineNumber), lastCursorColumn);
      editor.getEditorDocumentMutator().insertText(line, lineNumber, lastCursorColumn, "\n");
   }

   private void splitLine(TextEditorViewImpl editor)
   {
      // TODO: Add language specific logic (i.e. string splitting).
      SelectionModel selection = editor.getSelection();
      Position[] selectionRange = selection.getSelectionRange(false);
      Position cursor = selectionRange[0];

      editor.getEditorDocumentMutator().insertText(cursor.getLine(), cursor.getLineNumber(), cursor.getColumn(), "\n",
         true);
      selection.setCursorPosition(cursor.getLineInfo(), cursor.getColumn());
   }
}
