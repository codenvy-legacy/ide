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
import org.exoplatform.container.ConcurrentPicoContainer;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.picocontainer.ComponentAdapter;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TestGroovyRestDeployer extends Base
{
   private File script;

   private DummySecurityContext adminSecurityContext;

   private DummySecurityContext devSecurityContext;

   private ConcurrentPicoContainer restfulContainer;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream("test1.groovy");
      script =
         virtualFileSystem.createFile(testRoot.getId(), "script1", new MediaType("application", "x-groovy"), source);
      source.close();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");
      Set<String> devRoles = new HashSet<String>();
      devRoles.add("developers");

      adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);
      devSecurityContext = new DummySecurityContext(new MockPrincipal("dev"), devRoles);

      Provider provider = (Provider)container.getComponentInstance("RestfulContainerProvider");
      assertNotNull(provider);
      restfulContainer = (ConcurrentPicoContainer)provider.get();
      assertNotNull(container);

      // Register cleaner in container. It checks resources one time per second.
      ComponentCleaner cleaner = new ComponentCleaner(restfulContainer, 1, TimeUnit.SECONDS);
      cleaner.start();
      restfulContainer.registerComponentInstance("cleaner", cleaner);
   }

   @Test
   public void deploy() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(), Collections.<MembershipEntry> emptySet(),
         adminSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();

      int before = restfulContainer.getComponentAdapters().size();
      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(204, response.getStatus());

      // Check is resource deployed or not.
      int after = restfulContainer.getComponentAdapters().size();
      assertEquals(before + 1, after);
   }

   @Test
   public void undeploy() throws Exception
   {
      // Deploy resource.
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(), Collections.<MembershipEntry> emptySet(),
         adminSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();

      int before = restfulContainer.getComponentAdapters().size();

      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(204, response.getStatus());

      // Check is resource deployed or not.
      int after = restfulContainer.getComponentAdapters().size();
      assertEquals(before + 1, after);

      before = after;
      path = "/ide/groovy/undeploy" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId();

      response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(204, response.getStatus());

      // Resource must be removed.
      after = restfulContainer.getComponentAdapters().size();
      assertEquals(before - 1, after);
   }

   @Test
   public void validate() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(), Collections.<MembershipEntry> emptySet(),
         adminSecurityContext.getUserRoles())));
      String path = "/ide/groovy/validate-script" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&name=" + script.getName() //
         + "&projectid=" + testRoot.getId();

      ContentStream content = virtualFileSystem.getContent(script.getId());
      byte[] data = new byte[(int)content.getLength()];
      InputStream in = content.getStream();
      in.read(data);
      in.close();

      ContainerResponse response = launcher.service("POST", path, "", headers, data, null, ctx);
      assertEquals(204, response.getStatus());
   }

   @Test
   public void undeployNotAdmin() throws Exception
   {
      // Deploy resource as admin.
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(), Collections.<MembershipEntry> emptySet(),
         adminSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();

      int before = restfulContainer.getComponentAdapters().size();

      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(204, response.getStatus());

      // Check is resource deployed or not.
      int after = restfulContainer.getComponentAdapters().size();
      assertEquals(before + 1, after);

      before = after;

      // Try 'undeploy' resource as user with lower permissions (not admin).
      headers = new MultivaluedMapImpl();
      ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(devSecurityContext.getUserPrincipal().getName(),
         Collections.<MembershipEntry> emptySet(), devSecurityContext.getUserRoles())));
      path = "/ide/groovy/undeploy" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId();
      response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(403, response.getStatus());

      // Resources must be untouched.
      after = restfulContainer.getComponentAdapters().size();
      assertEquals(before, after);
   }

   @Test
   public void deployNotAdmin() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(devSecurityContext.getUserPrincipal().getName(),
         Collections.<MembershipEntry> emptySet(), devSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();

      int before = restfulContainer.getComponentAdapters().size();

      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(403, response.getStatus());

      int after = restfulContainer.getComponentAdapters().size();
      assertEquals(before, after);
   }

   @Test
   public void deploySandbox() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(devSecurityContext.getUserPrincipal().getName(),
         Collections.<MembershipEntry> emptySet(), devSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy-sandbox" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();

      int before = restfulContainer.getComponentAdapters().size();

      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(204, response.getStatus());

      // Check is resource deployed or not.
      int after = restfulContainer.getComponentAdapters().size();
      assertEquals(before + 1, after);
   }

   @Test
   public void deploySandboxAndUserAccess() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, devSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(devSecurityContext.getUserPrincipal().getName(),
         Collections.<MembershipEntry> emptySet(), devSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy-sandbox" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();

      int before = restfulContainer.getComponentAdapters().size();

      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(204, response.getStatus());

      int after = restfulContainer.getComponentAdapters().size();
      assertEquals(before + 1, after);

      response = launcher.service("GET", "/test-groovy/groovy1/developers", "", headers, null, null, ctx);
      assertEquals(200, response.getStatus());
      assertEquals("Hello from groovy to developers", response.getEntity());

      // Check access resource as different user (not who deploys resource).
      EnvironmentContext ctx1 = new EnvironmentContext();
      ctx1.put(SecurityContext.class, adminSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(), Collections.<MembershipEntry> emptySet(),
         adminSecurityContext.getUserRoles())));
      response = launcher.service("GET", "/test-groovy/groovy1/root", "", headers, null, null, ctx1);
      assertEquals(404, response.getStatus());
   }

   @Test
   public void deploySandboxNotDev() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(), Collections.<MembershipEntry> emptySet(),
         adminSecurityContext.getUserRoles())));
      String path = "/ide/groovy/deploy-sandbox" //
         + "?vfsid=" + virtualFileSystem.getInfo().getId() //
         + "&id=" + script.getId() //
         + "&projectid=" + testRoot.getId();
      ContainerResponse response = launcher.service("POST", path, "", headers, null, null, ctx);
      assertEquals(403, response.getStatus());
   }

   @Test
   public void resourceCleanerTest() throws Exception
   {
      GroovyComponentKey key = GroovyComponentKey.make("dev-monit", "some/resource");
      String userId = ConversationState.getCurrent().getIdentity().getUserId();
      long expired = System.currentTimeMillis() + 3000; // Alive time 3 seconds.
      key.setAttribute("ide.developer.id", userId);
      key.setAttribute("ide.expiration.date", expired);
      restfulContainer.registerComponentImplementation(key, CleanerTestResource.class);
      assertNotNull(restfulContainer.getComponentAdapter(key));
      Thread.sleep(2000); // wait 2 seconds
      assertNotNull(restfulContainer.getComponentAdapter(key)); // resource accessible
      Thread.sleep(3000); // wait 3 seconds more
      assertNull(restfulContainer.getComponentAdapter(key)); // must be removed
   }

   @Path("cleaner-test")
   public static class CleanerTestResource
   {
      @GET
      public void m()
      {
      }
   }

   @After
   public void tearDown() throws Exception
   {
      ComponentCleaner cleaner = (ComponentCleaner)restfulContainer.getComponentInstance("cleaner");
      cleaner.stop();
      Collection<ComponentAdapter> componentAdapters = restfulContainer.getComponentAdapters();
      for (Object c : componentAdapters.toArray(new ComponentAdapter[componentAdapters.size()]))
      {
         restfulContainer.unregisterComponent(((ComponentAdapter)c).getComponentKey());
      }
      super.tearDown();
   }
}
