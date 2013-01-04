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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import java.net.URI;

/**
 * Implementation of {@link IFolder}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FolderResource.java Dec 26, 2012 12:31:12 PM azatsarynnyy $
 *
 */
public class FolderResource extends ContainerResource implements IFolder
{

   /**
    * Creates new {@link FolderResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    * 
    * @param path {@link IPath}
    * @param workspace {@link WorkspaceResource}
    */
   protected FolderResource(IPath path, WorkspaceResource workspace)
   {
      super(path, workspace);
   }

   /**
    * @see org.eclipse.core.resources.IFolder#create(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException
   {
      create((force ? IResource.FORCE : IResource.NONE), local, monitor);
   }

   /**
    * @see org.eclipse.core.resources.IFolder#create(int, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException
   {
      workspace.createResource(this);
   }

   /**
    * @see org.eclipse.core.resources.IFolder#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFolder#createLink(java.net.URI, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.eclipse.resources.ItemResource#getType()
    */
   @Override
   public int getType()
   {
      return FOLDER;
   }

}
