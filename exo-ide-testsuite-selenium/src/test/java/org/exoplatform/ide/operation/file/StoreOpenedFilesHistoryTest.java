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

import static org.junit.Assert.*;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
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

   private static String SECOND_WORKSPACE_URL;
   
   private String secondWorkspaceName;
   
   private static final String TEXT_FILE = "Text File";
   
   private static final String HTML_FILE = "Html File";
   
   private static final String GADGET_FILE = "Gadget File";
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME_2 + "/";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER_TO_DELETE);
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
      deleteCookies();
      cleanRegistry();
      try
      {
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER);
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
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE);
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
   
   //IDE-66
   //Store Opened Files History 
   @Test
   public void storeOpenedFilesHistory() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
    
      secondWorkspaceName = getNonActiveWorkspaceName();
      SECOND_WORKSPACE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + secondWorkspaceName + "/";
      
      //select another workspace
      selectWorkspace(secondWorkspaceName);
      
      selectItemInWorkspaceTree(secondWorkspaceName);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      selectItemInWorkspaceTree(TEST_FOLDER_TO_DELETE);
      
      //create txt file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      saveAsUsingToolbarButton(TEXT_FILE);
      
      selectItemInWorkspaceTree(TEST_FOLDER);
      
      //create html file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      saveAsUsingToolbarButton(HTML_FILE);
      
      //create google gadget file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      saveAsUsingToolbarButton(GADGET_FILE);
      
      //create groovy script file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      
      //closing all files
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      openFileFromNavigationTreeWithCodeEditor(TEXT_FILE, false);
      openFileFromNavigationTreeWithCodeEditor(GADGET_FILE, false);
      openFileFromNavigationTreeWithCkEditor(HTML_FILE, true);
      
      checkCkEditorOpened(2);
      
      //delete Test Folder to Delete from server
      try
      {
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      
      selenium.open("http://www.google.com.ua/");
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      refresh();
      Thread.sleep(TestConstants.IDE_LOAD_PERIOD);
      
      checkCkEditorOpened(1);
      
      checkOpenedFilesHistory();
      
      IDE.editor().closeTab(0);
      IDE.editor().closeTabWithNonSaving(0);
      
      //open folder to select html file
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      assertElementPresentInWorkspaceTree(GADGET_FILE);
      assertElementPresentInWorkspaceTree(HTML_FILE);
   }
   
   /**
    * 
    * @throws Exception
    */
   private void checkOpenedFilesHistory() throws Exception
   {
      //check that files are opened and in right order.
      //check that tab with html file is selected
      assertEquals(GADGET_FILE, IDE.editor().getTabTitle(0));
      assertTrue(IDE.editor().getTabTitle(1).equals(HTML_FILE) 
         || IDE.editor().getTabTitle(1).equals(HTML_FILE + " *"));
      
      //select Gadget file
      IDE.editor().selectTab(0);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.FORMAT, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
}
