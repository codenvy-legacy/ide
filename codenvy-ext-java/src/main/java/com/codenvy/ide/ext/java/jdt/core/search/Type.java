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
