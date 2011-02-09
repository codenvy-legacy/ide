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
package org.exoplatform.ide.extension.groovy.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestGroovyRestDeployer extends Base
{

   private Node testGroovyDeploy;

   private Node testGroovyDeployAutoload;

   private Node scriptFile;

   private Node scriptFileAutoload;

   private Node script;

   private Node scriptAutolad;

   private SecurityContext adminSecurityContext;

   private SecurityContext devSecurityContext;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      resourceNumber = binder.getSize();
      testGroovyDeploy = root.addNode("testRoot", "nt:unstructured");
      scriptFile = testGroovyDeploy.addNode("script", "nt:file");
      script = scriptFile.addNode("jcr:content", "exo:groovyResourceContainer");
      script.setProperty("exo:autoload", false);
      script.setProperty("jcr:mimeType", "script/groovy");
      script.setProperty("jcr:lastModified", Calendar.getInstance());
      script
         .setProperty("jcr:data", Thread.currentThread().getContextClassLoader().getResourceAsStream("test1.groovy"));
      session.save();
      SessionProviderService sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      sessionProviderService.setSessionProvider(null, new SessionProvider(new ConversationState(new Identity("root"))));
      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");
      Set<String> devRoles = new HashSet<String>();
      devRoles.add("developers");
      adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);
      devSecurityContext = new DummySecurityContext(new MockPrincipal("dev"), devRoles);
   }

   @Test
   public void testUndeploy() throws Exception
   {
      assertEquals(resourceNumber, binder.getSize());
      putAutoladedService();
      assertEquals(resourceNumber + 1, binder.getSize());
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot2/scriptFileAutoload");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/groovy/undeploy", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      assertEquals(resourceNumber, binder.getSize());
   }

   @Test
   public void testUndeployNotAdmin() throws Exception
   {
      assertEquals(resourceNumber, binder.getSize());
      putAutoladedService();
      assertEquals(resourceNumber + 1, binder.getSize());
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot2/scriptFileAutoload");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/groovy/undeploy", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertEquals(resourceNumber + 1, binder.getSize());
   }

   @Test
   public void testDeploy() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot/script");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/groovy/deploy", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      assertEquals(resourceNumber + 1, binder.getSize());
   }

   @Test
   public void testDeployWithOtherUserAccess() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot/script");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/groovy/deploy", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      assertEquals(resourceNumber + 1, binder.getSize());
   }

   @Test
   public void testDeployNotAdmin() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot/script");
      Set<String> roles = new HashSet<String>();
      roles.add("developers");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/groovy/deploy", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertEquals(resourceNumber, binder.getSize());

   }

   @Test
   public void testDeploySandbox() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot/script");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ContainerResponse cres =
         launcher.service("POST", "/ide/groovy/deploy-sandbox", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      assertEquals(resourceNumber + 1, binder.getSize());
   }

//   @Test
//   public void testDeploySandboxAndUserAccess() throws IOException, Exception
//   {
//      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
//      headers.putSingle("Content-type", "script/groovy");
//      headers.putSingle("location", GroovyScriptService.WEBDAV_CONTEXT + "db1/ws/testRoot/script");
//      EnvironmentContext ctx = new EnvironmentContext();
//      ctx.put(SecurityContext.class, devSecurityContext);
//      ContainerResponse cres =
//         launcher.service("POST", "/ide/groovy/deploy-sandbox", "", headers, null, null, ctx);
//      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
//      assertEquals(resourceNumber + 1, binder.getSize());
//      cres = launcher.service("GET", "/test-groovy/groovy1/developers", "", headers, null, null, ctx);
//      assertEquals(HTTPStatus.OK, cres.getStatus());
//      assertEquals("Hello from groovy to developers", cres.getEntity());
//      EnvironmentContext ctx1 = new EnvironmentContext();
//      ctx1.put(SecurityContext.class, adminSecurityContext);
//      ContainerResponse cres2 = launcher.service("GET", "/test-groovy/groovy1/root", "", headers, null, null, ctx1);
//      assertEquals(HTTPStatus.NOT_FOUND, cres2.getStatus());
//   }

   @Test
   public void testDeploySandboxNotDev() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Content-type", "script/groovy");
      headers.putSingle("location", GroovyScriptServiceUtil.WEBDAV_CONTEXT + "db1/ws/testRoot/script");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres =
         launcher.service("POST", "/ide/groovy/deploy-sandbox", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.FORBIDDEN, cres.getStatus());
      assertEquals(resourceNumber, binder.getSize());
   }

   private void putAutoladedService() throws Exception
   {
      testGroovyDeployAutoload = root.addNode("testRoot2", "nt:unstructured");
      scriptFileAutoload = testGroovyDeployAutoload.addNode("scriptFileAutoload", "nt:file");
      scriptAutolad = scriptFileAutoload.addNode("jcr:content", "exo:groovyResourceContainer");
      scriptAutolad.setProperty("exo:autoload", true);
      scriptAutolad.setProperty("jcr:mimeType", "script/groovy");
      scriptAutolad.setProperty("jcr:lastModified", Calendar.getInstance());
      scriptAutolad.setProperty("jcr:data",
         Thread.currentThread().getContextClassLoader().getResourceAsStream("test2.groovy"));
      session.save();
      Thread.sleep(1000);
   }

   @After
   public void tearDown() throws Exception
   {
      binder.removeResource("groovy-test");
      binder.removeResource("test-groovy");
      super.tearDown();
   }

}
