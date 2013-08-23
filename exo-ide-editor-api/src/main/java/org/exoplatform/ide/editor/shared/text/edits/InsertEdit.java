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
package org.exoplatform.ide.editor.shared.text.edits;

import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * Text edit to insert a text at a given position in a document.
 * <p/>
 * An insert edit is equivalent to <code>ReplaceEdit(offset, 0, text)
 * </code>
 *
 * @since 3.0
 */
public final class InsertEdit extends TextEdit {

    private String fText;

    /**
     * Constructs a new insert edit.
     *
     * @param offset
     *         the insertion offset
     * @param text
     *         the text to insert
     */
    public InsertEdit(int offset, String text) {
        super(offset, 0);
        fText = text;
    }

    /* Copy constructor */
    private InsertEdit(InsertEdit other) {
        super(other);
        fText = other.fText;
    }

    /**
     * Returns the text to be inserted.
     *
     * @return the edit's text.
     */
    public String getText() {
        return fText;
    }

    /* @see TextEdit#doCopy */
    protected TextEdit doCopy() {
        return new InsertEdit(this);
    }

    /* @see TextEdit#accept0 */
    protected void accept0(TextEditVisitor visitor) {
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            acceptChildren(visitor);
        }
    }

    /* @see TextEdit#performDocumentUpdating */
    int performDocumentUpdating(IDocument document) throws BadLocationException {
        document.replace(getOffset(), getLength(), fText);
        fDelta = fText.length() - getLength();
        return fDelta;
    }

    /* @see TextEdit#deleteChildren */
    boolean deleteChildren() {
        return false;
    }

    /*
     * @see org.eclipse.text.edits.TextEdit#internalToString(java.lang.StringBuffer, int)
     * @since 3.3
     */
    void internalToString(StringBuffer buffer, int indent) {
        super.internalToString(buffer, indent);
        buffer.append(" <<").append(fText); //$NON-NLS-1$
    }
}
