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
package com.codenvy.ide.text;


/**
 * Convenience class for positions that have a type, similar to
 * {@link TypedRegion}.
 * <p/>
 * As {@link Position},<code>TypedPosition</code> can
 * not be used as key in hash tables as it overrides <code>equals</code> and
 * <code>hashCode</code> as it would be a value object.
 */
public class TypedPosition extends Position {

    /** The type of the region described by this position */
    private String fType;

    /**
     * Creates a position along the given specification.
     *
     * @param offset
     *         the offset of this position
     * @param length
     *         the length of this position
     * @param type
     *         the content type of this position
     */
    public TypedPosition(int offset, int length, String type) {
        super(offset, length);
        fType = type;
    }

    /**
     * Creates a position based on the typed region.
     *
     * @param region
     *         the typed region
     */
    public TypedPosition(TypedRegion region) {
        super(region.getOffset(), region.getLength());
        fType = region.getType();
    }

    /**
     * Returns the content type of the region.
     *
     * @return the content type of the region
     */
    public String getType() {
        return fType;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (o instanceof TypedPosition) {
            if (super.equals(o)) {
                TypedPosition p = (TypedPosition)o;
                return (fType == null && p.getType() == null) || fType.equals(p.getType());
            }
        }
        return false;
    }

    /*
    * @see java.lang.Object#hashCode()
    */
    public int hashCode() {
        int type = fType == null ? 0 : fType.hashCode();
        return super.hashCode() | type;
    }

    /*
     * @see org.eclipse.jface.text.Region#toString()
     */
    public String toString() {
        return fType + " - " + super.toString(); //$NON-NLS-1$
    }
}
