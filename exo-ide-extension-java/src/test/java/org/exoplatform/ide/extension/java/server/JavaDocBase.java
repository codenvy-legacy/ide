/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 29, 2011 2:11:05 PM evgen $
 *
 */
public abstract class JavaDocBase extends Base
{
   /**
    * 
    */
   protected static final String VFS_ID = "ws";

   protected static VirtualFileSystem vfs;

   protected static Folder project;

   protected static VirtualFileSystemRegistry vfsRegistry;

   protected static JavaCodeAssistant javaCa;

   @BeforeClass
   public static void init() throws VirtualFileSystemException, IOException, InterruptedException
   {
      vfsRegistry = (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
      vfs = vfsRegistry.getProvider(VFS_ID).newInstance(null);
      try
      {
         project = (Folder)vfs.getItemByPath(JavaDocBuilderVfsTest.class.getSimpleName(), null, PropertyFilter.NONE_FILTER);
         vfs.delete(project.getId(), null);
         project = vfs.createFolder(vfs.getInfo().getRoot().getId(), JavaDocBuilderVfsTest.class.getSimpleName());
      }
      catch (ItemNotFoundException e)
      {
         project = vfs.createFolder(vfs.getInfo().getRoot().getId(), JavaDocBuilderVfsTest.class.getSimpleName());
      }
      vfs.importZip(project.getId(),
         Thread.currentThread().getContextClassLoader().getResourceAsStream("exo-ide-client.zip"), true);
      javaCa = new JavaCodeAssistant(null, vfsRegistry);
   }

   @AfterClass
   public static void cleanUp() throws ItemNotFoundException, ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      vfs.delete(project.getId(), null);
   }
}
