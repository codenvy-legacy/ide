///*
// * Copyright (C) 2011 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.extension.java.server;
//
//import static org.fest.assertions.Assertions.assertThat;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//
//import org.everrest.core.impl.ContainerResponse;
//import org.exoplatform.common.http.HTTPStatus;
//import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
//import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
//import org.exoplatform.ide.vfs.shared.Folder;
//import org.exoplatform.ide.vfs.shared.Item;
//import org.exoplatform.ide.vfs.shared.PropertyFilter;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
// * @version $Id: RestCodeAssistantJavaTest Mar 30, 2011 11:40:04 AM evgen $
// * 
// */
//public class RestCodeAssistantJavaTest extends JavaDocBase
//{
//
//   private int methods;
//
//   @Test
//   @Ignore
//   public void testGetClassByFqn() throws Exception
//   {
//      methods = ClassLoader.getSystemClassLoader().loadClass(BigDecimal.class.getCanonicalName()).getMethods().length;
//      ContainerResponse cres =
//         launcher.service("GET",
//            "/ide/code-assistant/java/class-description?fqn=" + BigDecimal.class.getCanonicalName() + "&projectid="
//               + project.getId() + "&vfsid=ws", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      TypeInfo cd = (TypeInfo)cres.getEntity();
//
//      assertEquals(methods, cd.getMethods().size());
//   }
//
//   @Test
//   @SuppressWarnings("unchecked")
//   @Ignore
//   public void testFindClassByPackagePrefix() throws Exception
//   {
//      String pkg = java.math.BigInteger.class.getPackage().getName();
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/code-assistant/java/find-by-prefix/" + pkg + "?where=fqn" + "&projectid="
//            + project.getId() + "&vfsid=ws", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      // assertTrue(cres.getEntity().getClass().isArray());
//      TypesList types = (TypesList)cres.getEntity();
//      assertNotNull(types);
//      assertEquals(10, types.getTypes().size());
//   }
//
//   @Test
//   @SuppressWarnings("unchecked")
//   @Ignore
//   public void testFindClassByClassNamePrefix() throws Exception
//   {
//      String clazz = "B";
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/code-assistant/java/find-by-prefix/" + clazz + "?where=className"
//            + "&projectid=" + project.getId() + "&vfsid=ws", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      // assertTrue(cres.getEntity().getClass().isArray());
//      TypesList types = (TypesList)cres.getEntity();
//      assertNotNull(types);
//      assertEquals(6, types.getTypes().size());
//   }
//
//   @Test
//   @SuppressWarnings("unchecked")
//   @Ignore
//   public void testFindByType() throws Exception
//   {
//      String type = "class";
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/code-assistant/java/find-by-type/" + type + "?projectid=" + project.getId()
//            + "&vfsid=ws", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      TypesList types = (TypesList)cres.getEntity();
//      assertNotNull(types);
//      assertEquals(21, types.getTypes().size());
//   }
//
//   @Test
//   @Ignore
//   public void testMethodDoc() throws Exception
//   {
//      String method = BigDecimal.class.getCanonicalName() + ".add(BigDecimal)";
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/code-assistant/java/class-doc?fqn=" + method + "&projectid=" + project.getId()
//            + "&vfsid=ws", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      assertNotNull(cres.getEntity());
//   }
//
//   @Test
//   @SuppressWarnings("unchecked")
//   @Ignore
//   public void testFindClassesByPackage() throws Exception
//   {
//      Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), "testProj");
//      vfs.importZip(folder.getId(),
//         Thread.currentThread().getContextClassLoader().getResourceAsStream("spring-project.zip"), true);
//      Item file =
//         vfs.getItemByPath(folder.getPath() + "/src/main/java/test/CartController.java", null,
//            PropertyFilter.NONE_FILTER);
//
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/code-assistant/java/find-in-package?fileid=" + file.getId() + "&projectid="
//            + folder.getId() + "&vfsid=ws", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      TypesList types = (TypesList)cres.getEntity();
//      assertNotNull(types);
//      assertEquals(1, types.getTypes().size());
//      assertEquals("Product", types.getTypes().get(0).getName());
//   }
//   
//   @Test
//   @Ignore
//   public void findPackage() throws Exception
//   {
//      ContainerResponse cres =
//               launcher.service("GET", "/ide/code-assistant/java/fing-packages?projectid="
//                  + project.getId() + "&vfsid=ws" + "&package=org.exoplatform.ide", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      List<String> packages = (List<String>)cres.getEntity();
//      assertThat(packages).contains("org.exoplatform.ide.client", "org.exoplatform.ide.client.autocompletion");
//   }
//
//}
