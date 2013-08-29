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
 * @version $Id: UpdateTest.java 77229 2011-12-03 16:56:34Z andrew00x $
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

   public void testUpdatePropertiesFile4() throws Exception
   {
      Node file = testRoot.addNode("testUpdatePropertiesFile4", "exo:extFile");
      Node contentNode = file.addNode("jcr:content", "exo:extResource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:encoding", "utf8");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", "");

      contentNode.setProperty("a", "to be or not to be");
      contentNode.setProperty("b", "hello world");
      contentNode.setProperty("d", "test");
      session.save();

      String properties = "[{\"name\":\"a\", \"value\":[\"TEST\"]}," //
         + "{\"name\":\"b\", \"value\":[\"TEST\"]}," //
         + "{\"name\":\"c\", \"value\":[\"TEST\"]}," //
         + "{\"name\":\"d\", \"value\":[\"TEST\"]}," //
         + "{\"name\":\"e\", \"value\":[\"TEST\"]}]";
      doUpdate(((ExtendedNode)file).getIdentifier(), properties);

      // Property 'a' determined for node type 'exo:extResource' and was set previously.
      assertEquals("TEST", contentNode.getProperty("a").getString());
      // Property 'b' determined for node type 'exo:extResource' and was set previously.
      assertEquals("TEST", contentNode.getProperty("b").getString());
      // Property 'c' determined for node type 'exo:extResource' but was not set previously.
      assertEquals("TEST", contentNode.getProperty("c").getString());
      // Property 'd' determined for node type 'exo:extResource' and was set previously.
      assertEquals("TEST", contentNode.getProperty("d").getString());
      // Property 'e' not determined for any node types. Must be updated in exo:extFile
      // since it supports '*' property.
      assertEquals("TEST", file.getProperty("e").getString());
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
      String path = SERVICE_URI + "item/" + id;
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, h, rawData.getBytes(), null);
      assertEquals(200, response.getStatus());
   }
}
