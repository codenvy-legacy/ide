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

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.ide.commons.NameGenerator;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.ExitCodes;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Test1 extends LocalFileSystemTest
{

   private final String folderName = "Test1_Folder";

   private String destinationPath;
   private String destinationId;

   private String folderId;
   private String folderPath;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      folderPath = createDirectory(testRootPath, folderName);
      createTree(folderPath, 6, 4, null);
      destinationPath = createDirectory(testRootPath, "Test1_DestinationFolder");

      folderId = pathToId(folderPath);
      destinationId = pathToId(destinationPath);
   }





   public void getChildren() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "children/" + folderId;
      ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      log.info(new String(writer.getBody()));
   }

   public void copyFolder() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String requestPath = SERVICE_URI + "copy/" + folderId + '?' + "parentId=" + destinationId;
      final long start = System.currentTimeMillis();
      ContainerResponse response = launcher.service("POST", requestPath, BASE_URI, null, null, writer, null);
   }



   public void test1() throws Exception
   {
      LocalFileSystemProvider lp = (LocalFileSystemProvider)virtualFileSystemRegistry.getProvider("fs");
      final MountPoint mp = lp.mountPoint;
      final VirtualFile vf = mp.getVirtualFile(folderPath);
      VirtualFile ff=null;
      for (VirtualFile file : mp.getChildren(vf))
      {
         if (file.isFile()) {
            ff=file;
            break;
         }
      }

      final VirtualFile vf2 = mp.getVirtualFile(destinationPath);
      final VirtualFile vf3 = ff;
      final int num = 2;
      final CountDownLatch latch = new CountDownLatch(num);
      for (int i = 0; i < num; i++)
      {
         final int j = i;
         new Thread()
         {
            @Override
            public void run()
            {
               try
               {
                  if (j % 2 == 0)
                  {
                     mp.copy(vf, vf2);
                  }
                  else
                  {
                     mp.getContent(vf3);
                     //mp.getChildren(vf);
                  }
               }
               catch (VirtualFileSystemException e)
               {
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               } finally {
                  latch.countDown();
               }
            }
         }.start();
      }

      latch.await();
   }
}
