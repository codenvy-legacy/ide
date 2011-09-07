/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.model.wadl;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.ide.client.AbstractGwtTest;
import org.exoplatform.ide.client.Const;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlService;
import org.exoplatform.ide.extension.groovy.client.service.wadl.WadlServiceImpl;

import java.util.HashMap;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GwtTestWadlService extends AbstractGwtTest
{
   
   private VirtualFileSystem vfsWebDav;
   
   private GroovyService groovyService;
   
   private WadlService wadlService;
   
   private HashMap<String, String> images = new HashMap<String, String>();
   
   private HandlerManager eventbus;

   private final int DELAY_TEST = 5000;
   
   private static String testUrlCreate;
   
   private static String testUrlDeploy;
   
   private static String testUrlGetWadl;
   
   private final static String WADL_ERROR_MESSAGE = "Service is not deployed.";
   
   private File file;

   private final static String groovyFileContent = "// simple groovy script\n" 
      + "import javax.ws.rs.Path;\n" 
      + "import javax.ws.rs.GET;\n" 
      + "import javax.ws.rs.PathParam;\n"
      + "@Path(\"/mine\")\n" 
      + "public class HelloWorld {\n" 
         + "@GET\n" 
         + "@Path(\"helloworld/{name}\")\n"
         + "public String hello(@PathParam(\"name\") String name) {\n" 
            + "return \"Hello, \" + name +\"!\";\n" 
         + "}\n" 
      + "}\n";
   
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventbus = new HandlerManager(null);
      vfsWebDav = new WebDavVirtualFileSystem(eventbus, new EmptyLoader(), images, "/ideall");
      groovyService = new GroovyServiceImpl(eventbus, Const.REST_SERVICE_CONTEXT, new EmptyLoader());
      wadlService = new WadlServiceImpl(eventbus, new EmptyLoader());
      initUrls();
      initFile();
   }
   
   @Override
   protected void gwtTearDown() throws Exception
   {
      super.gwtTearDown();
      eventbus = null;
      vfsWebDav = null;
      groovyService = null;
      wadlService = null;
      testUrlCreate = null;
      testUrlDeploy = null;
      testUrlGetWadl = null;
      file = null;
   }

   /**
    * Save file content
    * 
    * @param file
    * @param path
    */
   public void testGetWadl()
   {
      vfsWebDav.saveContent(file, null, new FileContentSaveCallback()
      {
         
         @Override
         protected void onSuccess(FileData result)
         {
            assertNotNull(result.getFile());
            assertEquals(result.getFile().getContent(), groovyFileContent);
            groovyService.deploy(result.getFile().getHref(), new AsyncRequestCallback<String>()
            {
               
               @Override
               protected void onSuccess(String result)
               {
                  assertEquals(file.getHref(), result);
                  wadlService.getWadl(testUrlGetWadl + "/mine", new AsyncRequestCallback<WadlApplication>()
                  {
                     
                     @Override
                     protected void onSuccess(WadlApplication result)
                     {
                        checkWadl(result);
                        finishTest();
                     }
                     
                     @Override
                     protected void onFailure(Throwable exception)
                     {
                        fail();
                        finishTest();
                     }
                  });
               }
               
               @Override
               protected void onFailure(Throwable exception)
               {
                  fail(exception.getMessage());
                  finishTest();
               }
            });
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            finishTest();
         }
      });
      delayTestFinish(DELAY_TEST);
   }
   
   private void checkWadl(WadlApplication application)
   {
      assertEquals(testUrlGetWadl, application.getResources().getBase());
      assertEquals(1, application.getResources().getResource().size());
      
      Resource resource = application.getResources().getResource().get(0);
      
      assertEquals("/mine", resource.getPath());
      assertEquals(2, resource.getMethodOrResource().size());
      assertEquals(0, resource.getDoc().size());
      assertEquals(0, resource.getOtherAttributes().size());
      assertEquals(0, resource.getParam().size());
      assertEquals(0, resource.getType().size());
      
      for (Object obj : resource.getMethodOrResource())
      {
         if (obj instanceof Method)
         {
            Method method = (Method)obj;
            assertEquals("OPTIONS", method.getName());
         }
         else if (obj instanceof Resource)
         {
            Resource res = (Resource)obj;
            
            assertEquals("/mine/helloworld/{name}", res.getPath());
            assertEquals(1, res.getMethodOrResource().size());
            assertTrue(res.getMethodOrResource().get(0) instanceof Method);
            
            Method method = (Method)res.getMethodOrResource().get(0);
            
            assertEquals("GET", method.getName());
         }
         else
         {
            fail("Must receive Method or Resource, but received something else");
            finishTest();
         }
      }
      
      finishTest();
   }
   
   public void testGetWadlFail()
   {
      vfsWebDav.saveContent(file, null, new FileContentSaveCallback()
      {
         @Override
         protected void onFailure(Throwable exception)
         {
            //if error occurs on get Wadl Output - all OK
            if (WADL_ERROR_MESSAGE.equals(exception.getMessage()))
            {
               finishTest();
            }
            //if error occurs on something else - test fails
            else
            {
               fail(exception.getMessage());
               finishTest();
            }
         }
         
         @Override
         protected void onSuccess(FileData result)
         {
            assertNotNull(result.getFile());
            assertEquals(result.getFile().getContent(), groovyFileContent);
            groovyService.deploy(result.getFile().getHref(), new AsyncRequestCallback<String>()
            {
               
               @Override
               protected void onSuccess(String result)
               {
                  assertEquals(file.getHref(), result);
                  wadlService.getWadl(testUrlGetWadl + "/not-mine", new AsyncRequestCallback<WadlApplication>()
                  {
                     
                     @Override
                     protected void onSuccess(WadlApplication result)
                     {
                        fail("Check is URL for this test is incorrect");
                        finishTest();
                     }
                     
                     @Override
                     protected void onFailure(Throwable exception)
                     {
                        finishTest();
                     }
                  });
               }
               
               @Override
               protected void onFailure(Throwable exception)
               {
                  fail(exception.getMessage());
                  finishTest();
               }
            });
         }
      });
      delayTestFinish(DELAY_TEST);
   }
   
   private void initUrls()
   {
      testUrlCreate = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/dev-monit/";
      testUrlDeploy = "http://" + Window.Location.getHost() + "/ideall/rest/private/services/groovy/load?state=true";
      testUrlGetWadl = "http://" + Window.Location.getHost() + "/ideall/rest/private";
   }
   
   private void initFile()
   {
      file = new File(testUrlCreate + "newFile.groovy");
      file.setContentType(MimeType.GROOVY_SERVICE);
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(MimeType.GROOVY_SERVICE));
      file.setNewFile(true);
      file.setContent(groovyFileContent);
      file.setContentChanged(true);
   }
   
}
