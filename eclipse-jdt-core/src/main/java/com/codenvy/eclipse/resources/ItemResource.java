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

import com.codenvy.eclipse.core.internal.resources.ICoreConstants;
import com.codenvy.eclipse.core.internal.resources.Marker;
import com.codenvy.eclipse.core.internal.resources.ResourceException;
import com.codenvy.eclipse.core.internal.utils.Policy;
import com.codenvy.eclipse.core.internal.utils.WrappedRuntimeException;
import com.codenvy.eclipse.core.internal.watson.IPathRequestor;
import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IMarker;
import com.codenvy.eclipse.core.resources.IPathVariableManager;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IProjectDescription;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceProxy;
import com.codenvy.eclipse.core.resources.IResourceProxyVisitor;
import com.codenvy.eclipse.core.resources.IResourceStatus;
import com.codenvy.eclipse.core.resources.IResourceVisitor;
import com.codenvy.eclipse.core.resources.IWorkspace;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.QualifiedName;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;
import com.codenvy.eclipse.core.runtime.jobs.MultiRule;

import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Implementation of {@link IResource}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ItemResource.java Dec 26, 2012 12:20:07 PM azatsarynnyy $
 */
public abstract class ItemResource implements IResource, ICoreConstants {

    protected IPath path;

    protected WorkspaceResource workspace;

    /**
     * Creates new {@link ItemResource} with the specified <code>path</code> in pointed <code>workspace</code>.
     *
     * @param path
     *         {@link IPath}
     * @param workspace
     *         {@link WorkspaceResource}
     */
    protected ItemResource(IPath path, WorkspaceResource workspace) {
        this.path = path.removeTrailingSeparator();
        this.workspace = workspace;
    }

    /** @see com.codenvy.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class) */
    @Override
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule#contains(com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule) */
    @Override
    public boolean contains(ISchedulingRule rule) {
        if (this == rule) {
            return true;
        }
        //must allow notifications to nest in all resource rules
        if (rule.getClass().equals(WorkManager.NotifyRule.class)) {
            return true;
        }
        if (rule instanceof MultiRule) {
            MultiRule multi = (MultiRule)rule;
            ISchedulingRule[] children = multi.getChildren();
            for (int i = 0; i < children.length; i++) {
                if (!contains(children[i])) {
                    return false;
                }
            }
            return true;
        }
        if (!(rule instanceof IResource)) {
            return false;
        }
        IResource resource = (IResource)rule;
        if (!workspace.equals(resource.getWorkspace())) {
            return false;
        }
        return path.isPrefixOf(resource.getFullPath());
    }

    /* (non-Javadoc)
  * @see IResource#equals(Object)
  */
    public boolean equals(Object target) {
        if (this == target) {
            return true;
        }
        if (!(target instanceof ItemResource)) {
            return false;
        }
        ItemResource resource = (ItemResource)target;
        return getType() == resource.getType() && path.equals(resource.path) && workspace.equals(resource.workspace);
    }

    public int hashCode() {
        // the container may be null if the identified resource
        // does not exist so don't bother with it in the hash
        return getFullPath().hashCode();
    }

