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
import org.everrest.test.mock.MockHttpServletRequest;
import org.everrest.test.mock.MockHttpServletResponse;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TestGroovyTemplateRender extends Base
{
   private static String GTMPL = "<html><body><% import org.exoplatform.services.security.Identity\n"
      + " import org.exoplatform.services.security.ConversationState\n "
      + " ConversationState curentState = ConversationState.getCurrent();\n"
      + " if (curentState != null){ Identity identity = curentState.getIdentity();\n"
      + " 3.times { println \"Hello \" + identity.getUserId()}}%><br></body></html>";


   private DummySecurityContext adminSecurityContext;
   private File script;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      InputStream source = new ByteArrayInputStream(GTMPL.getBytes());
      script = virtualFileSystem.createFile(testRoot.getId(), "script1", new MediaType("application", "x-groovy"), source);
      source.close();
      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");
      adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);
      ConversationState.setCurrent(new ConversationState(new Identity(
         adminSecurityContext.getUserPrincipal().getName(),
         Collections.<MembershipEntry>emptySet(),
         adminSecurityContext.getUserRoles())));
   }

   @Test
   public void testRender() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.add(HttpHeaders.CONTENT_TYPE, "application/x-groovy+html");
      EnvironmentContext ctx = new EnvironmentContext();
      HttpServletRequest httpRequest =
         new MockHttpServletRequest("/", new ByteArrayInputStream(GTMPL.getBytes()), GTMPL.length(), "POST", headers);
      ctx.put(HttpServletRequest.class, httpRequest);
      HttpServletResponse httpServletResponse = new MockHttpServletResponse();
      ctx.put(HttpServletResponse.class, httpServletResponse);
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/gtmpl/render-source", "", headers, GTMPL.getBytes(), null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Assert.assertTrue(cres.getEntity().toString().contains("Hello root"));
   }

   @Test
   public void testRenderFromUrl() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      EnvironmentContext ctx = new EnvironmentContext();
      HttpServletRequest httpRequest =
         new MockHttpServletRequest("/", new ByteArrayInputStream(GTMPL.getBytes()), GTMPL.length(), "GET", headers);
      ctx.put(HttpServletRequest.class, httpRequest);
      HttpServletResponse httpServletResponse = new MockHttpServletResponse();
      ctx.put(HttpServletResponse.class, httpServletResponse);
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse response = launcher.service("GET", "/ide/gtmpl/render?vfsid=ws&id=" + script.getId(), "", headers, GTMPL.getBytes(), null, ctx);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity().toString().contains("Hello root"));
   }

   @After
   @Override
   public void tearDown() throws Exception
   {
      super.tearDown();
   }
}
