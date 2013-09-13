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

import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import java.net.URI;

/**
 * Implementation of root of the workspace.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: WorkspaceRootResource.java Dec 28, 2012 2:41:24 PM azatsarynnyy $
 */
public class WorkspaceRootResource extends ContainerResource implements IWorkspaceRoot {

    protected WorkspaceRootResource(IPath path, WorkspaceResource container) {
        super(path, container);
        Assert.isTrue(path.equals(Path.ROOT));
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#findContainersForLocation(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IContainer[] findContainersForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#findContainersForLocationURI(java.net.URI) */
    @Override
    public IContainer[] findContainersForLocationURI(URI location) {
        return findContainersForLocationURI(location, NONE);
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#findContainersForLocationURI(java.net.URI, int) */
    @Override
    public IContainer[] findContainersForLocationURI(URI location, int memberFlags) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#findFilesForLocation(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IFile[] findFilesForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#findFilesForLocationURI(java.net.URI) */
    @Override
    public IFile[] findFilesForLocationURI(URI location) {
        return findFilesForLocationURI(location, NONE);
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#findFilesForLocationURI(java.net.URI, int) */
    @Override
    public IFile[] findFilesForLocationURI(URI location, int memberFlags) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#getContainerForLocation(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IContainer getContainerForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#getFileForLocation(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IFile getFileForLocation(IPath location) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getProject() */
    @Override
    public IProject getProject() {
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#getProject(java.lang.String) */
    @Override
    public IProject getProject(String name) {
        return new ProjectResource(new Path("/" + name), workspace);
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#getProjects() */
    @Override
    public IProject[] getProjects() {
        return getProjects(IResource.NONE);
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspaceRoot#getProjects(int) */
    @Override
    public IProject[] getProjects(int memberFlags) {
        // TODO Auto-generated method stub
        return workspace.getProjects();
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getType() */
    @Override
    public int getType() {
        return IResource.ROOT;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getName() */
    @Override
    public String getName() {
        return ""; //$NON-NLS-1$
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getParent() */
    @Override
    public IContainer getParent() {
        return null;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#delete(boolean, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= deleteContent ? IResource.ALWAYS_DELETE_PROJECT_CONTENT : IResource.NEVER_DELETE_PROJECT_CONTENT;
        delete(updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#delete(boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        delete(updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#exists() */
    @Override
    public boolean exists() {
        return true;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getLocalTimeStamp() */
    @Override
    public long getLocalTimeStamp() {
        return IResource.NULL_STAMP;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getProjectRelativePath() */
    @Override
    public IPath getProjectRelativePath() {
        return Path.EMPTY;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#isDerived(int) */
    @Override
    public boolean isDerived(int options) {
        return false; //the root is never derived
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#isHidden() */
    @Override
    public boolean isHidden() {
        return false; //the root is never hidden
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#isHidden(int) */
    @Override
    public boolean isHidden(int options) {
        return false; //the root is never hidden
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#isTeamPrivateMember(int) */
    @Override
    public boolean isTeamPrivateMember(int options) {
        return false; //the root is never a team private member
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#isLinked(int) */
    @Override
    public boolean isLinked(int options) {
        return false; //the root is never linked
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#isPhantom() */
    @Override
    public boolean isPhantom() {
        return false;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#setHidden(boolean) */
    @Override
    public void setHidden(boolean isHidden) {
        //workspace root cannot be set hidden
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#setLocalTimeStamp(long) */
    @Override
    public long setLocalTimeStamp(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Illegal time stamp: " + value); //$NON-NLS-1$
        }
        //can't set local time for root
        return value;
    }

    /**
     * @see com.codenvy.eclipse.resources.ItemResource#setReadOnly(boolean)
     * @deprecated
     */
    @Override
    public void setReadOnly(boolean readonly) {
        //can't set the root read only
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#touch(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void touch(IProgressMonitor monitor) {
        // do nothing for the workspace root
    }

}
