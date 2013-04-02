/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
