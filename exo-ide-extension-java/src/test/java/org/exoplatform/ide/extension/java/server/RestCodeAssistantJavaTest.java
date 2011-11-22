/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.everrest.core.impl.ContainerResponse;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.codeassistant.framework.server.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.api.TypeInfo;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestCodeAssistantJavaTest Mar 30, 2011 11:40:04 AM evgen $
 *
 */
public class RestCodeAssistantJavaTest extends Base
{

   private int methods;

   private int decMethods;

   private VirtualFileSystem vfs;

   @Before
   public void init() throws VirtualFileSystemException
   {
      VirtualFileSystemRegistry vfsRegistry =
         (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
      vfs = vfsRegistry.getProvider("ws").newInstance(null);

   }

   @Test
   public void testGetClassByFqn() throws Exception
   {
      decMethods =
         ClassLoader.getSystemClassLoader().loadClass(BigDecimal.class.getCanonicalName()).getDeclaredMethods().length;
      methods = ClassLoader.getSystemClassLoader().loadClass(BigDecimal.class.getCanonicalName()).getMethods().length;

      ContainerResponse cres =
         launcher.service("GET",
            "/ide/code-assistant/java/class-description?fqn=" + BigDecimal.class.getCanonicalName(), "", null, null,
            null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      TypeInfo cd = (TypeInfo)cres.getEntity();

      assertEquals(methods, cd.getMethods().length);
      assertEquals(decMethods, cd.getDeclaredMethods().length);
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testFindFqnByClassName() throws Exception
   {
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/find?class=" + java.math.MathContext.class.getSimpleName(),
            "", null, null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      assertEquals(1, types.size());
      assertEquals(MathContext.class.getCanonicalName(), types.get(0).getQualifiedName());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testFindClassByPackagePrefix() throws Exception
   {
      String pkg = java.math.BigInteger.class.getPackage().getName();
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/find-by-prefix/" + pkg + "?where=fqn", "", null, null, null,
            null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      //      assertTrue(cres.getEntity().getClass().isArray());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      assertEquals(10, types.size());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testFindClassByClassNamePrefix() throws Exception
   {
      String clazz = "B";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/find-by-prefix/" + clazz + "?where=className", "", null,
            null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      //      assertTrue(cres.getEntity().getClass().isArray());
      List<ShortTypeInfo> types = (List<ShortTypeInfo>)cres.getEntity();
      assertEquals(6, types.size());
   }

   @Test
   public void testFindByType() throws Exception
   {
      String type = "class";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/find-by-type/" + type, "", null, null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(cres.getEntity().getClass().isArray());
      ShortTypeInfo[] types = (ShortTypeInfo[])cres.getEntity();
      assertEquals(10, types.length);
   }

   @Test
   public void testMethodDoc() throws Exception
   {
      assertTrue(root.hasNode("dev-doc/java/java.math/java.math.BigDecimal/methods-doc"));
      String method = BigDecimal.class.getCanonicalName() + ".add(BigDecimal)";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/class-doc?fqn=" + method, "", null, null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertNotNull(cres.getEntity());
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testFindClassesByPackage() throws Exception
   {
      Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), "testProj");
      vfs.importZip(folder.getId(),
         Thread.currentThread().getContextClassLoader().getResourceAsStream("spring-project.zip"), true);
      Item file =
         vfs.getItemByPath(folder.getPath() + "/src/main/java/test/CartController.java", null,
            PropertyFilter.NONE_FILTER);

      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/find-in-package?fileid=" + file.getId() + "&projectid="
            + folder.getId() + "&vfsid=ws", "", null, null, null, null);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      List<ShortTypeInfo> res = (List<ShortTypeInfo>)cres.getEntity();
      assertEquals(1, res.size());
      assertEquals("Product", res.get(0).getName());
   }

}
