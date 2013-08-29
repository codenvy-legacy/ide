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
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.exoplatform.services.jcr.core.ExtendedNode;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Node;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExportTest extends JcrFileSystemTest
{
   private Node exportFolder;
   private Set<String> expectedZipItems = new HashSet<String>();

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      exportFolder = testRoot.addNode(name, "nt:folder");

      Node file_1 = exportFolder.addNode("file_1", "nt:file");
      Node content = file_1.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node file_2 = exportFolder.addNode("file_2", "nt:file");
      content = file_2.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node file_3 = exportFolder.addNode("file_3", "nt:file");
      content = file_3.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node file_4 = exportFolder.addNode("file_4", "nt:file");
      content = file_4.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node folder_1 = exportFolder.addNode("folder_1", "nt:folder");
      folder_1.addMixin("vfs:project");
      Node folder_2 = exportFolder.addNode("folder_2", "nt:folder");
      Node folder_3 = exportFolder.addNode("folder_3", "nt:folder");
      Node folder_4 = exportFolder.addNode("folder_4", "nt:folder");
      Node folder_5 = folder_2.addNode("folder_2", "nt:folder");
      Node folder_6 = folder_4.addNode("folder_6", "nt:folder");

      Node file_5 = folder_1.addNode("file_5", "nt:file");
      content = file_5.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      Node file_6 = folder_3.addNode("file_6", "nt:file");
      content = file_6.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "text/plain");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));

      session.save();

      final String basePath = exportFolder.getPath() + "/";
      expectedZipItems.add(file_1.getPath().replace(basePath, ""));
      expectedZipItems.add(file_2.getPath().replace(basePath, ""));
      expectedZipItems.add(file_3.getPath().replace(basePath, ""));
      expectedZipItems.add(file_4.getPath().replace(basePath, ""));
      expectedZipItems.add(file_5.getPath().replace(basePath, ""));
      expectedZipItems.add(file_6.getPath().replace(basePath, ""));
      expectedZipItems.add(folder_1.getPath().replace(basePath, "") + "/");
      expectedZipItems.add(folder_2.getPath().replace(basePath, "") + "/");
      expectedZipItems.add(folder_3.getPath().replace(basePath, "") + "/");
      expectedZipItems.add(folder_4.getPath().replace(basePath, "") + "/");
      expectedZipItems.add(folder_5.getPath().replace(basePath, "") + "/");
      expectedZipItems.add(folder_6.getPath().replace(basePath, "") + "/");
      expectedZipItems.add(folder_1.getNode(".project").getPath().replace(basePath, ""));
   }

   public void testExport() throws Exception
   {
      String exportFolderId = ((ExtendedNode)exportFolder).getIdentifier();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "export/" + exportFolderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));

      ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(writer.getBody()));
      checkZipItems(zip);
   }

   public void testDownloadZip() throws Exception
   {
      // Expect the same as 'export in zip' plus header "Content-Disposition". 
      String exportFolderId = ((ExtendedNode)exportFolder).getIdentifier();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = SERVICE_URI + "downloadzip/" + exportFolderId;
      ContainerResponse response = launcher.service("GET", path, BASE_URI, null, null, writer, null);
      assertEquals(200, response.getStatus());
      assertEquals("application/zip", writer.getHeaders().getFirst("Content-Type"));
      assertEquals("attachment; filename=\"" + exportFolder.getName() + ".zip" + "\"",
         writer.getHeaders().getFirst("Content-Disposition"));

      ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(writer.getBody()));
      checkZipItems(zip);
   }

   private void checkZipItems(ZipInputStream zip) throws Exception
   {
      ZipEntry zipEntry;
      while ((zipEntry = zip.getNextEntry()) != null)
      {
         String name = zipEntry.getName();
         zip.closeEntry();
         assertTrue("Not found " + name + " entry in zip. ", expectedZipItems.remove(name));
      }
      assertTrue(expectedZipItems.isEmpty());
   }
}
