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

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ContainerResource.java Dec 26, 2012 5:32:18 PM azatsarynnyy $
 */
public abstract class ContainerResource extends ItemResource implements IContainer
{

   /**
    * Creates new {@link ContainerResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    *
    * @param path      {@link IPath}
    * @param workspace {@link WorkspaceResource}
    */
   protected ContainerResource(IPath path, WorkspaceResource workspace)
   {
      super(path, workspace);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#exists(org.eclipse.core.runtime.IPath)
    */
   @Override
   public boolean exists(IPath path)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String)
    */
   @Override
   public IResource findMember(String path)
   {
      return findMember(path, false);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String, boolean)
    */
   @Override
   public IResource findMember(String path, boolean includePhantoms)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IResource findMember(IPath path)
   {
      return findMember(path, false);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath, boolean)
    */
   @Override
   public IResource findMember(IPath path, boolean includePhantoms)
   {
      if (path.isEmpty())
      {
         return this;
      }
      return workspace.findMember(this, path);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#getDefaultCharset()
    */
   @Override
   public String getDefaultCharset() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#getDefaultCharset(boolean)
    */
   @Override
   public String getDefaultCharset(boolean checkImplicit) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#getFile(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IFile getFile(IPath path)
   {
      return (IFile)workspace.newResource(getFullPath().append(path), FILE);
   }

   /**
    * @see org.eclipse.core.resources.IFolder#getFile(java.lang.String)
    * @see org.eclipse.core.resources.IProject#getFile(java.lang.String)
    */
   public IFile getFile(String name)
   {
      return (IFile)workspace.newResource(getFullPath().append(name), FILE);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#getFolder(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IFolder getFolder(IPath path)
   {
      return (IFolder)workspace.newResource(getFullPath().append(path), FOLDER);
   }

   /**
    * @see org.eclipse.core.resources.IFolder#getFolder(java.lang.String)
    * @see org.eclipse.core.resources.IProject#getFolder(java.lang.String)
    */
   public IFolder getFolder(String name)
   {
      return (IFolder)workspace.newResource(getFullPath().append(name), FOLDER);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#members()
    */
   @Override
   public IResource[] members() throws CoreException
   {
      return members(IResource.NONE);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#members(boolean)
    */
   @Override
   public IResource[] members(boolean includePhantoms) throws CoreException
   {
      return members(includePhantoms ? INCLUDE_PHANTOMS : IResource.NONE);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public IResource[] members(int memberFlags) throws CoreException
   {
      return workspace.getMembers(getFullPath(), memberFlags);
   }

   /**
    * @see org.eclipse.core.resources.IContainer#findDeletedMembersWithHistory(int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#setDefaultCharset(java.lang.String)
    */
   @Override
   public void setDefaultCharset(String charset) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IContainer#setDefaultCharset(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setDefaultCharset(String charset, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IContainer#createFilter(int, org.eclipse.core.resources.FileInfoMatcherDescription, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IResourceFilterDescription createFilter(int type, FileInfoMatcherDescription matcherDescription,
      int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#getFilters()
    */
   @Override
   public IResourceFilterDescription[] getFilters() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

}
