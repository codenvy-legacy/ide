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
package org.eclipse.jdt.client.core;

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
