/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
///*
// * CODENVY CONFIDENTIAL
// * __________________
// *
// * [2012] - [2013] Codenvy, S.A.
// * All Rights Reserved.
// *
// * NOTICE:  All information contained herein is, and remains
// * the property of Codenvy S.A. and its suppliers,
// * if any.  The intellectual and technical concepts contained
// * herein are proprietary to Codenvy S.A.
// * and its suppliers and may be covered by U.S. and Foreign Patents,
// * patents in process, and are protected by trade secret or copyright law.
// * Dissemination of this information or reproduction of this material
// * is strictly forbidden unless prior written permission is obtained
// * from Codenvy S.A..
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
// *
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
