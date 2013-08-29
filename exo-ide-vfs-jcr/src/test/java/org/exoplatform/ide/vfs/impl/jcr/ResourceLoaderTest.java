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

import org.exoplatform.services.jcr.core.ExtendedNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;

import javax.jcr.Node;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceLoaderTest extends JcrFileSystemTest
{
   private Node resourceLoaderTestNode;

   private String folderId;

   private String folderPath;

   private String fileId;

   private String filePath;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      resourceLoaderTestNode = testRoot.addNode(name, "nt:unstructured");
      resourceLoaderTestNode.addMixin("exo:privilegeable");

      Node folderNode = resourceLoaderTestNode.addNode("GetResourceTest_FOLDER", "nt:folder");
      Node childNode = folderNode.addNode("file1", "nt:file");
      Node childContentNode = childNode.addNode("jcr:content", "nt:resource");
      childContentNode.setProperty("jcr:mimeType", "text/plain");
      childContentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      childContentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      folderId = ((ExtendedNode)folderNode).getIdentifier();
      folderPath = folderNode.getPath();

      Node fileNode = resourceLoaderTestNode.addNode("GetResourceTest_FILE", "nt:file");
      Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      fileId = ((ExtendedNode)fileNode).getIdentifier();
      filePath = fileNode.getPath();
      session.save();

      /*VirtualFileSystemRegistry registry =
         (VirtualFileSystemRegistry)StandaloneContainer.getInstance().getComponentInstanceOfType(
            VirtualFileSystemRegistry.class);
      URLHandlerFactorySetup.setup(registry);*/
   }

   public void testLoadFileByID() throws Exception
   {
      URL file = new URI("ide+vfs", '/' + REPOSITORY_NAME, fileId).toURL();
      final String expectedURL = "ide+vfs:/" + REPOSITORY_NAME + '#' + fileId;
      assertEquals(expectedURL, file.toString());
      byte[] b = new byte[128];
      InputStream in = file.openStream();
      int num = in.read(b);
      in.close();
      assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
   }

   public void testLoadFileByPath() throws Exception
   {
      URL file = new URI("ide+vfs", '/' + REPOSITORY_NAME, filePath).toURL();
      final String expectedURL = "ide+vfs:/" + REPOSITORY_NAME + '#' + filePath;
      assertEquals(expectedURL, file.toString());
      byte[] b = new byte[128];
      InputStream in = file.openStream();
      int num = in.read(b);
      in.close();
      assertEquals(DEFAULT_CONTENT, new String(b, 0, num));
   }

   public void testLoadFolderByID() throws Exception
   {
      URL folder = new URI("ide+vfs", '/' + REPOSITORY_NAME, folderId).toURL();
      final String expectedURL = "ide+vfs:/" + REPOSITORY_NAME + '#' + folderId;
      assertEquals(expectedURL, folder.toString());
      byte[] b = new byte[128];
      InputStream in = folder.openStream();
      int num = in.read(b);
      in.close();
      assertEquals("file1\n", new String(b, 0, num));
   }

   public void testLoadFolderByPath() throws Exception
   {
      URL folder = new URI("ide+vfs", '/' + REPOSITORY_NAME, folderPath).toURL();
      final String expectedURL = "ide+vfs:/" + REPOSITORY_NAME + '#' + folderPath;
      assertEquals(expectedURL, folder.toString());
      byte[] b = new byte[128];
      InputStream in = folder.openStream();
      int num = in.read(b);
      in.close();
      assertEquals("file1\n", new String(b, 0, num));
   }
}
