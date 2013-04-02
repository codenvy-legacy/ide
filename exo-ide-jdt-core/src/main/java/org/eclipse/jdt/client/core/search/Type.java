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
