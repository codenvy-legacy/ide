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
package org.exoplatform.ide.groovy.codeassistant;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.groovy.Base;
import org.exoplatform.ide.groovy.codeassistant.bean.ShortTypeInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.codeassistant.extractors.TypeInfoExtractor;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CodeAssitantTest extends Base
{

   private int methods;
   
   private int decMethods;
   
   public void setUp() throws Exception
   {
      super.setUp();
      decMethods = ClassLoader.getSystemClassLoader().loadClass(Address.class.getCanonicalName()).getDeclaredMethods().length;
      methods = ClassLoader.getSystemClassLoader().loadClass(Address.class.getCanonicalName()).getMethods().length;
      putClass(ClassLoader.getSystemClassLoader(), session, Address.class.getCanonicalName());
      putClass(ClassLoader.getSystemClassLoader(), session, A.class.getCanonicalName());
      putClass(ClassLoader.getSystemClassLoader(), session, Integer.class.getCanonicalName());
    }
   
   @Test
   public void testGetClassByFqn() throws Exception
   {
      ContainerResponse cres =
         launcher.service("GET",
            "/ide/code-assistant/class-description?fqn=" + Address.class.getCanonicalName(), "", null, null,
            null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertEquals(cres.getEntityType(), TypeInfo.class);
      TypeInfo cd = (TypeInfo)cres.getEntity();
      JsonGeneratorImpl jsonGen =  new JsonGeneratorImpl();
      System.out.println(jsonGen.createJsonObject(cd).toString());
      assertEquals(methods,cd.getMethods().length);
      assertEquals(decMethods, cd.getDeclaredMethods().length);
   }
   
   
   @Test
   public void testGetClassByFqnError() throws Exception
   {
      ContainerResponse cres =
         launcher.service("GET",
            "/ide/code-assistant/class-description?fqn=" + Address.class.getCanonicalName()+"error", "", null, null,
            null, null);
      assertEquals(500, cres.getStatus());
   }

   @Test
   public void testFindClassByName() throws Exception
   {
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/find?class=" + Address.class.getSimpleName(), "", null,
            null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(cres.getEntity().getClass().isArray());
      ShortTypeInfo[] types =  (ShortTypeInfo[])cres.getEntity();
      assertEquals(1, types.length);
   }
   
   
   @Test
   public void testFindClassByPrefix() throws Exception
   {
      String pkg = Address.class.getPackage().getName();
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/find-by-prefix?prefix=" + pkg, "", null,
            null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(cres.getEntity().getClass().isArray());
      ShortTypeInfo[] types =  (ShortTypeInfo[])cres.getEntity();
      assertEquals(2, types.length);
      
   }
   
   @Test
   public void testClassDoc() throws Exception
   {
      assertTrue(root.hasNode("dev-doc/java/java.math/java.math.BigDecimal/java.math.BigDecimal/jcr:content"));
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/class-doc?fqn=" + BigDecimal.class.getCanonicalName(), "", null,
            null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertNotNull(cres.getEntity());
      String doc  =  (String)cres.getEntity();
      assertTrue(doc.contains("Immutable, arbitrary-precision signed decimal numbers"));
      
   }
   
   @Test
   public void testMethodDoc() throws Exception
   {
      assertTrue(root.hasNode("dev-doc/java/java.math/java.math.BigDecimal/methods-doc"));
      String method = BigDecimal.class.getCanonicalName() + ".add(BigDecimal)";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/class-doc?fqn=" + method, "", null,
            null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertNotNull(cres.getEntity());
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
            JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
            child.setProperty("jcr:data", jsonGenerator.createJsonObject(cd).toString());
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

}
