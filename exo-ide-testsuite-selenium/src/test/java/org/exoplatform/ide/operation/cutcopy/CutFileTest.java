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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.UUID;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.CloseFileUtils;
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
   /**
    * 
    */
   private static final String FILE_NAME_1 = "CutFileTest.txt";

   /**
    * 
    */
   private static final String FOLDER_NAME_2 = "CutFileTest2";

   /**
    * 
    */
   private static final String FOLDER_NAME_1 = "CutFileTest1";

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static final String RANDOM_CONTENT = UUID.randomUUID().toString();

   
   @BeforeClass
   public static void setUp()
   {
      
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME_2);
         VirtualFileSystemUtils.put(RANDOM_CONTENT.getBytes(), MimeType.TEXT_PLAIN, URL + FOLDER_NAME_1 + "/" + FILE_NAME_1);
         VirtualFileSystemUtils.put(RANDOM_CONTENT.getBytes(), MimeType.TEXT_PLAIN, URL + FOLDER_NAME_2 + "/" + FILE_NAME_1);
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
   
   
   //IDE-114
   @Test
   public void testCutFile() throws Exception
   {
      Thread.sleep(TestConstants.IDE_INITIALIZATION_PERIOD);

      //      Open Server window and create next folders' structure in the workspace root: 
      //      "test 1/gadget.xml" file with sample content
      //      "test 2/gadget.xml" file with sample content
      String oldText = RANDOM_CONTENT; 
      
      selectItemInWorkspaceTree(WS_NAME);
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);

      openOrCloseFolder(FOLDER_NAME_1);

      selectItemInWorkspaceTree(FOLDER_NAME_2);

      //      Open Gadget window, open files "test 1/gadget.xml".
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME_1, false);

      checkPaste(false);
      
      checkMenuCutCopy(true);

      selectItemInWorkspaceTree(FILE_NAME_1);

      checkPaste(false);

      checkMenuCutCopy(true);

      selectItemInWorkspaceTree(FILE_NAME_1);

      //    Call the "Edit->Cut Items" topmenu command.
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU);

      checkPaste(true);

      selectItemInWorkspaceTree(FOLDER_NAME_1);

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertTrue(selenium.isTextPresent("Can't move items in the same directory!"));
      assertTrue(selenium.isTextPresent("OK"));

      selenium.mouseDownAt("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/", "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      selenium.mouseUpAt("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/", "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkPaste(true);

      openOrCloseFolder(FOLDER_NAME_1);

      selectItemInWorkspaceTree(FOLDER_NAME_2);

      //      Select "test 2" folder item in the Workspace Panel and then select "Edit->Paste Items" topmenu command.
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);

      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/headerLabel/"));
      assertTrue(selenium.isTextPresent("412 Precondition Failed"));
      assertTrue(selenium.isTextPresent("Precondition Failed"));
      assertTrue(selenium.isTextPresent("OK"));

      selenium.mouseDownAt("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/", "");
      selenium.mouseUpAt("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/", "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkPaste(true);

      //      Select root item and then click on "Paste" toolbar button.
      selectRootOfWorkspaceTree();

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      
      assertEquals(HTTPStatus.NOT_FOUND, VirtualFileSystemUtils.get(URL + FOLDER_NAME_1 + "/" + FILE_NAME_1).getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FILE_NAME_1).getStatusCode());
      selectItemInWorkspaceTree(FILE_NAME_1);

      String openedFileContent = getTextFromCodeEditor(0);
      assertEquals(oldText, openedFileContent);

      checkPaste(false);

      //      Change content of opened file "gadget.xml" in Content Panel, click on "Ctrl+S" hot key, close file tab and open file "gadget.xml".
      typeTextIntoEditor(0, "IT`s CHANGE!!!");
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      String oldText1 = getTextFromCodeEditor(0);

      runToolbarButton(ToolbarCommands.File.SAVE);
      
      CloseFileUtils.closeTab(0);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME_1, false);

      assertEquals(oldText1, getTextFromCodeEditor(0));
   }

   /**
    * @throws Exception
    */
   private void checkPaste(boolean enabled) throws Exception
   {
      checkToolbarButtonState(MenuCommands.Edit.PASTE_TOOLBAR, enabled);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, enabled);
   }

   /**
    * @throws Exception
    */
   private void checkMenuCutCopy(boolean enabled) throws Exception
   {
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, enabled);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, enabled);
      checkToolbarButtonState(MenuCommands.Edit.CUT_TOOLBAR, enabled);
      checkToolbarButtonState(MenuCommands.Edit.COPY_TOOLBAR, enabled);
   }

   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME_2);
        
         CloseFileUtils.closeTab(0);
         VirtualFileSystemUtils.delete(URL + FILE_NAME_1);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      } 
   }

}
