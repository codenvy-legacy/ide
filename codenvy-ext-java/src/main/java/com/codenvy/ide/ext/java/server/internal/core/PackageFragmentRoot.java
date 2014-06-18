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
package com.codenvy.ide.ext.java.server.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.core.OpenableElementInfo;
import org.eclipse.jdt.internal.core.util.MementoTokenizer;

import java.io.File;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class PackageFragmentRoot extends Openable implements  IPackageFragmentRoot {

    private final IPath path;
    protected PackageFragmentRoot(File folder, JavaProject project) {
        super(project);
        path = new Path(folder.getPath());
    }

    /*
 * Returns the exclusion patterns from the classpath entry associated with this root.
 */
    public char[][] fullExclusionPatternChars() {
        try {
            if (getKind() != IPackageFragmentRoot.K_SOURCE) return null;
            ClasspathEntry entry = (ClasspathEntry) getRawClasspathEntry();
            if (entry == null) {
                return null;
            } else {
                return entry.fullExclusionPatternChars();
            }
        } catch (JavaModelException e) {
            return null;
        }
    }

    /*
     * Returns the inclusion patterns from the classpath entry associated with this root.
     */
    public char[][] fullInclusionPatternChars() {
        try {
            if (getKind() != IPackageFragmentRoot.K_SOURCE) return null;
            ClasspathEntry entry = (ClasspathEntry)getRawClasspathEntry();
            if (entry == null) {
                return null;
            } else {
                return entry.fullInclusionPatternChars();
            }
        } catch (JavaModelException e) {
            return null;
        }
    }

    @Override
    public void attachSource(IPath sourcePath, IPath rootPath, IProgressMonitor monitor) throws JavaModelException {

    }

    @Override
    public void copy(IPath destination, int updateResourceFlags, int updateModelFlags, IClasspathEntry sibling, IProgressMonitor monitor)
            throws JavaModelException {

    }

    @Override
    public IPackageFragment createPackageFragment(String name, boolean force, IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    @Override
    public void delete(int updateResourceFlags, int updateModelFlags, IProgressMonitor monitor) throws JavaModelException {

    }

    @Override
    public int getKind() throws JavaModelException {
        return IPackageFragmentRoot.K_SOURCE;
    }

    @Override
    public Object[] getNonJavaResources() throws JavaModelException {
        return new Object[0];
    }

    @Override
    public IPackageFragment getPackageFragment(String packageName) {
        return null;
    }

    @Override
    public IClasspathEntry getRawClasspathEntry() throws JavaModelException {
        IClasspathEntry rawEntry = null;
        JavaProject project = (JavaProject)getJavaProject();
        project.getResolvedClasspath(); // force the reverse rawEntry cache to be populated
        Map rootPathToRawEntries = project.resolvedClasspath().rawReverseMap;
        if (rootPathToRawEntries != null) {
            rawEntry = (IClasspathEntry) rootPathToRawEntries.get(getPath());
        }
        if (rawEntry == null) {
            throw new JavaModelException(new JavaModelStatus(IJavaModelStatusConstants.ELEMENT_NOT_ON_CLASSPATH, this));
        }
        return rawEntry;
    }

    @Override
    public IClasspathEntry getResolvedClasspathEntry() throws JavaModelException {
        return null;
    }

    @Override
    public IPath getSourceAttachmentPath() throws JavaModelException {
        return null;
    }

    @Override
    public IPath getSourceAttachmentRootPath() throws JavaModelException {
        return null;
    }

    @Override
    public boolean isArchive() {
        return false;
    }

    @Override
    public boolean isExternal() {
        return false;
    }

    @Override
    public void move(IPath destination, int updateResourceFlags, int updateModelFlags, IClasspathEntry sibling, IProgressMonitor monitor)
            throws JavaModelException {

    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource)
            throws JavaModelException {
        return false;
    }

    @Override
    protected IResource resource(org.eclipse.jdt.internal.core.PackageFragmentRoot root) {
        return null;
    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return null;
    }

    @Override
    public IJavaElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
        return null;
    }

    @Override
    protected char getHandleMementoDelimiter() {
        return 0;
    }

    @Override
    public int getElementType() {
        return 0;
    }

    @Override
    public IPath getPath() {
        return path;
    }
}
