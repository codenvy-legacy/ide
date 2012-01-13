///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide;
//
//import org.everrest.core.impl.ContainerResponse;
//import org.everrest.core.impl.EnvironmentContext;
//import org.everrest.core.impl.MultivaluedMapImpl;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.List;
//
//import javax.ws.rs.core.MultivaluedMap;
//
///**
// * Created by The eXo Platform SAS.
// * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
// * @version $Id: $
//*/
//public class RepositoryDiscoveryServiceTest extends BaseTest
//{
//
///*   @Before
//   public void setUp() throws Exception
//   {
//      super.setUp();
//   }
//   
//   @Test
//   public void getDefaultEntryPoint() throws Exception
//   {
//      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
//      
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/discovery/defaultEntrypoint", "", headers, null, null, null);
//      
//      Assert.assertEquals(200, cres.getStatus());
//      Assert.assertNotNull(cres.getEntity());
//      Assert.assertTrue(cres.getEntity() instanceof HashMap<?, ?>);
//      @SuppressWarnings("unchecked")
//      HashMap<String, String> map = (HashMap<String, String>)cres.getEntity();
//      Assert.assertTrue(map.get("workspace").equals("dev-monit"));
//      Assert.assertTrue(map.get("href").equals("vfs/jcr/dev-monit"));
//   }
//   
//   
//   @Test
//   public void getEntryPoints() throws Exception
//   {
//      EnvironmentContext ctx = new EnvironmentContext();
//      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
//      
//      ContainerResponse cres =
//         launcher.service("GET", "/ide/discovery/entrypoints", "", headers, null, null, ctx);
//      
//      Assert.assertEquals(200, cres.getStatus());
//      Assert.assertNotNull(cres.getEntity());
//      Assert.assertTrue(cres.getEntity() instanceof List<?>);
//      List<?>entryPoints = (List<?>)cres.getEntity();
//      Assert.assertEquals(2, entryPoints.size());
//      for (Object obj : entryPoints)
//      {
//         Assert.assertTrue(obj instanceof HashMap<?, ?>);
//         @SuppressWarnings("unchecked")
//         HashMap<String, String> map = (HashMap<String, String>)obj;
//         Assert.assertTrue(map.get("workspace").equals("dev-monit") || map.get("workspace").equals("ws2"));
//         Assert.assertTrue(map.get("href").equals("vfs/jcr/dev-monit") || map.get("href").equals("vfs/jcr/ws2"));
//      }
//   }*/
// }
