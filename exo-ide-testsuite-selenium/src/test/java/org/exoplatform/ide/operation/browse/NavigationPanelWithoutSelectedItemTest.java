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
   
   private static final String TEST_FOLDER_NAME = "test";
   private static final String TEST_FILE_NAME = "test.html";
   private static final String SAVE_ERROR_MESSAGE = "Please, select target folder in the Workspace Panel before calling this command !";
   
   @Test
   public void testNavigationPanelWithoutSelectedItemTest() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      
      // create and select folder
      createFolder(TEST_FOLDER_NAME);
      selectItemInWorkspaceTree(TEST_FOLDER_NAME);
      
      // check command accessibility in top menu and main toolbar when Content Panel is empty
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, true);  
      
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
      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, true);      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);      

      checkToolbarButtonState(ToolbarCommands.File.CUT_SELECTED_ITEM, true);      
      checkToolbarButtonState(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
//      checkToolbarButtonState(ToolbarCommands.File.NEW, true);
      checkToolbarButtonState(ToolbarCommands.File.REFRESH, true);      
      checkToolbarButtonState(ToolbarCommands.File.SAVE, false);
      checkToolbarButtonState(ToolbarCommands.File.SAVE_AS, false);
      checkToolbarButtonState(ToolbarCommands.File.SEARCH, true);      
      
      // un-select folder item "test" in the navigation panel
      selenium.controlKeyDown();
      selectItemInWorkspaceTree(TEST_FOLDER_NAME);
      selenium.controlKeyUp();
      
      Thread.sleep(TestConstants.SLEEP);
      
      // check command accessibility in top menu and main toolbar when Content Panel is empty and there is no selected item in the Navigation Panel
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, false);  
      
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

      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);      
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      

      checkToolbarButtonState(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
      checkToolbarButtonState(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
//      checkToolbarButtonState(ToolbarCommands.File.NEW, true);
      checkToolbarButtonState(ToolbarCommands.File.REFRESH, false);      
      checkToolbarButtonState(ToolbarCommands.File.SAVE, false);
      checkToolbarButtonState(ToolbarCommands.File.SAVE_AS, false);
      checkToolbarButtonState(ToolbarCommands.File.SEARCH, false);        
      
      // create file when there is no selected item in the navigation panel
      createFileFromToolbar(MenuCommands.New.HTML_FILE);
      
      // check command accessibility in top menu and main toolbar and there is no selected item in the Navigation Panel
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, false);  
      
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

      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, false);      
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      

      checkToolbarButtonState(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
      checkToolbarButtonState(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
//      checkToolbarButtonState(ToolbarCommands.File.NEW, true);
      checkToolbarButtonState(ToolbarCommands.File.REFRESH, false);      
      checkToolbarButtonState(ToolbarCommands.File.SAVE, false);
      checkToolbarButtonState(ToolbarCommands.File.SAVE_AS, false);
      checkToolbarButtonState(ToolbarCommands.File.SEARCH, false);  

      // trying to save file new file by using "Ctrl+S" hotkey
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      
      // verify appearance of dialog with error message 
      selenium.isTextPresent(SAVE_ERROR_MESSAGE);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      // save and change new file
      selectItemInWorkspaceTree(TEST_FOLDER_NAME);
      saveAsUsingToolbarButton(TEST_FILE_NAME);
      selectItemInWorkspaceTree(TEST_FILE_NAME);
      
      // change file content
      typeTextIntoEditor(0, "Sample text");
      
      // check command accessibility in top menu and main toolbar when Content Panel is non-empty and there is no selected item in the Navigation Panel
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, true);  
      
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
      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, true);      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, true);      

      checkToolbarButtonState(ToolbarCommands.File.CUT_SELECTED_ITEM, true);      
      checkToolbarButtonState(ToolbarCommands.File.COPY_SELECTED_ITEM, true);
//      checkToolbarButtonState(ToolbarCommands.File.NEW, true);
      checkToolbarButtonState(ToolbarCommands.File.REFRESH, true);      
      checkToolbarButtonState(ToolbarCommands.File.SAVE, true);
      checkToolbarButtonState(ToolbarCommands.File.SAVE_AS, true);
      checkToolbarButtonState(ToolbarCommands.File.SEARCH, true);      
      
      // un-select item "test.groovy" in the navigation panel
      selenium.controlKeyDown();
      selectItemInWorkspaceTree(TEST_FILE_NAME);
      selenium.controlKeyUp();
      
      Thread.sleep(TestConstants.SLEEP);
      
      // check command accessibility in top menu and main toolbar and there is no selected item in the Navigation Panel
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DELETE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.DOWNLOAD_ZIPPED_FOLDER, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.REFRESH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.RENAME, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE, true);      
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SEARCH, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.UPLOAD, false);  
      
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

      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GET_URL, false);      
      checkMenuCommandState(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER, true);      
      
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.CUT_MENU, false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU, false);      

      checkToolbarButtonState(ToolbarCommands.File.CUT_SELECTED_ITEM, false);      
      checkToolbarButtonState(ToolbarCommands.File.COPY_SELECTED_ITEM, false);
//      checkToolbarButtonState(ToolbarCommands.File.NEW, true);
      checkToolbarButtonState(ToolbarCommands.File.REFRESH, false);      
      checkToolbarButtonState(ToolbarCommands.File.SAVE, true);
      checkToolbarButtonState(ToolbarCommands.File.SAVE_AS, false);
      checkToolbarButtonState(ToolbarCommands.File.SEARCH, false);  
   }

   @After
   public void testTearDown() throws IOException
   {
      // deleteCookies();
      cleanRegistry();
      cleanRepository(REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
