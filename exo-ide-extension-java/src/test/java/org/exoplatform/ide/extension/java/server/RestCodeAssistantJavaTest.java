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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.core.MultivaluedMap;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.codeassistant.framework.server.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.api.TypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.junit.Test;
/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestCodeAssistantJavaTest Mar 30, 2011 11:40:04 AM evgen $
 *
 */
public class RestCodeAssistantJavaTest extends Base
{
   
   private int methods;

   private int decMethods;

   /**
    * 
    */
   private static final String POGO = "Pojo.groovy";

   protected static final String TEST_CLASS_1_NAME = "HelloWorldApp.java";
   
   protected static final String TEST_CLASS_2_NAME = "CartController.java";
   
   protected static final String TEST_CLASS_3_NAME = "Product.java";
   
   private static final String TEST_JAVA_PROJECT_NAME = "SampleProject" ;
   
   private static final String TEST_JAVA_PROJECT_ROOT_PATH = "/" + TEST_JAVA_PROJECT_NAME ;
   
   private static final String JAVA_PROJECT_SOURCE_ROOT_PREFIX = "/src/main/java" ;
   
   protected static final String TEST_PACKAGE_NAME = "com.test";
   
   private static final String TEST_PACKAGE_PATH = getJavaProjectPackagePath(TEST_JAVA_PROJECT_ROOT_PATH, JAVA_PROJECT_SOURCE_ROOT_PREFIX, TEST_PACKAGE_NAME);
   
   protected static final String TEST_CLASS_LOCATION = TEST_PACKAGE_PATH + "/" + TEST_CLASS_1_NAME;

   private static final String FOLDER_NODE_TYPE = "nt:folder";
   
   private static final String FILE_NODE_TYPE = "nt:file";
   
   private static final String NT_RESOURCE_NODE_TYPE = "nt:resource";

   
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
      System.out.println("RestCodeAssistantJavaTest.testGetClassByFqn()" + cres.getEntity().toString());
      assertEquals(HTTPStatus.OK, cres.getStatus());
      TypeInfo cd = (TypeInfo)cres.getEntity();

