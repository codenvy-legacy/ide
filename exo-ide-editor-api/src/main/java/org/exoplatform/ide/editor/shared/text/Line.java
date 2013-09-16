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
package org.exoplatform.ide.editor.shared.text;

/**
 * Describes a line as a particular number of characters beginning at a particular offset, consisting of a particular number of
 * characters, and being closed with a particular line delimiter.
 */
final class Line implements IRegion {

    /** The offset of the line */
    public int offset;

    /** The length of the line */
    public int length;

    /** The delimiter of this line */
    public final String delimiter;

    /**
     * Creates a new Line.
     *
     * @param offset
     *         the offset of the line
     * @param end
     *         the last including character offset of the line
     * @param delimiter
     *         the line's delimiter
     */
    public Line(int offset, int end, String delimiter) {
        this.offset = offset;
        this.length = (end - offset) + 1;
        this.delimiter = delimiter;
    }

    /**
     * Creates a new Line.
     *
     * @param offset
     *         the offset of the line
     * @param length
     *         the length of the line
     */
    public Line(int offset, int length) {
        this.offset = offset;
        this.length = length;
        this.delimiter = null;
    }

    /* @see org.eclipse.jface.text.IRegion#getOffset() */
    public int getOffset() {
        return offset;
    }

    /* @see org.eclipse.jface.text.IRegion#getLength() */
    public int getLength() {
        return length;
    }
}
