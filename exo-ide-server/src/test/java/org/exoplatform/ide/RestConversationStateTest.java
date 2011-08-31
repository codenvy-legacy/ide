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
package org.exoplatform.ide;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.DummySecurityContext;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.ide.conversationstate.IdeUser;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class RestConversationStateTest extends BaseTest
{
   
   private SecurityContext securityContext;
   
   @Before
   public void setUp() throws Exception
   {
      super.setUp();
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
      assertEquals(200, cres.getStatus());
      assertNotNull(cres.getEntity());
      assertTrue(cres.getEntity() instanceof IdeUser);
      IdeUser user = (IdeUser)cres.getEntity();
      assertEquals("root", user.getUserId());
      assertTrue(user.getRoles().contains("users"));
      assertTrue(user.getRoles().contains("administrators"));
      assertEquals(2, user.getRoles().size());
   }
}
