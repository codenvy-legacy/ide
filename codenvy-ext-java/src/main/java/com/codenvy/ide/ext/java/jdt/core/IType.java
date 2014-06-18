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
package com.codenvy.ide.ext.java.jdt.core;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  11:42:48 AM Mar 30, 2012 evgen $
 */
public interface IType extends IJavaElement {

    /**
     * Returns the modifier flags for this member. The flags can be examined using class
     * <code>Flags</code>.
     * <p/>
     * Note that only flags as indicated in the source are returned. Thus if an interface
     * defines a method <code>void myMethod();</code> the flags don't include the
     * 'public' flag.
     *
     * @return the modifier flags for this member
     * @see Flags
     */
    int getFlags();

    /**
     * Returns the fully qualified name of this type,
     * including qualification for any containing types and packages.
     * This is the name of the package, followed by <code>'.'</code>,
     * followed by the type-qualified name.
     * <p>
     * <b>Note</b>: The enclosing type separator used in the type-qualified
     * name is <code>'$'</code>, not <code>'.'</code>.
     * </p>
     *
     * @return the fully qualified name of this type
     */
    String getFullyQualifiedName();

    /**
     * Returns the fully qualified name of this type,
     * including qualification for any containing types and packages.
     * This is the name of the package, followed by <code>'.'</code>,
     * followed by the type-qualified name using the <code>enclosingTypeSeparator</code>.
     * <p/>
     * For example:
     * <ul>
     * <li>the fully qualified name of a class B defined as a member of a class A in a compilation unit A.java
     * in a package x.y using the '.' separator is "x.y.A.B"</li>
     * <li>the fully qualified name of a class B defined as a member of a class A in a compilation unit A.java
     * in a package x.y using the '$' separator is "x.y.A$B"</li>
     * <li>the fully qualified name of a binary type whose class file is x/y/A$B.class
     * using the '.' separator is "x.y.A.B"</li>
     * <li>the fully qualified name of a binary type whose class file is x/y/A$B.class
     * using the '$' separator is "x.y.A$B"</li>
     * <li>the fully qualified name of an anonymous binary type whose class file is x/y/A$1.class
     * using the '.' separator is "x.y.A.1"</li>
     * </ul>
     * <p/>
     * This is a handle-only method.
     *
     * @param enclosingTypeSeparator
     *         the given enclosing type separator
     * @return the fully qualified name of this type, including qualification for any containing types and packages
     * @see IType#getTypeQualifiedName(char)
     */
    String getFullyQualifiedName(char c);

    /**
     * @param c
     * @return
     */
    String getTypeQualifiedName(char c);

    /**
     * Returns the package fragment in which this element is defined.
     * This is a handle-only method.
     *
     * @return the package fragment in which this element is defined
     */
    IPackageFragment getPackageFragment();

}
