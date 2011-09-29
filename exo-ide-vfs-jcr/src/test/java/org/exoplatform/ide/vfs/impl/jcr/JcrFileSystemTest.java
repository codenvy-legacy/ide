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
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.core.ManageableRepository;
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
   protected final String SERVICE_URI = BASE_URI + "/ide/vfs/ws/";
   protected final String DEFAULT_CONTENT = "__TEST__";

   protected Log log = ExoLogger.getExoLogger(getClass());
   protected String REPOSITORY_NAME = "db1";
   protected String WORKSPACE_NAME = "ws";
   protected String TEST_ROOT_NAME = "TESTROOT";

   protected Session session;
   protected Node testRoot;
   protected ResourceLauncher launcher;

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
      repositoryService.setCurrentRepositoryName(REPOSITORY_NAME);

      // REST
      RequestHandler requestHandler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, ProviderBinder.getInstance()));
      launcher = new ResourceLauncher(requestHandler);

      // RUNTIME VARIABLES
      ConversationState user = new ConversationState(new Identity("john"));
      ConversationState.setCurrent(user);
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
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      super.tearDown();
   }

   // --------------------------------------------

   protected Item getItem(String id) throws Exception
   {
      String path = new StringBuilder().append(SERVICE_URI).append("item/").append(id).toString();
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, null, null);
      if (response.getStatus() == 200)
         return (Item)response.getEntity();
      if (response.getStatus() == 404)
         return null;
      fail("Unable get " + id + ".\nStatus: " + response.getStatus() + "\nMessage: " + response.getEntity());
      // 
      return null;
   }

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

   protected void validateLinks(Item item) throws Exception
   {
      Map<String, Link> links = item.getLinks();

      if (links.size() == 0)
         fail("Links not found. ");

      Link link = links.get(Link.REL_SELF);
      assertNotNull("'" + Link.REL_SELF + "' link not found. ", link);
      assertEquals(MediaType.APPLICATION_JSON, link.getType());
      assertEquals(Link.REL_SELF, link.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("item").path(item.getId()).build().toString(), link.getHref());

      link = links.get(Link.REL_PARENT);
      if (item.getParentId() == null)
      {
         assertNull("'" + Link.REL_PARENT + "' link not allowed for root folder. ", link);
      }
      else
      {
         assertNotNull("'" + Link.REL_PARENT + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_PARENT, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("item").path(item.getParentId()).build().toString(),
            link.getHref());
      }

      link = links.get(Link.REL_ACL);
      assertNotNull("'" + Link.REL_ACL + "' link not found. ", link);
      assertEquals(MediaType.APPLICATION_JSON, link.getType());
      assertEquals(Link.REL_ACL, link.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("acl").path(item.getId()).build().toString(), link.getHref());

      link = links.get(Link.REL_DELETE);
      assertNotNull("'" + Link.REL_DELETE + "' link not found. ", link);
      assertEquals(null, link.getType());
      assertEquals(Link.REL_DELETE, link.getRel());
      assertEquals(
         UriBuilder.fromPath(SERVICE_URI).path("delete").path(item.getId()).queryParam("lockToken", "[lockToken]")
            .build().toString(), link.getHref());

      link = links.get(Link.REL_COPY);
      assertNotNull("'" + Link.REL_COPY + "' link not found. ", link);
      assertEquals(null, link.getType());
      assertEquals(Link.REL_COPY, link.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("copy").path(item.getId())
         .queryParam("parentId", "[parentId]").build().toString(), link.getHref());

      link = links.get(Link.REL_MOVE);
      assertNotNull("'" + Link.REL_MOVE + "' link not found. ", link);
      assertEquals(null, link.getType());
      assertEquals(Link.REL_MOVE, link.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("move").path(item.getId())
         .queryParam("parentId", "[parentId]").queryParam("lockToken", "[lockToken]").build().toString(),
         link.getHref());

      ItemType type = item.getItemType();
      if (type == ItemType.FILE)
      {
         File file = (File)item;

         link = links.get(Link.REL_CONTENT);
         assertNotNull("'" + Link.REL_CONTENT + "' link not found. ", link);
         assertEquals(file.getMimeType(), link.getType());
         assertEquals(Link.REL_CONTENT, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("content").path(file.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_CURRENT_VERSION);
         assertNotNull("'" + Link.REL_CURRENT_VERSION + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_CURRENT_VERSION, link.getRel());
         String id = file.getId();
         Node fileNode = ((ExtendedSession)session).getNodeByIdentifier(id);
         String expectedCurrentVersionId;
         if (fileNode.isNodeType("nt:frozenNode"))
         {
            expectedCurrentVersionId =
               ((javax.jcr.version.Version)fileNode.getParent()).getContainingHistory().getVersionableUUID();
         }
         else
         {
            expectedCurrentVersionId = id;
         }
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("item").path(expectedCurrentVersionId).build().toString(),
            link.getHref());

         link = links.get(Link.REL_VERSION_HISTORY);
         assertNotNull("'" + Link.REL_VERSION_HISTORY + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_VERSION_HISTORY, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("version-history").path(file.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_LOCK);
         if (file.isLocked())
         {
            assertNull("'" + Link.REL_LOCK + "' link not allowed for locked files. ", link);
            link = links.get(Link.REL_UNLOCK);
            assertEquals(null, link.getType());
            assertEquals(Link.REL_UNLOCK, link.getRel());
            assertEquals(
               UriBuilder.fromPath(SERVICE_URI).path("unlock").path(file.getId())
                  .queryParam("lockToken", "[lockToken]").build().toString(), link.getHref());
         }
         else
         {
            assertNotNull("'" + Link.REL_LOCK + "' link not found. ", link);
            assertEquals(MediaType.APPLICATION_JSON, link.getType());
            assertEquals(Link.REL_LOCK, link.getRel());
            assertEquals(UriBuilder.fromPath(SERVICE_URI).path("lock").path(file.getId()).build().toString(),
               link.getHref());
            link = links.get(Link.REL_UNLOCK);
            assertNull("'" + Link.REL_UNLOCK + "' link not allowed for unlocked files. ", link);
         }
      }
      else
      {
         link = links.get(Link.REL_CHILDREN);
         assertNotNull("'" + Link.REL_CHILDREN + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_CHILDREN, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("children").path(item.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_CREATE_FOLDER);
         assertNotNull("'" + Link.REL_CREATE_FOLDER + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_CREATE_FOLDER, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("folder").path(item.getId()).queryParam("name", "[name]")
            .build().toString(), link.getHref());

         link = links.get(Link.REL_CREATE_FILE);
         assertNotNull("'" + Link.REL_CREATE_FILE + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_CREATE_FILE, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("file").path(item.getId()).queryParam("name", "[name]")
            .build().toString(), link.getHref());

         link = links.get(Link.REL_CREATE_PROJECT);
         if (item instanceof Project)
         {
            assertNull("'" + Link.REL_CREATE_PROJECT + "' link not allowed for project. ", link);
         }
         else
         {
            assertNotNull("'" + Link.REL_CREATE_PROJECT + "' link not found. ", link);
            assertEquals(MediaType.APPLICATION_JSON, link.getType());
            assertEquals(Link.REL_CREATE_PROJECT, link.getRel());
            assertEquals(
               UriBuilder.fromPath(SERVICE_URI).path("project").path(item.getId()).queryParam("name", "[name]")
                  .queryParam("type", "[type]").build().toString(), link.getHref());
         }
      }
   }

   protected void validateUrlTemplates(VirtualFileSystemInfo info) throws Exception
   {
      Map<String, Link> templates = info.getUrlTemplates();
      //log.info(">>>>>>>>>\n" + templates);

      Link template = templates.get(Link.REL_ITEM);
      assertNotNull("'" + Link.REL_ITEM + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_ITEM, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("item").path("[id]").build().toString(), template.getHref());

      template = templates.get(Link.REL_ITEM_BY_PATH);
      assertNotNull("'" + Link.REL_ITEM_BY_PATH + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_ITEM_BY_PATH, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("itembypath").queryParam("path", "[path]").build().toString(),
         template.getHref());

      template = templates.get(Link.REL_COPY);
      assertNotNull("'" + Link.REL_COPY + "' template not found. ", template);
      assertEquals(null, template.getType());
      assertEquals(Link.REL_COPY, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("copy").path("[id]").queryParam("parentId", "[parentId]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_MOVE);
      assertNotNull("'" + Link.REL_MOVE + "' template not found. ", template);
      assertEquals(null, template.getType());
      assertEquals(Link.REL_MOVE, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("move").path("[id]").queryParam("parentId", "[parentId]")
         .queryParam("lockToken", "[lockToken]").build().toString(), template.getHref());

      template = templates.get(Link.REL_CREATE_FILE);
      assertNotNull("'" + Link.REL_CREATE_FILE + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_CREATE_FILE, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("file").path("[parentId]").queryParam("name", "[name]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_CREATE_FOLDER);
      assertNotNull("'" + Link.REL_CREATE_FOLDER + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_CREATE_FOLDER, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("folder").path("[parentId]").queryParam("name", "[name]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_CREATE_PROJECT);
      assertNotNull("'" + Link.REL_CREATE_PROJECT + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_CREATE_PROJECT, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("project").path("[parentId]").queryParam("name", "[name]")
         .queryParam("type", "[type]").build().toString(), template.getHref());

      template = templates.get(Link.REL_LOCK);
      assertNotNull("'" + Link.REL_LOCK + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_LOCK, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("lock").path("[id]").build().toString(), template.getHref());

      template = templates.get(Link.REL_UNLOCK);
      assertNotNull("'" + Link.REL_UNLOCK + "' template not found. ", template);
      assertEquals(null, template.getType());
      assertEquals(Link.REL_UNLOCK, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("unlock").path("[id]").queryParam("lockToken", "[lockToken]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_SEARCH);
      assertNotNull("'" + Link.REL_SEARCH + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_SEARCH, template.getRel());
      assertEquals(
         UriBuilder.fromPath(SERVICE_URI).path("search").queryParam("statement", "[statement]")
            .queryParam("maxItems", "[maxItems]").queryParam("skipCount", "[skipCount]").build().toString(),
         template.getHref());

      template = templates.get(Link.REL_SEARCH_FORM);
      assertNotNull("'" + Link.REL_SEARCH_FORM + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_SEARCH_FORM, template.getRel());
      assertEquals(
         UriBuilder.fromPath(SERVICE_URI).path("search").queryParam("maxItems", "[maxItems]")
            .queryParam("skipCount", "[skipCount]").queryParam("propertyFilter", "[propertyFilter]").build().toString(),
         template.getHref());
   }
}
