/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.util;

import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.IJavaProject;
import com.codenvy.eclipse.jdt.core.JavaConventions;
import com.codenvy.eclipse.jdt.core.JavaCore;

/**
 * Provides methods for checking Java-specific conventions such as name syntax.
 * <p>
 * This is a wrapper for {@link com.codenvy.eclipse.jdt.core.JavaConventions} with more convenient
 * <code>validate*(..)</code> methods. If multiple validations are planned on
 * the same element, please use {@link com.codenvy.eclipse.jdt.core.JavaConventions} directly (with the
 * arguments from {@link #getSourceComplianceLevels(com.codenvy.eclipse.jdt.core.IJavaElement)}).
 * </p>
 */
public class JavaConventionsUtil {

    /**
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return a <code>String[]</code> whose <code>[0]</code> is the
     *         {@link com.codenvy.eclipse.jdt.core.JavaCore#COMPILER_SOURCE} and whose <code>[1]</code> is
     *         the {@link com.codenvy.eclipse.jdt.core.JavaCore#COMPILER_COMPLIANCE} level at the given
     *         <code>context</code>.
     */
    public static String[] getSourceComplianceLevels(IJavaElement context) {
        if (context != null) {
            IJavaProject javaProject = context.getJavaProject();
            if (javaProject != null) {
                return new String[]{javaProject.getOption(JavaCore.COMPILER_SOURCE, true), javaProject.getOption(
                        JavaCore.COMPILER_COMPLIANCE, true)};
            }
        }
        return new String[]{JavaCore.getOption(JavaCore.COMPILER_SOURCE), JavaCore.getOption(
                JavaCore.COMPILER_COMPLIANCE)};
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateCompilationUnitName(String, String, String)
     */
    public static IStatus validateCompilationUnitName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateCompilationUnitName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateClassFileName(String, String, String)
     */
    public static IStatus validateClassFileName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateClassFileName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateFieldName(String, String, String)
     */
    public static IStatus validateFieldName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateFieldName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateIdentifier(String, String, String)
     */
    public static IStatus validateIdentifier(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateIdentifier(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateImportDeclaration(String, String, String)
     */
    public static IStatus validateImportDeclaration(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateImportDeclaration(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateJavaTypeName(String, String, String)
     */
    public static IStatus validateJavaTypeName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateJavaTypeName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateMethodName(String, String, String)
     */
    public static IStatus validateMethodName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateMethodName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validatePackageName(String, String, String)
     */
    public static IStatus validatePackageName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validatePackageName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }

    /**
     * @param name
     *         the name to validate
     * @param context
     *         an {@link com.codenvy.eclipse.jdt.core.IJavaElement} or <code>null</code>
     * @return validation status in <code>context</code>'s project or in the workspace
     * @see com.codenvy.eclipse.jdt.core.JavaConventions#validateTypeVariableName(String, String, String)
     */
    public static IStatus validateTypeVariableName(String name, IJavaElement context) {
        String[] sourceComplianceLevels = getSourceComplianceLevels(context);
        return JavaConventions.validateTypeVariableName(name, sourceComplianceLevels[0], sourceComplianceLevels[1]);
    }
}
