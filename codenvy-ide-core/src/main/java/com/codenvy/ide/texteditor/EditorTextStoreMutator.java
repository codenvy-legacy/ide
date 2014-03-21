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
package com.codenvy.ide.texteditor;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentCommand;
import com.codenvy.ide.text.edits.DeleteEdit;
import com.codenvy.ide.text.edits.InsertEdit;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.Position;
import com.codenvy.ide.text.store.TextChange;
import com.codenvy.ide.text.store.TextStoreMutator;
import com.codenvy.ide.text.store.util.LineUtils;
import com.codenvy.ide.texteditor.api.BeforeTextListener;
import com.codenvy.ide.texteditor.api.TextListener;
import com.codenvy.ide.texteditor.api.UndoManager;
import com.codenvy.ide.texteditor.selection.SelectionModel;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.loging.Log;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class EditorTextStoreMutator implements TextStoreMutator {

    private TextEditorViewImpl editor;

    private final ListenerManager<BeforeTextListener> beforeTextListenerManager = ListenerManager.create();

    private final ListenerManager<TextListener> textListenerManager = ListenerManager.create();

    private final DocumentCommand documentCommand = new DocumentCommand();

    /** @param editor */
    public EditorTextStoreMutator(TextEditorViewImpl editor) {
        this.editor = editor;
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#deleteText(com.codenvy.ide.text.store.Line, int, int) */
    @Override
    public TextChange deleteText(Line line, int column, int deleteCount) {
        return deleteText(line, line.getDocument().getLineFinder().findLine(line).number(), column, deleteCount);
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#deleteText(com.codenvy.ide.text.store.Line, int, int, int) */
    @Override
    public TextChange deleteText(Line line, int lineNumber, int column, int deleteCount) {
        if (editor.isReadOnly()) {
            return null;
        }
        Document document = editor.getDocument();
        try {
            int lineOffset = document.getLineOffset(lineNumber);
            documentCommand.initialize(lineOffset + column, deleteCount, "");
            editor.customizeDocumentCommand(documentCommand);


            DeleteEdit delete = new DeleteEdit(documentCommand.offset, documentCommand.length);
            TextChange textChange =
                    TextChange.createDeletion(line, lineNumber, column, document.get(documentCommand.offset, documentCommand.length));
            delete.apply(document);
            dispatchTextChange(textChange);

        } catch (BadLocationException e) {
            Log.error(EditorTextStoreMutator.class, e);
        }
        return null;
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#insertText(com.codenvy.ide.text.store.Line, int, java.lang.String) */
    @Override
    public TextChange insertText(Line line, int column, String text) {
        return insertText(line, line.getDocument().getLineFinder().findLine(line).number(), column, text);
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#insertText(com.codenvy.ide.text.store.Line, int, int, java.lang.String) */
    @Override
    public TextChange insertText(Line line, int lineNumber, int column, String text) {
        return insertText(line, lineNumber, column, text, true);
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#insertText(com.codenvy.ide.text.store.Line, int, int, java.lang.String, boolean) */
    @Override
    public TextChange insertText(Line line, int lineNumber, int column, String text, boolean canReplaceSelection) {
        if (editor.isReadOnly()) {
            return null;
        }
        TextChange textChange = null;
        SelectionModel selection = editor.getSelection();
        if (canReplaceSelection && selection.hasSelection()) {
            Position[] selectionRange = selection.getSelectionRange(true);
            Line beginLine = selectionRange[0].getLine();
            int beginLineNumber = selectionRange[0].getLineNumber();
            int beginColumn = selectionRange[0].getColumn();
            String textToDelete =
                    LineUtils.getText(beginLine, beginColumn, selectionRange[1].getLine(), selectionRange[1].getColumn());
            textChange = deleteText(beginLine, beginLineNumber, beginColumn, textToDelete.length());

            // The insertion should go where the selection was
            line = beginLine;
            lineNumber = beginLineNumber;
            column = beginColumn;
        }

        if (text.length() == 0) {
            return textChange;
        }

        Document document = editor.getDocument();
        try {
            int lineOffset = document.getLineOffset(lineNumber);
            documentCommand.initialize(lineOffset + column, 0, text);
            editor.customizeDocumentCommand(documentCommand);
            if(documentCommand.length != 0){
                DeleteEdit deleteEdit = new DeleteEdit(documentCommand.offset, documentCommand.length);
                deleteEdit.apply(document);
            }

            InsertEdit insert = new InsertEdit(documentCommand.offset, documentCommand.text);
            insert.apply(document);
            textChange = TextChange.createInsertion(line, lineNumber, column, line, lineNumber, text);
            dispatchTextChange(textChange);
            if (documentCommand.caretOffset > 0) {
                editor.getSelection().setCursorPosition(documentCommand.caretOffset);
            }

        } catch (BadLocationException e) {
            Log.error(EditorTextStoreMutator.class, e);
        }
        return null;
    }

    void dispatchTextChange(final TextChange textChange) {
        textListenerManager.dispatch(new Dispatcher<TextListener>() {
            @Override
            public void dispatch(TextListener listener) {
                listener.onTextChange(textChange);
            }
        });
    }

    /** @return  */
    public ListenerRegistrar<BeforeTextListener> getBeforeTextListenerRegistrar() {
        return beforeTextListenerManager;
    }

    /** @return  */
    public ListenerRegistrar<TextListener> getTextListenerRegistrar() {
        return textListenerManager;
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#getUndoManager() */
    @Override
    public UndoManager getUndoManager() {
        return editor.getUndoManager();
    }

}
