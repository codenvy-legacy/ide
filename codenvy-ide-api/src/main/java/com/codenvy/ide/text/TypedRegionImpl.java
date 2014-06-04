/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
