/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.edit;

import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.client.edit.control.MoveLineDownControl;
import org.exoplatform.ide.client.edit.control.MoveLineUpControl;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.edits.DeleteEdit;
import org.exoplatform.ide.editor.shared.text.edits.InsertEdit;
import org.exoplatform.ide.editor.shared.text.edits.MultiTextEdit;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class MoveLineUpDownManager implements EditorMoveLineUpHandler, EditorMoveLineDownHandler,
                                              EditorActiveFileChangedHandler {

    /** Currenlty active editor. */
    private Editor editor;

    public MoveLineUpDownManager() {
        IDE.getInstance().addControl(new MoveLineUpControl());
        IDE.getInstance().addControl(new MoveLineDownControl());

        IDE.addHandler(EditorMoveLineUpEvent.TYPE, this);
        IDE.addHandler(EditorMoveLineDownEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /**
     * Moves selection up.
     *
     * @see org.exoplatform.ide.client.framework.editor.event.EditorMoveLineDownHandler#onEditorMoveLineDown(org.exoplatform.ide.client
     * .framework.editor.event.EditorMoveLineDownEvent)
     */
    @Override
    public void onEditorMoveLineDown(EditorMoveLineDownEvent event) {
        if (editor == null || !editor.isCapable(EditorCapability.SET_CURSOR_POSITION)) {
            return;
        }

        int lines = editor.getDocument().getNumberOfLines();
        int cursorRow = editor.getCursorRow();
        int cursorColumn = editor.getCursorColumn();
        if (cursorRow == lines || cursorRow + 1 > lines) {
            return;
        }

        SelectionRange selectionRange = editor.getSelectionRange();

        int startLineNum = selectionRange.getStartLine();
        int endLineNum = selectionRange.getEndLine();
        int endSymbol = selectionRange.getEndSymbol();
        // when user select text from down to up in CollabEditor
        if (startLineNum > endLineNum) {
            startLineNum = selectionRange.getEndLine();
            endLineNum = selectionRange.getStartLine();
            endSymbol = selectionRange.getStartSymbol();
        }

        if (endSymbol == 0 && startLineNum != endLineNum) {
            endLineNum--;
        }
        if (startLineNum >= lines || endLineNum >= lines) {
            return;
        }

        IDocument document = editor.getDocument();
        MultiTextEdit multiEdit = new MultiTextEdit();
        try {
            IRegion nextLineInformation = document.getLineInformation(endLineNum);
            String nextLineContent = document.get(nextLineInformation.getOffset(), nextLineInformation.getLength());
            int nextLineOffset = nextLineInformation.getOffset();
            int nextLineLength = nextLineInformation.getLength();
            if (document.getLineDelimiter(endLineNum) != null) {
                nextLineLength++;
            }

            multiEdit.addChild(new InsertEdit(document.getLineOffset(startLineNum - 1), nextLineContent + "\n"));
            multiEdit.addChild(new DeleteEdit(nextLineOffset, nextLineLength));
            multiEdit.apply(document);

            if (editor instanceof CodeMirror) {
                editor.selectRange(startLineNum + 1, 0, endLineNum + 2, 0);
            }
        } catch (BadLocationException e) {
            Log.info(e.getMessage());
        }
    }

    /**
     * Moves selection down.
     *
     * @see org.exoplatform.ide.client.framework.editor.event.EditorMoveLineUpHandler#onEditorMoveLineUp(org.exoplatform.ide.client
     * .framework.editor.event.EditorMoveLineUpEvent)
     */
    @Override
    public void onEditorMoveLineUp(EditorMoveLineUpEvent event) {
        if (editor == null || !editor.isCapable(EditorCapability.SET_CURSOR_POSITION)) {
            return;
        }

        SelectionRange selectionRange = editor.getSelectionRange();
        int cursorRow = editor.getCursorRow();
        int cursorColumn = editor.getCursorColumn();

        int startLineNum = selectionRange.getStartLine();
        int endLineNum = selectionRange.getEndLine();
        int endSymbol = selectionRange.getEndSymbol();
        // when user select text from down to up in CollabEditor
        if (startLineNum > endLineNum) {
            startLineNum = selectionRange.getEndLine();
            endLineNum = selectionRange.getStartLine();
            endSymbol = selectionRange.getStartSymbol();
        }

        if (startLineNum == 1) {
            return;
        }

        if (endSymbol == 0 && startLineNum != endLineNum) {
            endLineNum--;
        }

        IDocument document = editor.getDocument();
        MultiTextEdit multiEdit = new MultiTextEdit();
        try {
            IRegion previousLineInformation = document.getLineInformation(startLineNum - 2);
            String previousLineContent = document.get(previousLineInformation.getOffset(), previousLineInformation.getLength());
            int previousLineOffset = previousLineInformation.getOffset();
            int previousLineLength = previousLineInformation.getLength() + 1;

            multiEdit.addChild(new InsertEdit(document.getLineOffset(endLineNum), previousLineContent + "\n"));
            multiEdit.addChild(new DeleteEdit(previousLineOffset, previousLineLength));
            multiEdit.apply(document);

            if (editor instanceof CodeMirror) {
                editor.selectRange(startLineNum - 1, 0, endLineNum, 0);
            }
        } catch (BadLocationException e) {
            Log.info(e.getMessage());
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editor = event.getEditor();
    }

}
