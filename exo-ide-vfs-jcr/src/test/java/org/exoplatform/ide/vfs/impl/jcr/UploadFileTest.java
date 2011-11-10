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
import org.exoplatform.services.jcr.core.ExtendedNode;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UploadFileTest extends JcrFileSystemTest
{
   private String uploadTestNodeID;
   private String uploadTestNodePath;
   private Node uploadTestNode;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      uploadTestNode = testRoot.addNode(name, "nt:unstructured");
      session.save();
      uploadTestNodeID = ((ExtendedNode)uploadTestNode).getIdentifier();
      uploadTestNodePath = uploadTestNode.getPath();
   }

   public void testUploadNewFile() throws Exception
   {
      // Passed by browser.
      String fileName = "testUploadNewFile";
      // File content.
      String fileContent = "test upload file";
      // Passed by browser.
      String fileMediaType = "text/plain; charset=utf8";
      ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", "", false);
      assertEquals(200, response.getStatus());
      String expectedPath = uploadTestNodePath + "/" + fileName;
      assertTrue("File was not created in expected location. ", session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals("utf8", file.getNode("jcr:content").getProperty("jcr:encoding").getString());
      assertEquals(fileContent, file.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testUploadNewFileInRootFolder() throws Exception
   {
      // Passed by browser.
      String fileName = "testUploadNewFile";
      // File content.
      String fileContent = "test upload file";
      // Passed by browser.
      String fileMediaType = "text/plain; charset=utf8";
      uploadTestNodeID = ((ExtendedNode)session.getRootNode()).getIdentifier();
      ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", "", false);
      assertEquals(200, response.getStatus());
      String expectedPath = "/" + fileName;
      assertTrue("File was not created in expected location. ", session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals("utf8", file.getNode("jcr:content").getProperty("jcr:encoding").getString());
      assertEquals(fileContent, file.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testUploadNewFileCustomizeName() throws Exception
   {
      // Passed by browser.
      String fileName = "testUploadNewFileCustomizeName";
      // File content.
      String fileContent = "test upload file with custom name";
      // Passed by browser.
      String fileMediaType = "text/plain; charset=utf8";
      // Name of file passed in HTML form. If present it should be used instead of original file name.
      String formFileName = fileName + ".txt";
      ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", formFileName, false);
      assertEquals(200, response.getStatus());
      String expectedPath = uploadTestNodePath + "/" + formFileName;
      assertTrue("File was not created in expected location. ", session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals("utf8", file.getNode("jcr:content").getProperty("jcr:encoding").getString());
      assertEquals(fileContent, file.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testUploadNewFileCustomizeMediaType() throws Exception
   {
      // Passed by browser.
      String fileName = "testUploadNewFileCustomizeMediaType";
      // File content.
      String fileContent = "test upload file with custom media type";
      // Passed by browser.
      String fileMediaType = "application/octet-stream";
      // Name of file passed in HTML form. If present it should be used instead of original file name.
      String formFileName = fileName + ".txt";
      // Media type of file passed in HTML form. If present it should be used instead of original file media type.
      String formMediaType = "text/plain; charset=utf8";
      ContainerResponse response =
         doUploadFile(fileName, fileMediaType, fileContent, formMediaType, formFileName, false);
      assertEquals(200, response.getStatus());
      String expectedPath = uploadTestNodePath + "/" + formFileName;
      assertTrue("File was not created in expected location. ", session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals("utf8", file.getNode("jcr:content").getProperty("jcr:encoding").getString());
      assertEquals(fileContent, file.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   public void testUploadFileAlreadyExists() throws Exception
   {
      String fileName = "existedFile";
      String fileMediaType = "application/octet-stream";
      Node uploadParent = (Node)session.getItem(uploadTestNodePath);
      Node fileNode = uploadParent.addNode(fileName, "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", fileMediaType);
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      session.save();

      ContainerResponse response = doUploadFile(fileName, fileMediaType, DEFAULT_CONTENT, "", "", false);
      assertEquals(400, response.getStatus());
      log.info(response.getEntity());
   }

   public void testUploadFileAlreadyExistsOverwrite() throws Exception
   {
      String fileName = "existedFileOverwrite";
      Node uploadParent = (Node)session.getItem(uploadTestNodePath);
      Node fileNode = uploadParent.addNode(fileName, "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "application/octet-stream");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      session.save();

      String fileContent = "test upload and overwrite existed fyle";
      String fileMediaType = "text/plain; charset=utf8";
      ContainerResponse response = doUploadFile(fileName, fileMediaType, fileContent, "", "", true);
      assertEquals(200, response.getStatus());
      String expectedPath = uploadTestNodePath + "/" + fileName;
      assertTrue("Cannot found file in expected location. ", session.itemExists(expectedPath));
      Node file = (Node)session.getItem(expectedPath);
      assertEquals("text/plain", file.getNode("jcr:content").getProperty("jcr:mimeType").getString());
      assertEquals("utf8", file.getNode("jcr:content").getProperty("jcr:encoding").getString());
      assertEquals(fileContent, file.getNode("jcr:content").getProperty("jcr:data").getString());
   }

   private ContainerResponse doUploadFile(String fileName, String fileMediaType, String fileContent,
      String formMediaType, String formFileName, boolean formOverwrite) throws Exception
   {
      String path = new StringBuilder() //
         .append(SERVICE_URI) //
         .append("uploadfile/") //
         .append(uploadTestNodeID).toString(); //

      Map<String, List<String>> headers = new HashMap<String, List<String>>();
      List<String> contentType = new ArrayList<String>();
      contentType.add("multipart/form-data; boundary=abcdef");
      headers.put("Content-Type", contentType);

      byte[] formData =
         String.format(uploadBodyPattern, fileName, fileMediaType, fileContent, formMediaType, formFileName,
            formOverwrite).getBytes();
      EnvironmentContext env = new EnvironmentContext();
      env.put(HttpServletRequest.class, new MockHttpServletRequest("", new ByteArrayInputStream(formData),
         formData.length, "POST", headers));

      ContainerResponse response = launcher.service("POST", path, BASE_URI, headers, formData, env);
      return response;
   }

   private final String uploadBodyPattern = "--abcdef\r\n"
      + "Content-Disposition: form-data; name=\"file\"; filename=\"%1$s\"\r\nContent-Type: %2$s\r\n\r\n"
      + "%3$s\r\n--abcdef\r\nContent-Disposition: form-data; name=\"mimeType\"\r\n\r\n%4$s"
      + "\r\n--abcdef\r\nContent-Disposition: form-data; name=\"name\"\r\n\r\n%5$s\r\n"
      + "--abcdef\r\nContent-Disposition: form-data; name=\"overwrite\"\r\n\r\n%6$b\r\n--abcdef--\r\n";
}
