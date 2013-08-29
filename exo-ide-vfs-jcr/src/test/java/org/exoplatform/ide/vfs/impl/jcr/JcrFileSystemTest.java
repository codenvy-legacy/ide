/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.vfs.impl.jcr;

import com.codenvy.commons.env.EnvironmentContext;

import junit.framework.TestCase;
import org.everrest.core.RequestHandler;
import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.everrest.core.tools.ResourceLauncher;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
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
 * @version $Id: JcrFileSystemTest.java 77587 2011-12-13 10:42:02Z andrew00x $
 */
public abstract class JcrFileSystemTest extends TestCase
{
   protected final String BASE_URI = "http://localhost/service";
   protected final String SERVICE_URI = BASE_URI + "/ide/vfs/v2/";
   protected final String DEFAULT_CONTENT = "__TEST__";
   private final String JCR_WORKSPACE_NAME = "ws";

   protected Log log = ExoLogger.getExoLogger(getClass());
   protected String REPOSITORY_NAME = "db1";
   protected String TEST_ROOT_NAME = "TESTROOT";

   protected Session session;
   protected Node testRoot;
   protected ResourceLauncher launcher;
   protected StandaloneContainer container;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      System.setProperty("org.exoplatform.mimetypes", "conf/mimetypes.properties");
      String conf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      StandaloneContainer.setConfigurationURL(conf);
      container = StandaloneContainer.getInstance();

      String loginConf = getClass().getResource("/login.conf").toString();
      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", loginConf);

      // JCR
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      ManageableRepository repository = repositoryService.getRepository(REPOSITORY_NAME);
      session = repository.login(new CredentialsImpl("root", "exo".toCharArray()), JCR_WORKSPACE_NAME);
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
      EnvironmentContext env = EnvironmentContext.getCurrent();
      env.setVariable(EnvironmentContext.WORKSPACE_ID, REPOSITORY_NAME);
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
      String path = SERVICE_URI + "item/" + id;
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
      if (item.getParentId() == null)
      {
         assertNull("'" + Link.REL_DELETE + "' link not allowed for root folder. ", link);
      }
      else
      {
         assertNotNull("'" + Link.REL_DELETE + "' link not found. ", link);
         assertEquals(null, link.getType());
         assertEquals(Link.REL_DELETE, link.getRel());
         if (item.getItemType() == ItemType.FILE && ((File)item).isLocked())
         {
            assertEquals(
               UriBuilder.fromPath(SERVICE_URI).path("delete").path(item.getId())
                  .queryParam("lockToken", "[lockToken]").build().toString(), link.getHref());
         }
         else
         {
            assertEquals(UriBuilder.fromPath(SERVICE_URI).path("delete").path(item.getId()).build().toString(),
               link.getHref());
         }
      }

      link = links.get(Link.REL_COPY);
      if (item.getParentId() == null)
      {
         assertNull("'" + Link.REL_COPY + "' link not allowed for root folder. ", link);
      }
      else
      {
         assertNotNull("'" + Link.REL_COPY + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_COPY, link.getRel());
         assertEquals(
            UriBuilder.fromPath(SERVICE_URI).path("copy").path(item.getId()).queryParam("parentId", "[parentId]")
               .build().toString(), link.getHref());
      }

      link = links.get(Link.REL_MOVE);
      if (item.getParentId() == null)
      {
         assertNull("'" + Link.REL_MOVE + "' link not allowed for root folder. ", link);
      }
      else
      {
         assertNotNull("'" + Link.REL_MOVE + "' link not found. ", link);
         assertEquals(MediaType.APPLICATION_JSON, link.getType());
         assertEquals(Link.REL_MOVE, link.getRel());
         if (item.getItemType() == ItemType.FILE && ((File)item).isLocked())
         {
            assertEquals(
               UriBuilder.fromPath(SERVICE_URI).path("move").path(item.getId()).queryParam("parentId", "[parentId]")
                  .queryParam("lockToken", "[lockToken]").build().toString(), link.getHref());
         }
         else
         {
            assertEquals(
               UriBuilder.fromPath(SERVICE_URI).path("move").path(item.getId()).queryParam("parentId", "[parentId]")
                  .build().toString(), link.getHref());
         }
      }

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

         link = links.get(Link.REL_DOWNLOAD_FILE);
         assertNotNull("'" + Link.REL_DOWNLOAD_FILE + "' link not found. ", link);
         assertEquals(file.getMimeType(), link.getType());
         assertEquals(Link.REL_DOWNLOAD_FILE, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("downloadfile").path(file.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_CONTENT_BY_PATH);
         assertNotNull("'" + Link.REL_CONTENT_BY_PATH + "' link not found. ", link);
         assertEquals(file.getMimeType(), link.getType());
         assertEquals(Link.REL_CONTENT_BY_PATH, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("contentbypath").path(file.getPath().substring(1)).build()
            .toString(), link.getHref());

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

         link = links.get(Link.REL_UPLOAD_FILE);
         assertNotNull("'" + Link.REL_UPLOAD_FILE + "' link not found. ", link);
         assertEquals(MediaType.TEXT_HTML, link.getType());
         assertEquals(Link.REL_UPLOAD_FILE, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("uploadfile").path(item.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_CREATE_PROJECT);
         if (item instanceof ProjectImpl)
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

         link = links.get(Link.REL_EXPORT);
         assertNotNull("'" + Link.REL_EXPORT + "' link not found. ", link);
         assertEquals("application/zip", link.getType());
         assertEquals(Link.REL_EXPORT, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("export").path(item.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_IMPORT);
         assertNotNull("'" + Link.REL_IMPORT + "' link not found. ", link);
         assertEquals("application/zip", link.getType());
         assertEquals(Link.REL_IMPORT, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("import").path(item.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_DOWNLOAD_ZIP);
         assertNotNull("'" + Link.REL_DOWNLOAD_ZIP + "' link not found. ", link);
         assertEquals("application/zip", link.getType());
         assertEquals(Link.REL_DOWNLOAD_ZIP, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("downloadzip").path(item.getId()).build().toString(),
            link.getHref());

         link = links.get(Link.REL_UPLOAD_ZIP);
         assertNotNull("'" + Link.REL_UPLOAD_ZIP + "' link not found. ", link);
         assertEquals(MediaType.TEXT_HTML, link.getType());
         assertEquals(Link.REL_UPLOAD_ZIP, link.getRel());
         assertEquals(UriBuilder.fromPath(SERVICE_URI).path("uploadzip").path(item.getId()).build().toString(),
            link.getHref());
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
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("itembypath").path("[path]").build().toString(),
         template.getHref());

      template = templates.get(Link.REL_COPY);
      assertNotNull("'" + Link.REL_COPY + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
      assertEquals(Link.REL_COPY, template.getRel());
      assertEquals(UriBuilder.fromPath(SERVICE_URI).path("copy").path("[id]").queryParam("parentId", "[parentId]")
         .build().toString(), template.getHref());

      template = templates.get(Link.REL_MOVE);
      assertNotNull("'" + Link.REL_MOVE + "' template not found. ", template);
      assertEquals(MediaType.APPLICATION_JSON, template.getType());
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
