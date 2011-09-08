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
package org.exoplatform.ide;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.DummySecurityContext;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.commons.utils.IOUtil;
import org.exoplatform.ide.conversationstate.IdeUser;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;



/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 24, 2011 evgen $
 *
 */
public class ConfigurationServiceTest extends BaseTest
{

   private SessionProviderService sessionProviderService;

   private RepositoryService repositoryService;

   private SecurityContext securityContext;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      ManageableRepository repository = repositoryService.getDefaultRepository();
      sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      ConversationState state = new ConversationState(new Identity("root"));
      SessionProvider sessionProvider = new SessionProvider(state);
      ConversationState.setCurrent(state);
      sessionProvider.setCurrentRepository(repository);
      sessionProviderService.setSessionProvider(null, sessionProvider);

      Authenticator authr = (Authenticator)container.getComponentInstanceOfType(Authenticator.class);
      String validUser =
         authr.validateUser(new Credential[]{new UsernameCredential("root"), new PasswordCredential("exo")});
      Identity id = authr.createIdentity(validUser);
      Set<String> roles = new HashSet<String>();
      roles.add("users");
      roles.add("administrators");
      id.setRoles(roles);
      ConversationState s = new ConversationState(id);
      ConversationState.setCurrent(s);
   }

   
   @Test
   public void appConfiguration() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres =
         launcher.service("GET", "/ide/configuration/init", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());

      Assert.assertNotNull(cres.getEntity());
      Map<String, Object> entity = (Map<String, Object>)cres.getEntity();
      Assert.assertTrue(entity.containsKey("defaultEntrypoint"));
      Assert.assertTrue(entity.containsKey("user"));
      Assert.assertTrue(entity.containsKey("discoverable"));
      Assert.assertTrue(entity.containsKey("userSettings"));
   }

   
   @Test
   public void whoami() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres =
         launcher.service("GET", "/ide/configuration/init", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Assert.assertNotNull(cres.getEntity());
      @SuppressWarnings("unchecked")
      Map<String, Object> entity = (Map<String, Object>)cres.getEntity();
      Assert.assertTrue(entity.containsKey("user"));
      Assert.assertTrue(entity.get("user") instanceof IdeUser);
      IdeUser user = (IdeUser)entity.get("user");
      Assert.assertEquals("root", user.getUserId());
      Assert.assertTrue(user.getRoles().contains("users"));
      Assert.assertTrue(user.getRoles().contains("administrators"));
      Assert.assertEquals(2, user.getRoles().size());
   }

   @Test
   public void entryPoint() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres =
         launcher.service("GET", "/ide/configuration/init", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());

      Assert.assertNotNull(cres.getEntity());
      @SuppressWarnings("unchecked")
      Map<String, Object> entity = (Map<String, Object>)cres.getEntity();
      Assert.assertTrue(entity.containsKey("defaultEntrypoint"));
      Assert.assertTrue(entity.containsKey("discoverable"));
   }
   
   @Test
   public void setConfiguration() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("userSettings.js");
      ContainerResponse cres =
         launcher.service("PUT", "/ide/configuration", "", headers, IOUtil.getStreamContentAsBytes(stream), null, ctx);
      Assert.assertEquals(204, cres.getStatus());
   }

   
   public void getConfiguration() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres = launcher.service("GET", "/ide/configuration", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());

      Assert.assertNotNull(cres.getEntity());
   }
   
   @Test
   public void userConfiguration() throws Exception
   {
      
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("userSettings.js");
      ContainerResponse cres =
         launcher.service("PUT", "/ide/configuration", "", headers, IOUtil.getStreamContentAsBytes(stream), null, ctx);
      Assert.assertEquals(204, cres.getStatus());
      
      cres =
         launcher.service("GET", "/ide/configuration/init", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Assert.assertNotNull(cres.getEntity());
      @SuppressWarnings("unchecked")
      Map<String, Object> entity = (Map<String, Object>)cres.getEntity();
      Assert.assertNotNull(entity.get("userSettings"));
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void getExistingUserSettings() throws Exception
   {
      
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/configuration/init", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Assert.assertNotNull(cres.getEntity());
      Map<String, Object> entity = (Map<String, Object>)cres.getEntity();
      Assert.assertNotNull(entity.get("userSettings"));
      Map<String, Object> userSettingsMap = (Map<String, Object>)entity.get("userSettings");
      Assert.assertNotNull(userSettingsMap.get("hotkeys"));
      Assert.assertNotNull(userSettingsMap.get("toolbar-items"));
      Assert.assertNotNull(userSettingsMap.get("default-editors"));
   }

}
