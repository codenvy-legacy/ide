/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Eicher (Avaloq Evolution AG) - block selection mode
 *******************************************************************************/
package com.codenvy.ide.api.text;


/**
 * Standard implementation of {@link TextSelection}.
 * <p>
 * Takes advantage of the weak contract of correctness of its interface. If
 * generated from a selection provider, it only remembers its offset and length
 * and computes the remaining information on request.</p>
 */
public class TextSelectionImpl implements TextSelection {


    /** Internal empty text selection */
    private static final TextSelection NULL = new TextSelectionImpl();
    /** Document which delivers the data of the selection, possibly <code>null</code>. */
    private final Document fDocument;
    /** Offset of the selection */
    private       int      fOffset;
    /** Length of the selection */
    private       int      fLength;

    /** Creates an empty text selection. */
    private TextSelectionImpl() {
        fOffset = -1;
        fLength = -1;
        fDocument = null;
    }


    /**
     * Creates a text selection for the given range. This
     * selection object describes generically a text range and
     * is intended to be an argument for the <code>setSelection</code>
     * method of selection providers.
     *
     * @param offset
     *         the offset of the range, must not be negative
     * @param length
     *         the length of the range, must not be negative
     */
    public TextSelectionImpl(int offset, int length) {
        this(null, offset, length);
    }

    /**
     * Creates a text selection for the given range of the given document.
     * This selection object is created by selection providers in responds
     * <code>getSelection</code>.
     *
     * @param document
     *         the document whose text range is selected in a viewer
     * @param offset
     *         the offset of the selected range, must not be negative
     * @param length
     *         the length of the selected range, must not be negative
     */
    public TextSelectionImpl(Document document, int offset, int length) {
        fDocument = document;
        fOffset = offset;
        fLength = length;
    }

    /**
     * Returns the shared instance of the empty text selection.
     *
     * @return the shared instance of an empty text selection
     */
    public static TextSelection emptySelection() {
        return NULL;
    }

    /**
     * Tells whether this text selection is the empty selection.
     * <p>
     * A selection of length 0 is not an empty text selection as it
     * describes, e.g., the cursor position in a viewer.</p>
     *
     * @return <code>true</code> if this selection is empty
     * @see #emptySelection()
     */
    public boolean isEmpty() {
        return this == NULL || /* backwards compatibility: */ fOffset < 0 || fLength < 0;
    }

    /*
     * @see org.eclipse.jface.text.ITextSelection#getOffset()
     */
    public int getOffset() {
        return fOffset;
    }

    /*
     * @see org.eclipse.jface.text.ITextSelection#getLength()
     */
    public int getLength() {
        return fLength;
    }

    /*
     * @see org.eclipse.jface.text.ITextSelection#getStartLine()
     */
    public int getStartLine() {

        try {
            if (fDocument != null)
                return fDocument.getLineOfOffset(fOffset);
        } catch (BadLocationException x) {
        }

        return -1;
    }

    /*
     * @see org.eclipse.jface.text.ITextSelection#getEndLine()
     */
    public int getEndLine() {
        try {
            if (fDocument != null) {
                int endOffset = fOffset + fLength;
                if (fLength != 0)
                    endOffset--;
                return fDocument.getLineOfOffset(endOffset);
            }
        } catch (BadLocationException x) {
        }

        return -1;
    }

    /*
     * @see org.eclipse.jface.text.ITextSelection#getText()
     */
    public String getText() {
        try {
            if (fDocument != null)
                return fDocument.get(fOffset, fLength);
        } catch (BadLocationException x) {
        }

        return null;
    }

    /*
     * @see java.lang.Object#isEquals(Object)
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        TextSelectionImpl s = (TextSelectionImpl)obj;
        boolean sameRange = (s.fOffset == fOffset && s.fLength == fLength);
        if (sameRange) {

            if (s.fDocument == null && fDocument == null)
                return true;
            if (s.fDocument == null || fDocument == null)
                return false;

            try {
                String sContent = s.fDocument.get(fOffset, fLength);
                String content = fDocument.get(fOffset, fLength);
                return sContent.equals(content);
            } catch (BadLocationException x) {
            }
        }

        return false;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int low = fDocument != null ? fDocument.hashCode() : 0;
        return (fOffset << 24) | (fLength << 16) | low;
    }

    /**
     * Returns the document underlying the receiver, possibly <code>null</code>.
     *
     * @return the document underlying the receiver, possibly <code>null</code>
     */
    protected Document getDocument() {
        return fDocument;
    }
}

