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
package org.exoplatform.ide.vfs.impl.jcr;

import org.everrest.core.impl.ContainerResponse;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UpdateTest extends JcrFileSystemTest
{
   private Node updatePropertiesTestNode;

   private String fileID;
   private String folderID;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      updatePropertiesTestNode = testRoot.addNode(name, "nt:unstructured");
      updatePropertiesTestNode.addMixin("exo:privilegeable");
      updatePropertiesTestNode.addMixin("mix:lockable");

      Node fileNode = updatePropertiesTestNode.addNode("UpdatePropertiesTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileNode.addMixin("exo:unstructuredMixin");
      fileID = ((ExtendedNode)fileNode).getIdentifier();

      Node folderNode = updatePropertiesTestNode.addNode("UpdatePropertiesTest_FOLDER", "nt:folder");
      folderID = ((ExtendedNode)folderNode).getIdentifier();

      session.save();
   }

   public void testUpdatePropertiesFile() throws Exception
   {
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      doUpdate(fileID, properties);
      Node file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      assertEquals("MyValue", file.getProperty("MyProperty").getString());
   }

   public void testUpdatePropertiesFile2() throws Exception
   {
      String properties = "[{\"name\":\"MyProperty\", \"value\":[123]}]";
      doUpdate(fileID, properties);
      Node file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      assertEquals(123L, file.getProperty("MyProperty").getLong());
   }

   public void testUpdatePropertiesFile3() throws Exception
   {
      String properties = "[{\"name\":\"MyProperty\", \"value\":[true]}]";
      doUpdate(fileID, properties);
      Node file = ((ExtendedSession)session).getNodeByIdentifier(fileID);
      assertEquals(true, file.getProperty("MyProperty").getBoolean());
   }

   public void testUpdatePropertiesAndChangeFolderType() throws Exception
   {
      String properties = "[{\"name\":\"vfs:mimeType\", \"value\":[\"text/vnd.ideproject+directory\"]}]";
      doUpdate(folderID, properties);
      Node folderNode = ((ExtendedSession)session).getNodeByIdentifier(folderID);
      assertTrue(folderNode.isNodeType("vfs:project"));
      assertTrue(folderNode.hasNode(".project"));
      Project project = (Project)getItem(folderID);
      assertEquals("text/vnd.ideproject+directory", project.getMimeType());
      assertEquals("default", project.getProjectType());
   }

   public void testUpdatePropertiesAndChangeFolderType2() throws Exception
   {
      Node folderNode = ((ExtendedSession)session).getNodeByIdentifier(folderID);
      folderNode.addMixin("vfs:project");
      session.save();
      String properties = "[{\"name\":\"vfs:mimeType\", \"value\":[\"text/directory\"]}]";
      doUpdate(folderID, properties);
      folderNode = ((ExtendedSession)session).getNodeByIdentifier(folderID);
      assertTrue(folderNode.isNodeType("nt:folder"));
      assertFalse(folderNode.isNodeType("vfs:project"));
      assertFalse(folderNode.hasNode(".project"));
      Folder folder = (Folder)getItem(folderID);
      assertEquals("text/directory", folder.getMimeType());
   }

   public void doUpdate(String id, String rawData) throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("item/") //
         .append(id) //
         .toString();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, rawData.getBytes(), null);
      assertEquals(204, response.getStatus());
   }
}
