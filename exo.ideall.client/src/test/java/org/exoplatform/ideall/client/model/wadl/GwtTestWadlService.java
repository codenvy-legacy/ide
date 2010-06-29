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
package org.exoplatform.ideall.client.model.wadl;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.wadl.Method;
import org.exoplatform.gwtframework.commons.wadl.Resource;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.model.groovy.GroovyService;
import org.exoplatform.ideall.client.model.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.client.model.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ideall.client.model.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ideall.client.model.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.vfs.webdav.NodeTypeUtil;
import org.exoplatform.ideall.vfs.webdav.WebDavVirtualFileSystem;

import java.util.HashMap;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

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
   
   private static String TEST_URL_CREATE;
   
   private static String TEST_URL_DEPLOY;
   
   private static String TEST_URL_GET_WADL;
   
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
      groovyService = new GroovyServiceImpl(eventbus, new EmptyLoader());
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
      TEST_URL_CREATE = null;
      TEST_URL_DEPLOY = null;
      TEST_URL_GET_WADL = null;
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
      eventbus.addHandler(WadlServiceOutputReceivedEvent.TYPE, new WadlServiceOutputReceiveHandler()
      {
         public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
         {
            if (event.getException() == null)
            {
               WadlApplication application = event.getApplication();
               
               assertEquals(TEST_URL_GET_WADL, application.getResources().getBase());
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
            else
            {
               fail(event.getException().getMessage());
               finishTest();
            }
         }
      });
      
      getWadl();
   }
   
   public void testGetWadlFail()
   {
      eventbus.addHandler(WadlServiceOutputReceivedEvent.TYPE, new WadlServiceOutputReceiveHandler()
      {
         public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
         {
            if (event.getException() == null)
            {
               fail("Check is URL for this test is incorrect");
               finishTest();
            }
            else
            {
               finishTest();
            }
         }
      });
      
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            if (event.getError() != null && event.getError().getMessage() != null)
            {
               //if error occurs on get Wadl Output - all OK
               if (WADL_ERROR_MESSAGE.equals(event.getErrorMessage()))
               {
                  finishTest();
               }
               //if error occurs on something else - test fails
               else
               {
                  fail(event.getError().getMessage());
                  finishTest();
               }
            }

         }
      });
      
      getWadlOnIncorrectUrl();
   }
   
   private void getWadlOnIncorrectUrl()
   {
      eventbus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               assertEquals(file.getHref(), event.getPath());
               wadlService.getWadl(TEST_URL_GET_WADL + "/not-mine");
            }
            else
            {
               fail(event.getException().getMessage());
               finishTest();
            }
         }
      });
      
      eventbus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            assertNotNull(event.getFile());
            assertEquals(event.getFile().getContent(), groovyFileContent);
            groovyService.deploy(event.getFile().getHref(), TEST_URL_DEPLOY);
         }
      });

      vfsWebDav.saveContent(file);
      delayTestFinish(DELAY_TEST);
   }
   
   private void getWadl()
   {
      eventbus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               assertEquals(file.getHref(), event.getPath());
               wadlService.getWadl(TEST_URL_GET_WADL + "/mine");
            }
            else
            {
               fail(event.getException().getMessage());
               finishTest();
            }
         }
      });
      
      deployGroovyScript();
   }
   
   private void deployGroovyScript()
   {
      eventbus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            assertNotNull(event.getFile());
            assertEquals(event.getFile().getContent(), groovyFileContent);
            groovyService.deploy(event.getFile().getHref(), TEST_URL_DEPLOY);
         }
      });

      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            if (event.getError() != null && event.getError().getMessage() != null)
            {
               fail(event.getError().getMessage());
               finishTest();
            }

         }
      });

      vfsWebDav.saveContent(file);
      delayTestFinish(DELAY_TEST);
   }
   
   private void initUrls()
   {
      TEST_URL_CREATE = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/dev-monit/";
      TEST_URL_DEPLOY = "http://" + Window.Location.getHost() + "/ideall/rest/private/services/groovy/load?state=true";
      TEST_URL_GET_WADL = "http://" + Window.Location.getHost() + "/ideall/rest/private";
   }
   
   private void initFile()
   {
      file = new File(TEST_URL_CREATE + "newFile.groovy");
      file.setContentType(MimeType.SCRIPT_GROOVY);
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(MimeType.SCRIPT_GROOVY));
      file.setNewFile(true);
      file.setContent(groovyFileContent);
      file.setContentChanged(true);
   }
   
}