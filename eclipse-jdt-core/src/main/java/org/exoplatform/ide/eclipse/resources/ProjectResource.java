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

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.ProjectImpl;

import java.net.URI;
import java.util.Map;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ProjectResource.java Dec 27, 2012 11:17:10 AM azatsarynnyy $
 *
 */
public class ProjectResource extends ContainerResource implements IProject
{

   /**
    * Creates new {@link ProjectResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    * 
    * @param path {@link IPath}
    * @param workspace {@link WorkspaceResource}
    * @param vfs {@link VirtualFileSystem}
    */
   protected ProjectResource(IPath path, WorkspaceResource workspace, VirtualFileSystem vfs)
   {
      super(path, workspace, vfs);
   }

   /**
    * Creates new {@link ProjectResource} with the specified <code>path</code> in the pointed <code>workspace</code>
    * with underlying {@link ProjectImpl}.
    * 
    * @param path {@link IPath}
    * @param workspace {@link WorkspaceResource}
    * @param vfs {@link VirtualFileSystem}
    * @param item {@link ProjectImpl}
    */
   protected ProjectResource(IPath path, WorkspaceResource workspace, VirtualFileSystem vfs, ProjectImpl item)
   {
      this(path, workspace, vfs);
      this.delegate = item;
   }

   /**
    * @see org.eclipse.core.resources.IProject#build(int, java.lang.String, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void build(int kind, String builderName, Map<String, String> args, IProgressMonitor monitor)
      throws CoreException
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
      try
      {
         vfs.createProject(delegate.getParentId(), getName(), null, null);
      }
      catch (ItemNotFoundException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
      }
      catch (InvalidArgumentException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
      }
      catch (ItemAlreadyExistException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1,
            "Project already exists in the workspace.", e));
      }
      catch (PermissionDeniedException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
      }
      catch (VirtualFileSystemException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
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
      // TODO Auto-generated method stub
      return null;
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
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getReferencingProjects()
    */
   @Override
   public IProject[] getReferencingProjects()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IProject#getReferencedBuildConfigs(java.lang.String, boolean)
    */
   @Override
   public IBuildConfiguration[] getReferencedBuildConfigs(String configName, boolean includeMissing)
      throws CoreException
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
      // TODO Auto-generated method stub
      return false;
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
    * @see org.eclipse.core.resources.IProject#move(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
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
   public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor)
      throws CoreException
   {
      // TODO Auto-generated method stub

   }

}
