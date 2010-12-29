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
package org.exoplatform.ide.operation.cutcopy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
//IDE-112
public class CutFolderTest extends BaseTest
{
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private final static String FOLDER_1 = "test 1";

   private final static String FOLDER_2 = "test 2";
    
   private final static String FOLDER_3 = "test 2";

   private final static String FILE_1 = "test.groovy";

   @BeforeClass
   public static void setUp()
   {
      String filePath ="src/test/resources/org/exoplatform/ide/operation/file/fileforrename.txt";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1 + "/" + FOLDER_2);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE,"exo:groovyResourceContainer", URL + FOLDER_1 + "/" + FOLDER_2 +"/" + FILE_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_3);
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
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL +FOLDER_1);
         VirtualFileSystemUtils.delete(URL +FOLDER_3);
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
   
   @Test
   public void testCutFolderOperation() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);

      //      step 1 - Open Gadget window and create next folders' structure in the workspace root:
      //     "test 1/test 2/test.groovy" file with sample content  and "test 2" folder

      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      assertElementPresentInWorkspaceTree(FOLDER_1);
      
      selectItemInWorkspaceTree(FOLDER_1);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      assertElementPresentInWorkspaceTree(FOLDER_2);
      
      selectItemInWorkspaceTree(FOLDER_2);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      assertElementPresentInWorkspaceTree(FILE_1);
      
      assertElementPresentInWorkspaceTree(FOLDER_3);

      //step2 - Open file "test 1/test 2/test.groovy

      selectRootOfWorkspaceTree();
      openFileFromNavigationTreeWithCodeEditor(FILE_1, false);

      // check in toolbar
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, true);

      //check in menu
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);

      //step 3 - Select folder "test 1/test 2". Click on "Cut" toolbar button.
      selectItemInWorkspaceTree(FOLDER_2);
      IDE.toolbar().runCommand(MenuCommands.Edit.CUT_TOOLBAR);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      //step 4 - Select file "test 1/test 2/test.groovy" in the Workspace Panel.
      selectItemInWorkspaceTree(FOLDER_2);
      selectItemInWorkspaceTree(FILE_1);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      //step5 - Select folder "test 1/test 2/" and click on "Paste" toolbar button.
      selectItemInWorkspaceTree(FOLDER_2);
      IDE.toolbar().runCommand(MenuCommands.Edit.PASTE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      
      openOrCloseFolder(FOLDER_2);

      //step 6 - Select root item and then click on "Paste" toolbar button.
      selectRootOfWorkspaceTree();
      
      IDE.toolbar().runCommand(MenuCommands.Edit.PASTE_TOOLBAR);
      
      //there is a message 412 Precondition Failed
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      // step 7 - Select folders "test 1" and "test 2".
      selectItemInWorkspaceTree(FOLDER_1);
      
      selenium.keyPressNative(""+KeyEvent.VK_CONTROL);
      selectItemInWorkspaceTree(FOLDER_2);
      selenium.keyUpNative(""+KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      // step 8 - Select file "test 1/test 2/test.groovy".
      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      openOrCloseFolder(FOLDER_1);
      openOrCloseFolder(FOLDER_2);
      selectItemInWorkspaceTree(FILE_1);
      
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      //step 9 - Select "test 2" item and then select "Edit->Paste Items" topmenu command.

      //select folder test 2 from root folder
      //as we have two folders with test 2 name, so here we use number of row in navigation tree
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      //TODO: http://jira.exoplatform.org/browse/IDE-225
      String newFileTabName = IDE.editor().getTabTitle(0);
      assertEquals(FILE_1, newFileTabName);
      assertElementPresentInWorkspaceTree(FOLDER_1);

      //step 10 - Change content of opened file "test.groovy" in Content Panel, click on "Ctrl+S" hot key, close file tab and open file "test 2/test 2/test.groovy".
      typeTextIntoEditor(0, "Content has been changed");
      saveCurrentFile();
      
      IDE.editor().closeTab(0);

      checkItemsOnWebDav();
      
      Thread.sleep(TestConstants.SLEEP*3);

      //open second folder test 2
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[0]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_1, false);

      // Check folders
      assertEquals("test 1", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]"));
      assertEquals("test 2", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[fieldName=name||0]"));
      assertEquals("test 2", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[fieldName=name||0]"));
      assertEquals("test.groovy", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[fieldName=name||0]"));
      //check there is no another element in the tree
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[5]/col[fieldName=name||0]"));

      //close test 2/test 2 folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[fieldName=name||0]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check that test.groovy file disappeared
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[4]/col[fieldName=name||0]"));

      //close test 2 folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[2]/col[fieldName=name||0]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //check that test 2/test 2 folder disappeared
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[fieldName=name||0]"));

      //open test 1 folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[fieldName=name||0]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //check no elements appeared
      assertFalse(selenium
         .isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[3]/col[fieldName=name||0]"));
   }

   private void checkItemsOnWebDav() throws Exception
   {
      HTTPResponse response = VirtualFileSystemUtils.get(URL + FOLDER_1);
      assertEquals(200, response.getStatusCode());
      
      response = VirtualFileSystemUtils.get(URL + FOLDER_2);
      assertEquals(200, response.getStatusCode());
      
      response = VirtualFileSystemUtils.get(URL + FOLDER_2 + "/" + FOLDER_2);
      assertEquals(200, response.getStatusCode());

      response = VirtualFileSystemUtils.get(URL + FOLDER_2 + "/" + FOLDER_2 + "/" + FILE_1);
      assertEquals(200, response.getStatusCode());
   }

}
