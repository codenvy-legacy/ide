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
 * A <code>CopyingRangeMarker</code> can be used to track positions when executing text edits. Additionally a copying range marker
 * stores a local copy of the text it captures when it gets executed.
 *
 * @since 3.0
 */
public final class CopyingRangeMarker extends TextEdit {

    private String fText;

    /**
     * Creates a new <tt>CopyRangeMarker</tt> for the given offset and length.
     *
     * @param offset
     *         the marker's offset
     * @param length
     *         the marker's length
     */
    public CopyingRangeMarker(int offset, int length) {
        super(offset, length);
    }

    /* Copy constructor */
    private CopyingRangeMarker(CopyingRangeMarker other) {
        super(other);
        fText = other.fText;
    }

    /* @see TextEdit#doCopy */
    protected TextEdit doCopy() {
        return new CopyingRangeMarker(this);
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
        fText = document.get(getOffset(), getLength());
        fDelta = 0;
        return fDelta;
    }

    /* @see TextEdit#deleteChildren */
    boolean deleteChildren() {
        return false;
    }
}
