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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.shared.FolderImpl;

import java.net.URI;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FolderResource.java Dec 26, 2012 12:31:12 PM azatsarynnyy $
 *
 */
public class FolderResource extends ContainerResource implements IFolder
{

   /**
    * @param folder
    * @param vfs
    */
   public FolderResource(FolderImpl folder, VirtualFileSystem vfs)
   {
      super(folder, vfs);
   }

   /**
    * @see org.eclipse.core.resources.IFolder#create(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFolder#create(int, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

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
    * @see org.eclipse.core.resources.IFolder#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFolder#getFile(java.lang.String)
    */
   @Override
   public IFile getFile(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFolder#getFolder(java.lang.String)
    */
   @Override
   public IFolder getFolder(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFolder#move(org.eclipse.core.runtime.IPath, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException
   {
      // TODO Auto-generated method stub

   }

}
