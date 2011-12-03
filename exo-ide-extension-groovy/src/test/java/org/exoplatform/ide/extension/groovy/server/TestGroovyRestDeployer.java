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

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.DummySecurityContext;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestGroovyRestDeployer extends Base
{

   private Node testRoot;

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
      testRoot = root.addNode("testRoot", "nt:unstructured");
      scriptFile = (NodeImpl)testRoot.addNode("script", "nt:file");
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
   public void deploy() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      String path = "/ide/groovy/deploy" // 
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptFile).getIdentifier() //
         + "&projectid=" + ((ExtendedNode)testRoot).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(204, cres.getStatus());
      Assert.assertEquals(resourceNumber + 1, binder.getSize());
   }

   @Test
   public void undeploy() throws Exception
   {
      Assert.assertEquals(resourceNumber, binder.getSize());
      putAutoladedService();
      Assert.assertEquals(resourceNumber + 1, binder.getSize());
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      String path = "/ide/groovy/undeploy" //
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptAutolad).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      System.out.println("TestGroovyRestDeployer.undeploy() " + cres.getEntity());
      Assert.assertEquals(204, cres.getStatus());
      Assert.assertEquals(resourceNumber, binder.getSize());
   }

   @Test
   public void undeployNotAdmin() throws Exception
   {
      Assert.assertEquals(resourceNumber, binder.getSize());
      putAutoladedService();
      Assert.assertEquals(resourceNumber + 1, binder.getSize());
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      String path = "/ide/groovy/undeploy" //
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptAutolad).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(403, cres.getStatus());
      Assert.assertEquals(resourceNumber + 1, binder.getSize());
   }

   @Test
   public void deployWithOtherUserAccess() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      String path = "/ide/groovy/deploy" // 
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptFile).getIdentifier() //
         + "&projectid=" + ((ExtendedNode)testRoot).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(403, cres.getStatus());
      Assert.assertEquals(resourceNumber, binder.getSize());
   }

   @Test
   public void deployNotAdmin() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      Set<String> roles = new HashSet<String>();
      roles.add("developers");
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      String path = "/ide/groovy/deploy" // 
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptFile).getIdentifier() //
         + "&projectid=" + ((ExtendedNode)testRoot).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(403, cres.getStatus());
      Assert.assertEquals(resourceNumber, binder.getSize());

   }

   @Test
   public void deploySandbox() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      String path = "/ide/groovy/deploy-sandbox" // 
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptFile).getIdentifier() //
         + "&projectid=" + ((ExtendedNode)testRoot).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(204, cres.getStatus());
      Assert.assertEquals(resourceNumber + 1, binder.getSize());
   }

   @Test
   public void deploySandboxAndUserAccess() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      String path = "/ide/groovy/deploy-sandbox" // 
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptFile).getIdentifier() //
         + "&projectid=" + ((ExtendedNode)testRoot).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(204, cres.getStatus());
      Assert.assertEquals(resourceNumber + 1, binder.getSize());
      cres = launcher.service("GET", "/test-groovy/groovy1/developers", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Assert.assertEquals("Hello from groovy to developers", cres.getEntity());
      EnvironmentContext ctx1 = new EnvironmentContext();
      ctx1.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres2 = launcher.service("GET", "/test-groovy/groovy1/root", "", headers, null, null, ctx1);
      Assert.assertEquals(404, cres2.getStatus());
   }

   @Test
   public void deploySandboxNotDev() throws IOException, Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      String path = "/ide/groovy/deploy-sandbox" // 
         + "?vfsid=ws" //
         + "&id=" + ((ExtendedNode)scriptFile).getIdentifier() //
         + "&projectid=" + ((ExtendedNode)testRoot).getIdentifier();
      ContainerResponse cres = launcher.service("POST", path, "", headers, null, null, ctx);
      Assert.assertEquals(403, cres.getStatus());
      Assert.assertEquals(resourceNumber, binder.getSize());
   }

   private void putAutoladedService() throws Exception
   {
      testGroovyDeployAutoload = root.addNode("testRootAutoload", "nt:unstructured");
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