    /** @see com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule) */
    @Override
    public boolean isConflicting(ISchedulingRule rule) {
        if (this == rule) {
            return true;
        }
        //must not schedule at same time as notification
        if (rule.getClass().equals(WorkManager.NotifyRule.class)) {
            return true;
        }
        if (rule instanceof MultiRule) {
            return rule.isConflicting(this);
        }
        if (!(rule instanceof IResource)) {
            return false;
        }
        IResource resource = (IResource)rule;
        if (!workspace.equals(resource.getWorkspace())) {
            return false;
        }
        IPath otherPath = resource.getFullPath();
        return path.isPrefixOf(otherPath) || otherPath.isPrefixOf(path);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#accept(com.codenvy.eclipse.core.resources.IResourceProxyVisitor, int) */
    @Override
    public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException {
        accept(visitor, IResource.DEPTH_INFINITE, memberFlags);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#accept(com.codenvy.eclipse.core.resources.IResourceProxyVisitor, int, int) */
    @Override
    public void accept(final IResourceProxyVisitor visitor, final int depth, final int memberFlags) throws CoreException {
        final ResourceProxy proxy = new ResourceProxy();
        IElementContentVisitor elementVisitor = new IElementContentVisitor() {
            public boolean visitElement(IPathRequestor requestor, Object contents) {
                ResourceInfo info = (ResourceInfo)contents;
                if (!isMember(getFlags(info), memberFlags)) {
                    return false;
                }
                proxy.requestor = requestor;
                proxy.info = info;
                try {
                    boolean shouldContinue = true;
                    switch (depth) {
                        case DEPTH_ZERO:
                            shouldContinue = false;
                            break;
                        case DEPTH_ONE:
                            shouldContinue = !path.equals(requestor.requestPath().removeLastSegments(1));
                            break;
                        case DEPTH_INFINITE:
                            shouldContinue = true;
                            break;
                    }
                    return visitor.visit(proxy) && shouldContinue;
                } catch (CoreException e) {
                    //throw an exception to bail out of the traversal
                    throw new WrappedRuntimeException(e);
                } finally {
                    proxy.reset();
                }
            }
        };
        try {
            new VfsTreeIterator(workspace, getFullPath()).iterate(elementVisitor);
        } catch (WrappedRuntimeException e) {
            throw (CoreException)e.getTargetException();
        } finally {
            proxy.requestor = null;
            proxy.info = null;
        }
    }

    public int getFlags(ResourceInfo info) {
        return (info == null) ? NULL_FLAG : info.getFlags();
    }

    /**
     * Returns whether a resource should be included in a traversal
     * based on the provided member flags.
     *
     * @param flags
     *         The resource info flags
     * @param memberFlags
     *         The member flag mask
     * @return Whether the resource is included
     */
    protected boolean isMember(int flags, int memberFlags) {
        int excludeMask = 0;
        if ((memberFlags & IContainer.INCLUDE_PHANTOMS) == 0) {
            excludeMask |= M_PHANTOM;
        }
        if ((memberFlags & IContainer.INCLUDE_HIDDEN) == 0) {
            excludeMask |= M_HIDDEN;
        }
        if ((memberFlags & IContainer.INCLUDE_TEAM_PRIVATE_MEMBERS) == 0) {
            excludeMask |= M_TEAM_PRIVATE_MEMBER;
        }
        if ((memberFlags & IContainer.EXCLUDE_DERIVED) != 0) {
            excludeMask |= M_DERIVED;
        }
        //the resource is a matching member if it matches none of the exclude flags
        return flags != NULL_FLAG && (flags & excludeMask) == 0;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#accept(com.codenvy.eclipse.core.resources.IResourceVisitor) */
    @Override
    public void accept(IResourceVisitor visitor) throws CoreException {
        accept(visitor, IResource.DEPTH_INFINITE, 0);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#accept(com.codenvy.eclipse.core.resources.IResourceVisitor, int, boolean) */
    @Override
    public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
        accept(visitor, depth, includePhantoms ? IContainer.INCLUDE_PHANTOMS : 0);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#accept(com.codenvy.eclipse.core.resources.IResourceVisitor, int, int) */
    @Override
    public void accept(final IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
        //use the fast visitor if visiting to infinite depth
        if (depth == IResource.DEPTH_INFINITE) {
            accept(new IResourceProxyVisitor() {
                public boolean visit(IResourceProxy proxy) throws CoreException {
                    return visitor.visit(proxy.requestResource());
                }
            }, memberFlags);
            return;
        }
        // it is invalid to call accept on a phantom when INCLUDE_PHANTOMS is not specified
        final boolean includePhantoms = (memberFlags & IContainer.INCLUDE_PHANTOMS) != 0;
        ResourceInfo info = getResourceInfo(includePhantoms, false);
        int flags = getFlags(info);
        //      if ((memberFlags & IContainer.DO_NOT_CHECK_EXISTENCE) == 0)
        //         checkAccessible(flags);

        //check that this resource matches the member flags
        if (!isMember(flags, memberFlags)) {
            return;
        }
        // visit this resource
        if (!visitor.visit(this) || depth == DEPTH_ZERO) {
            return;
        }
        // get the info again because it might have been changed by the visitor
        info = getResourceInfo(includePhantoms, false);
        if (info == null) {
            return;
        }
        // thread safety: (cache the type to avoid changes -- we might not be inside an operation)
        int type = info.getType();
        if (type == FILE) {
            return;
        }
        // if we had a gender change we need to fix up the resource before asking for its members
        IContainer resource = getType() != type ? (IContainer)workspace.newResource(getFullPath(),
                                                                                    type) : (IContainer)this;
        IResource[] members = resource.members(memberFlags);
        for (int i = 0; i < members.length; i++) {
            members[i].accept(visitor, DEPTH_ZERO, memberFlags | IContainer.DO_NOT_CHECK_EXISTENCE);
        }
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#clearHistory(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void clearHistory(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#copy(com.codenvy.eclipse.core.runtime.IPath, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        copy(destination, updateFlags, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#copy(com.codenvy.eclipse.core.runtime.IPath, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
        workspace.copyResource(this, destination);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#copy(com.codenvy.eclipse.core.resources.IProjectDescription, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        copy(description, updateFlags, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#copy(com.codenvy.eclipse.core.resources.IProjectDescription, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#createMarker(java.lang.String) */
    @Override
    public IMarker createMarker(String type) throws CoreException {

        return new Marker(this, 0);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#createProxy() */
    @Override
    public IResourceProxy createProxy() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#delete(boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
        delete(force ? IResource.FORCE : IResource.NONE, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#delete(int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
        final ISchedulingRule rule = workspace.getRuleFactory().deleteRule(this);
        try {
            workspace.prepareOperation(rule, monitor);
            workspace.beginOperation(true);
            workspace.deleteResource(this);
        } finally {
            workspace.endOperation(rule, true, Policy.subMonitorFor(monitor, Policy.endOpWork * 1000));
        }
    }

    /**
     * This is not an IResource method.
     *
     * @see com.codenvy.eclipse.core.resources.IFile#delete(boolean, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     * @see com.codenvy.eclipse.core.resources.IFolder#delete(boolean, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     * @see com.codenvy.eclipse.core.resources.IProject#delete(boolean, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        delete(updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int) */
    @Override
    public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#exists() */
    @Override
    public boolean exists() {
        try {
            workspace.getVfsItemByFullPath(getFullPath());
        } catch (ItemNotFoundException e) {
            return false;
        } catch (CoreException e) {
            return false;
        }
        return true;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#findMarker(long) */
    @Override
    public IMarker findMarker(long id) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int) */
    @Override
    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
        // TODO Auto-generated method stub
        return new IMarker[0];
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int) */
    @Override
    public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException {
        // TODO Auto-generated method stub
        return 0;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getFileExtension() */
    @Override
    public String getFileExtension() {
        String name = getName();
        int index = name.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        if (index == (name.length() - 1)) {
            return "";
        }
        return name.substring(index + 1);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getFullPath() */
    @Override
    public IPath getFullPath() {
        return path;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getLocalTimeStamp() */
    @Override
    public long getLocalTimeStamp() {
        // TODO Auto-generated method stub
        return 0;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getLocation() */
    @Override
    public IPath getLocation() {
        return getFullPath();
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getLocationURI() */
    @Override
    public URI getLocationURI() {
        try {
            return new URI(getLocation().toOSString());
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getMarker(long) */
    @Override
    public IMarker getMarker(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getModificationStamp() */
    @Override
    public long getModificationStamp() {
        return workspace.getModificationStamp(this);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getName() */
    @Override
    public String getName() {
        return path.lastSegment();
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getPathVariableManager() */
    @Override
    public IPathVariableManager getPathVariableManager() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getParent() */
    @Override
    public IContainer getParent() {
        int segments = path.segmentCount();
        //zero segment handled by subclasses
        if (segments < 1) {
            Assert.isLegal(false, path.toString());
        }
        if (segments == 1)
        //return workspace.getRoot().getProject(path.segment(0));
        {
            return (IProject)workspace.newResource(path.removeLastSegments(1), IResource.PROJECT);
        }
        return (IFolder)workspace.newResource(path.removeLastSegments(1), IResource.FOLDER);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getPersistentProperties() */
    @Override
    public Map<QualifiedName, String> getPersistentProperties() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getPersistentProperty(com.codenvy.eclipse.core.runtime.QualifiedName) */
    @Override
    public String getPersistentProperty(QualifiedName key) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getProject() */
    @Override
    public IProject getProject() {
        return workspace.getRoot().getProject(path.segment(0));
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getProjectRelativePath() */
    @Override
    public IPath getProjectRelativePath() {
        return getFullPath().removeFirstSegments(ICoreConstants.PROJECT_SEGMENT_LENGTH);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getRawLocation() */
    @Override
    public IPath getRawLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getRawLocationURI() */
    @Override
    public URI getRawLocationURI() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getSessionProperties() */
    @Override
    public Map<QualifiedName, Object> getSessionProperties() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getSessionProperty(com.codenvy.eclipse.core.runtime.QualifiedName) */
    @Override
    public Object getSessionProperty(QualifiedName key) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getType() */
    @Override
    public abstract int getType();

    /** @see com.codenvy.eclipse.core.resources.IResource#getWorkspace() */
    @Override
    public IWorkspace getWorkspace() {
        return workspace;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isAccessible() */
    @Override
    public boolean isAccessible() {
        return exists();
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isDerived() */
    @Override
    public boolean isDerived() {
        return isDerived(IResource.NONE);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isDerived(int) */
    @Override
    public boolean isDerived(int options) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isHidden() */
    @Override
    public boolean isHidden() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isHidden(int) */
    @Override
    public boolean isHidden(int options) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isLinked() */
    @Override
    public boolean isLinked() {
        return isLinked(NONE);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isVirtual() */
    @Override
    public boolean isVirtual() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isLinked(int) */
    @Override
    public boolean isLinked(int options) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isLocal(int) */
    @Override
    public boolean isLocal(int depth) {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isPhantom() */
    @Override
    public boolean isPhantom() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isReadOnly() */
    @Override
    public boolean isReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isSynchronized(int) */
    @Override
    public boolean isSynchronized(int depth) {
        return true;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isTeamPrivateMember() */
    @Override
    public boolean isTeamPrivateMember() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#isTeamPrivateMember(int) */
    @Override
    public boolean isTeamPrivateMember(int options) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#move(com.codenvy.eclipse.core.runtime.IPath, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
        move(destination, force ? IResource.FORCE : IResource.NONE, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#move(com.codenvy.eclipse.core.runtime.IPath, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
        workspace.moveResource(this, destination);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#move(com.codenvy.eclipse.core.resources.IProjectDescription, boolean, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IProjectDescription description, boolean force, boolean keepHistory,
                     IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        move(description, updateFlags, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#move(com.codenvy.eclipse.core.resources.IProjectDescription, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        Assert.isNotNull(description);
        if (getType() != IResource.PROJECT) {
            String message = "Cannot move " + getFullPath() + " to " + description.getName() +
                             ".  Source must be a project."; //NLS.bind(Messages.resources_moveNotProject, getFullPath(),
                             // description.getName());
            throw new ResourceException(IResourceStatus.INVALID_VALUE, getFullPath(), message, null);
        }
        ((ProjectResource)this).move(description, updateFlags, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IFile#move(IPath, boolean, boolean, IProgressMonitor)
     * @see com.codenvy.eclipse.core.resources.IFolder#move(IPath, boolean, boolean, IProgressMonitor)
     */
    public void move(IPath destination, boolean force, boolean keepHistory,
                     IProgressMonitor monitor) throws CoreException {
        int updateFlags = force ? IResource.FORCE : IResource.NONE;
        updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
        move(destination, updateFlags, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#refreshLocal(int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#revertModificationStamp(long) */
    @Override
    public void revertModificationStamp(long value) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setDerived(boolean) */
    @Override
    public void setDerived(boolean isDerived) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setDerived(boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setHidden(boolean) */
    @Override
    public void setHidden(boolean isHidden) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setLocal(boolean, int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setLocalTimeStamp(long) */
    @Override
    public long setLocalTimeStamp(long value) throws CoreException {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#setPersistentProperty(com.codenvy.eclipse.core.runtime.QualifiedName,
     *      java.lang.String)
     */
    @Override
    public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setReadOnly(boolean) */
    @Override
    public void setReadOnly(boolean readOnly) {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IResource#setSessionProperty(com.codenvy.eclipse.core.runtime.QualifiedName,
     *      java.lang.Object)
     */
    @Override
    public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#setTeamPrivateMember(boolean) */
    @Override
    public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IResource#touch(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void touch(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * Returns the resource info.  Returns null if the resource doesn't exist.
     * If the phantom flag is true, phantom resources are considered.
     * If the mutable flag is true, a mutable info is returned.
     */
    public ResourceInfo getResourceInfo(boolean phantom, boolean mutable) {
        return workspace.getResourceInfo(getFullPath(), phantom, mutable);
    }

    public String getTypeString() {
        switch (getType()) {
            case FILE:
                return "L"; //$NON-NLS-1$
            case FOLDER:
                return "F"; //$NON-NLS-1$
            case PROJECT:
                return "P"; //$NON-NLS-1$
            case ROOT:
                return "R"; //$NON-NLS-1$
        }
        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see Object#toString()
     */
    public String toString() {
        return getTypeString() + getFullPath().toString();
    }

}
