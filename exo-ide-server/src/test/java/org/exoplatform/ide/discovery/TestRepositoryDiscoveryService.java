/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.discovery;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.junit.Test;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TestRepositoryDiscoveryService extends BaseTest
{
   
   @Test
   public void testDefaultEntryPoint() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/discovery/defaultEntrypoint", "", headers, null, null, ctx);
      
      assertEquals(HTTPStatus.OK, cres.getStatus());
      assertNotNull(cres.getEntity());
      assertTrue(cres.getEntity() instanceof String);
      String defaultEntryPoint = (String)cres.getEntity();
      assertEquals("/rest/private/jcr/repository/dev-monit", defaultEntryPoint);
   }
   
   @Test
   public void testEntryPoints() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/discovery/entrypoints", "", headers, null, null, ctx);
      
      assertEquals(HTTPStatus.OK, cres.getStatus());
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
