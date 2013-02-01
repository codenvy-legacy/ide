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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URLEncoder;
import java.util.Locale;
import java.util.ResourceBundle;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-47: Creating and "Saving As" new XML file with non-latin name.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 * 
 */
public class CreateSaveAsXmlWithNonLatinNameTest extends BaseTest
{
   private static final String PROJECT = CreateSaveAsXmlWithNonLatinNameTest.class.getSimpleName();

   /**
    * Resource bundle for non-lating names.
    */
   private static final ResourceBundle rb = ResourceBundle.getBundle("FileMsg", Locale.getDefault());

   /**
    * Name of first XML file.
    */
   private static final String XML_FILE = rb.getString("xml.file.name");

   /**
    * Name of second XML file
    */
   private static final String NEW_XML_FILE = rb.getString("new.xml.file.name");

   /**
    * Content of first XML file.
    */
   private static final String XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?><test>test</test>";

   /**
    * Content of secont XML file.
    */
   private static final String XML_CONTENT_2 = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<settings>test</settings>";

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

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);

      }
      catch (Exception e)
      {
         fail("Cant create project ");
      }
   }

   // IDE-47: Creating and "Saving As" new XML file with non-latin name
   /**
    * Test added to Ignore, because at the moment not solved a problem with
    * encoding Cyrillic characters to URL. For example: create new file with
    * cyrillic name, save him, and get URL in IDE. In URL IDE we shall see
    * encoding characters in file name
    * 
    * @throws Exception
    */
   @Test
   public void testCreateAndSaveAsXmlWithNonLatinName() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.waitActiveFile();

      IDE.EDITOR.waitTabPresent("Untitled file.xml");
      IDE.TOOLBAR.waitForButtonDisabled(ToolbarCommands.File.SAVE);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.SAVE_AS);

      IDE.EDITOR.deleteFileContent();
      IDE.EDITOR.typeTextIntoEditor(XML_CONTENT);
      IDE.EDITOR.saveAs(1, XML_FILE);

      String pathXml = PROJECT + "/" + XML_FILE;
      IDE.PROJECT.EXPLORER.waitForItem(pathXml);

      assertEquals(XML_FILE, IDE.EDITOR.getTabTitle(1));

      // check file properties
      IDE.PROPERTIES.openProperties();
      // code mirror adds one more newline at the end of file
      checkProperties(XML_CONTENT.length(), MimeType.TEXT_XML, XML_FILE);

      IDE.EDITOR.closeFile(1);

      // check file on server
      checkFileExists(WS_URL + PROJECT + "/" + URLEncoder.encode(XML_FILE, "UTF-8"), XML_CONTENT);

      IDE.PROJECT.EXPLORER.waitItemPresent(pathXml);

      IDE.PROJECT.EXPLORER.openItem(pathXml);
      IDE.EDITOR.waitActiveFile();

      // change file content
      IDE.EDITOR.deleteFileContent();
      IDE.EDITOR.typeTextIntoEditor(XML_CONTENT_2);

      // save as file
      IDE.EDITOR.saveAs(1, NEW_XML_FILE);
      String pathNewXml = PROJECT + "/" + NEW_XML_FILE;
      IDE.PROJECT.EXPLORER.waitForItem(pathNewXml);

      assertEquals(NEW_XML_FILE, IDE.EDITOR.getTabTitle(1));
      IDE.PROPERTIES.openProperties();
      // code mirror adds one more newline at the end of file
      checkProperties(XML_CONTENT_2.length(), MimeType.TEXT_XML, NEW_XML_FILE);

      IDE.EDITOR.closeFile(1);

      // check two files exist
      checkFileExists(WS_URL + PROJECT + "/" + URLEncoder.encode(XML_FILE, "UTF-8"), XML_CONTENT);
      checkFileExists(WS_URL + PROJECT + "/" + URLEncoder.encode(NEW_XML_FILE, "UTF-8"), XML_CONTENT_2);

      IDE.PROJECT.EXPLORER.waitItemPresent(pathXml);
      IDE.PROJECT.EXPLORER.waitItemPresent(pathNewXml);
   }

   /**
    * Check is file created and check file content.
    * 
    * @param fileUrl
    *            - URL to file
    * @param fileContent
    *            - expected file content
    * @throws Exception
    */
   private void checkFileExists(String fileUrl, String fileContent) throws Exception
   {
      Response response = VirtualFileSystemUtils.get(fileUrl);
      assertEquals(200, response.getStatusCode());
      // code mirror adds one more newline at the end of file
      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("GOOGLE_CHROME"))
      {
         assertEquals(fileContent, response.getData());
      }
      else
      {
         assertEquals(fileContent + "\n", response.getData());
      }

   }

   /**
    * Check properties values.
    * 
    * @param contentLength
    *            content length property
    * @param contentType
    *            content type
    * @param displayName
    *            display name
    * @throws Exception
    */
   private void checkProperties(int contentLengh, String contentType, String displayName) throws Exception
   {
      assertEquals(contentType, IDE.PROPERTIES.getContentType());
      assertEquals(displayName, IDE.PROPERTIES.getDisplayName());
      //in FF browser codemirror return +1 symbols, in chrome - no
      
      assertTrue(Integer.parseInt(IDE.PROPERTIES.getContentLength()) == contentLengh
         || Integer.parseInt(IDE.PROPERTIES.getContentLength()) == contentLengh + 1);
   }

}
