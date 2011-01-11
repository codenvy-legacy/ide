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

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Dialogs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-112 : Cut folder
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CutFolderTest extends BaseTest
{
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private final static String FOLDER_1 = "test 1";

   private final static String FOLDER_2 = "test 2";
    
   private final static String FOLDER_3 = "test 2";

   private final static String FILE_1 = "test.groovy";
   
   private static final String FILE_CONTENT = "file content";
   
   private static final String CHANGED_FILE_CONTENT = "changed ";

   /**
    * Create next folders' structure in the workspace root:
    * test 1/
    *    test 2/
    *        test.groovy - file with sample content  
    * test 2/
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1 + "/" + FOLDER_2);
         VirtualFileSystemUtils.put(FILE_CONTENT.getBytes(), MimeType.APPLICATION_GROOVY, URL + FOLDER_1 + "/" 
            + FOLDER_2 + "/" + FILE_1);
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
         VirtualFileSystemUtils.delete(URL + FOLDER_1);
         VirtualFileSystemUtils.delete(URL + FOLDER_3);
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
      waitForRootElement();
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

      /*
       * 2.Open file "test 1/test 2/test.groovy".
       */
      openFileFromNavigationTreeWithCodeEditor(FILE_1, false);
      
      /*
       * Paste commands are disabled, Cut/Copy are enabled
       */
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, true);

      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);

      /*
       * 3. Select folder "test 1/test 2". Click on "Cut" toolbar button.
       * 
       * Select row number 2, because there are two "test 2" folders.
       */
      IDE.navigator().selectRow(2);
//      selectItemInWorkspaceTree(FOLDER_2);
      IDE.toolbar().runCommand(MenuCommands.Edit.CUT_TOOLBAR);
      /*
       * Paste commands are enabled.
       */
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      /*
       * 4. Select file "test 1/test 2/test.groovy" in the Workspace Panel.
       */
      selectItemInWorkspaceTree(FILE_1);

      /*
       * Paste commands are enabled.
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * 5. Select folder "test 1/test 2/" and click on "Paste" toolbar button.
       * Select row number 2, because there are two "test 2" folders.
       */
      IDE.navigator().selectRow(2);
//      selectItemInWorkspaceTree(FOLDER_2);
      IDE.toolbar().runCommand(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Error message about impossibility to paste folder into the itself should be displayed. 
       */
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
      IDE.dialogs().clickOkButton();
      assertFalse(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));

      /*
       * After closing error message dialog the toolbar button "Paste" and topmenu command "Edit->Paste Items" 
       * should be still enabled.
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * 6. Select root item and then click on "Paste" toolbar button.
       */
      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommand(MenuCommands.Edit.PASTE_TOOLBAR);

      /*
       * Error message about impossibility to paste folder with the existed name should be displayed. 
       */
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
      IDE.dialogs().clickOkButton();
      assertFalse(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));

      /*
       * After closing error message dialog the toolbar button "Paste" and topmenu command "Edit->Paste Items" 
       * should be still enabled.
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);

      /*
       * 7. Select folders "test 1" and "test 2".
       */
      selectItemInWorkspaceTree(FOLDER_1);

      selenium.keyPressNative("" + KeyEvent.VK_CONTROL);
      selectItemInWorkspaceTree(FOLDER_2);
      selenium.keyUpNative("" + KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      /*
       * "Paste" commands should be enabled.
       */
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      /*
       * 8. Select file "test 1/test 2/test.groovy".
       */
      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_1);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_2);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FILE_1);

      /*
       * "Paste" commands should be enabled.
       */
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      /*
       * 9. Select "test 2" item and then select "Edit->Paste Items" topmenu command.
       * 
       * Select row number 4, because there are two "test 2" folders.
       */
      IDE.navigator().selectRow(4);
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      /*
       * Check, that file name stays the same (IDE-225 issue).
       */
      assertEquals(FILE_1, IDE.editor().getTabTitle(0));
      assertElementPresentInWorkspaceTree(FOLDER_1);
      checkItemsOnWebDav();

      /*
       * 10. Change content of opened file "test.groovy" in Content Panel, 
       * save file, close file tab and open file "test 2/test 2/test.groovy".
       */
      typeTextIntoEditor(0, CHANGED_FILE_CONTENT);
      saveCurrentFile();
      IDE.editor().closeTab(0);

      IDE.navigator().selectRow(3);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      openFileFromNavigationTreeWithCodeEditor(FILE_1, false);
      
      checkCodeEditorOpened(0);
      assertEquals(CHANGED_FILE_CONTENT + FILE_CONTENT, getTextFromCodeEditor(0));

      /*
       * Check folders
       */
      assertEquals("test 1", IDE.navigator().getRowTitle(1));
      assertEquals("test 2", IDE.navigator().getRowTitle(2));
      assertEquals("test 2", IDE.navigator().getRowTitle(3));
      assertEquals("test.groovy", IDE.navigator().getRowTitle(4));
      
      /*
       * check there is no another element in the tree
       */
      assertFalse(selenium.isElementPresent(IDE.navigator().getScLocator(5, 0)));
   }

   private void checkItemsOnWebDav() throws Exception
   {
      HTTPResponse response = VirtualFileSystemUtils.get(URL + FOLDER_1);
      assertEquals(200, response.getStatusCode());
      
      response = VirtualFileSystemUtils.get(URL + FOLDER_1 + "/" + FOLDER_2);
      assertEquals(HTTPStatus.NOT_FOUND, response.getStatusCode());
      
      response = VirtualFileSystemUtils.get(URL + FOLDER_2);
      assertEquals(200, response.getStatusCode());
      
      response = VirtualFileSystemUtils.get(URL + FOLDER_2 + "/" + FOLDER_2);
      assertEquals(200, response.getStatusCode());

      response = VirtualFileSystemUtils.get(URL + FOLDER_2 + "/" + FOLDER_2 + "/" + FILE_1);
      assertEquals(200, response.getStatusCode());
   }

}
