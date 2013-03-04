/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.File;

import static org.exoplatform.ide.commons.ContainerUtils.readValueParam;

/**
 * Useful when virtual filesystem used in single workspace mode (not cloud environment).
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SimpleLocalFSMountStrategy implements LocalFSMountStrategy
{
   private final java.io.File mountRoot;

   public SimpleLocalFSMountStrategy(InitParams initParams)
   {
      this(new java.io.File(
         readValueParam(initParams, "mount-root", System.getProperty("org.exoplatform.ide.server.fs-root-path"))));
   }

   public SimpleLocalFSMountStrategy(java.io.File mountRoot)
   {
      this.mountRoot = mountRoot;
   }

   @Override
   public File getMountPath(String workspace) throws VirtualFileSystemException
   {
      return new java.io.File(mountRoot, workspace == null || workspace.isEmpty() ? "default" : workspace);
   }

   @Override
   public File getMountPath() throws VirtualFileSystemException
   {
      return getMountPath(null);
   }
}
