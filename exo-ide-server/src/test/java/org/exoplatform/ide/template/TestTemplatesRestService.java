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
package org.exoplatform.ide.template;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.junit.Test;

import java.util.List;

import javax.jcr.Node;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Test for template rest service.
 * 
 * Tests the receiving of list of templates
 * and creating of project from template.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TestTemplatesRestService.java Apr 7, 2011 9:53:55 AM vereshchaka $
 *
 */
public class TestTemplatesRestService extends BaseTest
{
   private static String WORKSPACE = "dev-monit";
   
   private RepositoryService repositoryService;
   
   private String repoName;
   
   private CredentialsImpl credentials;
   
   private SessionImpl session;
   
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      ManageableRepository repository = repositoryService.getDefaultRepository();
      repoName = repository.getConfiguration().getName();
      repositoryService.setCurrentRepositoryName(repoName);
      credentials = new CredentialsImpl("root", "exo".toCharArray());
      session = (SessionImpl)repository.login(credentials, WORKSPACE);
   }
   
   /**
    * Test for gettings list of templates (project of file templates).
    * 
    * @throws Exception
    */
   @Test
   public void testGetListOfTemplates() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      headers.add("type", "project");
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/templates/list", "", headers, null, null, null);
      
      assertEquals(HTTPStatus.OK, cres.getStatus());
      
      assertNotNull(cres.getEntity());
      
      assertTrue(cres.getEntity() instanceof List<?>);
      
      List<?>templates = (List<?>)cres.getEntity();
      assertEquals(3, templates.size());
      for (Object obj : templates)
      {
         assertTrue(obj instanceof String);
      }
      
   }
   
   /**
    * Test for creation sample project form template.
    * 
    * @throws Exception
    */
   @Test
   public void testCreateProjectFromTemplate() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      headers.add("template-name", "linkedin-contacts-project");
      
      headers.add("type", "project");
      
      headers.add("location", "http://localhost/jcr/db1/dev-monit/linkedin");
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/templates/create", "http://localhost", headers, null, null, null);
      
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      
      Node rootNode = session.getRootNode();
      assertTrue(rootNode.hasNode("linkedin"));
      Node linkedinNode = rootNode.getNode("linkedin");
      assertTrue(linkedinNode.hasNode("lib"));
      assertTrue(linkedinNode.hasNode("logic"));
      assertTrue(linkedinNode.hasNode("skin"));
      assertTrue(linkedinNode.hasNode("cache.manifest"));
      assertTrue(linkedinNode.hasNode("mobile.html"));
      assertTrue(linkedinNode.hasNode("readme-linkedin-contacts.txt"));
      Node libNode = linkedinNode.getNode("lib");
      assertTrue(libNode.hasNode("jqtouch.min.css"));
      assertTrue(libNode.hasNode("jqtouch.min.js"));
      assertTrue(libNode.hasNode("jquery.1.4.2.min.js"));
      assertTrue(libNode.hasNode("linkedin.js"));
      assertTrue(libNode.hasNode("template.js"));
      Node logicNode = linkedinNode.getNode("logic");
      assertTrue(logicNode.hasNode("contacts.js"));
      assertTrue(logicNode.hasNode("offline.js"));
      Node skinNode = linkedinNode.getNode("skin");
      assertTrue(skinNode.hasNode("exomobile.css"));
   }
   
   /**
    * Test for creation file from template.
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFileFromTemplate() throws Exception
   {
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      
      headers.add("template-name", "SampleChartWidget.html");
      
      headers.add("type", "file");
      
      headers.add("location", "http://localhost/jcr/db1/dev-monit/sample.html");
      
      ContainerResponse cres =
         launcher.service("GET", "/ide/templates/create", "http://localhost", headers, null, null, null);
      
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      
      Node rootNode = session.getRootNode();
      assertTrue(rootNode.hasNode("sample.html"));
   }
   

}
