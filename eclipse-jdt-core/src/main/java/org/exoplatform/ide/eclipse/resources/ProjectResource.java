/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.eclipse.resources;

import org.eclipse.core.internal.events.LifecycleEvent;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
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
public class ProjectResource extends ContainerResource implements IProject
{

   public static final String NATURES_ID = "NATURES_ID";

   private IProjectDescription description;

   /**
    * Creates new {@link ProjectResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    *
    * @param path      {@link IPath}
    * @param workspace {@link WorkspaceResource}
    */
   public ProjectResource(IPath path, WorkspaceResource workspace)
   {
      super(path, workspace);
      readDescription();
   }

   private void readDescription()
   {
      description = new ProjectDescription();
      if (exists())
      {
         Property property = workspace.getProjectProperty(NATURES_ID, this);
         if (property != null)
         {
            description.setNatureIds(property.getValue().toArray(new String[property.getValue().size()]));
         }
      }
   }

   /**
    * @see org.eclipse.core.resources.IProject#build(int, java.lang.String, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void build(int kind, String builderName, Map<String, String> args,
      IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#build(int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void build(int kind, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#build(org.eclipse.core.resources.IBuildConfiguration, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void build(IBuildConfiguration config, int kind, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#close(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void close(IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException
   {
      create(description, IResource.NONE, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(IProgressMonitor monitor) throws CoreException
   {
      create(null, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      final ISchedulingRule rule = workspace.getRuleFactory().createRule(this);
      try
      {
         workspace.prepareOperation(rule, monitor);
         workspace.broadcastEvent(LifecycleEvent.newEvent(LifecycleEvent.PRE_PROJECT_CREATE, this));
         workspace.beginOperation(true);
         workspace.createResource(this);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      finally
      {
         workspace.endOperation(rule, true, Policy.subMonitorFor(monitor, Policy.endOpWork));
      }
   }

   /**
    * @see org.eclipse.core.resources.IProject#getActiveBuildConfig()
    */
   @Override
   public IBuildConfiguration getActiveBuildConfig() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getBuildConfig(java.lang.String)
    */
   @Override
   public IBuildConfiguration getBuildConfig(String configName) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getBuildConfigs()
    */
   @Override
   public IBuildConfiguration[] getBuildConfigs() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getContentTypeMatcher()
    */
   @Override
   public IContentTypeMatcher getContentTypeMatcher() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getDescription()
    */
   @Override
   public IProjectDescription getDescription() throws CoreException
   {
      return description;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getNature(java.lang.String)
    */
   @Override
   public IProjectNature getNature(String natureId) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getWorkingLocation(java.lang.String)
    */
   @Override
   public IPath getWorkingLocation(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getReferencedProjects()
    */
   @Override
   public IProject[] getReferencedProjects() throws CoreException
   {
      // TODO Auto-generated method stub
      return new IProject[0];
   }

   /**
    * @see org.eclipse.core.resources.IProject#getReferencingProjects()
    */
   @Override
   public IProject[] getReferencingProjects()
   {
      // TODO Auto-generated method stub
      return new IProject[0];
   }

   /**
    * @see org.eclipse.core.resources.IProject#getReferencedBuildConfigs(java.lang.String, boolean)
    */
   @Override
   public IBuildConfiguration[] getReferencedBuildConfigs(String configName,
      boolean includeMissing) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#hasBuildConfig(java.lang.String)
    */
   @Override
   public boolean hasBuildConfig(String configName) throws CoreException
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
    */
   @Override
   public boolean hasNature(String natureId) throws CoreException
   {
      return description.hasNature(natureId);
   }

   /**
    * @see org.eclipse.core.resources.IProject#isNatureEnabled(java.lang.String)
    */
   @Override
   public boolean isNatureEnabled(String natureId) throws CoreException
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IProject#isOpen()
    */
   @Override
   public boolean isOpen()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IProject#loadSnapshot(int, java.net.URI, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void loadSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#move(org.eclipse.core.resources.IProjectDescription, boolean,
    *      org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException
   {
      Assert.isNotNull(description);
      move(description, force ? IResource.FORCE : IResource.NONE, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IProject#open(int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void open(int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#open(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void open(IProgressMonitor monitor) throws CoreException
   {
      open(IResource.NONE, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IProject#saveSnapshot(int, java.net.URI, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void saveSnapshot(int options, URI snapshotLocation, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException
   {
      setDescription(description, IResource.KEEP_HISTORY, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setDescription(IProjectDescription description, int updateFlags,
      IProgressMonitor monitor) throws CoreException
   {
      Property p = new PropertyImpl(NATURES_ID, Arrays.asList(description.getNatureIds()));
      workspace.setProjectProperty(p, this);
      this.description = description;
   }

   /**
    * @see org.exoplatform.ide.eclipse.resources.ItemResource#getType()
    */
   @Override
   public int getType()
   {
      return PROJECT;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getParent()
    */
   @Override
   public IContainer getParent()
   {
      return workspace.getRoot();
   }

   public void setPath(IPath pathInTenant)
   {
      path = pathInTenant;
   }

   @Override
   public IPath getLocation()
   {
      return super.getLocation();
   }

   @Override
   public IPath getFullPath()
   {
      return super.getFullPath();
   }
}
