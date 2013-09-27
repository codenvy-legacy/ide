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

import com.codenvy.eclipse.core.internal.events.LifecycleEvent;
import com.codenvy.eclipse.core.internal.resources.ProjectDescription;
import com.codenvy.eclipse.core.internal.utils.Policy;
import com.codenvy.eclipse.core.resources.IBuildConfiguration;
import com.codenvy.eclipse.core.resources.IContainer;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IProjectDescription;
import com.codenvy.eclipse.core.resources.IProjectNature;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.content.IContentTypeMatcher;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;

import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

/**
 * Implementation of {@link IProject}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ProjectResource.java Dec 27, 2012 11:17:10 AM azatsarynnyy $
 */
public class ProjectResource extends ContainerResource implements IProject {

    public static final String NATURES_ID = "NATURES_ID";

    private IProjectDescription description;

    /**
     * Creates new {@link ProjectResource} with the specified <code>path</code> in pointed <code>workspace</code>.
     *
     * @param path
     *         {@link IPath}
     * @param workspace
     *         {@link WorkspaceResource}
     */
    public ProjectResource(IPath path, WorkspaceResource workspace) {
        super(path, workspace);
        readDescription();
    }

    private void readDescription() {
        description = new ProjectDescription();
        if (exists()) {
            Property property = workspace.getProjectProperty(NATURES_ID, this);
            if (property != null) {
                description.setNatureIds(property.getValue().toArray(new String[property.getValue().size()]));
            }
        }
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#build(int, java.lang.String, java.util.Map,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void build(int kind, String builderName, Map<String, String> args,
                      IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IProject#build(int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void build(int kind, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#build(com.codenvy.eclipse.core.resources.IBuildConfiguration, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void build(IBuildConfiguration config, int kind, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IProject#close(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void close(IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#create(com.codenvy.eclipse.core.resources.IProjectDescription,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
        create(description, IResource.NONE, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#create(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void create(IProgressMonitor monitor) throws CoreException {
        create(null, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#create(com.codenvy.eclipse.core.resources.IProjectDescription, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void create(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
        final ISchedulingRule rule = workspace.getRuleFactory().createRule(this);
        try {
            workspace.prepareOperation(rule, monitor);
            workspace.broadcastEvent(LifecycleEvent.newEvent(LifecycleEvent.PRE_PROJECT_CREATE, this));
            workspace.beginOperation(true);
            workspace.createResource(this);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workspace.endOperation(rule, true, Policy.subMonitorFor(monitor, Policy.endOpWork));
        }
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getActiveBuildConfig() */
    @Override
    public IBuildConfiguration getActiveBuildConfig() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getBuildConfig(java.lang.String) */
    @Override
    public IBuildConfiguration getBuildConfig(String configName) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getBuildConfigs() */
    @Override
    public IBuildConfiguration[] getBuildConfigs() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getContentTypeMatcher() */
    @Override
    public IContentTypeMatcher getContentTypeMatcher() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getDescription() */
    @Override
    public IProjectDescription getDescription() throws CoreException {
        return description;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getNature(java.lang.String) */
    @Override
    public IProjectNature getNature(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getWorkingLocation(java.lang.String) */
    @Override
    public IPath getWorkingLocation(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getReferencedProjects() */
    @Override
    public IProject[] getReferencedProjects() throws CoreException {
        // TODO Auto-generated method stub
        return new IProject[0];
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getReferencingProjects() */
    @Override
    public IProject[] getReferencingProjects() {
        // TODO Auto-generated method stub
        return new IProject[0];
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#getReferencedBuildConfigs(java.lang.String, boolean) */
    @Override
    public IBuildConfiguration[] getReferencedBuildConfigs(String configName,
                                                           boolean includeMissing) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#hasBuildConfig(java.lang.String) */
    @Override
    public boolean hasBuildConfig(String configName) throws CoreException {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#hasNature(java.lang.String) */
    @Override
    public boolean hasNature(String natureId) throws CoreException {
        return description.hasNature(natureId);
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#isNatureEnabled(java.lang.String) */
    @Override
    public boolean isNatureEnabled(String natureId) throws CoreException {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#isOpen() */
    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#loadSnapshot(int, java.net.URI,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void loadSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#move(com.codenvy.eclipse.core.resources.IProjectDescription, boolean,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
        Assert.isNotNull(description);
        move(description, force ? IResource.FORCE : IResource.NONE, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#open(int, com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void open(int updateFlags, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /** @see com.codenvy.eclipse.core.resources.IProject#open(com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void open(IProgressMonitor monitor) throws CoreException {
        open(IResource.NONE, monitor);
    }

    /** @see com.codenvy.eclipse.core.resources.IProject#saveSnapshot(int, java.net.URI,
     * com.codenvy.eclipse.core.runtime.IProgressMonitor) */
    @Override
    public void saveSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#setDescription(com.codenvy.eclipse.core.resources.IProjectDescription,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {
        setDescription(description, IResource.KEEP_HISTORY, monitor);
    }

    /**
     * @see com.codenvy.eclipse.core.resources.IProject#setDescription(com.codenvy.eclipse.core.resources.IProjectDescription, int,
     *      com.codenvy.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setDescription(IProjectDescription description, int updateFlags,
                               IProgressMonitor monitor) throws CoreException {
        Property p = new PropertyImpl(NATURES_ID, Arrays.asList(description.getNatureIds()));
        workspace.setProjectProperty(p, this);
        this.description = description;
    }

    /** @see com.codenvy.eclipse.resources.ItemResource#getType() */
    @Override
    public int getType() {
        return PROJECT;
    }

    /** @see com.codenvy.eclipse.core.resources.IResource#getParent() */
    @Override
    public IContainer getParent() {
        return workspace.getRoot();
    }
}
