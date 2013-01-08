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

import org.eclipse.core.internal.resources.ICoreConstants;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;

import java.net.URI;
import java.util.Map;

/**
 * Implementation of {@link IResource}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ItemResource.java Dec 26, 2012 12:20:07 PM azatsarynnyy $
 *
 */
public abstract class ItemResource implements IResource
{

   protected IPath path;

   protected WorkspaceResource workspace;

   /**
    * Creates new {@link ItemResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    * 
    * @param path {@link IPath}
    * @param workspace {@link WorkspaceResource}
    */
   protected ItemResource(IPath path, WorkspaceResource workspace)
   {
      this.path = path.removeTrailingSeparator();
      this.workspace = workspace;
   }

   /**
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
    */
   @Override
   public boolean contains(ISchedulingRule rule)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
    */
   @Override
   public boolean isConflicting(ISchedulingRule rule)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
    */
   @Override
   public void accept(IResourceProxyVisitor visitor, int memberFlags) throws CoreException
   {
      accept(visitor, IResource.DEPTH_INFINITE, memberFlags);
   }

   /**
    * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int, int)
    */
   @Override
   public void accept(IResourceProxyVisitor visitor, int depth, int memberFlags) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
    */
   @Override
   public void accept(IResourceVisitor visitor) throws CoreException
   {
      accept(visitor, IResource.DEPTH_INFINITE, 0);
   }

   /**
    * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
    */
   @Override
   public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException
   {
      accept(visitor, depth, includePhantoms ? IContainer.INCLUDE_PHANTOMS : 0);
   }

