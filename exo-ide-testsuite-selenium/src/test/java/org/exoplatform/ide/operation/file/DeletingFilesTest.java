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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * IDE-11: Deleting files. 
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class DeletingFilesTest extends BaseTest
{

   private static String PROJECT = DeletingFilesTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String GOOGLE_GADGET_FILE_NAME = "newGoogleGadget.gadget";

   private static String JAVA_SCRIPT_FILE_NAME = "newJavaScriptFile.js";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private static String TEXT_FILE_NAME = "newTxtFile.txt";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFile(link, HTML_FILE_NAME, MimeType.TEXT_HTML, "");
         VirtualFileSystemUtils.createFile(link, GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, "");
         VirtualFileSystemUtils.createFile(link, GOOGLE_GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, "");
         VirtualFileSystemUtils.createFile(link, JAVA_SCRIPT_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, "");
         VirtualFileSystemUtils.createFile(link, XML_FILE_NAME, MimeType.APPLICATION_XML, "");
         VirtualFileSystemUtils.createFile(link, TEXT_FILE_NAME, MimeType.TEXT_PLAIN, "");
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   //IDE-11: Deleting files.
   @Test
   public void testDeletingFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + GROOVY_FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + GROOVY_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + GROOVY_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GOOGLE_GADGET_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GOOGLE_GADGET_FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + GOOGLE_GADGET_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + GOOGLE_GADGET_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + JAVA_SCRIPT_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + JAVA_SCRIPT_FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + JAVA_SCRIPT_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + JAVA_SCRIPT_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + XML_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + XML_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEXT_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEXT_FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + TEXT_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + TEXT_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + HTML_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + HTML_FILE_NAME).getStatusCode());
   }
}
