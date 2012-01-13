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
package org.exoplatform.ide.extension.chromattic.server;

import org.everrest.core.impl.ContainerResponse;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.services.jcr.RepositoryService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class TestDataObjectService extends BaseTest
{
   /** . */
   private static final String DATA_OBJECT_BODY =
      "@org.chromattic.api.annotations.PrimaryType(name=\"exo:testNodeType\")\n" + "class DataObject {\n"
         + "@org.chromattic.api.annotations.Property(name = \"a\")" + "String a\n" + "}";

   private static final String ntd =
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
         + "<!--Node type generation prototype-->"
         + "<nodeTypes xmlns:jcr=\"http://www.jcp.org/jcr/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\">"
         + "<nodeType name=\"exo:testNodeType\" isMixin=\"false\" hasOrderableChildNodes=\"false\">"
         + "<supertypes>"
         + "<supertype>nt:base</supertype>"
         + "<supertype>mix:referenceable</supertype>"
         + "</supertypes>"
         + "<propertyDefinitions>"
         + "<propertyDefinition name=\"a\" requiredType=\"String\" autoCreated=\"false\" mandatory=\"false\" onParentVersion=\"COPY\" protected=\"false\" multiple=\"false\">"
         + "<valueConstraints/>" + "</propertyDefinition>" + "</propertyDefinitions>" + "<childNodeDefinitions/>"
         + "</nodeType>" + "</nodeTypes>";

   private Folder dependencies;

   private File dataObject;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      dependencies = virtualFileSystem.createFolder(testRoot.getId(), "dependencies");
      dataObject =
         virtualFileSystem.createFile(dependencies.getId(), "DataObject.cmtc", new MediaType("application",
            "x-chromattic+groovy"), new ByteArrayInputStream(DATA_OBJECT_BODY.getBytes()));
   }

   @After
   public void tearDown() throws Exception
   {
      virtualFileSystem.delete(testRoot.getId(), null);
      super.tearDown();
   }

   @Test
   public void testNodeTypeGeneration() throws Exception
   {
      ContainerResponse response =
         launcher.service("POST", "/ide/chromattic/generate-nodetype-definition?vfsid=ws&id=" + dataObject.getId()
            + "&nodeTypeFormat=EXO", "", null, null, null, null);
      assertEquals(200, response.getStatus());
      log.info("Generated node types " + response.getEntity());

      // Check is response contains node type description.
      // Do not check response structure just be sure <nodeType> tag exists.
      Document xml =
         DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(new ByteArrayInputStream(((String)response.getEntity()).getBytes()));
      XPath xpath = XPathFactory.newInstance().newXPath();
      xpath.setNamespaceContext(new NodeTypesNamespaceContext());
      org.w3c.dom.Node nodeType = (org.w3c.dom.Node)xpath.evaluate("//nodeType", xml, XPathConstants.NODE);
      assertNotNull("There is no generated node type in response. ", nodeType);
   }

   @Test
   public void testNodeTypeGenerationScriptNotFound() throws Exception
   {
      ContainerResponse response =
         launcher.service("POST", "/ide/chromattic/generate-nodetype-definition?vfsid=ws&id=" + dataObject.getId()
            + "_WRONG" + "&nodeTypeFormat=EXO", "", null, null, null, null);
      assertEquals(500, response.getStatus());
      log.info("Generated node types " + response.getEntity());
   }

   @Test
   public void testNodeTypeGenerationCND() throws Exception
   {
      ContainerResponse response =
         launcher.service("POST", "/ide/chromattic/generate-nodetype-definition?vfsid=ws&id=" + dataObject.getId()
            + "&nodeTypeFormat=CND", "", null, null, null, null);
      assertEquals(200, response.getStatus());
      log.info("Generated node types " + response.getEntity());
   }

   @Test
   public void testNodeType() throws Exception
   {
      ContainerResponse response =
         launcher.service("POST", "/ide/chromattic/register-nodetype/EXO/4", "", null, ntd.getBytes(), null, null);
      assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      Repository repository = repositoryService.getCurrentRepository();
      Session session = repository.login("ws");
      session.getWorkspace().getNodeTypeManager().getNodeType("exo:testNodeType");
   }
}
