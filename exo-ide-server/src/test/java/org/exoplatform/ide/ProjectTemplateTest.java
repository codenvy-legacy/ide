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
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 20, 2011 evgen $
 *
 */
public class ProjectTemplateTest extends BaseTest
{
   private SecurityContext securityContext;

   private VirtualFileSystem vfs;

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
      
      VirtualFileSystemRegistry vfsRegistry =
         (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
      vfs = vfsRegistry.getProvider("dev-monit").newInstance(null);
      
      ItemList<Item> children = vfs.getChildren(vfs.getInfo().getRoot().getId(), -1, 0, PropertyFilter.ALL_FILTER);
      for(Item i : children.getItems())
      {
         vfs.delete(i.getId(), null);
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetProjectTemplate() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ContainerResponse cres = launcher.service("GET", "/ide/templates/project/list", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Assert.assertNotNull(cres.getEntity());
      List<ProjectTemplate> templates = (List<ProjectTemplate>)cres.getEntity();
      Assert.assertEquals(1, templates.size());
      ProjectTemplate proj = templates.get(0);
      Assert.assertEquals("SpringDemoProject", proj.getName());
      Assert.assertEquals("spring", proj.getType());
      Assert.assertEquals("Demo Spring Project", proj.getDescription());
   }

   @Test
   public void testCreateProjectFromTempalte() throws Exception
   {
      Set<String> userRoles = new HashSet<String>();
      userRoles.add("users");
      securityContext = new DummySecurityContext(new MockPrincipal("root"), userRoles);
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, securityContext);
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      String rootId = vfs.getInfo().getRoot().getId();
      ContainerResponse cres =
         launcher.service("POST", "/ide/templates/project/create?vfsid=dev-monit&name=testProj&parentId=" + rootId
            + "&templateName=SpringDemoProject", "", headers, null, null, ctx);
      Assert.assertEquals(200, cres.getStatus());
      Item item = (Item)cres.getEntity();
      Assert.assertTrue(item instanceof Project);
      Project p = (Project)item;
      Assert.assertEquals("spring", p.getProjectType());
   }
}
