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
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.junit.Test;

import javax.jcr.Node;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ClassInfoStorageTest extends Base
{
  
   private String jar;
   
   private String javaHome;
   
   
   
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      jar = "src/test/resources/jsr311-api-1.0.jar";
      javaHome = System.getProperty("java.home");
      String fileSeparator = System.getProperty("file.separator");
      javaHome = javaHome.substring(0,javaHome.lastIndexOf(fileSeparator)+1) + "src.zip";
   }

   @Test
   public void testAddClassesInfoFormJar() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/jar?jar-path=" + jar, "", null, null, null, ctx);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(root.hasNode("classpath/javax/javax.ws/javax.ws.rs/javax.ws.rs.POST"));
      assertTrue(root.hasNode("classpath/javax/javax.ws/javax.ws.rs/javax.ws.rs.GET"));
      assertTrue(root.hasNode("classpath/javax/javax.ws/javax.ws.rs/javax.ws.rs.ext/javax.ws.rs.ext.MessageBodyReader"));
      
   }
   
   @Test
   public void testAddClassesInfoFormJarPkg() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/jar?jar-path=" + jar + "&package=" + "javax.ws.rs.ext", "", null, null, null, ctx);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertFalse(root.hasNode("classpath/javax/javax.ws/javax.ws.rs/javax.ws.rs.POST"));
      assertFalse(root.hasNode("classpath/javax/javax.ws/javax.ws.rs/javax.ws.rs.GET"));
      assertTrue(root.hasNode("classpath/javax/javax.ws/javax.ws.rs/javax.ws.rs.ext/javax.ws.rs.ext.MessageBodyReader"));
   }
   
   @Test
   public void testAddClassesInfoFormJarPkgForbidden() throws Exception
   {
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/jar?jar-path=" + jar + "&package=" + "org.exoplatform.services.jcr.ext.access", "", null, null, null, null);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertFalse(root.hasNode("classpath"));
   }
   
   
   @Test
   public void testAddClass() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/class?fqn=" + CodeAssistantImpl.class.getCanonicalName(), "", null, null, null, ctx);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      Node classpath = root.getNode("classpath");
      assertTrue(classpath.hasNode("org/org.exoplatform/org.exoplatform.ide/org.exoplatform.ide.groovy/org.exoplatform.ide.groovy.codeassistant"));
      assertFalse(classpath.hasNode("org/org.exoplatform/org.exoplatform.services/org.exoplatform.services.jcr"));
   }
   
   @Test
   public void testAddClassForbidden() throws Exception
   {
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/class?fqn=" + CodeAssistantImpl.class.getCanonicalName(), "", null, null, null, null);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertFalse(root.hasNode("classpath"));
   }
   
   
   @Test
   public void testAddClassesInfoFormJarForbidden() throws Exception
   {
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/jar?jar-path=" + jar, "", null, null, null, null);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertFalse(root.hasNode("classpath"));
   }
   
 
   @Test
   public void testAddClassesInfoFormJavaSrc() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/java?java-source-path=" + javaHome + "&package=" + "java.lang", "", null, null, null, ctx);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(root.hasNode("classpath"));
      assertTrue(root.hasNode("classpath/java/java.lang/java.lang.String"));
      assertFalse(root.hasNode("classpath/javax"));
   }
   
   @Test
   public void testAddClassesInfoFormJavaSrcForbidden() throws Exception
   {
      ContainerResponse cres = launcher.service("POST", "/ide/class-info-storage/java?java-source-path=" + javaHome + "&package=" + "java.lang", "", null, null, null, null);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertFalse(root.hasNode("classpath"));
   }

}
