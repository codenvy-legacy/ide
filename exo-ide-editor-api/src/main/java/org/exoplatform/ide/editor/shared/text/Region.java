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

/** The default implementation of the {@link org.eclipse.jdt.client.text.jface.text.IRegion} interface. */
public class Region implements IRegion {

    /** The region offset */
    private int fOffset;

    /** The region length */
    private int fLength;

    /**
     * Create a new region.
     *
     * @param offset
     *         the offset of the region
     * @param length
     *         the length of the region
     */
    public Region(int offset, int length) {
        fOffset = offset;
        fLength = length;
    }

    /* @see org.eclipse.jface.text.IRegion#getLength() */
    public int getLength() {
        return fLength;
    }

    /* @see org.eclipse.jface.text.IRegion#getOffset() */
    public int getOffset() {
        return fOffset;
    }

    /* @see java.lang.Object#equals(java.lang.Object) */
    public boolean equals(Object o) {
        if (o instanceof IRegion) {
            IRegion r = (IRegion)o;
            return r.getOffset() == fOffset && r.getLength() == fLength;
        }
        return false;
    }

    /* @see java.lang.Object#hashCode() */
    public int hashCode() {
        return (fOffset << 24) | (fLength << 16);
    }

    /* @see java.lang.Object#toString() */
    public String toString() {
        return "offset: " + fOffset + ", length: " + fLength; //$NON-NLS-1$ //$NON-NLS-2$;
    }
}
