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
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.extension.groovy.server.Base;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CodeAssitantTest extends Base
{


   /**
    * 
    */
   private static final String SERVICE_NAME = "HelloWorld.grs";

   private int methods;

   private int decMethods;

   private Folder project;

   private VirtualFileSystem vfs;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      decMethods = classLoader.loadClass(Address.class.getCanonicalName()).getDeclaredMethods().length;
      methods = classLoader.loadClass(Address.class.getCanonicalName()).getMethods().length;
      putClass(classLoader, session, Address.class.getCanonicalName());
      putClass(classLoader, session, A.class.getCanonicalName());
      putClass(classLoader, session, Integer.class.getCanonicalName());
      putClass(classLoader, session, C.class.getCanonicalName());
      putClass(classLoader, session, Foo.class.getCanonicalName());
      VirtualFileSystemRegistry vfsRegistry =
         (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
      vfs = vfsRegistry.getProvider(WS_NAME).newInstance(null);
      project = vfs.createFolder(vfs.getInfo().getRoot().getId(), "project");
      vfs.importZip(project.getId(),
         Thread.currentThread().getContextClassLoader().getResourceAsStream("groovy-test-project.zip"), true);
   }

   @Test
   public void getClassByFqn() throws Exception
   {
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/class-description?fqn=" + Address.class.getCanonicalName()
            + "&projectid=" + project.getId() + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      TypeInfo cd = (TypeInfo)cres.getEntity();
      Assert.assertEquals(methods, cd.getMethods().length);
      Assert.assertEquals(decMethods, cd.getDeclaredMethods().length);
   }

   @Test
   public void getGroovyClassByFqn() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/class-description?fqn=PHelloTest" + "&projectid="
            + project.getId() + "&vfsid=" + WS_NAME, "", headers, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      TypeInfo cd = (TypeInfo)cres.getEntity();
      Assert.assertEquals("PHelloTest", cd.getName());
   }

   @Test
   public void getClassByFqnError() throws Exception
   {
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/class-description?fqn=" + Address.class.getCanonicalName()
            + "error" + "&projectid=" + project.getId() + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.NO_CONTENT.getStatusCode(), cres.getStatus());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findGroovyClassByName() throws Exception
   {
      String className = "Pojo";
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/project/services/" + SERVICE_NAME);
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + className + "?where=className" +"&projectid=" + project.getId()
            + "&vfsid=" + WS_NAME, "", headers, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      Assert.assertEquals(1, types.size());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void findClassByPrefix() throws Exception
   {
      String pkg = Address.class.getPackage().getName();
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + pkg + "?where=fqn" + "&projectid="
            + project.getId() + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      Assert.assertEquals(4, types.size());

   }

   @Test
   public void findClassByPartName() throws Exception
   {
      String name = "P";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + name + "?where=className"
            + "&projectid=" + project.getId() + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      @SuppressWarnings("unchecked")
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      Assert.assertEquals(2, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findRestServiceClassByPartName() throws Exception
   {
      String name = "H";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-prefix/" + name + "?where=className"
            + "&projectid=" + project.getId() + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      Assert.assertEquals(1, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findAnnotations() throws Exception
   {
      String type = "ANNOTATION";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-type/" + type + "?projectid=" + project.getId()
            + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      Assert.assertEquals(2, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void findAnnotationsWithPrefix() throws Exception
   {
      String type = "ANNOTATION";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/groovy/find-by-type/" + type + "?prefix=Fo" + "&projectid="
            + project.getId() + "&vfsid=" + WS_NAME, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      Assert.assertEquals(1, types.size());
   }

   @Test
   public void classDoc() throws Exception
   {
      Assert.assertTrue(root.hasNode("dev-doc/java/java.math/java.math.BigDecimal/java.math.BigDecimal/jcr:content"));
      session.getRootNode().getNode("project");
      String path = "/ide/code-assistant/groovy/class-doc" //
         + "?fqn=" + BigDecimal.class.getCanonicalName() //
         + "&projectid=" + ((ExtendedNode)session.getRootNode().getNode("project")).getIdentifier();
      ContainerResponse cres = launcher.service("GET", path, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      Assert.assertNotNull(cres.getEntity());
      String doc = (String)cres.getEntity();
      Assert.assertTrue(doc.contains("Immutable, arbitrary-precision signed decimal numbers"));
   }

   @Test
   public void methodDoc() throws Exception
   {
      Assert.assertTrue(root.hasNode("dev-doc/java/java.math/java.math.BigDecimal/methods-doc"));
      String method = BigDecimal.class.getCanonicalName() + ".add(BigDecimal)";
      String path = "/ide/code-assistant/groovy/class-doc" //
         + "?fqn=" + method //
         + "&projectid=" + ((ExtendedNode)session.getRootNode().getNode("project")).getIdentifier();
      ContainerResponse cres = launcher.service("GET", path, "", null, null, null, null);
      Assert.assertEquals(Response.Status.OK.getStatusCode(), cres.getStatus());
      Assert.assertNotNull(cres.getEntity());
   }

   private void putClass(ClassLoader classLoader, Session session, String fqn) throws RepositoryException,
      ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
      ConstraintViolationException, IncompatibleClassChangeError, ValueFormatException, JsonException,
      AccessDeniedException, InvalidItemStateException
   {
      Node base;
      if (!session.getRootNode().hasNode("classpath"))
      {
         base = session.getRootNode().addNode("classpath", "nt:folder");

      }
      base = session.getRootNode().getNode("classpath");

      try
      {
         String clazz = fqn;
         TypeInfo cd = TypeInfoExtractor.extract(classLoader.loadClass(clazz));
         Node child = base;
         String[] seg = fqn.split("\\.");
         String path = new String();
         for (int i = 0; i < seg.length - 1; i++)
         {
            path = path + seg[i];
            if (!child.hasNode(path))
            {
               child = child.addNode(path, "nt:folder");
            }
            else
            {
               child = child.getNode(path);
            }
            path = path + ".";
         }

         if (!child.hasNode(clazz))
         {
            child = child.addNode(clazz, "nt:file");
            child = child.addNode("jcr:content", "exoide:classDescription");
            child.setProperty("jcr:data", JsonGenerator.createJsonObject(cd).toString());
            child.setProperty("jcr:lastModified", Calendar.getInstance());
            child.setProperty("jcr:mimeType", "text/plain");
            child.setProperty("exoide:className", clazz.substring(clazz.lastIndexOf(".") + 1));
            child.setProperty("exoide:fqn", clazz);
            child.setProperty("exoide:type", cd.getType().toString());
            child.setProperty("exoide:modifieres", cd.getModifiers());
         }
         session.save();
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      }

   }

   //   /**
   //    * @throws RepositoryException 
   //    * @throws ConstraintViolationException 
   //    * @throws VersionException 
   //    * @throws LockException 
   //    * @throws NoSuchNodeTypeException 
   //    * @throws PathNotFoundException 
   //    * @throws ItemExistsException 
   //    * 
   //    */
   //   private void createProject(Session session) throws ItemExistsException, PathNotFoundException,
   //      NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException
   //   {
   //      Node base;
   //      if (!session.getRootNode().hasNode("project"))
   //      {
   //         base = session.getRootNode().addNode("project", "nt:folder");
   //         session.save();
   //      }
   //      base = session.getRootNode().getNode("project");
   //
   //      Node classPath = base.addNode(".groovyclasspath", "nt:file");
   //      classPath = classPath.addNode("jcr:content", "nt:resource");
   //      classPath.setProperty("jcr:data", CLASSPATH);
   //      classPath.setProperty("jcr:lastModified", Calendar.getInstance());
   //      classPath.setProperty("jcr:mimeType", MediaType.APPLICATION_JSON);
   //
   //      Node services = base.addNode("services", "nt:folder");
   //      Node scriptFile = services.addNode(SERVICE_NAME, "nt:file");
   //      Node script = scriptFile.addNode("jcr:content", "exo:groovyResourceContainer");
   //      script.setProperty("exo:autoload", false);
   //      script.setProperty("jcr:mimeType", "aplication/x-jaxrs+groovy");
   //      script.setProperty("jcr:lastModified", Calendar.getInstance());
   //      script.setProperty("jcr:data", Thread.currentThread().getContextClassLoader().getResourceAsStream(SERVICE_NAME));
   //      session.save();
   //      //create pojo class
   //      Node data = base.addNode("data", "nt:folder");
   //      Node pojo = data.addNode(POGO, "nt:file");
   //      pojo = pojo.addNode("jcr:content", "nt:resource");
   //      pojo.setProperty("jcr:mimeType", "application/x-groovy");
   //      pojo.setProperty("jcr:lastModified", Calendar.getInstance());
   //      pojo.setProperty("jcr:data", Thread.currentThread().getContextClassLoader().getResourceAsStream(POGO));
   //      session.save();
   //
   //      Node res = base.addNode("testClass.gg", "nt:file");
   //      res = res.addNode("jcr:content", "nt:resource");
   //      res.setProperty("jcr:mimeType", "application/x-groovy");
   //      res.setProperty("jcr:lastModified", Calendar.getInstance());
   //      res.setProperty("jcr:data", Thread.currentThread().getContextClassLoader().getResourceAsStream("testClass.gg"));
   //      session.save();
   //
   //   }

}
