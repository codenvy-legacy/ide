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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-66.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 4, 2011 $
 *
 */
public class StoreOpenedFilesHistoryTest extends BaseTest
{
   private static final String TEST_FOLDER = StoreOpenedFilesHistoryTest.class.getSimpleName();

   private static final String TEST_FOLDER_TO_DELETE = StoreOpenedFilesHistoryTest.class.getSimpleName() + "-to Delete";

   private static String SECOND_WORKSPACE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME_2 + "/";

   private static final String TEXT_FILE = "Text File";

   private static final String HTML_FILE = "Html File";

   private static final String GADGET_FILE = "Gadget File";

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME_2 + "/";
   
   private static final String CK_EDITOR_FRAME_LOCATOR = "//div[@panel-id='editor' and @tab-index='1']//table[@class='cke_editor']//td[@class='cke_contents']/iframe";

   @BeforeClass
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER_TO_DELETE);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Can't create folders");
      }
   }

   @AfterClass
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      try
      {
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      deleteCookies();
      cleanRegistry();
   }

   //IDE-66
   //Store Opened Files History 
   @Test
   public void storeOpenedFilesHistory() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      //select another workspace
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      IDE.SELECT_WORKSPACE.waitForDialog();
      IDE.SELECT_WORKSPACE.doubleClickInListGrid(WS_NAME_2);

      IDE.WORKSPACE.waitForItem(SECOND_WORKSPACE_URL);
      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE + "/");
      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE + "/");

      //create txt file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      
      saveAsUsingToolbarButton(TEXT_FILE);
      IDE.WORKSPACE.waitForItem(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE + "/" + TEXT_FILE);
      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/");

      //create html file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      saveAsUsingToolbarButton(HTML_FILE);
      IDE.WORKSPACE.waitForItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/" + HTML_FILE);

      //create google gadget file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      saveAsUsingToolbarButton(GADGET_FILE);
      IDE.WORKSPACE.waitForItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/" + GADGET_FILE);

      //create groovy script file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);

      //closing all files
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);

      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE + "/" + TEXT_FILE);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.OPENWITH.openSelectedFileWithCodeEditor(false);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      
      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/" + GADGET_FILE);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.OPENWITH.openSelectedFileWithCodeEditor(false);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      
      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/" + HTML_FILE);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.OPENWITH.openSelectedFileWithCkEditor(true);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      
      IDE.EDITOR.checkCkEditorOpened(2);

      //delete Test Folder to Delete from server
      try
      {
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      selenium().open("http://www.google.com.ua/");
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);

      selenium().goBack();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);

      //IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.waitForItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/");
      waitForElementPresent(CK_EDITOR_FRAME_LOCATOR);

      
      IDE.EDITOR.checkCkEditorOpened(1);

      checkOpenedFilesHistory();

      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.closeFile(0);

      //open folder to select html file
      IDE.WORKSPACE.selectItem(SECOND_WORKSPACE_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.assertItemVisible(SECOND_WORKSPACE_URL + TEST_FOLDER + "/" + GADGET_FILE);
      IDE.NAVIGATION.assertItemVisible(SECOND_WORKSPACE_URL + TEST_FOLDER + "/" + HTML_FILE);
   }

   /**
    * Check, that two files: gadget and html file are opened
    * and check menu commands for enabling and disabling.
    * 
    * @throws Exception
    */
   private void checkOpenedFilesHistory() throws Exception
   {
      //check that files are opened and in right order.
      //check that tab with html file is selected
      assertEquals(GADGET_FILE, IDE.EDITOR.getTabTitle(0));
      assertTrue(IDE.EDITOR.getTabTitle(1).equals(HTML_FILE) || IDE.EDITOR.getTabTitle(1).equals(HTML_FILE + " *"));

      //select Gadget file
      IDE.EDITOR.selectTab(0);

      //IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW, true);
      //IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, true);
   }

}
