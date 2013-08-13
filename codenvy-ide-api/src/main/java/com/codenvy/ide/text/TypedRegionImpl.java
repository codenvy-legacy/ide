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

/** Default implementation of {@link TypedRegion}. A <code>TypedRegion</code> is a value object. */
public class TypedRegionImpl extends RegionImpl implements TypedRegion {

    /** The region's type */
    private String fType;

    /**
     * Creates a typed region based on the given specification.
     *
     * @param offset
     *         the region's offset
     * @param length
     *         the region's length
     * @param type
     *         the region's type
     */
    public TypedRegionImpl(int offset, int length, String type) {
        super(offset, length);
        fType = type;
    }

    /* @see org.eclipse.jface.text.ITypedRegion#getType() */
    public String getType() {
        return fType;
    }

    /* @see java.lang.Object#equals(java.lang.Object) */
    public boolean equals(Object o) {
        if (o instanceof TypedRegionImpl) {
            TypedRegionImpl r = (TypedRegionImpl)o;
            return super.equals(r) && ((fType == null && r.getType() == null) || fType.equals(r.getType()));
        }
        return false;
    }

    /* @see java.lang.Object#hashCode() */
    public int hashCode() {
        int type = fType == null ? 0 : fType.hashCode();
        return super.hashCode() | type;
    }

    /*
     * @see org.eclipse.jface.text.Region#toString()
     * @since 3.5
     */
    public String toString() {
        return fType + " - " + super.toString(); //$NON-NLS-1$
    }

}
