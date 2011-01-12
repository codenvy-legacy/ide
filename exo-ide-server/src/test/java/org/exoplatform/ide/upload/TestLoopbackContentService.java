/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.upload;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MockHttpServletRequest;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Test for UploadService class.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestLoopbackContentService extends BaseTest
{

   private static Log log = ExoLogger.getLogger(TestLoopbackContentService.class);

   private static String WORKSPACE = "dev-monit";

   private SessionImpl session;

   private RepositoryImpl repository;

   private CredentialsImpl credentials;

   private RepositoryService repositoryService;
   
   private MultivaluedMap<String, String> headers;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();

      credentials = new CredentialsImpl("root", "exo".toCharArray());

      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      repository = (RepositoryImpl)repositoryService.getDefaultRepository();
      session = (SessionImpl)repository.login(credentials, WORKSPACE);
      
      SessionProviderService sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      assertNotNull(sessionProviderService);
      
      sessionProviderService
         .setSessionProvider(null, new SessionProvider(new ConversationState(new Identity("admin"))));
      
      headers = new MultivaluedMapImpl();
      headers.putSingle("content-type", "multipart/form-data; boundary=-----abcdef");
      
   }
   
   @Test
   public void testUploadFile() throws Exception
   {
      session.save();

      EnvironmentContext ctx = new EnvironmentContext();

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintWriter w = new PrintWriter(out);
      
      String source = "-------abcdef\r\n" 
         +"Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n"
         +"Content-Type: text/plain\r\n\r\ntest file content\r\n" 
         +"-------abcdef\r\n"
         +"Content-Disposition: form-data; name=\"location\"\r\n\r\n"
         +"http://localhost/jcr/db1/dev-monit/test.txt\r\n"
         +"-------abcdef\r\n"
         +"Content-Disposition: form-data; name=\"mimeType\"\r\n\r\ntext/plain\r\n" 
         +"-------abcdef\r\n"
         +"Content-Disposition: form-data; name=\"nodeType\"\r\n\r\n\r\n"
         +"-------abcdef\r\n"
         +"Content-Disposition: form-data; name=\"jcrContentNodeType\"\r\n\r\nnt:resource\r\n" 
         +"-------abcdef--\r\n";
      
      w.write(source);
      w.flush();
      
      byte[] data = out.toByteArray();
      
      HttpServletRequest httpRequest =
         new MockHttpServletRequest(new ByteArrayInputStream(data), data.length, "POST", headers);
      ctx.put(HttpServletRequest.class, httpRequest);

      ContainerResponse response = launcher.service("POST", "/ide/loopbackcontent", "http://localhost", headers,
            data, null, ctx);
      
      assertEquals(HTTPStatus.OK, response.getStatus());
      assertTrue(response.getEntity() instanceof String);
      
      String text = (String)response.getEntity();
      
      assertEquals("<filecontent>test+file+content%0A</filecontent>", text);
      
      
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
