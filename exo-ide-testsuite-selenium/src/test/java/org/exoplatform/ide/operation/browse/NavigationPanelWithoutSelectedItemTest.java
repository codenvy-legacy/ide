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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
      createFolder(TEST_FOLDER_NAME);
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      
      // check command accessibility in top menu and main toolbar when Content Panel is empty
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);  
      
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
      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);      

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
//      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.REFRESH, true);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SEARCH, true);      
      
      // un-select folder item "test" in the navigation panel
      selenium.controlKeyDown();
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      selenium.controlKeyUp();
      
      Thread.sleep(TestConstants.SLEEP);
      
      // check command accessibility in top menu and main toolbar when Content Panel is empty and there is no selected item in the Navigation Panel
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);  
      
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
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);  

      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);      
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
//      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.REFRESH, false);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SEARCH, false);        
      
      // create file when there is no selected item in the navigation panel
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      
      // check command accessibility in top menu and main toolbar and there is no selected item in the Navigation Panel
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);  
      
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
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);  

      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);      
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
//      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.REFRESH, false);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SEARCH, false);  

      // trying to save file new file by using "Ctrl+S" hotkey
      IDE.editor().runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      
      // verify appearance of dialog with error message 
      selenium.isTextPresent(SAVE_ERROR_MESSAGE);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      // save and change new file
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      saveAsUsingToolbarButton(TEST_FILE_NAME);
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      
      // change file content
      IDE.editor().typeTextIntoEditor(0, "Sample text");
      
      // check command accessibility in top menu and main toolbar when Content Panel is non-empty and there is no selected item in the Navigation Panel
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, true);  
      
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
      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);      

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, true);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
//      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.REFRESH, true);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE_AS, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SEARCH, true);      
      
      // un-select item "test.groovy" in the navigation panel
      selenium.controlKeyDown();
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER_NAME + "/");
      selenium.controlKeyUp();
      
      Thread.sleep(TestConstants.SLEEP);
      
      // check command accessibility in top menu and main toolbar and there is no selected item in the Navigation Panel
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE, false);  
      
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
//      checkMenuCommandState(MenuCommands.New.NEW, MenuCommands.New.FOLDER, false);  

      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
      IDE.menu().checkCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);      
      
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
//      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.NEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.REFRESH, false);      
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.SEARCH, false);  
   }

   @After
   public void testTearDown() throws IOException
   {
      // deleteCookies();
      cleanRegistry();
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
