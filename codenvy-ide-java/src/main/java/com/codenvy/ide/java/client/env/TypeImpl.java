/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.java.client.env;

import com.codenvy.ide.java.client.core.IJavaElement;
import com.codenvy.ide.java.client.core.IPackageFragment;
import com.codenvy.ide.java.client.core.IType;
import com.google.gwt.json.client.JSONObject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:28:31 AM Mar 30, 2012 evgen $
 */
public class TypeImpl implements IType {

    private JSONObject jsObj;

    private PackageFragment packageFragment;

    /** @param jsObj */
    public TypeImpl(JSONObject jsObj) {
        this.jsObj = jsObj;
    }

    /** @see com.codenvy.ide.java.client.core.IJavaElement#getElementName() */
    @Override
    public String getElementName() {
        return jsObj.get("name").isString().stringValue();
    }

    /** @see com.codenvy.ide.java.client.core.IJavaElement#getElementType() */
    @Override
    public int getElementType() {
        return IJavaElement.TYPE;
    }

    /** @see com.codenvy.ide.java.client.core.IType#getFlags() */
    @Override
    public int getFlags() {
        return (int)jsObj.get("modifiers").isNumber().doubleValue();
    }

    /** @see com.codenvy.ide.java.client.core.IType#getFullyQualifiedName() */
    @Override
    public String getFullyQualifiedName() {
        return jsObj.get("name").isString().stringValue();
    }

    /** @see com.codenvy.ide.java.client.core.IType#getFullyQualifiedName(char) */
    @Override
    public String getFullyQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** @see com.codenvy.ide.java.client.core.IType#getTypeQualifiedName(char) */
    @Override
    public String getTypeQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** @see com.codenvy.ide.java.client.core.IType#getPackageFragment() */
    @Override
    public IPackageFragment getPackageFragment() {
        if (packageFragment == null)
            packageFragment = new PackageFragment(getFullyQualifiedName());
        return packageFragment;
    }

}
