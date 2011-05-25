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

import java.util.UUID;

import org.exoplatform.common.http.HTTPStatus;
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
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class CutFileTest extends BaseTest
{
   private static final String FILE_NAME_1 = "CutFileTest.txt";

   private static final String FOLDER_NAME_1 = CutFileTest.class.getSimpleName() + "-1";

   private static final String FOLDER_NAME_2 = CutFileTest.class.getSimpleName() + "-2";

   private static final String RANDOM_CONTENT = UUID.randomUUID().toString();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_2);
         VirtualFileSystemUtils.put(RANDOM_CONTENT.getBytes(), MimeType.TEXT_PLAIN, WS_URL + FOLDER_NAME_1 + "/"
            + FILE_NAME_1);
         VirtualFileSystemUtils.put(RANDOM_CONTENT.getBytes(), MimeType.TEXT_PLAIN, WS_URL + FOLDER_NAME_2 + "/"
            + FILE_NAME_1);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_2);
         VirtualFileSystemUtils.delete(WS_URL + FILE_NAME_1);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //IDE-114
   @Test
   public void testCutFile() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME_1 + "/");

      //      waitForRootElement();
      //      IDE.WORKSPACE.selectRootItem();
      //      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      //      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_1 + "/");
      //      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_2 + "/");

      //Open files "test 1/gadget.xml".
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME_1 + "/" + FILE_NAME_1, false);

      checkPasteCommands(false);
      checkCutCopyCommands(true);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_1 + "/" + FILE_NAME_1);

      checkPasteCommands(false);
      checkCutCopyCommands(true);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_1 + "/" + FILE_NAME_1);

      //Call the "Edit->Cut Items" topmenu command.
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);
      checkPasteCommands(true);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_1 + "/");
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      IDE.WARNING_DIALOG.checkIsOpened("Can't move items in the same directory!");
      IDE.WARNING_DIALOG.clickOk();

      checkPasteCommands(true);

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + FOLDER_NAME_1 + "/");
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_2 + "/");

      //Select "test 2" folder item in the Workspace Panel and then select "Edit->Paste Items" topmenu command.
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      /*
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
      assertTrue(selenium.isTextPresent("412 Precondition Failed"));
      assertTrue(selenium.isTextPresent("Precondition Failed"));
      assertTrue(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG_OK_BTN));
      IDE.dialogs().clickOkButton();
      */

      IDE.WARNING_DIALOG.checkIsOpened();
      IDE.WARNING_DIALOG.clickOk();

      checkPasteCommands(true);

      //Select root item and then click on "Paste" toolbar button.
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      assertEquals(HTTPStatus.NOT_FOUND, VirtualFileSystemUtils.get(WS_URL + FOLDER_NAME_1 + "/" + FILE_NAME_1)
         .getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(WS_URL + FILE_NAME_1).getStatusCode());

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + FILE_NAME_1);

      assertEquals(RANDOM_CONTENT, IDE.EDITOR.getTextFromCodeEditor(0));

      checkPasteCommands(false);

      //Change content of opened file "gadget.xml" in Content Panel, click on "Ctrl+S" hot key, 
      //close file tab and open file "gadget.xml".
      IDE.EDITOR.typeTextIntoEditor(0, "IT`s CHANGE!!!");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      final String oldText = IDE.EDITOR.getTextFromCodeEditor(0);

      IDE.NAVIGATION.saveFile();
      
      IDE.EDITOR.closeFile(0);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FILE_NAME_1, false);
      assertEquals(oldText, IDE.EDITOR.getTextFromCodeEditor(0));
      IDE.EDITOR.closeFile(0);
   }

   /**
    * @throws Exception
    */
   private void checkPasteCommands(boolean enabled) throws Exception
   {
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, enabled);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, enabled);
   }

   /**
    * @throws Exception
    */
   private void checkCutCopyCommands(boolean enabled) throws Exception
   {
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, enabled);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, enabled);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.CUT_TOOLBAR, enabled);
      IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Edit.COPY_TOOLBAR, enabled);
   }

}
