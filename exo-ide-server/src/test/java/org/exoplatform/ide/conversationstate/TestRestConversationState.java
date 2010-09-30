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
package org.exoplatform.ide.conversationstate;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import junit.framework.TestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestRestConversationState extends TestCase
{
   /**
     * Class logger.
     */
   private final Log log = ExoLogger.getLogger(TestRestConversationState.class);

   protected StandaloneContainer container;
   
   public ResourceLauncher launcher;
   
   private SecurityContext securityContext;
   
   @Before
   public void setUp() throws Exception
   {
      String containerConf = TestRestConversationState.class.getResource("/conf/standalone/test-configuration.xml").toString();

      StandaloneContainer.addConfigurationURL(containerConf);

      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
            .getResource("login.conf").toString());

      RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      launcher = new ResourceLauncher(handler);
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
   public void testWhoami() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres =
         launcher.service("POST", "/ide/conversation-state/whoami", "", headers, null, null, ctx);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertNotNull(cres.getEntity());
      assertTrue(cres.getEntity() instanceof IdeUser);
      IdeUser user = (IdeUser)cres.getEntity();
      assertEquals("root", user.getUserId());
      assertTrue(user.getRoles().contains("users"));
      assertTrue(user.getRoles().contains("administrators"));
      assertEquals(2, user.getRoles().size());
   }
}
