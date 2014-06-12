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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.ext.java.jdt.core.JavaConventions;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;

/**
 * A collection methods for Java-specific things.
 *
 * @author Artem Zatsarynnyy
 */
public class JavaUtils {
    private JavaUtils() {
    }

    /**
     * Checks if the given name is valid compilation unit name.
     * <p>
     * A compilation unit name must obey the following rules:
     * <ul>
     * <li> it must not be null
     * <li> it must be suffixed by a dot ('.') followed by one of the java like extension
     * <li> its prefix must be a valid identifier
     * </ul>
     * </p>
     *
     * @param name
     *         name to check
     * @throws com.codenvy.ide.ext.java.client.projectmodel.JavaModelException
     */
    public static void checkCompilationUnitName(String name) throws JavaModelException {
        IStatus status = validateCompilationUnitName(name);
        if (status.getSeverity() == IStatus.ERROR) {
            throw new JavaModelException(status.getMessage());
        }
    }

    /**
     * Checks if the specified text is a valid compilation unit name.
     *
     * @param name
     *         the text to check
     * @return <code>true</code> if the specified text is a valid compilation unit name, <code>false</code> otherwise
     */
    public static boolean isValidCompilationUnitName(String name) {
        IStatus status = validateCompilationUnitName(name);
        switch (status.getSeverity()) {
            case Status.WARNING:
            case Status.OK:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the given package name is a valid package name.
     * <p/>
     * The syntax of a package name corresponds to PackageName as
     * defined by PackageDeclaration (JLS2 7.4). For example, <code>"java.lang"</code>.
     * <p/>
     *
     * @param name
     *         name of the package
     * @throws com.codenvy.ide.ext.java.client.projectmodel.JavaModelException
     */
    public static void checkPackageName(String name) throws JavaModelException {
        IStatus status = validatePackageName(name);
        if (status.getSeverity() == IStatus.ERROR) {
            throw new JavaModelException(status.getMessage());
        }
    }

    /**
     * Checks if the specified text is a valid package name.
     *
     * @param name
     *         the text to check
     * @return <code>true</code> if the specified text is a valid package name, <code>false</code> otherwise
     */
    public static boolean isValidPackageName(String name) {
        IStatus status = validatePackageName(name);
        switch (status.getSeverity()) {
            case Status.WARNING:
            case Status.OK:
                return true;
            default:
                return false;
        }
    }

    private static IStatus validateCompilationUnitName(String name) {
        return JavaConventions.validateCompilationUnitName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                           JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
    }

    private static IStatus validatePackageName(String name) {
        return JavaConventions.validatePackageName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
                                                   JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
    }

}
