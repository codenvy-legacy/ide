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
