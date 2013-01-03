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

import static org.junit.Assert.assertFalse;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkspaceResourceTest extends ResourcesBaseTest
{

   @Test
   public void testWorkspaceResource() throws VirtualFileSystemException, CoreException
   {
      VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(ID).newInstance(null, eventListenerList);
      WorkspaceResource ws = new WorkspaceResource(vfs);
      FolderResource resource = (FolderResource)ws.newResource(new Path("/test/my"), IResource.FOLDER);
      assertFalse(resource.exists());
      resource.create(true, true, new NullProgressMonitor());
      Item itemByPath = vfs.getItemByPath("/test/my", null, PropertyFilter.NONE_FILTER);

   }
}
