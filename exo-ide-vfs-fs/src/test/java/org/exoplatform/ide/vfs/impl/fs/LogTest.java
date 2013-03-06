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
import org.exoplatform.ide.commons.EnvironmentContext;
import org.exoplatform.ide.vfs.server.util.LinksHelper;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LogTest extends LocalFileSystemTest
{
   String filePath;
   String fileId;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      filePath = createFile(testRootPath, "GetObjectTest_File", DEFAULT_CONTENT_BYTES);
      fileId = pathToId(filePath);
   }

   volatile boolean stop = false;

   public void test1() throws Exception
   {
      final int num = 10;
      final CountDownLatch latch = new CountDownLatch(num);
      final AtomicLong counter = new AtomicLong();
      for (int i = 0; i < num; i++)
      {
         Thread t = new Thread()
         {
            public void run()
            {
               System.err.printf("start thread %d%n", getId());
               ConversationState user = new ConversationState(new Identity("admin"));
               ConversationState.setCurrent(user);
               EnvironmentContext env = EnvironmentContext.getCurrent();
               env.setVariable(EnvironmentContext.WORKSPACE_ID, MY_WORKSPACE_ID);
               env.setVariable(EnvironmentContext.VFS_ROOT_DIR, root);
               String requestPath = SERVICE_URI + "item/" + fileId;
               try
               {
                  while (!stop)
                  {
                     ContainerResponse response = launcher.service("GET", requestPath, BASE_URI, null, null, null, null);
                     assertEquals("Error: " + response.getEntity(), 200, response.getStatus());
                     counter.incrementAndGet();
                     //Item item = (Item)response.getEntity();
                     //validateLinks(item);
                  }
               }
               catch (Exception e)
               {
                  System.err.println(e.getMessage());
               }
               finally
               {
                  ConversationState.setCurrent(null);
                  latch.countDown();
                  System.err.printf("stop thread %d%n", getId());
               }
            }
         };
         t.setDaemon(true);
         t.start();
      }
      new Thread()
      {
         @Override
         public void run()
         {
            try
            {
               BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
               while (!"stop".equalsIgnoreCase(r.readLine()))
               {
               }
            }
            catch (IOException ioe)
            {
            }
            finally
            {
               stop=true;
            }
         }
      }.start();
//      new Thread(){
//         @Override
//         public void run()
//         {
//            final long end = System.currentTimeMillis() + 5000;
//            while(System.currentTimeMillis() < end){
//            }
//            stop=true;
//         }
//      }.start();
      latch.await();
      System.out.println("counter: " + counter.get());
   }

   public void _test2() throws Exception
   {
      URI baseUri = new URI("http://localhost:8080/rest");
      while(true)
      LinksHelper.createFileLinks(baseUri,
         "fs1",
         "L3Rlc3RDcmVhdGVGb2xkZXJIaWVyYXJjaHkyL0NyZWF0ZVRlc3RfRm9sZGVyL3Rlc3RDcmVhdGVGb2xkZXJIaWVyYXJjaHkyLzEvMi8zLzQ",
         "L3Rlc3RDcmVhdGVGb2xkZXJIaWVyYXJjaHkyL0NyZWF0ZVRlc3RfRm9sZGVyL3Rlc3RDcmVhdGVGb2xkZXJIaWVyYXJjaHkyLzEvMi8zLzQ",
         "/testCreateFolderHierarchy2/CreateTest_Folder/testCreateFolderHierarchy2/1/2/3/4",
         "text/directory",
         false,
         "L3Rlc3RDcmVhdGVGb2xkZXJIaWVyYXJjaHkyL0NyZWF0ZVRlc3RfRm9sZGVyL3Rlc3RDcmVhdGVGb2xkZXJIaWVyYXJjaHkyLzEvMi8zLzQ");

//      assertEquals("http://localhost:8080/rest/xxx/b/c", UriBuilder.fromPath("http://localhost:8080/rest/{a/b/c").build("xxx").toString());

   }

   public void _testLog(){
      final long start = System.currentTimeMillis();
      final long end = start + 1000;
      long num = 0;
      while(System.currentTimeMillis() < end){
         log.info("param: " + "a" + "," + 0 + "."); // 7887
         //log.info("param: {},{}.", "a", 0); // 7812
         num++;
      }
      System.out.println("num: " + num);
   }

}
