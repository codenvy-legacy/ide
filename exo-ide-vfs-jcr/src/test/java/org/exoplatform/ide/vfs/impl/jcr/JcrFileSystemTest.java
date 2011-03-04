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
package org.exoplatform.ide.vfs.impl.jcr;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class JcrFileSystemTest extends TestCase
{
   protected final String BASE_URI = "http://localhost/service";
   protected final String SERVICE_URI = BASE_URI + "/vfs/jcr/db1/ws/";
   protected final String DEFAULT_CONTENT = "__TEST__";

   protected Log log = ExoLogger.getExoLogger(getClass());
   protected String REPOSITORY_NAME = "db1";
   protected String WORKSPACE_NAME = "ws";
   protected String TEST_ROOT_NAME = "TESTROOT";

   protected Session session;
   protected Node testRoot;
   protected ResourceLauncher launcher;
   private ThreadLocalSessionProviderService sessionFactory;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      String conf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      StandaloneContainer.setConfigurationURL(conf);
      StandaloneContainer container = StandaloneContainer.getInstance();

      String loginConf = getClass().getResource("/login.conf").toString();
      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", loginConf);

      // JCR
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      ManageableRepository repository = repositoryService.getRepository(REPOSITORY_NAME);
      session = (Session)repository.login(new CredentialsImpl("root", "exo".toCharArray()), WORKSPACE_NAME);
      testRoot = session.getRootNode().addNode(TEST_ROOT_NAME, "nt:unstructured");
      session.save();

      // REST
      RequestHandler requestHandler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, ProviderBinder.getInstance()));
      launcher = new ResourceLauncher(requestHandler);

      // RUNTIME VARIABLES
      ConversationState user = new ConversationState(new Identity("john"));
      ConversationState.setCurrent(user);
      sessionFactory =
         (ThreadLocalSessionProviderService)container
            .getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      sessionFactory.setSessionProvider(null, new SessionProvider(user));
   }

   /**
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception
   {
      try
      {
         if (session != null)
         {
            if (testRoot != null)
            {
               testRoot.remove();
               session.save();
            }
            session.logout();
         }
         if (sessionFactory != null)
         {
            sessionFactory.getSessionProvider(null).close();
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      super.tearDown();
   }

   // --------------------------------------------

   protected void checkPage(String url, String httpMethod, Method m, List<Object> expected) throws Exception
   {
      checkPage(url, httpMethod, null, null, m, expected);
   }

   protected void checkPage(String url, String httpMethod, Map<String, List<String>> headers, byte[] body, Method m,
      List<Object> expected) throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = launcher.service(httpMethod, url, BASE_URI, headers, body, writer, null);
      assertEquals(200, response.getStatus());
      @SuppressWarnings("unchecked")
      List<Item> items = ((ItemList<Item>)response.getEntity()).getItems();
      List<Object> all = new ArrayList<Object>(expected.size());
      for (Item i : items)
      {
         validateLinks(i);
         all.add(m.invoke(i));
      }
      assertEquals(all, expected);
   }

   protected void validateLinks(Item item)
   {
      Map<String, Link> links = item.getLinks();
      
      if (links.size() == 0)
         fail("Links not found. ");

      Link link = links.get(Item.REL_SELF);
      assertNotNull("'self' link not found. ", link);
      assertEquals(MediaType.APPLICATION_JSON, link.getType());
      assertEquals(Item.REL_SELF, link.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("item").path(item.getId()).build().toString(), link.getHref());

      link = links.get(Item.REL_ACL);
      assertNotNull("'acl' link not found. ", link);
      assertEquals(MediaType.APPLICATION_JSON, link.getType());
      assertEquals(Item.REL_ACL, link.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("acl").path(item.getId()).build().toString(), link.getHref());

      ItemType type = item.getItemType();
      if (type == ItemType.FILE)
      {
         File file = (File)item;

         link = links.get(File.REL_CONTENT);
         assertNotNull("'content' link not found. ", link);
         assertEquals(file.getMimeType(), link.getType());
         assertEquals(File.REL_CONTENT, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("content").path(file.getId()).build().toString(),
            link.getHref());

         link = links.get(Item.REL_CURRENT);
         assertNotNull("'current' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Item.REL_CURRENT, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("item").path(file.getId()).build().toString(),
            link.getHref());

         link = links.get(File.REL_VERSION_HISTORY);
         assertNotNull("'version-history' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(File.REL_VERSION_HISTORY, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("version-history").path(file.getId()).build().toString(),
            link.getHref());
      }
      else
      {
         link = links.get(Folder.REL_CHILDREN);
         assertNotNull("'children' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Folder.REL_CHILDREN, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("children").path(item.getId()).build().toString(),
            link.getHref());
      }
   }
}
