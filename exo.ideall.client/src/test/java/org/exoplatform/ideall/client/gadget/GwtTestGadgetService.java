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
package org.exoplatform.ideall.client.gadget;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.model.gadget.GadgetService;
import org.exoplatform.ideall.client.model.gadget.GadgetServiceImpl;
import org.exoplatform.ideall.client.model.gadget.TokenRequest;
import org.exoplatform.ideall.client.model.gadget.TokenResponse;
import org.exoplatform.ideall.client.model.gadget.event.GadgetDeployResultEvent;
import org.exoplatform.ideall.client.model.gadget.event.GadgetDeployResultHandler;
import org.exoplatform.ideall.client.model.gadget.event.GadgetMetadaRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.event.GadgetMetadaRecievedHandler;
import org.exoplatform.ideall.client.model.gadget.event.GadgetUndeployResultEvent;
import org.exoplatform.ideall.client.model.gadget.event.GadgetUndeployResultHandler;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedHandler;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.vfs.webdav.NodeTypeUtil;
import org.exoplatform.ideall.vfs.webdav.WebDavVirtualFileSystem;

import java.util.HashMap;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.URL;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GwtTestGadgetService extends GWTTestCase
{
   private HandlerManager eventBus;

   private Loader loader = new EmptyLoader();

   private final int DELAY_TEST = 5000;

   private String fileURL;

   private String context;

   private String publicContext = "/rest";

   private String gadgetServer;

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp()
    */
   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      eventBus = new HandlerManager(null);
      fileURL = "http://" + Window.Location.getHost() + "/ideall/jcr/repository/dev-monit/";
      context = "http://" + Window.Location.getHost() + "/ideall/rest/private";
      gadgetServer = "http://" + Window.Location.getHost() + "/ideall/gadgets/";
      new GadgetServiceImpl(eventBus, loader, context, gadgetServer, publicContext);
   }

   /**
    * Test getting security token.
    */
   public void testGetSecurityToken()
   {
      final String owner = "root";
      final String viewer = "root";
      final Long moduleId = 0L;
      final String container = "default";
      final String domain = null;

      final File gadget = createGadget();

      eventBus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            assertNotNull(event.getFile());
            assertEquals(event.getFile().getContent(), gadget.getContent());
            String href = event.getFile().getHref();
            TokenRequest tokenRequest = new TokenRequest(URL.encode(href), owner, viewer, moduleId, container, domain);
            GadgetService.getInstance().getSecurityToken(tokenRequest);
         }
      });

      eventBus.addHandler(SecurityTokenRecievedEvent.TYPE, new SecurityTokenRecievedHandler()
      {
         public void onSecurityTokenRecieved(SecurityTokenRecievedEvent securityTokenRecievedEvent)
         {
            TokenResponse tokenResponse = securityTokenRecievedEvent.getTokenResponse();
            assertEquals(moduleId, tokenResponse.getModuleId());
            assertEquals(gadget.getHref(), tokenResponse.getGadgetURL());
            assertNotNull(tokenResponse.getSecurityToken());
            finishTest();
         }

      });

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
         }
      });
      VirtualFileSystem.getInstance().saveContent(gadget);

      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test getting gadget's metadata.
    */
   public void testGetMetaData()
   {
      final String owner = "root";
      final String viewer = "root";
      final Long moduleId = 0L;
      final String container = "default";
      final String domain = null;

      final File gadget = createGadget();

      eventBus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            assertNotNull(event.getFile());
            assertEquals(event.getFile().getContent(), gadget.getContent());
            String href = event.getFile().getHref();
            TokenRequest tokenRequest = new TokenRequest(URL.encode(href), owner, viewer, moduleId, container, domain);
            GadgetService.getInstance().getSecurityToken(tokenRequest);
         }
      });

      eventBus.addHandler(SecurityTokenRecievedEvent.TYPE, new SecurityTokenRecievedHandler()
      {
         public void onSecurityTokenRecieved(SecurityTokenRecievedEvent securityTokenRecievedEvent)
         {
            GadgetService.getInstance().getGadgetMetadata(securityTokenRecievedEvent.getTokenResponse());
         }

      });

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
         }
      });

      eventBus.addHandler(GadgetMetadaRecievedEvent.TYPE, new GadgetMetadaRecievedHandler()
      {
         public void onMetadataRecieved(GadgetMetadaRecievedEvent event)
         {
            finishTest();
         }
      });
      VirtualFileSystem.getInstance().saveContent(gadget);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test deploying gadget.
    */
   public void testDeployGadget()
   {
      final File gadget = createGadget();

      eventBus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            String href = event.getFile().getHref();
            GadgetService.getInstance().deployGadget(href);
         }
      });

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
         }
      });

      eventBus.addHandler(GadgetDeployResultEvent.TYPE, new GadgetDeployResultHandler()
      {
         public void onGadgetDeployResultReceived(GadgetDeployResultEvent event)
         {
            finishTest();
         }
      });

      VirtualFileSystem.getInstance().saveContent(gadget);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * Test undeploying deployed gadget.
    */
   public void testUndeployGadget()
   {
      final File gadget = createGadget();

      eventBus.addHandler(FileContentSavedEvent.TYPE, new FileContentSavedHandler()
      {
         public void onFileContentSaved(FileContentSavedEvent event)
         {
            String href = event.getFile().getHref();
            GadgetService.getInstance().deployGadget(href);
         }
      });

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
         }
      });

      eventBus.addHandler(GadgetDeployResultEvent.TYPE, new GadgetDeployResultHandler()
      {
         public void onGadgetDeployResultReceived(GadgetDeployResultEvent event)
         {
            GadgetService.getInstance().undeployGadget(gadget.getContent());
         }
      });

      eventBus.addHandler(GadgetUndeployResultEvent.TYPE, new GadgetUndeployResultHandler()
      {
         public void onGadgetUndeployResultReceived(GadgetUndeployResultEvent event)
         {
            finishTest();
         }
      });

      VirtualFileSystem.getInstance().saveContent(gadget);
      delayTestFinish(DELAY_TEST);
   }

   public void testDeployNotExitedGadget()
   {
      String href = fileURL + "nogadget";

      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
         }
      });

      eventBus.addHandler(GadgetDeployResultEvent.TYPE, new GadgetDeployResultHandler()
      {
         public void onGadgetDeployResultReceived(GadgetDeployResultEvent event)
         {
            assertNotNull(event.getException());
            finishTest();
         }
      });

      GadgetService.getInstance().deployGadget(href);
      delayTestFinish(DELAY_TEST);
   }

   public void testUndeployNotExitedGadget()
   {
      String href = fileURL + "nogadget";
      eventBus.addHandler(ExceptionThrownEvent.TYPE, new ExceptionThrownHandler()
      {
         public void onError(ExceptionThrownEvent event)
         {
            fail(event.getErrorMessage());
         }
      });

      eventBus.addHandler(GadgetUndeployResultEvent.TYPE, new GadgetUndeployResultHandler()
      {
         public void onGadgetUndeployResultReceived(GadgetUndeployResultEvent event)
         {
            assertNotNull(event.getException());
            finishTest();
         }
      });

      GadgetService.getInstance().undeployGadget(href);
      delayTestFinish(DELAY_TEST);
   }

   /**
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ideall.IDEGwtTest";
   }

   private File createGadget()
   {
      new WebDavVirtualFileSystem(eventBus, new EmptyLoader(), new HashMap<String, String>(), "/rest");
      final String fileContent =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + "<Module>" + "<ModulePrefs title=\"Hello World!\" />"
            + "<Content type=\"html\">" + "<![CDATA[ Hello, world!Hello, world!]]></Content></Module>";
      String fileName = "gadget";

      final File file = new File(fileURL + fileName);
      file.setContentType(MimeType.GOOGLE_GADGET);
      file.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(MimeType.GOOGLE_GADGET));
      file.setNewFile(true);
      file.setContent(fileContent);
      file.setContentChanged(true);
      return file;
   }
}
