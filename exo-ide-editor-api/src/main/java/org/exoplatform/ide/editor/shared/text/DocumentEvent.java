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
package org.exoplatform.ide.editor.shared.text;

import org.exoplatform.ide.editor.shared.runtime.Assert;

/**
 * Specification of changes applied to documents. All changes are represented as replace commands, i.e. specifying a document
 * range whose text gets replaced with different text. In addition to this information, the event also contains the changed
 * document.
 *
 * @see org.eclipse.jface.text.IDocument
 */
public class DocumentEvent {

    /** The changed document */
    public IDocument fDocument;

    /** The document offset */
    public int fOffset;

    /** Length of the replaced document text */
    public int fLength;

    /** Text inserted into the document */
    public String fText = ""; //$NON-NLS-1$

    /**
     * The modification stamp of the document when firing this event.
     *
     * @since 3.1 and public since 3.3
     */
    public long fModificationStamp;

    /**
     * Creates a new document event.
     *
     * @param doc
     *         the changed document
     * @param offset
     *         the offset of the replaced text
     * @param length
     *         the length of the replaced text
     * @param text
     *         the substitution text
     */
    public DocumentEvent(IDocument doc, int offset, int length, String text) {

        Assert.isNotNull(doc);
        Assert.isTrue(offset >= 0);
        Assert.isTrue(length >= 0);
        //
        // if (ASSERT_TEXT_NOT_NULL)
        // Assert.isNotNull(text);

        fDocument = doc;
        fOffset = offset;
        fLength = length;
        fText = text;
        //
        // if (fDocument instanceof IDocumentExtension4)
        // fModificationStamp= ((IDocumentExtension4)fDocument).getModificationStamp();
        // else
        // fModificationStamp= IDocumentExtension4.UNKNOWN_MODIFICATION_STAMP;
    }

    /** Creates a new, not initialized document event. */
    public DocumentEvent() {
    }

    /**
     * Returns the changed document.
     *
     * @return the changed document
     */
    public IDocument getDocument() {
        return fDocument;
    }

    /**
     * Returns the offset of the change.
     *
     * @return the offset of the change
     */
    public int getOffset() {
        return fOffset;
    }

    /**
     * Returns the length of the replaced text.
     *
     * @return the length of the replaced text
     */
    public int getLength() {
        return fLength;
    }

    /**
     * Returns the text that has been inserted.
     *
     * @return the text that has been inserted
     */
    public String getText() {
        return fText;
    }

    /**
     * Returns the document's modification stamp at the time when this event was sent.
     *
     * @return the modification stamp or {@link IDocumentExtension4#UNKNOWN_MODIFICATION_STAMP}.
     * @see IDocumentExtension4#getModificationStamp()
     * @since 3.1
     */
    public long getModificationStamp() {
        return fModificationStamp;
    }

    /*
     * @see java.lang.Object#toString()
     * @since 3.4
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("offset: "); //$NON-NLS-1$
        buffer.append(fOffset);
        buffer.append(", length: "); //$NON-NLS-1$
        buffer.append(fLength);
        buffer.append(", timestamp: "); //$NON-NLS-1$
        buffer.append(fModificationStamp);
        buffer.append("\ntext:>"); //$NON-NLS-1$
        buffer.append(fText);
        buffer.append("<\n"); //$NON-NLS-1$
        return buffer.toString();
    }
}
