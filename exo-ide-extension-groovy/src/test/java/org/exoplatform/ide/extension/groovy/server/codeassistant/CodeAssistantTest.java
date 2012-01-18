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
package org.exoplatform.ide.extension.groovy.server.codeassistant;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.extension.groovy.server.Base;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class CodeAssistantTest extends Base
{
   private static final String SERVICE_NAME = "HelloWorld.grs";

   private int methods;

   private int decMethods;

   private Folder project;

   private Folder classpath;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      decMethods = classLoader.loadClass(Address.class.getCanonicalName()).getDeclaredMethods().length;
      methods = classLoader.loadClass(Address.class.getCanonicalName()).getMethods().length;
      putClass(classLoader, Address.class.getCanonicalName());
      putClass(classLoader, A.class.getCanonicalName());
      putClass(classLoader, Integer.class.getCanonicalName());
      putClass(classLoader, C.class.getCanonicalName());
      putClass(classLoader, Foo.class.getCanonicalName());

      project = virtualFileSystem.createFolder(virtualFileSystem.getInfo().getRoot().getId(), "project");
      virtualFileSystem.importZip(project.getId(),
         Thread.currentThread().getContextClassLoader().getResourceAsStream("groovy-test-project.zip"), true);

      //
      SessionProviderService sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      SessionProvider sessionProvider = new SessionProvider(new ConversationState(new Identity("root")));
      sessionProviderService.setSessionProvider(null, sessionProvider);
   }

   @Test
   @Ignore
   public void getClassByFqn() throws Exception
   {
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/class-description?fqn=" + Address.class.getCanonicalName()
            + "&projectid=" + project.getId() + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      TypeInfo cd = (TypeInfo)response.getEntity();
      Assert.assertEquals(methods, cd.getMethods().size());
   }

   @Test
   public void getGroovyClassByFqn() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/class-description?fqn=PHelloTest" + "&projectid="
            + project.getId() + "&vfsid=" + vfs_id, "", headers, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      TypeInfo cd = (TypeInfo)response.getEntity();
      Assert.assertEquals("PHelloTest", cd.getName());
   }

   @Test
   public void getClassByFqnError() throws Exception
   {
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/class-description?fqn=" + Address.class.getCanonicalName()
            + "error" + "&projectid=" + project.getId() + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findGroovyClassByName() throws Exception
   {
      String className = "Pojo";
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/project/services/" + SERVICE_NAME);
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + className + "?where=className"
            + "&projectid=" + project.getId() + "&vfsid=" + vfs_id, "", headers, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(1, types.size());
   }

   @SuppressWarnings("unchecked")
   @Test
   @Ignore
   public void findClassByPrefix() throws Exception
   {
      String pkg = Address.class.getPackage().getName();
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + pkg + "?where=fqn" + "&projectid="
            + project.getId() + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(4, types.size());

   }

   @Test
   public void findClassByPartName() throws Exception
   {
      String name = "P";
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + name + "?where=className"
            + "&projectid=" + project.getId() + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      @SuppressWarnings("unchecked")
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(2, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findRestServiceClassByPartName() throws Exception
   {
      String name = "H";
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + name + "?where=className"
            + "&projectid=" + project.getId() + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(1, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findAnnotations() throws Exception
   {
      String type = "ANNOTATION";
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-type/" + type + "?projectid=" + project.getId()
            + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(2, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   @Ignore
   public void findAnnotationsWithPrefix() throws Exception
   {
      String type = "ANNOTATION";
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-type/" + type + "?prefix=Fo" + "&projectid="
            + project.getId() + "&vfsid=" + vfs_id, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(1, types.size());
   }

   @Test
   public void classDoc() throws Exception
   {
      String path = "/ide/code-assistant/groovy/class-doc" //
         + "?fqn=" + BigDecimal.class.getCanonicalName() //
         + "&projectid=" + project.getId();
      ContainerResponse response = launcher.service("GET", path, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertNotNull(response.getEntity());
      String doc = (String)response.getEntity();
      Assert.assertTrue(doc.contains("Immutable, arbitrary-precision signed decimal numbers"));
   }

   @Test
   public void methodDoc() throws Exception
   {
      String method = BigDecimal.class.getCanonicalName() + ".add(BigDecimal)";
      String path = "/ide/code-assistant/groovy/class-doc" //
         + "?fqn=" + method //
         + "&projectid=" + project.getId();
      ContainerResponse response = launcher.service("GET", path, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      Assert.assertNotNull(response.getEntity());
   }
   
   @Test
   @SuppressWarnings("unchecked")
   public void findClassesInOnePackage() throws Exception
   {
      Item file =
         virtualFileSystem.getItemByPath(project.getPath() + "/data/Pojo.groovy", null, PropertyFilter.NONE_FILTER);
      String fileId = file.getId();
      ContainerResponse response =
         launcher.service("GET", "/ide/code-assistant/groovy/find-in-package" + "?projectid=" + project.getId()
            + "&vfsid=" + vfs_id + "&fileid=" + fileId, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)response.getEntity();
      Assert.assertEquals(2, types.size());
   }

   private void putClass(ClassLoader classLoader, String fqn) throws Exception
   {
      try
      {
         classpath = (Folder)virtualFileSystem.getItemByPath("/classpath", null, PropertyFilter.NONE_FILTER);
      }
      catch (ItemNotFoundException e)
      {
         classpath = virtualFileSystem.createFolder(virtualFileSystem.getInfo().getRoot().getId(), "classpath");
      }

      String name = fqn.substring(fqn.lastIndexOf(".") + 1);
      String path = fqn.replace(".", "/");
      TypeInfo cd = TypeInfoExtractor.extract(classLoader.loadClass(fqn));
      Folder f = virtualFileSystem.createFolder(classpath.getId(), path);
      String content = JsonGenerator.createJsonObject(cd).toString();

      // Replace by direct usage of JCR by usage of virtual file system.
      Session session = null;
      try
      {
         RepositoryService repositoryService =
            (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
         session = repositoryService.getCurrentRepository().login(vfs_id);
         Node fNode = ((ExtendedSession)session).getNodeByIdentifier(f.getId());
         Node fileNode = fNode.addNode(name, "nt:file");
         Node contentNode = fileNode.addNode("jcr:content", "exoide:classDescription");
         contentNode.setProperty("jcr:mimeType", "text/plain" /* media type ?? */);
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", content);
         contentNode.setProperty("exoide:className", fqn);
         contentNode.setProperty("exoide:fqn", name);
         contentNode.setProperty("exoide:type", cd.getType());
         contentNode.setProperty("exoide:modifieres", Integer.toString(cd.getModifiers()));
         session.save();
      }
      finally
      {
         if (session != null)
         {
            session.logout();
         }
      }
   }

   @After
   @Override
   public void tearDown() throws Exception
   {
      virtualFileSystem.delete(classpath.getId(), null);
      virtualFileSystem.delete(project.getId(), null);
      super.tearDown();
   }
}
