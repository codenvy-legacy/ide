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
package com.codenvy.ide.ext.java.jdt.core.search;

import com.codenvy.ide.ext.java.jdt.core.IJavaElement;
import com.codenvy.ide.ext.java.jdt.core.IPackageFragment;
import com.codenvy.ide.ext.java.jdt.core.IType;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.env.PackageFragment;
import com.codenvy.ide.ext.java.worker.env.BinaryType;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Type implements IType {

    private BinaryType typeInfo;

    private PackageFragment packageFragment;

    private String name;

    /** @param typeInfo */
    public Type(BinaryType typeInfo) {
        this.typeInfo = typeInfo;
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IJavaElement#getElementName() */
    @Override
    public String getElementName() {
        if (name == null) {
            char[][] chars = CharOperation.splitOn('/', typeInfo.getName());
            name = new String(Signature.getSimpleName(Signature.toQualifiedName(chars)));
            if (name.contains("."))
                name = name.substring(name.lastIndexOf('.'));
        }
        return name;
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IJavaElement#getElementType() */
    @Override
    public int getElementType() {
        return IJavaElement.TYPE;
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IType#getFlags() */
    @Override
    public int getFlags() {
        return typeInfo.getModifiers();
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IType#getFullyQualifiedName() */
    @Override
    public String getFullyQualifiedName() {
        return getElementName();
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IType#getFullyQualifiedName(char) */
    @Override
    public String getFullyQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IType#getTypeQualifiedName(char) */
    @Override
    public String getTypeQualifiedName(char c) {
        return getFullyQualifiedName();
    }

    /** @see com.codenvy.ide.ext.java.jdt.core.IType#getPackageFragment() */
    @Override
    public IPackageFragment getPackageFragment() {
        if (packageFragment == null)
            packageFragment = new PackageFragment(getFullyQualifiedName());
        return packageFragment;
    }

}
