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

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.xml.namespace.QName;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.ide.vfs.webdav.command.propfind.PropFindResponseEntity;
import org.exoplatform.ide.vfs.webdav.resource.property.ACLProperty;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ext.provider.HierarchicalPropertyEntityProvider;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.IdentityConstants;
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

public class TestAclProperty extends BaseStandaloneTest
{

   private final Log log = ExoLogger.getLogger(TestAclProperty.class);

   protected Node testPropFind;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();

      //      System.out.println("root node >>> " + root);
      //
      //      testPropFind = root.addNode("TestPropFind", "nt:folder");
      //      testPropFind.getSession().save();
      //      testPropFind.addMixin("exo:privilegeable");
      //      testPropFind.getSession().save();
      //      testPropFind.addMixin("exo:owneable");
      //      testPropFind.getSession().save();
      //      
      //      NodeImpl node = (NodeImpl)testPropFind;
      //      node.setPermission("vetal", new String []{"read"});
      //      node.setPermission("vetal", new String []{"add_node"});
      //      node.setPermission("vetal", new String []{"set_property"});
      //      node.setPermission("vetal", new String []{"remove"});
      //      node.getSession().save();
   }

   @Test
   public void testPermissionsOnRoot() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "0");
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:propfind xmlns:D=\"DAV:\">" + "<D:prop>" + "<D:owner/>"
            + "<D:acl/>" + "</D:prop>" + "</D:propfind>";

      ContainerResponse response =
         launcher.service("PROPFIND", "/ide-vfs-webdav/db1/ws/", "http://localhost", headers, request.getBytes(), null,
            ctx);

      assertEquals(HTTPStatus.MULTISTATUS, response.getStatus());
      assertNotNull(response.getEntity());

      HierarchicalPropertyEntityProvider provider = new HierarchicalPropertyEntityProvider();
      InputStream inputStream = Utils.getResponseAsStream(response);
      HierarchicalProperty multistatus = provider.readFrom(null, null, null, null, null, inputStream);

      assertEquals(new QName("DAV:", "multistatus"), multistatus.getName());
      assertEquals(1, multistatus.getChildren().size());

      HierarchicalProperty resourceProp = multistatus.getChildren().get(0);

      HierarchicalProperty resourceHref = resourceProp.getChild(new QName("DAV:", "href"));
      assertNotNull(resourceHref);
      assertEquals("http://localhost/ide-vfs-webdav/db1/ws/", resourceHref.getValue());

      HierarchicalProperty propstatProp = resourceProp.getChild(new QName("DAV:", "propstat"));
      HierarchicalProperty propProp = propstatProp.getChild(new QName("DAV:", "prop"));

      HierarchicalProperty ownerProp = propProp.getChild(new QName("DAV:", "owner"));
      HierarchicalProperty ownerHrefProp = ownerProp.getChild(new QName("DAV:", "href"));

      assertEquals("__system", ownerHrefProp.getValue());

      HierarchicalProperty aclProp = propProp.getChild(ACLProperty.NAME);
      assertEquals(1, aclProp.getChildren().size());

      HierarchicalProperty aceProp = aclProp.getChild(ACLProperty.ACE);
      assertEquals(2, aceProp.getChildren().size());

      HierarchicalProperty principalProp = aceProp.getChild(ACLProperty.PRINCIPAL);
      assertEquals(1, principalProp.getChildren().size());

      HierarchicalProperty allProp = principalProp.getChild(ACLProperty.ALL);
      assertNotNull(allProp);

      HierarchicalProperty grantProp = aceProp.getChild(ACLProperty.GRANT);
      assertEquals(2, grantProp.getChildren().size());

      HierarchicalProperty writeProp = grantProp.getChild(0).getChild(ACLProperty.WRITE);
      assertNotNull(writeProp);
      HierarchicalProperty readProp = grantProp.getChild(1).getChild(ACLProperty.READ);
      assertNotNull(readProp);
   }

   @Test
   public void testAclOnNode() throws Exception
   {

      NodeImpl testNode = (NodeImpl)root.addNode("test_acl_property", "nt:folder");
      session.save();

      testNode.addMixin("exo:owneable");
      testNode.addMixin("exo:privilegeable");
      session.save();

      Map<String, String[]> permissions = new HashMap<String, String[]>();

      String userName = "john";
      permissions.put(userName, PermissionType.ALL);

      testNode.setPermissions(permissions);
      testNode.getSession().save();

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("Depth", "1");
      headers.putSingle("Content-Type", "text/xml; charset=\"utf-8\"");
      
      EnvironmentContext ctx = new EnvironmentContext();

      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");

      DummySecurityContext adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);

      ctx.put(SecurityContext.class, adminSecurityContext);

      RequestHandlerImpl handler = (RequestHandlerImpl)container.getComponentInstanceOfType(RequestHandlerImpl.class);
      ResourceLauncher launcher = new ResourceLauncher(handler);

      String request =
         "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" + "<D:propfind xmlns:D=\"DAV:\">" + "<D:prop>" + "<D:owner/>"
            + "<D:acl/>" + "</D:prop>" + "</D:propfind>";

      ContainerResponse cres =
         launcher.service("PROPFIND", "/ide-vfs-webdav/db1/ws/" + root.getName() + "/" + testNode.getName(),
            "http://localhost", headers, request.getBytes(), null, ctx);

      assertEquals(HTTPStatus.MULTISTATUS, cres.getStatus());

      HierarchicalPropertyEntityProvider provider = new HierarchicalPropertyEntityProvider();
      InputStream inputStream = Utils.getResponseAsStream(cres);
      HierarchicalProperty multistatus = provider.readFrom(null, null, null, null, null, inputStream);

      assertEquals(new QName("DAV:", "multistatus"), multistatus.getName());
      assertEquals(1, multistatus.getChildren().size());

      HierarchicalProperty resourceProp = multistatus.getChildren().get(0);

      HierarchicalProperty resourceHref = resourceProp.getChild(new QName("DAV:", "href"));
      assertNotNull(resourceHref);
      assertEquals("http://localhost/ide-vfs-webdav/db1/ws/" + root.getName() + "/" + testNode.getName() + "/",
         resourceHref.getValue());

      HierarchicalProperty propstatProp = resourceProp.getChild(new QName("DAV:", "propstat"));
      HierarchicalProperty propProp = propstatProp.getChild(new QName("DAV:", "prop"));

      HierarchicalProperty aclProp = propProp.getChild(ACLProperty.NAME);
      assertEquals(1, aclProp.getChildren().size());

      HierarchicalProperty aceProp = aclProp.getChild(ACLProperty.ACE);
      assertEquals(2, aceProp.getChildren().size());

      HierarchicalProperty principalProp = aceProp.getChild(ACLProperty.PRINCIPAL);
      assertEquals(1, principalProp.getChildren().size());

      assertEquals(userName, principalProp.getChildren().get(0).getValue());

      HierarchicalProperty grantProp = aceProp.getChild(ACLProperty.GRANT);
      assertEquals(2, grantProp.getChildren().size());

      HierarchicalProperty writeProp = grantProp.getChild(0).getChild(ACLProperty.WRITE);
      assertNotNull(writeProp);
      HierarchicalProperty readProp = grantProp.getChild(1).getChild(ACLProperty.READ);
      assertNotNull(readProp);

   }


   @Override
   protected String getRepositoryName()
   {
      return null;
   }

}
