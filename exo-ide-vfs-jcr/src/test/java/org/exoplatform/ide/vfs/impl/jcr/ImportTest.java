/*
 * Copyright (C) 2011 eXo Platform SAS.
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("import/") //
         .append(importFolderId) //
         .toString();
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
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("uploadzip/") //
         .append(importFolderId) //
         .toString();
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
}
