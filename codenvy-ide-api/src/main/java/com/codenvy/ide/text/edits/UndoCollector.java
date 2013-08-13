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
package com.codenvy.ide.text.edits;

class UndoCollector {

    protected UndoEdit undo;

    private int fOffset;

    private int fLength;

    private String fLastCurrentText;

    public UndoCollector(TextEdit root) {
        fOffset = root.getOffset();
        fLength = root.getLength();
    }

    // public void connect(IDocument document) {
    // document.addDocumentListener(this);
    // undo= new UndoEdit();
    // }
    //
    // public void disconnect(IDocument document) {
    // if (undo != null) {
    // document.removeDocumentListener(this);
    // undo.defineRegion(fOffset, fLength);
    // }
    // }
    //
    // public void documentChanged(DocumentEvent event) {
    // fLength+= getDelta(event);
    // }
    //
    // private static int getDelta(DocumentEvent event) {
    // String text= event.getText();
    // return text == null ? -event.getLength() : (text.length() - event.getLength());
    // }
    //
    // public void documentAboutToBeChanged(DocumentEvent event) {
    // int offset= event.getOffset();
    // int currentLength= event.getLength();
    // String currentText= null;
    // try {
    // currentText= event.getDocument().get(offset, currentLength);
    // } catch (BadLocationException cannotHappen) {
    //         Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
    // }
    //
    // /*
    // * see https://bugs.eclipse.org/bugs/show_bug.cgi?id=93634
    // * If the same string is replaced on many documents (e.g. rename
    // * package), the size of the undo can be reduced by using the same
    // * String instance in all edits, instead of using the unique String
    // * returned from IDocument.get(int, int).
    // */
    // if (fLastCurrentText != null && fLastCurrentText.equals(currentText))
    // currentText= fLastCurrentText;
    // else
    // fLastCurrentText= currentText;
    //
    // String newText= event.getText();
    // undo.add(new ReplaceEdit(offset, newText != null ? newText.length() : 0, currentText));
    // }
}
