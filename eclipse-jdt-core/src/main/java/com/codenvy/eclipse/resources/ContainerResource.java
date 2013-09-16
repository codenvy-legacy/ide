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
package com.codenvy.eclipse.resources;

import com.codenvy.eclipse.core.resources.FileInfoMatcherDescription;
import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceFilterDescription;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

/**
 * Implementation of {@link IContainer}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ContainerResource.java Dec 26, 2012 5:32:18 PM azatsarynnyy $
 */
public abstract class ContainerResource extends ItemResource implements IContainer {

    /**
     * Creates new {@link ContainerResource} with the specified <code>path</code> in pointed <code>workspace</code>.
     *
     * @param path
     *         {@link IPath}
     * @param workspace
     *         {@link WorkspaceResource}
     */
    protected ContainerResource(IPath path, WorkspaceResource workspace) {
        super(path, workspace);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#exists(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public boolean exists(IPath path) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#findMember(java.lang.String) */
    @Override
    public IResource findMember(String path) {
        return findMember(path, false);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#findMember(java.lang.String, boolean) */
    @Override
    public IResource findMember(String path, boolean includePhantoms) {
        if (path.isEmpty()) {
            return this;
        }
        return workspace.findMember(this, new Path(path));
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#findMember(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IResource findMember(IPath path) {
        return findMember(path, false);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#findMember(com.codenvy.eclipse.core.runtime.IPath, boolean) */
    @Override
    public IResource findMember(IPath path, boolean includePhantoms) {
        if (path.isEmpty()) {
            return this;
        }
        return workspace.findMember(this, path);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#getDefaultCharset() */
    @Override
    public String getDefaultCharset() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#getDefaultCharset(boolean) */
    @Override
    public String getDefaultCharset(boolean checkImplicit) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#getFile(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IFile getFile(IPath path) {
        return (IFile)workspace.newResource(getFullPath().append(path), FILE);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFolder#getFile(java.lang.String)
     * @see com.codenvy.eclipse.core.resources.IProject#getFile(java.lang.String)
     */
    public IFile getFile(String name) {
        return (IFile)workspace.newResource(getFullPath().append(name), FILE);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#getFolder(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IFolder getFolder(IPath path) {
        return (IFolder)workspace.newResource(getFullPath().append(path), FOLDER);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFolder#getFolder(java.lang.String)
     * @see com.codenvy.eclipse.core.resources.IProject#getFolder(java.lang.String)
     */
    public IFolder getFolder(String name) {
        return (IFolder)workspace.newResource(getFullPath().append(name), FOLDER);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#members() */
    @Override
    public IResource[] members() throws CoreException {
        return members(IResource.NONE);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#members(boolean) */
    @Override
    public IResource[] members(boolean includePhantoms) throws CoreException {
        return members(includePhantoms ? INCLUDE_PHANTOMS : IResource.NONE);
    }

    /** {@inheritDoc} */
    @Override
    public IResource[] members(int memberFlags) throws CoreException {
        return workspace.getMembers(getFullPath(), memberFlags);
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#findDeletedMembersWithHistory(int,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#setDefaultCharset(java.lang.String) */
    @Override
    public void setDefaultCharset(String charset) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#setDefaultCharset(java.lang.String,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IContainer#createFilter(int, com.codenvy.eclipse.core.resources.FileInfoMatcherDescription,
     *      int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IResourceFilterDescription createFilter(int type, FileInfoMatcherDescription matcherDescription,
                                                   int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IContainer#getFilters() */
    @Override
    public IResourceFilterDescription[] getFilters() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

}
