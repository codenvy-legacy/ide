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
import static org.junit.Assert.assertNotNull;

import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
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
public class KeyServiceTest extends BaseTest
{

   private static String WORKSPACE = "dev-monit";

   private SessionImpl session;

   private RepositoryImpl repository;

   private CredentialsImpl credentials;

   private RepositoryService repositoryService;

   private MultivaluedMap<String, String> headers;

   @Before
   public void prepare() throws Exception
   {
      credentials = new CredentialsImpl("root", "exo".toCharArray());

      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      repository = (RepositoryImpl)repositoryService.getDefaultRepository();
      session = (SessionImpl)repository.login(credentials, WORKSPACE);

      SessionProviderService sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      assertNotNull(sessionProviderService);

      sessionProviderService
         .setSessionProvider(null, new SessionProvider(new ConversationState(new Identity("admin"))));

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

      headers = new MultivaluedMapImpl();
      headers.putSingle("content-type", "multipart/form-data; boundary=-----abcdef");
   }

   @Test
   public void testAddPrivateKey() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      DummySecurityContext securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      ctx.put(SecurityContext.class, securityContext);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintWriter w = new PrintWriter(out);

      InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream("test.txt");
      byte[] res = new byte[resourceAsStream.available()];
      resourceAsStream.read(res);
      resourceAsStream.close();     
      String source =
         getRequestSource("Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n", new String(res));

      w.write(source);
      w.flush();

      byte[] data = out.toByteArray();

      HttpServletRequest httpRequest =
         new MockHttpServletRequest(new ByteArrayInputStream(data), data.length, "POST", headers);
      ctx.put(HttpServletRequest.class, httpRequest);

      ContainerResponse response =
         launcher.service("POST", "/ide/ssh-keys/add?host=exoplatform.com", "http://localhost", headers, data, null,
            ctx);

      assertEquals(HTTPStatus.OK, response.getStatus());
      assertEquals("Success", response.getEntity());

      session.refresh(false);
   }

   private String getRequestSource(String fileContentDisposition, String content)
   {
      String source =
         "-------abcdef\r\n" + fileContentDisposition + "Content-Type: text/plain\r\n\r\n" + content+"\r\n"
            + "-------abcdef--\r\n";

      return source;
   }

}
