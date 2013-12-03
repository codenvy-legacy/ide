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
package com.codenvy.ide.ext.java.jdt.env;

import com.codenvy.ide.ext.java.jdt.core.IJavaElement;
import com.codenvy.ide.ext.java.jdt.core.IPackageFragment;
import com.codenvy.ide.ext.java.jdt.core.IType;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:28:31 AM Mar 30, 2012 evgen $
 */
public class TypeImpl implements IType {

    private PackageFragment packageFragment;
    private BinaryTypeImpl type;

    public TypeImpl(BinaryTypeImpl type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public String getElementName() {
        return String.valueOf(type.getFqn());// jsObj.get("name").isString().stringValue();
    }

    /** {@inheritDoc} */
    @Override
    public int getElementType() {
        return IJavaElement.TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public int getFlags() {
        return type.getModifiers(); //int)jsObj.get("modifiers").isNumber().doubleValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getFullyQualifiedName() {
        return String.valueOf(type.getFqn());// jsObj.get("name").isString().stringValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getFullyQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** {@inheritDoc} */
    @Override
    public String getTypeQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** {@inheritDoc} */
    @Override
    public IPackageFragment getPackageFragment() {
        if (packageFragment == null)
            packageFragment = new PackageFragment(getFullyQualifiedName());
        return packageFragment;
    }

}
