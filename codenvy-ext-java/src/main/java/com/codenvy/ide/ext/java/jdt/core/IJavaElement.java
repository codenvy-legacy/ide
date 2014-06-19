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
 * Common protocol for all elements provided by the Java model.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  11:36:36 AM Mar 30, 2012 evgen $
 */
public interface IJavaElement {

    /**
     * Constant representing a package fragment root.
     * A Java element with this type can be safely cast to {@link IPackageFragmentRoot}.
     */
    int PACKAGE_FRAGMENT_ROOT = 3;

    /**
     * Constant representing a package fragment.
     * A Java element with this type can be safely cast to {@link IPackageFragment}.
     */
    int PACKAGE_FRAGMENT = 4;

    /**
     * Constant representing a Java compilation unit.
     * A Java element with this type can be safely cast to {@link ICompilationUnit}.
     */
    int COMPILATION_UNIT = 5;

    /**
     * Constant representing a class file.
     * A Java element with this type can be safely cast to {@link IClassFile}.
     */
    int CLASS_FILE = 6;

    /**
     * Constant representing a type (a class or interface).
     * A Java element with this type can be safely cast to {@link IType}.
     */
    int TYPE = 7;

    /**
     * Constant representing a field.
     * A Java element with this type can be safely cast to {@link IField}.
     */
    int FIELD = 8;

    /**
     * Constant representing a method or constructor.
     * A Java element with this type can be safely cast to {@link IMethod}.
     */
    int METHOD = 9;

    /**
     * Constant representing a stand-alone instance or class initializer.
     * A Java element with this type can be safely cast to {@link IInitializer}.
     */
    int INITIALIZER = 10;

    /**
     * Constant representing a package declaration within a compilation unit.
     * A Java element with this type can be safely cast to {@link IPackageDeclaration}.
     */
    int PACKAGE_DECLARATION = 11;

    /**
     * Constant representing all import declarations within a compilation unit.
     * A Java element with this type can be safely cast to {@link IImportContainer}.
     */
    int IMPORT_CONTAINER = 12;

    /**
     * Constant representing an import declaration within a compilation unit.
     * A Java element with this type can be safely cast to {@link IImportDeclaration}.
     */
    int IMPORT_DECLARATION = 13;

    /**
     * Constant representing a local variable declaration.
     * A Java element with this type can be safely cast to {@link ILocalVariable}.
     *
     * @since 3.0
     */
    int LOCAL_VARIABLE = 14;

    /**
     * Constant representing a type parameter declaration.
     * A Java element with this type can be safely cast to {@link ITypeParameter}.
     *
     * @since 3.1
     */
    int TYPE_PARAMETER = 15;

    /**
     * Constant representing an annotation.
     * A Java element with this type can be safely cast to {@link IAnnotation}.
     *
     * @since 3.4
     */
    int ANNOTATION = 16;

    /**
     * Returns the name of this element. This is a handle-only method.
     *
     * @return the element name
     */
    String getElementName();

    /**
     * Returns this element's kind encoded as an integer.
     * This is a handle-only method.
     *
     * @return the kind of element; one of the constants declared in
     *         <code>IJavaElement</code>
     * @see IJavaElement
     */
    int getElementType();

}
