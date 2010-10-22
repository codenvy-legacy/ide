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

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.ide.vfs.webdav.command.propfind.PropFindResponseEntity;
import org.exoplatform.services.jcr.access.AccessControlEntry;
import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.jcr.webdav.Depth;
import org.exoplatform.services.jcr.webdav.command.PropFindCommand;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
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

public class TestVFSWebDavServer extends BaseStandaloneTest
{

   private final Log log = ExoLogger.getLogger(TestVFSWebDavServer.class);

   protected Node testPropFind;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();

      System.out.println("root node >>> " + root);

      testPropFind = root.addNode("TestPropFind", "nt:folder");
      testPropFind.getSession().save();
      testPropFind.addMixin("exo:privilegeable");
      testPropFind.getSession().save();
      testPropFind.addMixin("exo:owneable");
      testPropFind.getSession().save();
      
      NodeImpl node = (NodeImpl)testPropFind;
      node.setPermission("vetal", new String []{"read"});
      node.setPermission("vetal", new String []{"add_node"});
      node.setPermission("vetal", new String []{"set_property"});
      node.setPermission("vetal", new String []{"remove"});
      node.getSession().save();
      
   }

   @Test
   public void testTest() throws Exception
   {
      
      NodeImpl node = (NodeImpl)testPropFind;

      System.out.println("--------------------------------");
      PropertyIterator pi = node.getProperties();
      while (pi.hasNext())
      {
         Property p = pi.nextProperty();
         System.out.println("property >> " + p.getName());
      }
      
      for (String m : node.getMixinTypeNames()) {
         System.out.println("mixin > " + m);
      }
      

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "1");
      //      headers.putSingle("Content-type", "script/groovy");
      //      headers.putSingle("location", "/jcr/db1/ws/testRoot2/scriptFileAutoload");
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
      		"<D:propfind xmlns:D=\"DAV:\">" +
      		   "<D:prop>" +
      		      "<D:owner/>" +
      		      "<D:acl/>" +
      		   "</D:prop>" +
      		"</D:propfind>";
      
      ContainerResponse cres = launcher.service("PROPFIND", "/ide-vfs-webdav/db1/ws/" + root.getName(), "", headers, request.getBytes(), null, ctx);

      System.out.println("RESPONSE STATUS >>>>> " + cres.getStatus());
      System.out.println("RESPONSE > " + cres.getEntity().toString());
      
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      if (cres.getEntity() instanceof PropFindResponseEntity) {
         PropFindResponseEntity propFindResponse = (PropFindResponseEntity)cres.getEntity();
         propFindResponse.write(outputStream);
         
         String s = new String(outputStream.toByteArray());
         s = s.replaceAll(">", ">\r\n");
         System.out.println("> " + s);
      }

      ///assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      //assertEquals(resourceNumber, binder.getSize());

   }

   public void sstestVFSWebDavServer() throws Exception
   {

      if (true)
      {
         return;
      }

      log.info("YAYAY!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

      //String path = testPropFind.getPath() + "/testPropfindComplexContent";            
      String path = testPropFind.getPath();

      NodeImpl node = (NodeImpl)testPropFind;

      System.out.println("--------------------------------");
      PropertyIterator pi = node.getProperties();
      while (pi.hasNext())
      {
         Property p = pi.nextProperty();
         System.out.println("property >> " + p.getName());
      }

      for (String s : ((NodeImpl)session.getRootNode()).getMixinTypeNames())
      {
         System.out.println("mixin > " + s);
      }

      System.out.println("node acl > " + node.getACL());

      AccessControlList acl = node.getACL();

      System.out.println("node owner > " + acl.getOwner());
      List<AccessControlEntry> entries = acl.getPermissionEntries();
      for (AccessControlEntry entry : entries)
      {
         System.out.println("ACL --------------------------");
         System.out.println("identity > " + entry.getIdentity());
         System.out.println("permissions > " + entry.getPermission());
      }

      // test
      HierarchicalProperty body = new HierarchicalProperty("D:propfind", null, "DAV:");

      HierarchicalProperty prop = new HierarchicalProperty("D:prop", null, "DAV:");
      body.addChild(prop);

      prop.addChild(new HierarchicalProperty("D:owner", null, "DAV:"));
      prop.addChild(new HierarchicalProperty("D:displayname", null, "DAV:"));
      prop.addChild(new HierarchicalProperty("D:resourcetype", null, "DAV:"));
      prop.addChild(new HierarchicalProperty("D:acl", null, "DAV:"));

      Response resp = new PropFindCommand().propfind(session, path, body, Depth.INFINITY_VALUE, "http://localhost");

      System.out.println("response status >> " + resp.getStatus());

      ByteArrayOutputStream bas = new ByteArrayOutputStream();
      ((PropFindResponseEntity)resp.getEntity()).write(bas);
      System.out.println(">>>>>>>>>>RESSSSSSSSSSSP>>>>>>>>>>>>>>> " + new String(bas.toByteArray()));
   }

   @Override
   protected String getRepositoryName()
   {
      return null;
   }

}
