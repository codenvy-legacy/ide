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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.net.URI;

/**
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
    * @param vfs {@link VirtualFileSystem}
    */
   protected FolderResource(IPath path, WorkspaceResource workspace, VirtualFileSystem vfs)
   {
      super(path, workspace, vfs);
   }

   /**
    * Creates new {@link FolderResource} with the specified <code>path</code> in the pointed <code>workspace</code>
    * with underlying {@link Folder}.
    * 
    * @param path {@link IPath}
    * @param workspace {@link WorkspaceResource}
    * @param vfs {@link VirtualFileSystem}
    * @param item {@link Folder}
    */
   protected FolderResource(IPath path, WorkspaceResource workspace, VirtualFileSystem vfs, Folder item)
   {
      this(path, workspace, vfs);
      this.delegate = item;
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
      Item folder = null;
      try
      {
         folder = vfs.createFolder(delegate.getParentId(), getName());
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
            "Folder already exists in the workspace.", e));
      }
      catch (PermissionDeniedException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
      }
      catch (VirtualFileSystemException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, null, e));
      }
      delegate = folder;
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

}
