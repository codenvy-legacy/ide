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

import org.exoplatform.ide.vfs.server.URLHandlerFactorySetup;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.MemoryFileSystemProvider;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFileSystemContext;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFolder;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.junit.Before;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class ResourcesBaseTest
{

   protected static final String ID = "memory";

   protected static EventListenerList eventListenerList;

   protected static VirtualFileSystemRegistry virtualFileSystemRegistry = new VirtualFileSystemRegistry();

   static
   {
      URLHandlerFactorySetup.setup(virtualFileSystemRegistry, eventListenerList);
   }


   protected MemoryFileSystemContext memoryContext;

   protected MemoryFolder testRoot;

   protected String TEST_ROOT_NAME = "TESTROOT";

   @Before
   public void stUp() throws VirtualFileSystemException
   {
      System.setProperty("org.exoplatform.mimetypes", "conf/mimetypes.properties");

      eventListenerList = new EventListenerList();
      memoryContext = new MemoryFileSystemContext();

      MemoryFolder root = memoryContext.getRoot();
      testRoot = new MemoryFolder(TEST_ROOT_NAME);
      root.addChild(testRoot);
      memoryContext.putItem(testRoot);

      virtualFileSystemRegistry.registerProvider(ID, new MemoryFileSystemProvider(ID, memoryContext));
   }

}
