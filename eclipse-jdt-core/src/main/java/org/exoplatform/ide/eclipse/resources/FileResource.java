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
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentDescription;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FileResource.java Dec 26, 2012 12:27:39 PM azatsarynnyy $
 *
 */
public class FileResource extends ItemResource implements IFile
{

   /**
    * Creates new {@link FileResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    * 
    * @param path {@link IPath}
    * @param workspace {@link WorkspaceResource}
    */
   protected FileResource(IPath path, WorkspaceResource workspace)
   {
      super(path, workspace);
   }

   /**
    * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException
   {
      create(source, (force ? IResource.FORCE : IResource.NONE), monitor);
   }

   /**
    * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      workspace.createResource(this);
   }

   /**
    * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#createLink(java.net.URI, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#getCharset()
    */
   @Override
   public String getCharset() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFile#getCharset(boolean)
    */
   @Override
   public String getCharset(boolean checkImplicit) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFile#getCharsetFor(java.io.Reader)
    */
   @Override
   public String getCharsetFor(Reader reader) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFile#getContentDescription()
    */
   @Override
   public IContentDescription getContentDescription() throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFile#getContents()
    */
   @Override
   public InputStream getContents() throws CoreException
   {
      return getContents(true);
   }

   /**
    * @see org.eclipse.core.resources.IFile#getContents(boolean)
    */
   @Override
   public InputStream getContents(boolean force) throws CoreException
   {
      try
      {
         String id = workspace.getVfsIdByFullPath(getFullPath());
         return workspace.getVFS().getContent(id).getStream();
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
    * @see org.eclipse.core.resources.IFile#getEncoding()
    */
   @Override
   public int getEncoding() throws CoreException
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.core.resources.IFile#getHistory(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String)
    */
   @Override
   public void setCharset(String newCharset) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setCharset(String newCharset, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor)
      throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.eclipse.resources.ItemResource#getType()
    */
   @Override
   public int getType()
   {
      return FILE;
   }

}
