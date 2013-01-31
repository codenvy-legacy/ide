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
package com.codenvy.eclipse.resources;

import com.codenvy.eclipse.core.internal.utils.Policy;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;

import java.net.URI;

/**
 * Implementation of {@link IFolder}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FolderResource.java Dec 26, 2012 12:31:12 PM azatsarynnyy $
 */
public class FolderResource extends ContainerResource implements IFolder
{

   /**
    * Creates new {@link FolderResource} with the specified <code>path</code> in pointed <code>workspace</code>.
    *
    * @param path      {@link IPath}
    * @param workspace {@link WorkspaceResource}
    */
   public FolderResource(IPath path, WorkspaceResource workspace)
   {
      super(path, workspace);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IFolder#create(boolean, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException
   {
      create((force ? IResource.FORCE : IResource.NONE), local, monitor);
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IFolder#create(int, boolean, com.codenvy.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException
   {
      final ISchedulingRule rule = workspace.getRuleFactory().createRule(this);
      try
      {
         workspace.prepareOperation(rule, monitor);
         workspace.beginOperation(true);
         workspace.createResource(this);
      }
      finally
      {
         workspace.endOperation(rule, true, Policy.subMonitorFor(monitor, Policy.endOpWork));
      }
   }

   /**
    * @see com.codenvy.eclipse.core.resources.IFolder#createLink(com.codenvy.eclipse.core.runtime.IPath, int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void createLink(IPath localLocation, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see com.codenvy.eclipse.core.resources.IFolder#createLink(java.net.URI, int, com.codenvy.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void createLink(URI location, int updateFlags, IProgressMonitor monitor) throws CoreException
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see com.codenvy.eclipse.resources.ItemResource#getType()
    */
   @Override
   public int getType()
   {
      return FOLDER;
   }

}
