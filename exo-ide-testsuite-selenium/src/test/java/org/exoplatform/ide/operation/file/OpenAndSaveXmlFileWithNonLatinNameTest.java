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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * IDE-48: Opening and Saving new XML file with non-latin name.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OpenAndSaveXmlFileWithNonLatinNameTest extends BaseTest
{
   /**
    * Resource bundle for non-lating names.
    */
   private static final ResourceBundle rb = ResourceBundle.getBundle("FileMsg", Locale.getDefault());

   private static final String FILE_NAME = rb.getString("xml.file.name");

   private static final String FOLDER_NAME = OpenAndSaveXmlFileWithNonLatinNameTest.class.getSimpleName();

   private static String XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n"
      + "<settings>value</settings>\n" + "</test>";

   private static String XML_CONTENT_2 = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n"
      + "<settings>param</settings>\n" + "<bean>\n" + "<name>MineBean</name>\n" + "</bean>\n" + "</test>";

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test added to Ignore, because at the moment not solved a problem with encoding Cyrillic characters to URL.
    * For example: create new file with cyrillic name, save him, and get URL in IDE. In URL IDE we  shall see 
    * encoding characters in file name
    * @throws Exception
    */
   @Test
   public void testOpenAndSaveXmlFileWithNonLatinName() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.CREATE.createProject(FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(FOLDER_NAME);
      IDE.PROJECT.EXPLORER.selectItem(FOLDER_NAME);
      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitTabPresent(1);
      IDE.EDITOR.waitActiveFile(FOLDER_NAME + "/Untitled file.xml");
      assertEquals("Untitled file.xml *", IDE.EDITOR.getTabTitle(1));

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, true);

      IDE.EDITOR.deleteFileContent(1);
      IDE.EDITOR.typeTextIntoEditor(1, XML_CONTENT);

      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.closeFile(1);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.deleteFileContent(0);
      IDE.EDITOR.typeTextIntoEditor(0, XML_CONTENT_2);

      //Save command enabled
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
      //File name ends with *
      assertEquals(FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));

      saveCurrentFile();

      //File name doesn't end with *
      assertEquals(FILE_NAME, IDE.EDITOR.getTabTitle(0));

      //Save command disabled
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
   }

}
