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

import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * IDE-47: Creating and "Saving As" new XML file with non-latin name. 
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateSaveAsXmlWithNonLatinNameTest extends BaseTest
{

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
   private static final String XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>test</test>";

   /**
    * Content of secont XML file.
    */
   private static final String XML_CONTENT_2 = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<settings>test</settings>";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode(XML_FILE, "UTF-8"));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }

      try
      {
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode(NEW_XML_FILE, "UTF-8"));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   //IDE-47: Creating and "Saving As" new XML file with non-latin name 
   @Test
   public void testCreateAndSaveAsXmlWithNonLatinName() throws Exception
   {
      waitForRootElement();

      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(0);

      assertEquals("Untitled file.xml *", IDE.EDITOR.getTabTitle(0));
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, true);

      IDE.EDITOR.deleteFileContent();

      IDE.EDITOR.typeTextIntoEditor(0, XML_CONTENT);

      saveAsUsingToolbarButton(XML_FILE);
      IDE.WORKSPACE.waitForItem(WS_URL + XML_FILE);

      assertEquals(XML_FILE, IDE.EDITOR.getTabTitle(0));

      //check file properties
      IDE.PROPERTIES.openProperties();
      checkProperties(String.valueOf(XML_CONTENT.length() + 1), MimeType.TEXT_XML, XML_FILE);

      IDE.EDITOR.closeTab(0);

      //check file on server
      checkFileExists(URL + URLEncoder.encode(XML_FILE, "UTF-8"), XML_CONTENT);

      IDE.NAVIGATION.assertItemVisible(WS_URL + XML_FILE);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + XML_FILE, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.checkCodeEditorOpened(0);

      //change file content
      IDE.EDITOR.deleteFileContent();

      IDE.EDITOR.typeTextIntoEditor(0, XML_CONTENT_2);

      //save as file

      saveAsUsingToolbarButton(NEW_XML_FILE);
      IDE.WORKSPACE.waitForItem(WS_URL + NEW_XML_FILE);

      assertEquals(NEW_XML_FILE, IDE.EDITOR.getTabTitle(0));
      checkProperties(String.valueOf(XML_CONTENT_2.length() + 1), MimeType.TEXT_XML, NEW_XML_FILE);

      IDE.EDITOR.closeTab(0);

      //check two files exist
      checkFileExists(URL + URLEncoder.encode(XML_FILE, "UTF-8"), XML_CONTENT);
      checkFileExists(URL + URLEncoder.encode(NEW_XML_FILE, "UTF-8"), XML_CONTENT_2);

      IDE.NAVIGATION.assertItemVisible(WS_URL + XML_FILE);
      IDE.NAVIGATION.assertItemVisible(WS_URL + NEW_XML_FILE);

   }

   /**
    * Check is file created and check file content.
    * 
    * @param fileUrl - URL to file
    * @param fileContent - expected file content
    * @throws Exception
    */
   private void checkFileExists(String fileUrl, String fileContent) throws Exception
   {
      HTTPResponse response = VirtualFileSystemUtils.get(fileUrl);
      assertEquals(200, response.getStatusCode());
      //code mirror adds one more newline at the end of file
      assertEquals(fileContent + "\n", response.getText());
   }

   /**
    * Check properties values.
    * 
    * @param contentLength content length property 
    * @param contentType content type
    * @param displayName display name
    * @throws Exception
    */
   private void checkProperties(String contentLength, String contentType, String displayName) throws Exception
   {
      assertEquals(contentLength, IDE.PROPERTIES.getContentLength());
      assertEquals("nt:resource", IDE.PROPERTIES.getContentNodeType());
      assertEquals(contentType, IDE.PROPERTIES.getContentType());
      assertEquals(displayName, IDE.PROPERTIES.getDisplayName());
      assertEquals("nt:file", IDE.PROPERTIES.getFileNodeType());
   }

}