   /**
    * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
    */
   @Override
   public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void clearHistory(IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException
   {
      int updateFlags = force ? IResource.FORCE : IResource.NONE;
      copy(destination, updateFlags, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      workspace.copyResource(this, destination);
   }

   /**
    * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException
   {
      int updateFlags = force ? IResource.FORCE : IResource.NONE;
      copy(description, updateFlags, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
    */
   @Override
   public IMarker createMarker(String type) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#createProxy()
    */
   @Override
   public IResourceProxy createProxy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void delete(boolean force, IProgressMonitor monitor) throws CoreException
   {
      delete(force ? IResource.FORCE : IResource.NONE, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      workspace.deleteResource(this);
   }

   /**
    * This is not an IResource method.
    * 
    * @see org.eclipse.core.resources.IFile#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    * @see org.eclipse.core.resources.IFolder#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    * @see org.eclipse.core.resources.IProject#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException
   {
      int updateFlags = force ? IResource.FORCE : IResource.NONE;
      updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
      delete(updateFlags, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
    */
   @Override
   public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#exists()
    */
   @Override
   public boolean exists()
   {
      try
      {
         workspace.getVfsItemByFullPath(getFullPath());
      }
      catch (ItemNotFoundException e)
      {
         return false;
      }
      catch (CoreException e)
      {
         return false;
      }
      return true;
   }

   /**
    * @see org.eclipse.core.resources.IResource#findMarker(long)
    */
   @Override
   public IMarker findMarker(long id) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
    */
   @Override
   public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int)
    */
   @Override
   public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) throws CoreException
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getFileExtension()
    */
   @Override
   public String getFileExtension()
   {
      String name = getName();
      int index = name.lastIndexOf('.');
      if (index == -1)
         return null;
      if (index == (name.length() - 1))
         return "";
      return name.substring(index + 1);
   }

   /**
    * @see org.eclipse.core.resources.IResource#getFullPath()
    */
   @Override
   public IPath getFullPath()
   {
      return path;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getLocalTimeStamp()
    */
   @Override
   public long getLocalTimeStamp()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getLocation()
    */
   @Override
   public IPath getLocation()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getLocationURI()
    */
   @Override
   public URI getLocationURI()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getMarker(long)
    */
   @Override
   public IMarker getMarker(long id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getModificationStamp()
    */
   @Override
   public long getModificationStamp()
   {
      return workspace.getModificationStamp(this);
   }

   /**
    * @see org.eclipse.core.resources.IResource#getName()
    */
   @Override
   public String getName()
   {
      return path.lastSegment();
   }

   /**
    * @see org.eclipse.core.resources.IResource#getPathVariableManager()
    */
   @Override
   public IPathVariableManager getPathVariableManager()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getParent()
    */
   @Override
   public IContainer getParent()
   {
      int segments = path.segmentCount();
      //zero segment handled by subclasses
      if (segments < 1)
         Assert.isLegal(false, path.toString());
      if (segments == 1)
         //return workspace.getRoot().getProject(path.segment(0));
         return (IProject)workspace.newResource(path.removeLastSegments(1), IResource.PROJECT);
      return (IFolder)workspace.newResource(path.removeLastSegments(1), IResource.FOLDER);
   }

   /**
    * @see org.eclipse.core.resources.IResource#getPersistentProperties()
    */
   @Override
   public Map<QualifiedName, String> getPersistentProperties() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
    */
   @Override
   public String getPersistentProperty(QualifiedName key) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getProject()
    */
   @Override
   public IProject getProject()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
    */
   @Override
   public IPath getProjectRelativePath()
   {
      return getFullPath().removeFirstSegments(ICoreConstants.PROJECT_SEGMENT_LENGTH);
   }

   /**
    * @see org.eclipse.core.resources.IResource#getRawLocation()
    */
   @Override
   public IPath getRawLocation()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getRawLocationURI()
    */
   @Override
   public URI getRawLocationURI()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getSessionProperties()
    */
   @Override
   public Map<QualifiedName, Object> getSessionProperties() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
    */
   @Override
   public Object getSessionProperty(QualifiedName key) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IResource#getType()
    */
   @Override
   public abstract int getType();

   /**
    * @see org.eclipse.core.resources.IResource#getWorkspace()
    */
   @Override
   public IWorkspace getWorkspace()
   {
      return workspace;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isAccessible()
    */
   @Override
   public boolean isAccessible()
   {
      return exists();
   }

   /**
    * @see org.eclipse.core.resources.IResource#isDerived()
    */
   @Override
   public boolean isDerived()
   {
      return isDerived(IResource.NONE);
   }

   /**
    * @see org.eclipse.core.resources.IResource#isDerived(int)
    */
   @Override
   public boolean isDerived(int options)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isHidden()
    */
   @Override
   public boolean isHidden()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isHidden(int)
    */
   @Override
   public boolean isHidden(int options)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isLinked()
    */
   @Override
   public boolean isLinked()
   {
      return isLinked(NONE);
   }

   /**
    * @see org.eclipse.core.resources.IResource#isVirtual()
    */
   @Override
   public boolean isVirtual()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isLinked(int)
    */
   @Override
   public boolean isLinked(int options)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isLocal(int)
    */
   @Override
   public boolean isLocal(int depth)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isPhantom()
    */
   @Override
   public boolean isPhantom()
   {
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isSynchronized(int)
    */
   @Override
   public boolean isSynchronized(int depth)
   {
      return true;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
    */
   @Override
   public boolean isTeamPrivateMember()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#isTeamPrivateMember(int)
    */
   @Override
   public boolean isTeamPrivateMember(int options)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException
   {
      move(destination, force ? IResource.FORCE : IResource.NONE, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      workspace.moveResource(this, destination);
   }

   /**
    * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException
   {
      int updateFlags = force ? IResource.FORCE : IResource.NONE;
      updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
      move(description, updateFlags, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      Assert.isNotNull(description);
      if (getType() != IResource.PROJECT)
      {
         String message =
            "Cannot move " + getFullPath() + " to " + description.getName() + ".  Source must be a project."; //NLS.bind(Messages.resources_moveNotProject, getFullPath(), description.getName());
         throw new ResourceException(IResourceStatus.INVALID_VALUE, getFullPath(), message, null);
      }
      ((ProjectResource)this).move(description, updateFlags, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IFile#move(IPath, boolean, boolean, IProgressMonitor)
    * @see org.eclipse.core.resources.IFolder#move(IPath, boolean, boolean, IProgressMonitor)
    */
   public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException
   {
      int updateFlags = force ? IResource.FORCE : IResource.NONE;
      updateFlags |= keepHistory ? IResource.KEEP_HISTORY : IResource.NONE;
      move(destination, updateFlags, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#revertModificationStamp(long)
    */
   @Override
   public void revertModificationStamp(long value) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setDerived(boolean)
    */
   @Override
   public void setDerived(boolean isDerived) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setDerived(boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setDerived(boolean isDerived, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setHidden(boolean)
    */
   @Override
   public void setHidden(boolean isHidden) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setLocalTimeStamp(long)
    */
   @Override
   public long setLocalTimeStamp(long value) throws CoreException
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
    */
   @Override
   public void setPersistentProperty(QualifiedName key, String value) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
    */
   @Override
   public void setReadOnly(boolean readOnly)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
    */
   @Override
   public void setSessionProperty(QualifiedName key, Object value) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
    */
   @Override
   public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void touch(IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

}
