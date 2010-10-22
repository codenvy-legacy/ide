/**
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
 *
 */

package org.exoplatform.ide.vfs.webdav;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestAclCommand extends BaseStandaloneTest
{
   
   @Before
   public void setUp() throws Exception
   {
      super.setUp();      
   }
   
   //@Test
   public void aaatestSetACLOnNonPrivilegableResource() throws Exception {
      NodeImpl testNode = (NodeImpl)root.addNode("test_set_acl_node", "nt:folder");
      session.save();      
      
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
         "<D:acl xmlns:D=\"DAV:\">" +
           "<D:ace>" +
             "<D:principal>" +
               "<D:href>vetal</D:href>" +
             "</D:principal>" +
             "<D:grant>" +
               "<D:privilege><D:read/></D:privilege>" +
             "</D:grant>" +
           "</D:ace>" + 
           "<D:ace>" +
             "<D:principal>" +
               "<D:href>jeka</D:href>" +
             "</D:principal>" +
             "<D:grant>" +
               "<D:privilege><D:write/></D:privilege>" +
             "</D:grant>" +
           "</D:ace>" + 
           "<D:ace>" +
             "<D:principal>" +
               "<D:all />" +
             "</D:principal>" +
             "<D:grant>" +
               "<D:privilege><D:all/></D:privilege>" +
             "</D:grant>" +
           "</D:ace>" + 
         "</D:acl>";

      ContainerResponse response =
         launcher.service("ACL", "/ide-vfs-webdav/db1/ws" + testNode.getPath(), "http://localhost", headers, request.getBytes(), null, ctx);
      
      System.out.println("set ACL status > " + response.getStatus());      
   }
   
   @Test
   public void testSetACLOnPrivilegableResource() throws Exception {
      NodeImpl testNode = (NodeImpl)root.addNode("test_set_acl_node2", "nt:folder");
      session.save();      
      
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
         "<D:acl xmlns:D=\"DAV:\">" +
           "<D:ace>" +
             "<D:principal>" +
               "<D:href>Anya</D:href>" +
             "</D:principal>" +
             "<D:grant>" +
               "<D:privilege><D:write/></D:privilege>" +
             "</D:grant>" +
           "</D:ace>" + 
           "<D:ace>" +
             "<D:principal>" +
               "<D:href>Oksana</D:href>" +
             "</D:principal>" +
             "<D:grant>" +
               "<D:privilege><D:write/></D:privilege>" +
             "</D:grant>" +
           "</D:ace>" + 
         "</D:acl>";

      ContainerResponse response =
         launcher.service("ACL", "/ide-vfs-webdav/db1/ws" + testNode.getPath(), "http://localhost", headers, request.getBytes(), null, ctx);
      
      System.out.println("set ACL status > " + response.getStatus());            
   }

   @Override
   protected String getRepositoryName()
   {
      return null;
   }

}