      assertEquals(methods, cd.getMethods().length);
      assertEquals(decMethods, cd.getDeclaredMethods().length);
   }

   @Test
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
   public void testFindClassByClassNamePrefix() throws Exception
   {
      String clazz = "B";
      ContainerResponse cres =
         launcher.service("GET", "/ide/code-assistant/java/find-by-prefix/" + clazz + "?where=className", "", null, null, null,
            null);
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
   public void testFindClassesByProject() throws Exception
   {
      createTestProject(session);
      
      String testLocation;
      
      // test with existed file TEST_CLASS_1_NAME location in package
      testLocation = TEST_CLASS_LOCATION;
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("location", testLocation);
      ContainerResponse containerResponse =
         launcher.service("GET", "/ide/code-assistant/java/find-by-project",
            "", headers, null, null, null);
      
      assertEquals(HTTPStatus.OK, containerResponse.getStatus());
      List<ShortTypeInfo> foundClasses = (List<ShortTypeInfo>)containerResponse.getEntity();  
      assertEquals(2, foundClasses.size());
      testFoundClass(foundClasses.get(0), getClassNameOnFileName(TEST_CLASS_2_NAME), TEST_PACKAGE_NAME + "." + GroovyScriptServiceUtil.getClassNameOnFileName(TEST_CLASS_2_NAME));
      testFoundClass(foundClasses.get(1), getClassNameOnFileName(TEST_CLASS_3_NAME), TEST_PACKAGE_NAME + "." + GroovyScriptServiceUtil.getClassNameOnFileName(TEST_CLASS_3_NAME));
      
      // test with non-existed file location in package
      testLocation = TEST_CLASS_LOCATION + ".non-existed-file";
      headers.clear();
      headers.putSingle("location", testLocation);
      containerResponse =
         launcher.service("GET", "/ide/code-assistant/java/find-by-project",
            "", headers, null, null, null);
      
      assertEquals(HTTPStatus.OK, containerResponse.getStatus());
      foundClasses = (List<ShortTypeInfo>)containerResponse.getEntity();
      assertEquals(3, foundClasses.size());
      testFoundClass(foundClasses.get(0), getClassNameOnFileName(TEST_CLASS_1_NAME), TEST_PACKAGE_NAME + "." + GroovyScriptServiceUtil.getClassNameOnFileName(TEST_CLASS_1_NAME));
      testFoundClass(foundClasses.get(1), getClassNameOnFileName(TEST_CLASS_2_NAME), TEST_PACKAGE_NAME + "." + GroovyScriptServiceUtil.getClassNameOnFileName(TEST_CLASS_2_NAME));
      testFoundClass(foundClasses.get(2), getClassNameOnFileName(TEST_CLASS_3_NAME), TEST_PACKAGE_NAME + "." + GroovyScriptServiceUtil.getClassNameOnFileName(TEST_CLASS_3_NAME));

      // test with non-existed file location at the project source root
      testLocation = TEST_JAVA_PROJECT_ROOT_PATH + JAVA_PROJECT_SOURCE_ROOT_PREFIX + "/" + TEST_CLASS_1_NAME + ".non-existed-file";
      headers.clear();
      headers.putSingle("location", testLocation);
      containerResponse =
         launcher.service("GET", "/ide/code-assistant/java/find-by-project",
            "", headers, null, null, null);
      
      assertEquals(HTTPStatus.OK, containerResponse.getStatus());
      foundClasses = (List<ShortTypeInfo>)containerResponse.getEntity();
      assertEquals(1, foundClasses.size());
      testFoundClass(foundClasses.get(0), getClassNameOnFileName(TEST_CLASS_1_NAME), GroovyScriptServiceUtil.getClassNameOnFileName(TEST_CLASS_1_NAME));
            
      // test with non-existed file location at the project root
      testLocation = TEST_JAVA_PROJECT_ROOT_PATH + "/" + TEST_CLASS_1_NAME + ".non-existed-file";
      headers.clear();
      headers.putSingle("location", testLocation);
      containerResponse =
         launcher.service("GET", "/ide/code-assistant/java/find-by-project",
            "", headers, null, null, null);
      
      assertEquals(HTTPStatus.OK, containerResponse.getStatus());
      foundClasses = (List<ShortTypeInfo>)containerResponse.getEntity();
      assertEquals(0, foundClasses.size());
      
      // test with non-existed file location at the workspace root
      testLocation = "/" + WS_NAME + "/" + TEST_CLASS_1_NAME + ".non-existed-file";
      headers.clear();
      headers.putSingle("location", testLocation);
      containerResponse =
         launcher.service("GET", "/ide/code-assistant/java/find-by-project",
            "", headers, null, null, null);
      
      assertEquals(HTTPStatus.OK, containerResponse.getStatus());
      foundClasses = (List<ShortTypeInfo>)containerResponse.getEntity();
      assertEquals(0, foundClasses.size());
   }

   private void testFoundClass(ShortTypeInfo classInfo, String className, String classFqn)
   {
      assertEquals(className, classInfo.getName());
      assertEquals(classFqn, classInfo.getQualifiedName());
      assertEquals("CLASS", classInfo.getType());
   }
   
   /**
    * Create sample project in JCR:
    *<pre>
    *[workspace]
    *   TEST_CLASS_1_NAME
    *   \TEST_JAVA_PROJECT_NAME\
    *      TEST_CLASS_1_NAME
    *      src\
    *         main\
    *            TEST_CLASS_1_NAME
    *            java\            
    *               com\
    *                  test\
    *                     TEST_CLASS_1_NAME
    *                     TEST_CLASS_2_NAME
    *                     TEST_CLASS_3_NAME
    *                     POJO 
    * </pre>              
    * @throws RepositoryException 
    * @throws ConstraintViolationException 
    * @throws VersionException 
    * @throws LockException 
    * @throws NoSuchNodeTypeException 
    * @throws PathNotFoundException 
    * @throws ItemExistsException 
    * 
    */
   private void createTestProject(Session session) throws ItemExistsException, PathNotFoundException,
      NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException
   {
      Node javaProject;
      
      if (!session.getRootNode().hasNode(TEST_JAVA_PROJECT_NAME))
      {
         javaProject = session.getRootNode().addNode(TEST_JAVA_PROJECT_NAME, FOLDER_NODE_TYPE);
         session.save();
      }

      javaProject = session.getRootNode().getNode(TEST_JAVA_PROJECT_NAME);

      // create test project structure
      Node testSourceRootNode = javaProject.addNode("src", FOLDER_NODE_TYPE)
         .addNode("main", FOLDER_NODE_TYPE)
         .addNode("java", FOLDER_NODE_TYPE);
         
      Node testPackageRootNode = testSourceRootNode.addNode("com", FOLDER_NODE_TYPE)
         .addNode("test", FOLDER_NODE_TYPE);
      
      // add test class 1 to the package folder "test"
      Node class1FileResourse = testPackageRootNode.addNode(TEST_CLASS_1_NAME, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      class1FileResourse.setProperty("jcr:mimeType", "aplication/java");
      class1FileResourse.setProperty("jcr:lastModified", Calendar.getInstance());
      class1FileResourse.setProperty("jcr:data", TEST_CLASS_1_NAME + " fake data");
      session.save();

      // add test class 1 to the project root folder
      class1FileResourse = javaProject.addNode(TEST_CLASS_1_NAME, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      class1FileResourse.setProperty("jcr:mimeType", "aplication/java");
      class1FileResourse.setProperty("jcr:lastModified", Calendar.getInstance());
      class1FileResourse.setProperty("jcr:data", TEST_CLASS_1_NAME + " fake data");
      session.save();

      // add test class 1 to the workspace folder
      class1FileResourse = session.getRootNode().addNode(TEST_CLASS_1_NAME, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      class1FileResourse.setProperty("jcr:mimeType", "aplication/java");
      class1FileResourse.setProperty("jcr:lastModified", Calendar.getInstance());
      class1FileResourse.setProperty("jcr:data", TEST_CLASS_1_NAME + " fake data");
      session.save();

      // add test class 1 to the project source root folder
      class1FileResourse = testSourceRootNode.addNode(TEST_CLASS_1_NAME, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      class1FileResourse.setProperty("jcr:mimeType", "aplication/java");
      class1FileResourse.setProperty("jcr:lastModified", Calendar.getInstance());
      class1FileResourse.setProperty("jcr:data", TEST_CLASS_1_NAME + " fake data");
      session.save();
      
      // add test class 2 to the folder "test"
      Node class2FileResourse = testPackageRootNode.addNode(TEST_CLASS_2_NAME, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      class2FileResourse.setProperty("jcr:mimeType", "aplication/java");
      class2FileResourse.setProperty("jcr:lastModified", Calendar.getInstance());
      class2FileResourse.setProperty("jcr:data", TEST_CLASS_2_NAME + " fake data");      
      session.save();

      // add test class 3 to the folder "test"
      Node class3FileResourse = testPackageRootNode.addNode(TEST_CLASS_3_NAME, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      class3FileResourse.setProperty("jcr:mimeType", "aplication/java");
      class3FileResourse.setProperty("jcr:lastModified", Calendar.getInstance());
      class3FileResourse.setProperty("jcr:data", TEST_CLASS_3_NAME + " fake data");
      session.save();
      
      // add pojo script to the folder "test"
      Node pojoFileResource = testPackageRootNode.addNode(POGO, FILE_NODE_TYPE).addNode("jcr:content", NT_RESOURCE_NODE_TYPE);
      pojoFileResource.setProperty("jcr:mimeType", "application/x-groovy");
      pojoFileResource.setProperty("jcr:lastModified", Calendar.getInstance());
      pojoFileResource.setProperty("jcr:data", POGO + " fake data");
      session.save();
   }
   
   /**
    * 
    * @param javaProjectRootPath
    * @param javaProjectSourceRootPrefix
    * @param packageName
    * @return javaProjectRoot + javaProjectSourceRootPrefix + "/" + packageName.replaceAll("[.]", "/")
    */
   private static String getJavaProjectPackagePath(String javaProjectRootPath, String javaProjectSourceRootPrefix, String packageName)
   {
      return javaProjectRootPath + javaProjectSourceRootPrefix + "/" + packageName.replaceAll("[.]", "/");
   }
   
   /**
    * Return word until first point like "ClassName" on file name "ClassName.java"
    * @param fileName
    * @return
    */
   public static String getClassNameOnFileName(String fileName)
   {
      if (fileName != null)
         return fileName.substring(0, fileName.indexOf("."));
         
      return null;
   }
   
}
