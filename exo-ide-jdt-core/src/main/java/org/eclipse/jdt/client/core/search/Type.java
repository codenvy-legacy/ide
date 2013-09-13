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
package org.eclipse.jdt.client.core.search;

import org.eclipse.jdt.client.core.IJavaElement;
import org.eclipse.jdt.client.core.IPackageFragment;
import org.eclipse.jdt.client.core.IType;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.env.PackageFragment;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Type implements IType {

    private ShortTypeInfo typeInfo;

    private PackageFragment packageFragment;

    private String name;

    /** @param typeInfo */
    public Type(ShortTypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    /** @see org.eclipse.jdt.client.core.IJavaElement#getElementName() */
    @Override
    public String getElementName() {
        if (name == null) {
            name = Signature.getSimpleName(typeInfo.getName());
            if (name.contains("."))
                name = name.substring(name.lastIndexOf('.'));
        }
        return name;
    }

    /** @see org.eclipse.jdt.client.core.IJavaElement#getElementType() */
    @Override
    public int getElementType() {
        return IJavaElement.TYPE;
    }

    /** @see org.eclipse.jdt.client.core.IType#getFlags() */
    @Override
    public int getFlags() {
        return typeInfo.getModifiers();
    }

    /** @see org.eclipse.jdt.client.core.IType#getFullyQualifiedName() */
    @Override
    public String getFullyQualifiedName() {
        return typeInfo.getName();
    }

    /** @see org.eclipse.jdt.client.core.IType#getFullyQualifiedName(char) */
    @Override
    public String getFullyQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** @see org.eclipse.jdt.client.core.IType#getTypeQualifiedName(char) */
    @Override
    public String getTypeQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** @see org.eclipse.jdt.client.core.IType#getPackageFragment() */
    @Override
    public IPackageFragment getPackageFragment() {
        if (packageFragment == null)
            packageFragment = new PackageFragment(getFullyQualifiedName());
        return packageFragment;
    }

}
