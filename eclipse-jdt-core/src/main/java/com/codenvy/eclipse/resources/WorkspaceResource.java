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

import com.codenvy.eclipse.core.internal.Policy;
import com.codenvy.eclipse.core.internal.events.ILifecycleListener;
import com.codenvy.eclipse.core.internal.events.LifecycleEvent;
import com.codenvy.eclipse.core.internal.events.NotificationManager;
import com.codenvy.eclipse.core.internal.events.ResourceChangeEvent;
import com.codenvy.eclipse.core.internal.events.ResourceComparator;
import com.codenvy.eclipse.core.internal.resources.ICoreConstants;
import com.codenvy.eclipse.core.internal.resources.ProjectInfo;
import com.codenvy.eclipse.core.internal.resources.ResourceException;
import com.codenvy.eclipse.core.internal.resources.ResourceStatus;
import com.codenvy.eclipse.core.internal.resources.RootInfo;
import com.codenvy.eclipse.core.internal.resources.Rules;
import com.codenvy.eclipse.core.internal.watson.ElementTree;
import com.codenvy.eclipse.core.resources.IBuildConfiguration;
import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFilterMatcherDescriptor;
import com.codenvy.eclipse.core.resources.IMarker;
import com.codenvy.eclipse.core.resources.IPathVariableManager;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IProjectDescription;
import com.codenvy.eclipse.core.resources.IProjectNatureDescriptor;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceChangeEvent;
import com.codenvy.eclipse.core.resources.IResourceChangeListener;
import com.codenvy.eclipse.core.resources.IResourceRuleFactory;
import com.codenvy.eclipse.core.resources.IResourceStatus;
import com.codenvy.eclipse.core.resources.ISaveParticipant;
import com.codenvy.eclipse.core.resources.ISavedState;
import com.codenvy.eclipse.core.resources.ISynchronizer;
import com.codenvy.eclipse.core.resources.IWorkspace;
import com.codenvy.eclipse.core.resources.IWorkspaceDescription;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.resources.IWorkspaceRunnable;
import com.codenvy.eclipse.core.resources.team.TeamHook;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.OperationCanceledException;
import com.codenvy.eclipse.core.runtime.Path;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.SubProgressMonitor;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.FileImpl;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.core.MediaType;

/**
 * Implementation of {@link IWorkspace}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: WorkspaceResource.java Dec 27, 2012 12:47:21 PM azatsarynnyy $
 */
public class WorkspaceResource implements IWorkspace {
    static final int M_PHANTOM = 0x8;

    protected final IWorkspaceRoot defaultRoot = new WorkspaceRootResource(Path.ROOT, this);

    /** {@link VirtualFileSystem} instance. */
    private VirtualFileSystem vfs;

    private IResourceRuleFactory ruleFactory;

    private WorkManager _workManager;

    private IWorkspaceDescription description;

//   protected NotificationManager notificationManager;


    protected final CopyOnWriteArrayList<ILifecycleListener> lifecycleListeners = new CopyOnWriteArrayList<ILifecycleListener>();

    /**
     * The workspace tree.  The tree is an in-memory representation
     * of the resources that make up the workspace.  The tree caches
     * the structure and state of files and directories on disk (their existence
     * and last modified times).  When external parties make changes to
     * the files on disk, this representation becomes out of sync. A local refresh
     * reconciles the state of the files on disk with this tree (@link {@link IResource#refreshLocal(int, IProgressMonitor)}).
     * The tree is also used to store metadata associated with resources in
     * the workspace (markers, properties, etc).
     * <p/>
     * While the ElementTree data structure can handle both concurrent
     * reads and concurrent writes, write access to the tree is governed
     * by {@link WorkManager}.
     */
    protected volatile ElementTree tree;

    /**
     * This field is used to control access to the workspace tree during
     * resource change notifications. It tracks which thread, if any, is
     * in the middle of a resource change notification.  This is used to cause
     * attempts to modify the workspace during notifications to fail.
     */
    protected Thread treeLocked = null;

    protected ElementTree operationTree; // tree at the start of the current operation

    protected long nextNodeId = 1;

    private TeamHook teamHook = null;

    //   private MarkerManager markerManager;

    public WorkspaceResource(VirtualFileSystem vfs) {
        this.vfs = vfs;
        _workManager = new WorkManager(this);
        _workManager.startup(null);
        _workManager.postWorkspaceStartup();
//      notificationManager = new NotificationManager(this);
//      notificationManager.startup(null);
        description = new WorkspaceDescription();
        tree = new ElementTree();
      /* tree should only be modified during operations */
        tree.immutable();
        //      treeLocked = Thread.currentThread();
        tree.setTreeData(newElement(IResource.ROOT));
        //      markerManager = new MarkerManager(this);
        //      markerManager.startup(null);
    }

