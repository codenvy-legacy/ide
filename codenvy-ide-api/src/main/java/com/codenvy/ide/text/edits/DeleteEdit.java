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

/**
 * Text edit to delete a range in a document.
 * <p/>
 * A delete edit is equivalent to <code>ReplaceEdit(
 * offset, length, "")</code>.
 */
public final class DeleteEdit extends TextEdit {

    /**
     * Constructs a new delete edit.
     *
     * @param offset
     *         the offset of the range to replace
     * @param length
     *         the length of the range to replace
     */
    public DeleteEdit(int offset, int length) {
        super(offset, length);
    }

    /* Copy constructor */
    private DeleteEdit(DeleteEdit other) {
        super(other);
    }

    /* @see TextEdit#doCopy */
    protected TextEdit doCopy() {
        return new DeleteEdit(this);
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
        document.replace(getOffset(), getLength(), ""); //$NON-NLS-1$
        fDelta = -getLength();
        return fDelta;
    }

    /* @see TextEdit#deleteChildren */
    boolean deleteChildren() {
        return true;
    }
}
