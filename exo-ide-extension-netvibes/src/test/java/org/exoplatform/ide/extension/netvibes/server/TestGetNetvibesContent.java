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
package org.exoplatform.ide.extension.netvibes.server;

import junit.framework.TestCase;

import org.everrest.core.RequestHandler;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.ResourceLauncher;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Test for NetvibesWidgetService to get content of netvibes file.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 21, 2010 $
 * 
 */
public class TestGetNetvibesContent extends TestCase
{

   private static Log log = ExoLogger.getLogger(TestGetNetvibesContent.class);

   private static String WORKSPACE = "dev-monit";

   private StandaloneContainer container;

   private ResourceLauncher launcher;

   private RepositoryService repositoryService;

   private SessionImpl session;

   private RepositoryImpl repository;

   private CredentialsImpl credentials;

   private final String netvibesFileData =
      "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
         + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
         + "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n" + "xmlns:widget=\"http://www.netvibes.com/ns/\">\n"
         + "  <body>\n" + "<p>Hello world!</p>\n" + "</body>\n" + "</html>";

   @Before
   public void setUp() throws Exception
   {
      String containerConf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();

      StandaloneContainer.addConfigurationURL(containerConf);

      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
            .getResource("login.conf").toString());

      RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      launcher = new ResourceLauncher(handler);

      credentials = new CredentialsImpl("root", "exo".toCharArray());

      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      repository = (RepositoryImpl)repositoryService.getDefaultRepository();
      session = (SessionImpl)repository.login(credentials, WORKSPACE);

      SessionProviderService sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      assertNotNull(sessionProviderService);

      sessionProviderService
         .setSessionProvider(null, new SessionProvider(new ConversationState(new Identity("admin"))));

   }

   @Test
   public void testGetNetvibesPreview() throws Exception
   {
      // create file node (netvibes widget)
      final String filePath = "netvibes.html";
      Node base = session.getRootNode();

      base = base.addNode(filePath, "nt:file");
      base = base.addNode("jcr:content", "nt:resource");
      base.setProperty("jcr:data", netvibesFileData);
      base.setProperty("jcr:lastModified", Calendar.getInstance());
      base.setProperty("jcr:mimeType", "application/x-uwa-widget");

      session.save();

      // make request to get netvibes data

      EnvironmentContext ctx = new EnvironmentContext();

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();

      ContainerResponse response =
         launcher.service("GET", "/ide/netvibes/db1/" + WORKSPACE + "/" + filePath, "http://localhost", headers, null,
            null, ctx);

      // check response
      assertEquals(HTTPStatus.OK, response.getStatus());

      assertTrue(response.getEntity() instanceof InputStream);

      InputStream inputStream = (InputStream)response.getEntity();

      int bytesRead;
      byte[] buf = new byte[1024];

      ByteArrayOutputStream outS = new ByteArrayOutputStream();

      while ((bytesRead = inputStream.read(buf, 0, 1024)) > -1)
      {
         outS.write(buf, 0, bytesRead);
      }

      String data = outS.toString();
      outS.close();

      assertEquals(netvibesFileData, data);

      session.refresh(false);
   }

   @After
   protected void tearDown() throws Exception
   {

      if (session != null)
      {
         try
         {
            session.refresh(false);
            Node rootNode = session.getRootNode();
            if (rootNode.hasNodes())
            {
               // clean test root
               for (NodeIterator children = rootNode.getNodes(); children.hasNext();)
               {
                  Node node = children.nextNode();
                  if (!node.getPath().startsWith("/jcr:system"))
                  {
                     node.remove();
                  }
               }
               session.save();
            }
         }
         catch (Exception e)
         {
            log.error("tearDown() ERROR " + getClass().getName() + "." + getName() + " " + e, e);
         }
         finally
         {
            session.logout();
         }
      }
      super.tearDown();
   }

}
