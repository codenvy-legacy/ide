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
package com.google.collide.client;

import com.codenvy.ide.client.util.logging.Log;
import com.google.collide.client.CollabEditor.TextListenerImpl;
import com.google.collide.client.editor.EditorDocumentMutator;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.DocumentMutator;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.LineInfo;

import org.exoplatform.ide.editor.client.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.DocumentEvent;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IDocumentListener;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DocumentAdaptor implements IDocumentListener {

    private DocumentMutator mutator;

    private Document editorDocument;

    /** Listener for updating an appropriate IDocument instance. */
    private TextListenerImpl textListener;

    private CollabEditor collabEditor;

    /** @see org.exoplatform.ide.editor.shared.text.IDocumentListener#documentChanged(org.exoplatform.ide.editor.shared.text
     * .DocumentEvent) */
    @Override
    public void documentChanged(final DocumentEvent event) {
//      mutator.insertText(editorDocument.getFirstLine(), 0, 0, "it's alive");
        try {
            // Temporary disable textListener to prevent updating an appropriate IDocument instance again.
            //
            // Do not use editorDocument.getTextListenerRegistrar().remove(textListener)
            // and editorDocument.getTextListenerRegistrar().add(textListener)
            // because textListener must be always the first listener for the editorDocument.
            textListener.setIgnoreTextChanges(true);

            IDocument document = event.getDocument();
            int lineNumber = document.getLineOfOffset(event.getOffset());
            int col = event.getOffset() - document.getLineOffset(lineNumber);

            LineInfo lineInfo = editorDocument.getLineFinder().findLine(lineNumber);
            Line line = lineInfo.line();
            mutator.deleteText(line, col, event.fLength);
//         StringBuilder b = new StringBuilder(line.getText());
//         int length = col + event.getLength();
//         int nextLine = lineNumber + 1;

//         while (length > b.length())
//         {
//            line = editorDocument.getLineFinder().findLine(nextLine).line();
//            b.append(line.getText());
//            mutator.deleteText(line, 0, line.length());
////            deleteLine(editorObject, nextLine);
//            // symbol '\n' not present in line content
//            length--;
//         }
//         b.replace(col, length, event.getText());

//         LineInfo lineIn = editorDocument.getLineFinder().findLine(lineNumber);
//         mutator.deleteText(lineIn.line(), 0, lineIn.line().length());
            mutator.insertText(line, lineInfo.number(), col, event.fText, false);
//         setLineText(lineNumber, b.toString());

            collabEditor.asWidget().fireEvent(new EditorContentChangedEvent(collabEditor));
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        } finally {
            textListener.setIgnoreTextChanges(false);
        }
    }

    /** @see org.exoplatform.ide.editor.shared.text.IDocumentListener#documentAboutToBeChanged(org.exoplatform.ide.editor.shared.text
     * .DocumentEvent) */
    @Override
    public void documentAboutToBeChanged(DocumentEvent event) {
        // TODO Auto-generated method stub
    }

    /**
     * @param editorDocument
     * @param editorDocumentMutator
     * @param textListener
     *         textListener for updating an appropriate IDocument instance
     */
    public void setDocument(Document editorDocument, EditorDocumentMutator editorDocumentMutator,
                            TextListenerImpl textListener, CollabEditor editor) {
        this.editorDocument = editorDocument;
        mutator = editorDocumentMutator;
        this.textListener = textListener;
        this.collabEditor = editor;
    }

}
