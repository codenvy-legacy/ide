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

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;

/** Text edit to replace a range in a document with a different string. */
public final class ReplaceEdit extends TextEdit {

    private String fText;

    /**
     * Constructs a new replace edit.
     *
     * @param offset
     *         the offset of the range to replace
     * @param length
     *         the length of the range to replace
     * @param text
     *         the new text
     */
    public ReplaceEdit(int offset, int length, String text) {
        super(offset, length);
        // Assert.isNotNull(text);
        fText = text;
    }

    /*
     * Copy constructor
     * @param other the edit to copy from
     */
    private ReplaceEdit(ReplaceEdit other) {
        super(other);
        fText = other.fText;
    }

    /**
     * Returns the new text replacing the text denoted by the edit.
     *
     * @return the edit's text.
     */
    public String getText() {
        return fText;
    }

    /* @see TextEdit#doCopy */
    protected TextEdit doCopy() {
        return new ReplaceEdit(this);
    }

    /* @see TextEdit#accept0 */
    protected void accept0(TextEditVisitor visitor) {
        boolean visitChildren = visitor.visit(this);
        if (visitChildren) {
            acceptChildren(visitor);
        }
    }

    /* @see TextEdit#performDocumentUpdating */
    int performDocumentUpdating(Document document) throws BadLocationException {
        document.replace(getOffset(), getLength(), fText);
        fDelta = fText.length() - getLength();
        return fDelta;
    }

    /* @see TextEdit#deleteChildren */
    boolean deleteChildren() {
        return true;
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
