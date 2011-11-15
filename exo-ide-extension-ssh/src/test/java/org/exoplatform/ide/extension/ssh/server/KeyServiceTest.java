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
package org.exoplatform.ide.extension.ssh.server;

import static org.junit.Assert.assertEquals;

import org.everrest.core.RequestHandler;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.DummySecurityContext;
import org.everrest.core.tools.ResourceLauncher;
import org.everrest.test.mock.MockHttpServletRequest;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class KeyServiceTest
{
   private ResourceLauncher launcher;

   @Before
   public void setUp() throws Exception
   {
      String containerConf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      StandaloneContainer.setConfigurationURL(containerConf);
      StandaloneContainer container = StandaloneContainer.getInstance();
      if (System.getProperty("java.security.auth.login.config") == null)
      {
         System.setProperty("java.security.auth.login.config", //
            Thread.currentThread().getContextClassLoader().getResource("login.conf").toString());
      }
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
   public void testAddPrivateKey() throws Exception
   {
      InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.txt");
      byte[] res = new byte[file.available()];
      file.read(res);
      file.close();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      out.write("-------abcdef\r\nContent-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n\r\n"
         .getBytes());
      out.write("\r\n-------abcdef--".getBytes());
      byte[] data = out.toByteArray();

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("content-type", "multipart/form-data; boundary=-----abcdef");

      EnvironmentContext ctx = new EnvironmentContext();
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      DummySecurityContext securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      ctx.put(SecurityContext.class, securityContext);
      HttpServletRequest httpRequest =
         new MockHttpServletRequest("http://localhost/ide/ssh-keys/add?host=exoplatform.com", new ByteArrayInputStream(
            data), data.length, "POST", headers);
      ctx.put(HttpServletRequest.class, httpRequest);

      ContainerResponse response =
         launcher.service("POST", "http://localhost/ide/ssh-keys/add?host=exoplatform.com", "http://localhost",
            headers, data, null, ctx);

      assertEquals(200, response.getStatus());
      assertEquals("", response.getEntity());
   }
}
