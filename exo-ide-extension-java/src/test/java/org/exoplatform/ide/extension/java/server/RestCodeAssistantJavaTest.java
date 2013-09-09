///*
// * CODENVY CONFIDENTIAL
// * __________________
// *
// * [2012] - [2013] Codenvy, S.A.
// * All Rights Reserved.
// *
// * NOTICE:  All information contained herein is, and remains
// * the property of Codenvy S.A. and its suppliers,
// * if any.  The intellectual and technical concepts contained
// * herein are proprietary to Codenvy S.A.
// * and its suppliers and may be covered by U.S. and Foreign Patents,
// * patents in process, and are protected by trade secret or copyright law.
// * Dissemination of this information or reproduction of this material
// * is strictly forbidden unless prior written permission is obtained
// * from Codenvy S.A..
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
//               launcher.service("GET", "/ide/code-assistant/java/find-packages?projectid="
//                  + project.getId() + "&vfsid=ws" + "&package=org.exoplatform.ide", "", null, null, null, null);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      List<String> packages = (List<String>)cres.getEntity();
//      assertThat(packages).contains("org.exoplatform.ide.client", "org.exoplatform.ide.client.autocompletion");
//   }
//
//}
