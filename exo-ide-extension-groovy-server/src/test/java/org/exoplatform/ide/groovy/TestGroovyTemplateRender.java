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
package org.exoplatform.ide.groovy;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.exoplatform.services.test.mock.MockHttpServletResponse;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestGroovyTemplateRender extends Base
{
   private Node testGroovyDeploy;
   
   private Node scriptFile;

   private Node script;
   
  private static String GTMPL = "<html><body><% import org.exoplatform.services.security.Identity\n"                                                                                                                                                           
                                   + " import org.exoplatform.services.security.ConversationState\n "
                                   + " ConversationState curentState = ConversationState.getCurrent();\n"                                                                                                                                        
                                   + " if (curentState != null){ Identity identity = curentState.getIdentity();\n"
                                   + " 3.times { println \"Hello \" + identity.getUserId()}}%><br></body></html>";  
   

   private SecurityContext adminSecurityContext;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      resourceNumber = binder.getSize();
      testGroovyDeploy = root.addNode("testRoot", "nt:unstructured");
      scriptFile = testGroovyDeploy.addNode("script", "nt:file");
      script = scriptFile.addNode("jcr:content", "nt:resource");
      script.setProperty("jcr:mimeType", "application/x-chromattic+groovy");
      script.setProperty("jcr:lastModified", Calendar.getInstance());
      script
         .setProperty("jcr:data", GTMPL);
      session.save();
      SessionProviderService sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      sessionProviderService.setSessionProvider(null, new SessionProvider(new ConversationState(new Identity("root"))));
      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");
      adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);
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
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(cres.getEntity().toString().contains("Hello root"));
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
      ContainerResponse cres = launcher.service("GET", "/ide/gtmpl/render?url=/jcr/db1/ws/testRoot/script", "", headers, GTMPL.getBytes(), null, ctx);
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertTrue(cres.getEntity().toString().contains("Hello root"));
   }
}
