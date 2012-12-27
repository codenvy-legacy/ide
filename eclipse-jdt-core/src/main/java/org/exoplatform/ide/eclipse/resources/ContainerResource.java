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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemImpl;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ContainerResource.java Dec 26, 2012 5:32:18 PM azatsarynnyy $
 *
 */
public class ContainerResource extends ItemResource implements IContainer
{

   /**
    * @param item
    * @param vfs
    */
   public ContainerResource(ItemImpl item, VirtualFileSystem vfs)
   {
      super(item, vfs);
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
      // TODO Auto-generated method stub
      return null;
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
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath, boolean)
    */
   @Override
   public IResource findMember(IPath path, boolean includePhantoms)
   {
      // TODO Auto-generated method stub
      return null;
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
      try
      {
         Item item = vfs.getItemByPath(path.toString(), null, PropertyFilter.NONE_FILTER);
         if (ItemType.FILE == item.getItemType())
         {
            return (FileResource)item;
         }
         return null;
      }
      catch (ItemNotFoundException e)
      {
         return null;
         // TODO
         //return (FileResource)vfs.createFile(delegate.getId(), "", MediaType.TEXT_PLAIN_TYPE, null);
      }
      catch (PermissionDeniedException e)
      {
         return null;
      }
      catch (VirtualFileSystemException e)
      {
         return null;
      }
   }

   /**
    * @see org.eclipse.core.resources.IContainer#getFolder(org.eclipse.core.runtime.IPath)
    */
   @Override
   public IFolder getFolder(IPath path)
   {
      try
      {
         Item item = vfs.getItemByPath(path.toString(), null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER == item.getItemType())
         {
            return (FolderResource)item;
         }
         return null;
      }
      catch (Exception e)
      {
         return null;
      }
   }

   /**
    * @see org.eclipse.core.resources.IContainer#members()
    */
   @Override
   public IResource[] members() throws CoreException
   {
      try
      {
         ItemList<Item> childrenList = vfs.getChildren(delegate.getId(), -1, 0, null, PropertyFilter.NONE_FILTER);
         if (childrenList.getNumItems() > 0)
         {
            IResource[] resourceArray = new IResource[childrenList.getNumItems()];
            int i = 0;
            for (Item item : childrenList.getItems())
            {
               switch (item.getItemType())
               {
                  case FILE :
                     resourceArray[i++] = (FileResource)item;
                     break;
                  case FOLDER :
                     resourceArray[i++] = (FolderResource)item;
                     break;
                  case PROJECT :
                     resourceArray[i++] = (ProjectResource)item;
                     break;
               }
            }
            return resourceArray;
         }

         return new IResource[0];
      }
      catch (ItemNotFoundException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
      }
      catch (InvalidArgumentException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
      }
      catch (PermissionDeniedException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
      }
      catch (VirtualFileSystemException e)
      {
         throw new CoreException(new Status(IStatus.ERROR, Status.CANCEL_STATUS.getPlugin(), 1, "", e));
      }
   }

   /**
    * @see org.eclipse.core.resources.IContainer#members(boolean)
    */
   @Override
   public IResource[] members(boolean includePhantoms) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IContainer#members(int)
    */
   @Override
   public IResource[] members(int memberFlags) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
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
