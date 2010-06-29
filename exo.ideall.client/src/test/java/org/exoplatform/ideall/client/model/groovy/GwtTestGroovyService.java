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
package org.exoplatform.ideall.client.model.groovy;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.AbstractGwtTest;
import org.exoplatform.ideall.client.model.SimpleParameterEntry;
import org.exoplatform.ideall.client.model.groovy.event.GroovyDeployResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyDeployResultReceivedHandler;
import org.exoplatform.ideall.client.model.groovy.event.GroovyUndeployResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyUndeployResultReceivedHandler;
import org.exoplatform.ideall.client.model.groovy.event.GroovyValidateResultReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.GroovyValidateResultReceivedHandler;
import org.exoplatform.ideall.client.model.groovy.event.RestServiceOutputReceivedEvent;
import org.exoplatform.ideall.client.model.groovy.event.RestServiceOutputReceivedHandler;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.vfs.webdav.NodeTypeUtil;
import org.exoplatform.ideall.vfs.webdav.WebDavVirtualFileSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class GwtTestGroovyService extends AbstractGwtTest
{
   
   private VirtualFileSystem vfsWebDav;
   
   private GroovyService groovyService;
   
   private HashMap<String, String> images = new HashMap<String, String>();
   
   private HandlerManager eventbus;

   private final int DELAY_TEST = 5000;
   
   private static String TEST_URL_CREATE;
   
   private static String TEST_URL_VALIDATE;
   
   private static String TEST_URL_DEPLOY;
   
   private static String TEST_URL_UNDEPLOY;
   
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
      TEST_URL_CREATE = null;
      TEST_URL_VALIDATE = null;
      TEST_URL_DEPLOY = null;
      TEST_URL_UNDEPLOY = null;
      file = null;
   }

   /**
    * Save file content
    * 
    * @param file
    * @param path
    */
   public void testValidate()
   {
      
      eventbus.addHandler(GroovyValidateResultReceivedEvent.TYPE, new GroovyValidateResultReceivedHandler()
      {
      
         public void onGroovyValidateResultReceived(GroovyValidateResultReceivedEvent event)
         {
            final String fileName = TEST_URL_CREATE + "/newFile.groovy";
            assertEquals(fileName, event.getFileName());
            
            if (event.getException() != null)
            {
               ServerException exception = (ServerException)event.getException();
               fail("HTTP Status : " + exception.getHTTPStatus() + "\n" + event.getException().getMessage());
            }
            finishTest();
         }
      });
      eventbus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            assertNotNull(event.getFile());
            assertEquals(event.getFile().getContent(), groovyFileContent);
            groovyService.validate(event.getFile().getHref(), groovyFileContent, TEST_URL_VALIDATE);
         }
      });
      
      eventbus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getError().getMessage());
            finishTest();
         }
      });
      
      vfsWebDav.saveContent(file);
      delayTestFinish(DELAY_TEST);
   }
   
   public void testDeploy()
   {
      eventbus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               assertEquals(file.getHref(), event.getPath());
               finishTest();
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
   
   public void testDeployUnexistedGroovy()
   {
      eventbus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               fail();
               finishTest();
            }
            else
            {
               finishTest();
            }
         }
      });
      
      groovyService.deploy(TEST_URL_CREATE + "/unexisted.file", TEST_URL_DEPLOY);
      delayTestFinish(DELAY_TEST);
   }
   
   public void testUndeploy()
   {
      eventbus.addHandler(GroovyUndeployResultReceivedEvent.TYPE, new GroovyUndeployResultReceivedHandler()
      {
         public void onGroovyUndeployResultReceived(GroovyUndeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               assertEquals(file.getHref(), event.getPath());
               finishTest();
            }
            else
            {
               fail(event.getException().getMessage());
               finishTest();
            }
         }
      });
      
      eventbus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               assertEquals(file.getHref(), event.getPath());
               groovyService.undeploy(file.getHref(), TEST_URL_UNDEPLOY);
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
   
   public void testUndeployUnexistedGroovy()
   {
      eventbus.addHandler(GroovyUndeployResultReceivedEvent.TYPE, new GroovyUndeployResultReceivedHandler()
      {
         public void onGroovyUndeployResultReceived(GroovyUndeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               fail();
               finishTest();
            }
            else
            {
               finishTest();
            }
         }
      });
      
      groovyService.undeploy(TEST_URL_CREATE + "/unexisted.file", TEST_URL_UNDEPLOY);
      delayTestFinish(DELAY_TEST);
   }
   
   public void testGetOutput()
   {
      eventbus.addHandler(RestServiceOutputReceivedEvent.TYPE, new RestServiceOutputReceivedHandler()
      {
      
         public void onRestServiceOutputReceived(RestServiceOutputReceivedEvent event)
         {
            if (event.getException() == null)
            {
               Response response = event.getOutput().getResponse();
               
               assertEquals(200, response.getStatusCode());
               assertEquals("OK", response.getStatusText());
               assertEquals(4, response.getHeaders().length);
               assertEquals("Hello, Ivan!", response.getText());
               
               finishTest();
            }
            else
            {
               fail(event.getException().getMessage());
               finishTest();
            }
         }
      });
      
      eventbus.addHandler(GroovyDeployResultReceivedEvent.TYPE, new GroovyDeployResultReceivedHandler()
      {
         public void onGroovyDeployResultReceived(GroovyDeployResultReceivedEvent event)
         {
            if (event.getException() == null)
            {
               assertEquals(file.getHref(), event.getPath());
               String url = "http://" + Window.Location.getHost() + "/ideall/rest/private/mine/helloworld/Ivan";
               String method = "GET";
               List<SimpleParameterEntry> headers = new ArrayList<SimpleParameterEntry>();
               List<SimpleParameterEntry> params = new ArrayList<SimpleParameterEntry>();
               String body = "";
               groovyService.getOutput(url, method, headers, params, body);
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
      TEST_URL_CREATE = "http://" + Window.Location.getHost() + "/ideall/rest/private/jcr/repository/dev-monit";
      TEST_URL_VALIDATE = "http://" + Window.Location.getHost() + "/ideall/rest/private/services/groovy/validate";
      TEST_URL_DEPLOY = "http://" + Window.Location.getHost() + "/ideall/rest/private/services/groovy/load?state=true";
      TEST_URL_UNDEPLOY = "http://" + Window.Location.getHost() + "/ideall/rest/private/services/groovy/load?state=false";
   }
   
   private void initFile()
   {
      file = new File(TEST_URL_CREATE + "/newFile.groovy");
      file.setContentType(MimeType.SCRIPT_GROOVY);
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(MimeType.SCRIPT_GROOVY));
      file.setNewFile(true);
      file.setContent(groovyFileContent);
      file.setContentChanged(true);
   }
   
}