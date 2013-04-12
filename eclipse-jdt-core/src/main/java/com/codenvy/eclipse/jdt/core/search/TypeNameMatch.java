/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.core.search;

import com.codenvy.eclipse.jdt.core.IAccessRule;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.IMember;
import com.codenvy.eclipse.jdt.core.IPackageFragmentRoot;
import com.codenvy.eclipse.jdt.core.IType;

/**
 * A match collected while {@link SearchEngine searching} for
 * all type names methods using a {@link TypeNameRequestor requestor}.
 * <p>
 * The type of this match is available from {@link #getType()}.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @see TypeNameMatchRequestor
 * @see SearchEngine#searchAllTypeNames(char[], int, char[], int, int, IJavaSearchScope, TypeNameMatchRequestor, int,
 *      org.eclipse.core.runtime.IProgressMonitor)
 * @see SearchEngine#searchAllTypeNames(char[][], char[][], IJavaSearchScope, TypeNameMatchRequestor, int,
 *      org.eclipse.core.runtime.IProgressMonitor)
 * @since 3.3
 */
public abstract class TypeNameMatch {

    /**
     * Returns the accessibility of the type name match
     *
     * @return the accessibility of the type name which may be
     *         {@link IAccessRule#K_ACCESSIBLE}, {@link IAccessRule#K_DISCOURAGED}
     *         or {@link IAccessRule#K_NON_ACCESSIBLE}.
     *         The default returned value is {@link IAccessRule#K_ACCESSIBLE}.
     * @see IAccessRule
     * @since 3.6
     */
    public abstract int getAccessibility();

    /**
     * Returns the matched type's fully qualified name using '.' character
     * as separator (e.g. package name + '.' enclosing type names + '.' simple name).
     *
     * @return Fully qualified type name of the type
     * @throws NullPointerException
     *         if matched type is <code> null</code>
     * @see #getType()
     * @see IType#getFullyQualifiedName(char)
     */
    public String getFullyQualifiedName() {
        return getType().getFullyQualifiedName('.');
    }

    /**
     * Returns the modifiers of the matched type.
     * <p/>
     * This is a handle-only method as neither Java Model nor classpath
     * initialization is done while calling this method.
     *
     * @return the type modifiers
     */
    public abstract int getModifiers();

    /**
     * Returns the package fragment root of the stored type.
     * Package fragment root cannot be null and <strong>does</strong> exist.
     *
     * @return the existing java model package fragment root (i.e. cannot be <code>null</code>
     *         and will return <code>true</code> to <code>exists()</code> message).
     * @throws NullPointerException
     *         if matched type is <code> null</code>
     * @see #getType()
     * @see IJavaElement#getAncestor(int)
     */
    public IPackageFragmentRoot getPackageFragmentRoot() {
        return (IPackageFragmentRoot)getType().getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
    }

    /**
     * Returns the package name of the stored type.
     *
     * @return the package name
     * @throws NullPointerException
     *         if matched type is <code> null</code>
     * @see #getType()
     * @see IType#getPackageFragment()
     */
    public String getPackageName() {
        return getType().getPackageFragment().getElementName();
    }

    /**
     * Returns the name of the stored type.
     *
     * @return the type name
     * @throws NullPointerException
     *         if matched type is <code> null</code>
     * @see #getType()
     * @see IJavaElement#getElementName()
     */
    public String getSimpleTypeName() {
        return getType().getElementName();
    }

    /**
     * Returns a java model type handle.
     * This handle may exist or not, but is not supposed to be <code>null</code>.
     * <p/>
     * This is a handle-only method as neither Java Model nor classpath
     * initializations are done while calling this method.
     *
     * @return the non-null handle on matched java model type.
     * @see IType
     */
    public abstract IType getType();

    /**
     * Name of the type container using '.' character
     * as separator (e.g. package name + '.' + enclosing type names).
     *
     * @return name of the type container
     * @throws NullPointerException
     *         if matched type is <code> null</code>
     * @see #getType()
     * @see IMember#getDeclaringType()
     */
    public String getTypeContainerName() {
        IType outerType = getType().getDeclaringType();
        if (outerType != null) {
            return outerType.getFullyQualifiedName('.');
        } else {
            return getType().getPackageFragment().getElementName();
        }
    }

    /**
     * Returns the matched type's type qualified name using '.' character
     * as separator (e.g. enclosing type names + '.' + simple name).
     *
     * @return fully qualified type name of the type
     * @throws NullPointerException
     *         if matched type is <code> null</code>
     * @see #getType()
     * @see IType#getTypeQualifiedName(char)
     */
    public String getTypeQualifiedName() {
        return getType().getTypeQualifiedName('.');
    }
}
