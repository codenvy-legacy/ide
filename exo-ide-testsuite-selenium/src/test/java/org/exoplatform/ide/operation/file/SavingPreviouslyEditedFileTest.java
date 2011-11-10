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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * IDE-13:Saving previously edited file.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */

public class SavingPreviouslyEditedFileTest extends BaseTest
{

   private static final String PROJECT = SavingPreviouslyEditedFileTest.class.getSimpleName();

   private static final String FILE_NAME = "TestXmlFile";

   private static final String DEFAULT_XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>";

   private final static String XML_TEXT1 = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n"
      + "<settings>param</settings>\n<bean>\n<name>MineBean</name>\n" + "</bean>";

   private final static String XML_TEXT2 = "\n</test>";

   private static final String FORMATTED_XML_TEXT = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n"
      + "  <settings>param</settings>\n" + "  <bean>\n" + "    <name>MineBean</name>\n" + "  </bean>\n" + "</test>";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testSavePreviouslyEditedFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Create new XML file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      assertEquals("Untitled file.xml *", IDE.EDITOR.getTabTitle(1));
      assertEquals(DEFAULT_XML_CONTENT, IDE.EDITOR.getTextFromCodeEditor(0));

      //Save new XML file:
      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT);
      assertFalse(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FILE_NAME));

      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT);
      assertTrue(IDE.PROJECT.EXPLORER.isItemVisible(PROJECT + "/" + FILE_NAME));

      assertEquals(FILE_NAME, IDE.EDITOR.getTabTitle(1));
      IDE.EDITOR.closeFile(1);

      //Check that the files created on the server
      Response response = VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FILE_NAME);
      assertEquals(200, response.getStatusCode());
      assertEquals(DEFAULT_XML_CONTENT + "\n", response.getData());

      //Edit file and save it:
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.deleteFileContent(1);
      IDE.EDITOR.typeTextIntoEditor(1, XML_TEXT1);
      IDE.EDITOR.typeTextIntoEditor(1, XML_TEXT2);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      waitForLoaderDissapeared();
      IDE.EDITOR.closeFile(1);

      selenium.refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      //TODO remove opening project, when saving current project is done:
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      //Open file:
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      assertEquals(FORMATTED_XML_TEXT, IDE.EDITOR.getTextFromCodeEditor(0));

      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      //Edit file and save
      IDE.EDITOR.typeTextIntoEditor(0, "<root>admin</root>");

      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
   }

   /**
    * Bug http://jira.exoplatform.org/browse/IDE-342.
    * 
    * Type one letter to just created and saved file.
    * @throws Exception
    */
   @Test
   public void testEditAndSaveJustCreatedFile() throws Exception
   {
      selenium.refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Open new file and save it:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      //File is not modified:
      assertFalse(IDE.EDITOR.isFileContentChanged(FILE_NAME));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      //Modify file:
      IDE.EDITOR.typeTextIntoEditor(0, "X");
      IDE.EDITOR.waitFileContentModificationMark(FILE_NAME);

      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
   }

   /**
     * Bug http://jira.exoplatform.org/browse/IDE-342.
     * 
     * Create, save and close file. Than open file and type one letter.
     * @throws Exception
     */
   @Test
   public void testOpenEditAndSaveJustCreatedFile() throws Exception
   {
      selenium.refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      //Open new file and save it:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.closeFile(1);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //File is not modified:
      assertFalse(IDE.EDITOR.isFileContentChanged(FILE_NAME));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));

      //Modify file:
      IDE.EDITOR.typeTextIntoEditor(1, "X");
      IDE.EDITOR.waitFileContentModificationMark(FILE_NAME);

      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.SAVE));
   }
}
