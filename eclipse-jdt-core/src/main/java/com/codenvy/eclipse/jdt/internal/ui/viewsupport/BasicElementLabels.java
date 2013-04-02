/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.ui.viewsupport;

import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.jdt.core.ITypeRoot;
import com.codenvy.eclipse.jdt.internal.corext.util.Strings;

import java.io.File;

/**
 * A label provider for basic elements like paths. The label provider will make sure that the labels are correctly
 * shown in RTL environments.
 *
 * @since 3.4
 */
public class BasicElementLabels {
    // commonly used delimiters

    /** Dot (.) delimiter. Used most often in package names and file extensions. */
    private static final String DOT = "."; //$NON-NLS-1$

    /** Colon (:) delimiter. Used most often in file paths and URLs. */
    private static final String COLON = ":"; //$NON-NLS-1$

    /** Forward slash (/) delimiter. Used most often in file paths and URLs. */
    private static final String FILE_SEP_FSLASH = "/"; //$NON-NLS-1$

    /** Backslash (\) delimiter. Used most often in file paths. */
    private static final String FILE_SEP_BSLASH = "\\"; //$NON-NLS-1$

    /** The default set of delimiters to use to segment a string. */
    private static final String delimiterString = DOT + COLON + FILE_SEP_FSLASH + FILE_SEP_BSLASH;

    // TextProcessor delimiters
    private static final String CODE_DELIMITERS = delimiterString + "<>()?,{}+-*!%=^|&;[]~"; //$NON-NLS-1$

    private static final String FILE_PATTERN_DELIMITERS = delimiterString + "*.?"; //$NON-NLS-1$

    private static final String URL_DELIMITERS = delimiterString + ":@?-"; //$NON-NLS-1$


    /**
     * Returns the label of a path.
     *
     * @param path
     *         the path
     * @param isOSPath
     *         if <code>true</code>, the path represents an OS path, if <code>false</code> it is a workspace path.
     * @return the label of the path to be used in the UI.
     */
    public static String getPathLabel(IPath path, boolean isOSPath) {
        String label;
        if (isOSPath) {
            label = path.toOSString();
        } else {
            label = path.makeRelative().toString();
        }
        return Strings.markLTR(label);
    }

    /**
     * Returns the label of the path of a file.
     *
     * @param file
     *         the file
     * @return the label of the file path to be used in the UI.
     */
    public static String getPathLabel(File file) {
        return Strings.markLTR(file.getAbsolutePath());
    }

    /**
     * Returns the label for a file pattern like '*.java'
     *
     * @param name
     *         the pattern
     * @return the label of the pattern.
     */
    public static String getFilePattern(String name) {
        return Strings.markLTR(name, FILE_PATTERN_DELIMITERS);
    }

    /**
     * Returns the label for a URL, URI or URL part. Example is 'http://www.x.xom/s.html#1'
     *
     * @param name
     *         the URL string
     * @return the label of the URL.
     */
    public static String getURLPart(String name) {
        return Strings.markLTR(name, URL_DELIMITERS);
    }

    /**
     * Returns a label for a resource name.
     *
     * @param resource
     *         the resource
     * @return the label of the resource name.
     */
    public static String getResourceName(IResource resource) {
        return Strings.markLTR(resource.getName());
    }

    /**
     * Returns a label for a resource name.
     *
     * @param resourceName
     *         the resource name
     * @return the label of the resource name.
     */
    public static String getResourceName(String resourceName) {
        return Strings.markLTR(resourceName);
    }

    /**
     * Returns a label for a type root name which is a file name.
     *
     * @param typeRoot
     *         the typeRoot
     * @return the label of the resource name.
     */
    public static String getFileName(ITypeRoot typeRoot) {
        return Strings.markLTR(typeRoot.getElementName());
    }

    /**
     * Returns a label for Java element name. Example is 'new Test<? extends List>() { ...}'.
     * This method should only be used for simple element names. Use
     * {@link com.codenvy.eclipse.jdt.ui.JavaElementLabels} to create a label from a Java element or {@link BindingLabelProvider}
     * for labels of bindings.
     *
     * @param name
     *         the Java element name.
     * @return the label for the Java element
     */
    public static String getJavaElementName(String name) {
        return Strings.markJavaElementLabelLTR(name);
    }

    /**
     * Returns a label for Java code snippet used in a label. Example is 'Test test= new Test<? extends List>() { ...}'.
     *
     * @param string
     *         the Java code snippet
     * @return the label for the Java code snippet
     */
    public static String getJavaCodeString(String string) {
        return Strings.markLTR(string, CODE_DELIMITERS);
    }

    /**
     * Returns a label for a version name. Example is '1.4.1'
     *
     * @param name
     *         the version string
     * @return the version label
     */
    public static String getVersionName(String name) {
        return Strings.markLTR(name);
    }

    //	/**
    //	 * Returns a label for a working set
    //	 *
    //	 * @param set the working set
    //	 * @return the label of the working set
    //	 */
    //	public static String getWorkingSetLabel(IWorkingSet set) {
    //		return Strings.markLTR(set.getLabel());
    //	}


}