    public void shutdown() {
        _workManager.shutdown(null);
        _workManager = null;
    }

    /** @see com.codenvy.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class) */
    @Override
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setVfs(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    /**
     * Adds a listener for internal workspace lifecycle events.  There is no way to
     * remove lifecycle listeners.
     */
    public void addLifecycleListener(ILifecycleListener listener) {
        lifecycleListeners.addIfAbsent(listener);
    }


    /** Returns the installed team hook.  Never returns null. */
    public TeamHook getTeamHook() {
        if (teamHook == null)
            initializeTeamHook();
        return teamHook;
    }

    private void initializeTeamHook() {
        // default to use Core's implementation
        //create anonymous subclass because TeamHook is abstract
        if (teamHook == null)
            teamHook = new TeamHook() {
                // empty
            };
    }

    /* (non-Javadoc)
     * @see IWorkspace#addResourceChangeListener(IResourceChangeListener)
     */
    public void addResourceChangeListener(IResourceChangeListener listener) {
//      notificationManager.addListener(listener,
//         IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.POST_CHANGE);
    }

    /* (non-Javadoc)
     * @see IWorkspace#addResourceChangeListener(IResourceChangeListener, int)
     */
    public void addResourceChangeListener(IResourceChangeListener listener, int eventMask) {
//      notificationManager.addListener(listener, eventMask);
    }


    /** Create and return a new tree element of the given type. */
    protected ResourceInfo newElement(int type) {
        ResourceInfo result = null;
        switch (type) {
            case IResource.FILE:
            case IResource.FOLDER:
                result = new ResourceInfo();
                break;
            case IResource.PROJECT:
                result = new ProjectInfo();
                break;
            case IResource.ROOT:
                result = new RootInfo();
                break;
        }
        result.setNodeId(nextNodeId());
        updateModificationStamp(result);
        result.setType(type);
        return result;
    }

    protected long nextNodeId() {
        return nextNodeId++;
    }

    public void updateModificationStamp(ResourceInfo info) {
        info.incrementModificationStamp();
    }
    //   /**
    //    * Returns the marker manager for this workspace
    //    */
    //   public MarkerManager getMarkerManager()
    //   {
    //      return markerManager;
    //   }

    public void broadcastPostChange() {
//      ResourceChangeEvent event = new ResourceChangeEvent(this, IResourceChangeEvent.POST_CHANGE, 0, null);
//      notificationManager.broadcastChanges(tree, event, true);
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#addSaveParticipant(java.lang.String,
     * com.codenvy.eclipse.core.resources.ISaveParticipant) */
    @Override
    public ISavedState addSaveParticipant(String pluginId, ISaveParticipant participant) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#build(int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void build(int kind, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#build(com.codenvy.eclipse.core.resources.IBuildConfiguration[], int, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void build(IBuildConfiguration[] buildConfigs, int kind, boolean buildReferences,
                      IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#checkpoint(boolean) */
    @Override
    public void checkpoint(boolean build) {
        try {
            final ISchedulingRule rule = getWorkManager().getNotifyRule();
            try {
                prepareOperation(rule, null);
                beginOperation(true);
                broadcastPostChange();
            } finally {
                endOperation(rule, build, null);
            }
        } catch (CoreException e) {
            com.codenvy.eclipse.core.internal.utils.Policy.log(e.getStatus());
        }
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#computePrerequisiteOrder(com.codenvy.eclipse.core.resources.IProject[]) */
    @Override
    public IProject[][] computePrerequisiteOrder(IProject[] projects) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#computeProjectOrder(com.codenvy.eclipse.core.resources.IProject[]) */
    @Override
    public ProjectOrder computeProjectOrder(IProject[] projects) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#copy(com.codenvy.eclipse.core.resources.IResource[],
     *      com.codenvy.eclipse.core.runtime.IPath, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus copy(IResource[] resources, IPath destination, boolean force,
                        IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#copy(com.codenvy.eclipse.core.resources.IResource[],
     *      com.codenvy.eclipse.core.runtime.IPath, int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus copy(IResource[] resources, IPath destination, int updateFlags,
                        IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#delete(com.codenvy.eclipse.core.resources.IResource[], boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus delete(IResource[] resources, boolean force, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#delete(com.codenvy.eclipse.core.resources.IResource[], int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus delete(IResource[] resources, int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#deleteMarkers(com.codenvy.eclipse.core.resources.IMarker[]) */
    @Override
    public void deleteMarkers(IMarker[] markers) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#forgetSavedTree(java.lang.String) */
    @Override
    public void forgetSavedTree(String pluginId) {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getFilterMatcherDescriptors() */
    @Override
    public IFilterMatcherDescriptor[] getFilterMatcherDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getFilterMatcherDescriptor(java.lang.String) */
    @Override
    public IFilterMatcherDescriptor getFilterMatcherDescriptor(String filterMatcherId) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getNatureDescriptors() */
    @Override
    public IProjectNatureDescriptor[] getNatureDescriptors() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getNatureDescriptor(java.lang.String) */
    @Override
    public IProjectNatureDescriptor getNatureDescriptor(String natureId) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getDanglingReferences() */
    @Override
    public Map<IProject, IProject[]> getDanglingReferences() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getDescription() */
    @Override
    public IWorkspaceDescription getDescription() {
        return description;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getRoot() */
    @Override
    public IWorkspaceRoot getRoot() {
        return defaultRoot;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getRuleFactory() */
    @Override
    public IResourceRuleFactory getRuleFactory() {
        //note that the rule factory is created lazily because it
        //requires loading the teamHook extension
        if (ruleFactory == null) {
            ruleFactory = new Rules(this);
        }
        return ruleFactory;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getSynchronizer() */
    @Override
    public ISynchronizer getSynchronizer() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#isAutoBuilding() */
    @Override
    public boolean isAutoBuilding() {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see IWorkspace#isTreeLocked()
     */
    public boolean isTreeLocked() {
        return treeLocked == Thread.currentThread();
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#loadProjectDescription(com.codenvy.eclipse.core.runtime.IPath) */
    @Override
    public IProjectDescription loadProjectDescription(IPath projectDescriptionFile) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#loadProjectDescription(java.io.InputStream) */
    @Override
    public IProjectDescription loadProjectDescription(InputStream projectDescriptionFile) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#move(com.codenvy.eclipse.core.resources.IResource[],
     *      com.codenvy.eclipse.core.runtime.IPath, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus move(IResource[] resources, IPath destination, boolean force,
                        IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#move(com.codenvy.eclipse.core.resources.IResource[],
     *      com.codenvy.eclipse.core.runtime.IPath, int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus move(IResource[] resources, IPath destination, int updateFlags,
                        IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#newBuildConfig(java.lang.String, java.lang.String) */
    @Override
    public IBuildConfiguration newBuildConfig(String projectName, String configName) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#newProjectDescription(java.lang.String) */
    @Override
    public IProjectDescription newProjectDescription(String projectName) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Creates new {@link ItemResource} of the specified <code>type</code>.
     *
     * @param path
     *         {@link IPath} of resource to create
     * @param type
     *         type of resource to create
     * @return created resource
     */
    public ItemResource newResource(IPath path, int type) {
        String message;
        switch (type) {
            case IResource.FOLDER:
                if (path.segmentCount() < ICoreConstants.MINIMUM_FOLDER_SEGMENT_LENGTH) {
                    message = "Path must include project and resource name: " + path.toString();
                    Assert.isLegal(false, message);
                }
                return new FolderResource(path.makeAbsolute(), this);
            case IResource.FILE:
                if (path.segmentCount() < ICoreConstants.MINIMUM_FILE_SEGMENT_LENGTH) {
                    message = "Path must include project and resource name: " + path.toString();
                    Assert.isLegal(false, message);
                }
                return new FileResource(path.makeAbsolute(), this);
            case IResource.PROJECT:
                //return (ItemResource)getRoot().getProject(path.lastSegment());
                return new ProjectResource(path.makeAbsolute(), this);
            case IResource.ROOT:
                return (ItemResource)getRoot();
        }
        Assert.isLegal(false);
        // will never get here because of assertion.
        return null;
    }

    /**
     * Creates provided {@link IResource} in the {@link VirtualFileSystem}.
     *
     * @param resource
     *         {@link IResource} to create in {@link VirtualFileSystem}
     * @return created {@link Item}
     * @throws CoreException
     */
    public Item createResource(IResource resource) throws CoreException {
        return createResource(resource, null);
    }

    /**
     * Creates provided {@link IResource} in the {@link VirtualFileSystem} with provided <code>contents</code>.
     *
     * @param resource
     *         {@link IResource} to create in {@link VirtualFileSystem}
     * @param contents
     *         make sense only for file
     * @return created {@link Item}
     * @throws CoreException
     */
    public Item createResource(IResource resource, InputStream contents) throws CoreException {

        IContainer parent = resource.getParent();
        if (!parent.exists()) {
            createResource(parent, null);
        }

        try {
            //some times jdt try create existed resource
            if (resource.exists()) {
                return getItemByPath(resource.getFullPath());
            }
            String parentId = getVfsIdByFullPath(resource.getParent().getFullPath());
            Item i = null;
            switch (resource.getType()) {
                case IResource.FILE:
                    i = vfs.createFile(parentId, resource.getName(), /* TODO use special resolver*/
                                       MediaType.valueOf("application/java"), contents);
                    break;
                case IResource.FOLDER:
                    i = vfs.createFolder(parentId, resource.getName());
                    break;
                case IResource.PROJECT:
                    i = vfs.createProject(parentId, resource.getName(), null, null);
                    break;
            }
            ResourceInfo info = newElement(resource.getType());
            ResourceInfo original = getResourceInfo(resource.getFullPath(), true, false);
            // if nothing existed at the destination then just create the resource in the tree
            if (original == null) {
                info.setSyncInfo(null);
                tree.createElement(resource.getFullPath(), info);
            } else {

                info.set(ICoreConstants.M_MARKERS_SNAP_DIRTY);
                tree.setElementData(resource.getFullPath(), info);
            }
            return i;
        } catch (ItemNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (InvalidArgumentException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (ItemAlreadyExistException e) {
            throw new CoreException(
                    new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "Resource already exists in the workspace.",
                               e));
        } catch (PermissionDeniedException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } finally {
            safeClose(contents);
        }
    }

    /**
     * Returns VFS {@link Item} identifier by provided {@link IPath}.
     *
     * @param path
     *         {@link IPath}
     * @return {@link Item} identifier
     * @throws CoreException
     * @throws ItemNotFoundException
     * @throws PermissionDeniedException
     * @throws VirtualFileSystemException
     */
    String getVfsIdByFullPath(IPath path) throws CoreException, ItemNotFoundException {
        try {
            return vfs.getItemByPath(path.toString(), null, false, PropertyFilter.NONE_FILTER).getId();
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, e.getMessage(), e));
        }
    }

    /**
     * Returns VFS {@link Item} by provided {@link IPath}.
     *
     * @param path
     *         {@link IPath}
     * @return {@link Item}
     * @throws CoreException
     * @throws ItemNotFoundException
     */
    Item getVfsItemByFullPath(IPath path) throws CoreException, ItemNotFoundException {
        try {
            return vfs.getItemByPath(path.toString(), null, false, PropertyFilter.NONE_FILTER);
        } catch (ItemNotFoundException e) {
            throw e;
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, e.getMessage(), e));
        }
    }

    @Override
    public void removeResourceChangeListener(IResourceChangeListener listener) {
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#removeSaveParticipant(java.lang.String) */
    @Override
    public void removeSaveParticipant(String pluginId) {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#run(com.codenvy.eclipse.core.resources.IWorkspaceRunnable,
     *      com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule, int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(IWorkspaceRunnable action, ISchedulingRule rule, int options,
                    IProgressMonitor monitor) throws CoreException {
        monitor = Policy.monitorFor(monitor);
        try {
            monitor.beginTask("", Policy.totalWork); //$NON-NLS-1$
            int depth = -1;
            boolean avoidNotification = (options & IWorkspace.AVOID_UPDATE) != 0;
            try {
                prepareOperation(rule, monitor);
                beginOperation(true);
                //            if (avoidNotification)
                //            {
                //               avoidNotification = notificationManager.beginAvoidNotify();
                //            }
                depth = getWorkManager().beginUnprotected();
                action.run(Policy.subMonitorFor(monitor, Policy.opWork, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
            } catch (OperationCanceledException e) {
                getWorkManager().operationCanceled();
                throw e;
            } finally {
                //            if (avoidNotification)
                //            {
                //               notificationManager.endAvoidNotify();
                //            }
                if (depth >= 0) {
                    getWorkManager().endUnprotected(depth);
                }
                endOperation(rule, false, Policy.subMonitorFor(monitor, Policy.endOpWork));
            }
        } finally {
            monitor.done();
        }
    }

    public void beginOperation(boolean createNewTree) throws CoreException {
        WorkManager workManager = getWorkManager();
        workManager.incrementNestedOperations();
        if (!workManager.isBalanced()) {
            Assert.isTrue(false, "Operation was not prepared."); //$NON-NLS-1$
        }
        if (workManager.getPreparedOperationDepth() > 1) {
            if (createNewTree && tree.isImmutable()) {
                newWorkingTree();
            }
            return;
        }
        // stash the current tree as the basis for this operation.
        operationTree = tree;
        if (createNewTree && tree.isImmutable()) {
            newWorkingTree();
        }
    }


    /**
     * Broadcasts an internal workspace lifecycle event to interested
     * internal listeners.
     */
    protected void broadcastEvent(LifecycleEvent event) throws CoreException {
        for (ILifecycleListener listener : lifecycleListeners) {
            listener.handleEvent(event);
        }
    }

    /**
     * Called before checking the pre-conditions of an operation.  Optionally supply
     * a scheduling rule to determine when the operation is safe to run.  If a scheduling
     * rule is supplied, this method will block until it is safe to run.
     *
     * @param rule
     *         the scheduling rule that describes what this operation intends to modify.
     */
    public void prepareOperation(ISchedulingRule rule, IProgressMonitor monitor) throws CoreException {
        try {
            //make sure autobuild is not running if it conflicts with this operation
            //         if (rule != null && rule.isConflicting(getRuleFactory().buildRule()))
            //            buildManager.interrupt();
        } finally {
            getWorkManager().checkIn(rule, monitor);
        }
        if (!isOpen()) {
            String message = "Workspace is closed.";
            throw new ResourceException(IResourceStatus.OPERATION_FAILED, null, message, null);
        }
    }

    /**
     * End an operation (group of resource changes).
     * Notify interested parties that resource changes have taken place.  All
     * registered resource change listeners are notified.  If autobuilding is
     * enabled, a build is run.
     */
    public void endOperation(ISchedulingRule rule, boolean build, IProgressMonitor monitor) throws CoreException {
        WorkManager workManager = getWorkManager();
        //don't do any end operation work if we failed to check in
        if (workManager.checkInFailed(rule)) {
            return;
        }
        // This is done in a try finally to ensure that we always decrement the operation count
        // and release the workspace lock.  This must be done at the end because snapshot
        // and "hasChanges" comparison have to happen without interference from other threads.
        boolean hasTreeChanges = false;
        boolean depthOne = false;
        try {
            workManager.setBuild(build);
            // if we are not exiting a top level operation then just decrement the count and return
            depthOne = workManager.getPreparedOperationDepth() == 1;
//         if (!(notificationManager.shouldNotify() || depthOne))
//         {
//            notificationManager.requestNotify();
//            return;
//         }
            // do the following in a try/finally to ensure that the operation tree is nulled at the end
            // as we are completing a top level operation.
            try {
                //            notificationManager.beginNotify();
                // check for a programming error on using beginOperation/endOperation
                Assert.isTrue(workManager.getPreparedOperationDepth() > 0, "Mismatched begin/endOperation"); //$NON-NLS-1$

                // At this time we need to re-balance the nested operations. It is necessary because
                // build() and snapshot() should not fail if they are called.
                workManager.rebalanceNestedOperations();

                //find out if any operation has potentially modified the tree
                hasTreeChanges = workManager.shouldBuild();
                //double check if the tree has actually changed
                if (hasTreeChanges) {
                    hasTreeChanges = operationTree != null && ElementTree.hasChanges(tree, operationTree,
                                                                                     ResourceComparator.getBuildComparator(), true);
                }
                broadcastPostChange();
                //            // Request a snapshot if we are sufficiently out of date.
                //            saveManager.snapshotIfNeeded(hasTreeChanges);
            } finally {
                // make sure the tree is immutable if we are ending a top-level operation.
                if (depthOne) {
                    tree.immutable();
                    operationTree = null;
                } else {
                    newWorkingTree();
                }
            }
        } finally {
            workManager.checkOut(rule);
        }
        if (depthOne) {
            //         buildManager.endTopLevel(hasTreeChanges);
        }
    }


    /**
     * Opens a new mutable element tree layer, thus allowing
     * modifications to the tree.
     */
    public ElementTree newWorkingTree() {
        tree = tree.newEmptyDelta();
        return tree;
    }

    private boolean isOpen() {
        return true;
    }

    /**
     * We should not have direct references to this field. All references should go through
     * this method.
     */
    public WorkManager getWorkManager() throws CoreException {
        if (_workManager == null) {
            String message = "Workspace was not properly initialized or has already shutdown.";
            throw new ResourceException(new ResourceStatus(IResourceStatus.INTERNAL_ERROR, null, message));
        }
        return _workManager;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#run(com.codenvy.eclipse.core.resources.IWorkspaceRunnable,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#save(boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public IStatus save(boolean full, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#setDescription(com.codenvy.eclipse.core.resources.IWorkspaceDescription) */
    @Override
    public void setDescription(IWorkspaceDescription description) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#sortNatureSet(java.lang.String[]) */
    @Override
    public String[] sortNatureSet(String[] natureIds) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#validateEdit(com.codenvy.eclipse.core.resources.IFile[], java.lang.Object) */
    @Override
    public IStatus validateEdit(IFile[] files, Object context) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#validateFiltered(com.codenvy.eclipse.core.resources.IResource) */
    @Override
    public IStatus validateFiltered(IResource resource) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#validateLinkLocation(com.codenvy.eclipse.core.resources.IResource,
     *      com.codenvy.eclipse.core.runtime.IPath)
     */
    @Override
    public IStatus validateLinkLocation(IResource resource, IPath location) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#validateLinkLocationURI(com.codenvy.eclipse.core.resources.IResource,
     *      java.net.URI)
     */
    @Override
    public IStatus validateLinkLocationURI(IResource resource, URI location) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#validateName(java.lang.String, int) */
    @Override
    public IStatus validateName(String segment, int typeMask) {
        return new Status(IStatus.OK, "exo", "");
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#validateNatureSet(java.lang.String[]) */
    @Override
    public IStatus validateNatureSet(String[] natureIds) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#validatePath(java.lang.String, int) */
    @Override
    public IStatus validatePath(String path, int typeMask) {
        // TODO Auto-generated method stub
        return Status.OK_STATUS;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#validateProjectLocation(com.codenvy.eclipse.core.resources.IProject,
     *      com.codenvy.eclipse.core.runtime.IPath)
     */
    @Override
    public IStatus validateProjectLocation(IProject project, IPath location) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IWorkspace#validateProjectLocationURI(com.codenvy.eclipse.core.resources.IProject,
     *      java.net.URI)
     */
    @Override
    public IStatus validateProjectLocationURI(IProject project, URI location) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IWorkspace#getPathVariableManager() */
    @Override
    public IPathVariableManager getPathVariableManager() {
        // TODO Auto-generated method stub
        return null;
    }

    public VirtualFileSystem getVFS() {
        return vfs;
    }

    /**
     * Returns an open input stream on the contents of provided {@link IFile}.
     * The client is responsible for closing the stream when finished.
     *
     * @param file
     *         {@link IFile} to get contents
     * @return an input stream containing the contents of the file
     * @throws CoreException
     * @see com.codenvy.eclipse.core.resources.IFile#getContents(boolean)
     */
    InputStream getFileContents(IFile file) throws CoreException {
        try {
            String id = getVfsIdByFullPath(file.getFullPath());
            return vfs.getContent(id).getStream();
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, e.getMessage(), e));
        }
    }

    /**
     * Update binary contents of specified {@link IFile}.
     *
     * @param file
     *         {@link IFile} to update contents
     * @param newContent
     *         new content of {@link IFile}
     * @throws CoreException
     * @see com.codenvy.eclipse.core.resources.IFile#setContents(java.io.InputStream, int,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    public void setFileContents(IFile file, InputStream newContent) throws CoreException {
        try {

            vfs.updateContent(getVfsIdByFullPath(file.getFullPath()), /* TODO use special resolver*/
                              MediaType.valueOf("application/java"), newContent, null);
        } catch (ItemNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
        } catch (InvalidArgumentException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
        } catch (LockException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
        } catch (PermissionDeniedException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, e.getMessage(), e));
        }
    }

    /**
     * Move provided {@link IResource} to <code>destination</code> path.
     *
     * @param resource
     *         {@link IResource} to move
     * @param destination
     *         the destination path
     * @throws CoreException
     * @see com.codenvy.eclipse.core.resources.IResource#move(com.codenvy.eclipse.core.runtime.IPath, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    public void moveResource(IResource resource, IPath destination) throws CoreException {
        if (resource.getType() == IResource.ROOT || destination.isRoot()) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1,
                                               "The workspace root may not be the source or destination location of a move operation",
                                               null));
        }

        IPath parentDestinationPath = destination.removeLastSegments(1);
        try {
            Item item = getItemByPath(resource.getFullPath());
            //if just rename
            if (!parentDestinationPath.equals(resource.getFullPath().removeLastSegments(1))) {
                String destinationParentId = getVfsIdByFullPath(parentDestinationPath);
                vfs.move(item.getId(), destinationParentId, null);
            }
            vfs.rename(item.getId(), MediaType.valueOf(item.getMimeType()),
                       destination.segment(destination.segmentCount() - 1), null);
        } catch (ItemNotFoundException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (ConstraintException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (ItemAlreadyExistException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (LockException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (PermissionDeniedException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, e.getMessage(), e));
        }
    }

    /**
     * Copy provided {@link IResource} to <code>destination</code> path.
     *
     * @param resource
     *         {@link IResource} to copy
     * @param destination
     *         the destination path
     * @throws CoreException
     * @see com.codenvy.eclipse.core.resources.IResource#copy(com.codenvy.eclipse.core.runtime.IPath, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    void copyResource(IResource resource, IPath destination) throws CoreException {
        if (resource.getType() == IResource.ROOT || destination.isRoot()) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1,
                                               "The workspace root may not be the source or destination location of a copy operation",
                                               null));
        }

        IPath parentDestinationPath = destination.removeLastSegments(1);
        try {
            String parentDestinationId = getVfsIdByFullPath(parentDestinationPath);
            String id = getVfsIdByFullPath(resource.getFullPath());
            Item copiedItem = vfs.copy(id, parentDestinationId);
            vfs.rename(copiedItem.getId(), MediaType.valueOf(copiedItem.getMimeType()),
                       destination.segment(destination.segmentCount() - 1), null);
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, e.getMessage(), e));
        }
    }

    /**
     * Deletes specified {@link IResource} from the workspace.
     * Deletion applies recursively to all members of specified {@link IResource}.
     *
     * @param resource
     *         {@link IResource} to delete
     * @throws CoreException
     * @see com.codenvy.eclipse.core.resources.IResource#delete(int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    void deleteResource(IResource resource) throws CoreException {
        if (resource.getType() == IResource.ROOT) {
            IResource[] members = ((IContainer)resource).members();
            for (IResource member : members) {
                deleteResource(member);
            }
            return;
        }

        try {
            vfs.delete(getVfsIdByFullPath(resource.getFullPath()), null);
            IPath path = resource.getFullPath();
            if (path.equals(Path.ROOT)) {
                IProject[] children = getRoot().getProjects(IContainer.INCLUDE_HIDDEN);
                for (int i = 0; i < children.length; i++) {
                    tree.deleteElement(children[i].getFullPath());
                }
            } else {
                tree.deleteElement(path);
            }
        } catch (ItemNotFoundException e) {
            return;
        } catch (ConstraintException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (LockException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (PermissionDeniedException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        }
    }

    private Item getItemByPath(IPath path) throws VirtualFileSystemException {
        return vfs.getItemByPath(path.toString(), null, false, PropertyFilter.ALL_FILTER);
    }

    /**
     * Get all children's from path.
     *
     * @param fullPath
     *         path to parent
     * @param memberFlags
     *         the member flags
     * @return array of the children resources
     * @throws CoreException
     *         if parent not exist
     */
    public IResource[] getMembers(IPath fullPath, int memberFlags) throws CoreException {
        try {
            Item item = getItemByPath(fullPath);
            if (item instanceof Folder) {
                ItemList<Item> vfsChildren = vfs.getChildren(item.getId(), -1, 0, null, false, PropertyFilter.ALL_FILTER);
                IResource[] children = new IResource[vfsChildren.getItems().size()];
                List<Item> items = vfsChildren.getItems();
                for (int i = 0; i < items.size(); i++) {
                    Item c = items.get(i);
                    if (c instanceof Folder) {
                        children[i] = new FolderResource(new Path(c.getPath()), this);
                    } else if (c instanceof File) {
                        children[i] = new FileResource(new Path(c.getPath()), this);
                    } else if (c instanceof Project) {
                        children[i] = new ProjectResource(new Path(c.getPath()), this);
                    } else {
                        throw new CoreException(
                                new Status(IStatus.ERROR, "", "Unknown type of item: " + c.getItemType().toString()));
                    }
                }
                return children;
            } else {
                throw new CoreException(new Status(IStatus.ERROR, "", "Resource no a folder"));
            }
        } catch (VirtualFileSystemException e) {
            throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
        }

    }

    /**
     * Finds and returns the member resource identified by the given path in this path,
     * or null if no such resource exists. The supplied path may be absolute or relative;
     * Trailing separators and the path's device are ignored.
     * Parent references in the supplied path are discarded if they go above the workspace root.
     *
     * @param containerResource
     * @param path
     *         the path for resource
     * @return the resource
     */
    public IResource findMember(ContainerResource containerResource, IPath path) {
        try {
            if (path.isAbsolute()) {
                Item item = getItemByPath(path);
                //            Folder f = (Folder)item;
                //            ItemList<Item> children = vfs.getChildren(f.getId(), -1, 0, null, PropertyFilter.ALL_FILTER);
                //            for (Item i : children.getItems())
                //            {
                if (item.getPath().equals(path.toString())) {
                    Path resPath = new Path(item.getPath());
                    if (item instanceof File) {
                        return new FileResource(resPath, this);
                    } else if (item instanceof Folder) {
                        return new FolderResource(resPath, this);
                    } else {
                        return new ProjectResource(resPath, this);
                    }
                }
            }
            //         }
        } catch (ItemNotFoundException e) {
            return null;
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Property getProjectProperty(String propertyId, ProjectResource projectResource) {
        try {
            Item item = getItemByPath(projectResource.getFullPath());
            return item.getProperty(propertyId);
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    public void setProjectProperty(Property property, ProjectResource projectResource) {
        try {
            Item item = getItemByPath(projectResource.getFullPath());
            vfs.updateItem(item.getId(), Arrays.asList(property), null);
        } catch (VirtualFileSystemException e) {
            //TODO
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createFile(FileResource resource, InputStream source) {
        String parentId = null;
        try {
            parentId = getVfsIdByFullPath(resource.getParent().getFullPath());
            vfs.createFile(parentId, resource.getName(), /* TODO use special resolver*/
                           MediaType.valueOf("application/java"), source);
        } catch (CoreException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ItemNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ItemAlreadyExistException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidArgumentException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (PermissionDeniedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    /**
     * Returns a non-negative modification stamp, or <code>NULL_STAMP</code> if
     * the resource does not exist or is not accessible.
     *
     * @param resource
     *         {@link IResource}
     * @return the modification stamp, or <code>NULL_STAMP</code> if this resource either does not exist or is not accessible
     * @see com.codenvy.eclipse.core.resources.IResource#getModificationStamp()
     */
    public long getModificationStamp(IResource resource) {
        try {
            Item item = getVfsItemByFullPath(resource.getFullPath());
            if (item instanceof FileImpl) {
                return ((FileImpl)item).getLastModificationDate();
            }
        } catch (ItemNotFoundException e) {
            return IResource.NULL_STAMP;
        } catch (CoreException e) {
            return IResource.NULL_STAMP;
        }
        return IResource.NULL_STAMP;
    }

    /**
     * Closes a stream and ignores any resulting exception. This is useful
     * when doing stream cleanup in a finally block where secondary exceptions
     * are not worth logging.
     */
    private static void safeClose(Closeable stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * Returns the resource info for the identified resource.
     * null is returned if no such resource can be found.
     * If the phantom flag is true, phantom resources are considered.
     * If the mutable flag is true, the info is opened for change.
     * <p/>
     * This method DOES NOT throw an exception if the resource is not found.
     */
    public ResourceInfo getResourceInfo(IPath path, boolean phantom, boolean mutable) {
        try {
            if (path.segmentCount() == 0) {
                ResourceInfo info = (ResourceInfo)tree.getTreeData();
                Assert.isNotNull(info, "Tree root info must never be null"); //$NON-NLS-1$
                return info;
            }
            ResourceInfo result = null;
            if (!tree.includes(path)) {
                return null;
            }
            if (mutable) {
                result = (ResourceInfo)tree.openElementData(path);
            } else {
                result = (ResourceInfo)tree.getElementData(path);
            }
            if (result != null && (!phantom && result.isSet(M_PHANTOM))) {
                return null;
            }
            return result;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setTreeLocked(boolean locked) {
        Assert.isTrue(!locked || treeLocked == null, "The workspace tree is already locked"); //$NON-NLS-1$
        treeLocked = locked ? Thread.currentThread() : null;
    }

    /** Returns the current element tree for this workspace */
    public ElementTree getElementTree() {
        return tree;
    }

    public IProject[] getProjects() {
        try {
            Item rootItem = getVfsItemByFullPath(defaultRoot.getFullPath());
            ItemList<Item> childrens = vfs.getChildren(rootItem.getId(), -1, 0, null, false, PropertyFilter.ALL_FILTER);
            List<IProject> projects = new ArrayList<IProject>();

            for (int i = 0; i < childrens.getItems().size(); i++) {

                Item item = childrens.getItems().get(i);
                if (item instanceof Project) {
                    projects.add(new ProjectResource(new Path(item.getPath()), this));
                }
            }
            return projects.toArray(new IProject[projects.size()]);
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CoreException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return new IProject[0];
    }
}
