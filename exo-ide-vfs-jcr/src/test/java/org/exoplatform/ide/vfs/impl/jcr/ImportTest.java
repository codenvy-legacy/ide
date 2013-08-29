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
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.test.mock.MockHttpServletRequest;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ImportTest extends JcrFileSystemTest
{
   private String importFolderId;
   private byte[] zip;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      Node importFolder = testRoot.addNode(name, "nt:folder");
      session.save();
      importFolderId = ((ExtendedNode)importFolder).getIdentifier();
      URL testZipResource = Thread.currentThread().getContextClassLoader().getResource("spring-project.zip");
      java.io.File f = new java.io.File(testZipResource.toURI());
      FileInputStream in = new FileInputStream(f);
      zip = new byte[(int)f.length()];
      in.read(zip);
      in.close();
   }

   public void testImport() throws Exception
   {
      String path = SERVICE_URI + "import/" + importFolderId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      headers.put("Content-Type", Arrays.asList("application/zip"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, zip, null, null);
      assertEquals(204, response.getStatus());

      // Check imported structure.
      Node node = ((ExtendedSession)session).getNodeByIdentifier(importFolderId);
      assertTrue("'.project' node missed", node.hasNode(".project"));
      Project project = (Project)getItem(importFolderId);
      assertEquals("spring", project.getProjectType());
      java.io.File unzip = ZipUtils.unzip(new ByteArrayInputStream(zip));
      new ZipUtils.TreeWalker(unzip, (FolderData)ItemData.fromNode(node, null)).walk();
   }

   public void testUploadZip() throws Exception
   {
      // Do the same as 'import' but send content in HTML form. 
      String path = SERVICE_URI + "uploadzip/" + importFolderId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      headers.put("Content-Type", Arrays.asList("multipart/form-data; boundary=abcdef"));

      // Build multipart request.
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      buf.write("--abcdef\r\nContent-Disposition: form-data; name=\"file\"; filename=\"zippedfolder\"\r\nContent-Type: application/zip\r\n\r\n"
         .getBytes());
      buf.write(zip);
      buf.write("\r\n--abcdef--".getBytes());
      byte[] body = buf.toByteArray();

      // Need set EnvironmentContext.  HttpServletRequest used to obtain HTML form data.
      EnvironmentContext env = new EnvironmentContext();
      env.put(HttpServletRequest.class, new MockHttpServletRequest("", new ByteArrayInputStream(body), body.length,
         "POST", headers));

      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, body, null, env);
      assertEquals(200, response.getStatus());

      // Check imported structure.
      Node node = ((ExtendedSession)session).getNodeByIdentifier(importFolderId);
      assertTrue("'.project' node missed", node.hasNode(".project"));
      Project project = (Project)getItem(importFolderId);
      assertEquals("spring", project.getProjectType());
      java.io.File unzip = ZipUtils.unzip(new ByteArrayInputStream(zip));
      new ZipUtils.TreeWalker(unzip, (FolderData)ItemData.fromNode(node, null)).walk();
   }

   public void testZipBomb() throws Exception
   {
      final int uncompressedSize = 1000001;
      // Uncompressed size bigger then 1000000 (~1M).
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ZipOutputStream zip = new ZipOutputStream(bout);
      zip.putNextEntry(new ZipEntry("null"));
      for (int i = 0; i < uncompressedSize; i++)
      {
         zip.write(0);
      }
      zip.closeEntry();
      zip.close();
      byte b[] = bout.toByteArray();
      // Be sure source data for test is correct. Zero data should be compressed with very high ratio.
      assertTrue((uncompressedSize / b.length) > 100);
      String path = SERVICE_URI + "import/" + importFolderId;
      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      headers.put("Content-Type", Arrays.asList("application/zip"));
      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, b, null, null);
      // Exception must be thrown.
      assertEquals(500, response.getStatus());
      log.info(response.getEntity());
   }
}
