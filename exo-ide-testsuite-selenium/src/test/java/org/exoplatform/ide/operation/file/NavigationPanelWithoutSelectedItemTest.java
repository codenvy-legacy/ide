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

import java.io.IOException;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.After;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class NavigationPanelWithoutSelectedItemTest extends BaseTest
{
   
   private static final String TEST_FOLDER_NAME = NavigationPanelWithoutSelectedItemTest.class.getSimpleName();
   private static final String TEST_FILE_NAME = "test.html";
   private static final String SAVE_ERROR_MESSAGE = "Please, select target folder in the Workspace Panel before calling this command !";
   
   @Test
   public void testNavigationPanelWithoutSelectedItemTest() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      
      // create and select folder
      IDE.NAVIGATION.createFolder(TEST_FOLDER_NAME);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      
      // check command accessibility in top menu and main toolbar when Content Panel is empty
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, false);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);  
      
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.CSS_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GOOGLE_GADGET_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_SCRIPT_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_TEMPLATE_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.HTML_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.JAVASCRIPT_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.REST_SERVICE_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.TEXT_FILE, true);      
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.XML_FILE, true);           
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FROM_TEMPLATE, true); 
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, true);   
      
      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);
      
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);      

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);      
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
//      IDE.TOOLBAR.checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, true);      
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SEARCH, true);      
      
      // un-select folder item "test" in the navigation panel
      
      
      //------------------------------------------------------------------
      // TODO After of capability select the few elements in IDE navigator 
      selenium.controlKeyDown();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      selenium.controlKeyUp();
      
      Thread.sleep(TestConstants.SLEEP);
      
      // check command accessibility in top menu and main toolbar when Content Panel is empty and there is no selected item in the Navigation Panel
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);  
//      
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.CSS_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GOOGLE_GADGET_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_SCRIPT_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_TEMPLATE_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.HTML_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.JAVASCRIPT_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.REST_SERVICE_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.TEXT_FILE, true);      
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.XML_FILE, true);           
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FROM_TEMPLATE, true); 
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);  
//
//      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);      
//      
//      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      
//
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
////      IDE.TOOLBAR.checkButtonEnabled(ToolbarCommands.File.NEW, true);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);      
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SEARCH, false);        
//      
      // create file when there is no selected item in the navigation panel
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
//      
//      // check command accessibility in top menu and main toolbar and there is no selected item in the Navigation Panel
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);  
//      
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.CSS_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GOOGLE_GADGET_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_SCRIPT_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_TEMPLATE_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.HTML_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.JAVASCRIPT_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.REST_SERVICE_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.TEXT_FILE, true);      
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.XML_FILE, true);           
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FROM_TEMPLATE, true); 
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);  
//
//      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);      
//      
//      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      
//
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
////      IDE.TOOLBAR.checkButtonEnabled(ToolbarCommands.File.NEW, true);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);      
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SEARCH, false);  
 //-----------------------------------------------------------------------------------------------------------------------
      // trying to save file new file by using "Ctrl+S" hotkey
     IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      
      // verify appearance of dialog with error message 
     // selenium.isTextPresent(SAVE_ERROR_MESSAGE);
      //selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      // save and change new file
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      saveAsUsingToolbarButton(TEST_FILE_NAME);
      Thread.sleep(20000);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER_NAME + "/" + TEST_FILE_NAME);
      
      // change file content
     IDE.EDITOR.typeTextIntoEditor(0, "Sample text");
      
      // check command accessibility in top menu and main toolbar when Content Panel is non-empty and there is no selected item in the Navigation Panel
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);  
      
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.CSS_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GOOGLE_GADGET_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_SCRIPT_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_TEMPLATE_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.HTML_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.JAVASCRIPT_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.REST_SERVICE_FILE, true);
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.TEXT_FILE, true);      
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.XML_FILE, true);           
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FROM_TEMPLATE, true); 
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, true);   
      
      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, true);      
      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);
      
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);      

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);      
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
//      IDE.TOOLBAR.checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, true);      
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SEARCH, true);      
      
//      // un-select item "test.groovy" in the navigation panel
//      //------------------------------------------------------------------
//      // TODO After of capability select the few elements in IDE navigator 
//      selenium.controlKeyDown();
//      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER_NAME + "/");
//      selenium.controlKeyUp();
//      
//      Thread.sleep(TestConstants.SLEEP);
//      
//      // check command accessibility in top menu and main toolbar and there is no selected item in the Navigation Panel
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);  
//      
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.CSS_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GOOGLE_GADGET_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_SCRIPT_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.GROOVY_TEMPLATE_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.HTML_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.JAVASCRIPT_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.REST_SERVICE_FILE, true);
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.TEXT_FILE, true);      
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.XML_FILE, true);           
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FROM_TEMPLATE, true); 
////      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);  
//
//      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
//      IDE.MENU.checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);      
//      
//      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
//      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      
//
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
////      IDE.TOOLBAR.checkButtonEnabled(ToolbarCommands.File.NEW, true);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.REFRESH, false);      
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
//      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SEARCH, false);  
   }

   @After
   public void testTearDown() throws IOException
   {
      // deleteCookies();
      cleanRegistry();
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
