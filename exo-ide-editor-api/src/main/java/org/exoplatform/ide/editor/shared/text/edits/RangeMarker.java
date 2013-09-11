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
 * A range marker can be used to track positions when executing text edits.
 *
 * @since 3.0
 */
public final class RangeMarker extends TextEdit {

    /**
     * Creates a new range marker for the given offset and length.
     *
     * @param offset
     *         the marker's offset
     * @param length
     *         the marker's length
     */
    public RangeMarker(int offset, int length) {
        super(offset, length);
    }

    /* Copy constructor */
    private RangeMarker(RangeMarker other) {
        super(other);
    }

    /* @see TextEdit#copy */
    protected TextEdit doCopy() {
        return new RangeMarker(this);
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
        fDelta = 0;
        return fDelta;
    }

    /* @see TextEdit#deleteChildren */
    boolean deleteChildren() {
        return false;
    }
}
