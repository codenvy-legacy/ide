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
import org.exoplatform.ide.discovery.EntryPoint;
import org.exoplatform.ide.discovery.RepositoryDiscoveryService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class RepositoryDiscoveryServiceTest extends BaseTest
{
   private RepositoryService repositoryService;
   
   private String repoName;
   
   
   public void setUp() throws Exception
   {
      super.setUp();
      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      ManageableRepository repository = repositoryService.getDefaultRepository();
      repoName = repository.getConfiguration().getName();
      repositoryService.setCurrentRepositoryName(repoName);
      
   }
   
   
   
   public void testDefaultEntryPoint() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/discovery/defaultEntrypoint", "", headers, null, null, null);
      
      assertEquals(200, cres.getStatus());
      assertNotNull(cres.getEntity());
      assertTrue(cres.getEntity() instanceof String);
      String defaultEntryPoint = (String)cres.getEntity();
      System.out.println("TestRepositoryDiscoveryService.testDefaultEntryPoint()" + defaultEntryPoint);
      assertEquals(RepositoryDiscoveryService.getWebDavConetxt() + "/" + repoName + "/dev-monit/", defaultEntryPoint);
   }
   
   
   
   public void testEntryPoints() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/discovery/entrypoints", "", headers, null, null, ctx);
      
      assertEquals(200, cres.getStatus());
      assertNotNull(cres.getEntity());
      assertTrue(cres.getEntity() instanceof List<?>);
      List<?>entryPoints = (List<?>)cres.getEntity();
      assertEquals(2, entryPoints.size());
      for (Object obj : entryPoints)
      {
         assertTrue(obj instanceof EntryPoint);
      }
   }
}
