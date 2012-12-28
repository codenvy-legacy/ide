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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.net.URI;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: WorkspaceRootResource.java Dec 28, 2012 2:41:24 PM azatsarynnyy $
 *
 */
public class WorkspaceRootResource extends ContainerResource implements IWorkspaceRoot
{

   protected WorkspaceRootResource(IPath path, WorkspaceResource container)
   {
      super(path, container);
      Assert.isTrue(path.equals(Path.ROOT));
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#findContainersForLocation(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IContainer[] findContainersForLocation(IPath location)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#findContainersForLocationURI(java.net.URI)
    */
   @Override
   public IContainer[] findContainersForLocationURI(URI location)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#findContainersForLocationURI(java.net.URI, int)
    */
   @Override
   public IContainer[] findContainersForLocationURI(URI location, int memberFlags)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#findFilesForLocation(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IFile[] findFilesForLocation(IPath location)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#findFilesForLocationURI(java.net.URI)
    */
   @Override
   public IFile[] findFilesForLocationURI(URI location)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#findFilesForLocationURI(java.net.URI, int)
    */
   @Override
   public IFile[] findFilesForLocationURI(URI location, int memberFlags)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#getContainerForLocation(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IContainer getContainerForLocation(IPath location)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#getFileForLocation(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IFile getFileForLocation(IPath location)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#getProject(java.lang.String)
    */
   @Override
   public IProject getProject(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#getProjects()
    */
   @Override
   public IProject[] getProjects()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IWorkspaceRoot#getProjects(int)
    */
   @Override
   public IProject[] getProjects(int memberFlags)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.eclipse.resources.ItemResource#getType()
    */
   @Override
   public int getType()
   {
      // TODO Auto-generated method stub
      return 0;
   }


   /**
    * @see org.eclipse.core.resources.IResource#getParent()
    */
   @Override
   public IContainer getParent()
   {
      return null;
   }

}
