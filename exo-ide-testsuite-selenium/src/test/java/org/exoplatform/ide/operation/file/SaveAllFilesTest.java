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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * IDE-54:Save All Files
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class SaveAllFilesTest extends BaseTest
{
   
   private static final String FOLDER_NAME = "SaveAllFilesTest";
   
   private static final String FOLDER_NAME_2 = "SaveAllFilesTest 2";
   
   private static final String SAVED_FILE_XML = "Saved File.xml";
   
   private static final String SAVED_FILE_GROOVY = "Saved File.groovy";
   
   private static final String NEW_HTML_FILE_NAME = "Untitled file.html";
   
   private static final String NEW_TEXT_FILE_NAME = "Untitled file.txt";
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME);
         VirtualFileSystemUtils.delete(BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME_2);
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
   
   //IDE-54:Save All Files
   //@Ignore
   @Test
   public void saveAllFiles() throws Exception
   {
      //---- 2 ----------------
      //Create  "Test" and  "Test 2" folders in the root folder.
      createFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      
      selectRootOfWorkspaceTree();
      createFolder(FOLDER_NAME_2);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 3 ----------------
      //Create file "Saved File.xml" in "Test", "Saved File.groovy" in the "Test 2" folder.
      selectItemInWorkspaceTree(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //create new xml file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //check Save All command is disabled
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //save file
      saveAsUsingToolbarButton(SAVED_FILE_XML);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP);
      
      //create new groovy file
      selectItemInWorkspaceTree(FOLDER_NAME_2);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //Save All command is disabled
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      saveAsByTopMenu(SAVED_FILE_GROOVY);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP);
      //Save All command is disabled
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      
      //---- 4 ----------------
      //Click on "Test 2" folder in "Workspace" panel.
      selectItemInWorkspaceTree(FOLDER_NAME_2);
      //Save All command is disabled
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      
      //---- 5 ----------------
      //Click on "New->From Template" toolbar button, select "Empty HTML" template, 
      //type name "Untitled file.html" and then click on "Create" button. 
      //Click on "New->From Template" button, select "Empty TEXT" template, 
      //type name "Untitled file.txt" and then click on "Create" button.
      
      //create Empty HTML
      createFileFromTemplate("Empty HTML", NEW_HTML_FILE_NAME);
      
      //create Empty Text file
      createFileFromTemplate("Empty TEXT", NEW_TEXT_FILE_NAME);
      //Save All command is disabled
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, false);
      
      //---- 6 ----------------
      //Open and change content of  "Saved File.xml" and "Saved File.groovy".
      openFileFromNavigationTreeWithCodeEditor(SAVED_FILE_XML, false);
      Thread.sleep(TestConstants.SLEEP);
      typeTextIntoEditor(2, "<root>admin</root>");
      
      //open and change content of groovy file
      openFileFromNavigationTreeWithCodeEditor(SAVED_FILE_GROOVY, false);
      Thread.sleep(TestConstants.SLEEP);
      typeTextIntoEditor(3, "changed content of file");
      Thread.sleep(TestConstants.SLEEP);
      
      //Until the step 6 and after the step 7 the "File->Save All" top menu command 
      //should be disabled.
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL, true);
      
      //---- 7 ----------------
      //Click on "Save All" button in File menu.
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL);
      Thread.sleep(TestConstants.SLEEP);
      
      //after step 7
      /*
       * - there are non-saved file tabs "Untitled file.html", "Untitled file.txt" 
       * in the Content Panel marked with "*".
       * 
       * - there are saved files "Saved File.xml" in "Test" folder, 
       * and "Saved File.groovy" in the "Test 2" folder with file tab title without "*".
       */
      
      assertEquals(NEW_HTML_FILE_NAME + " *", getTabTitle(0));
      assertEquals(NEW_TEXT_FILE_NAME + " *", getTabTitle(1));
      assertEquals(SAVED_FILE_XML, getTabTitle(2));
      assertEquals(SAVED_FILE_GROOVY, getTabTitle(3));
      
      //---- 8 ----------------
      //Save and reopen files "Untitled file.groovy" , "Untitled file.xml".
      selectEditorTab(0);
      saveAsUsingToolbarButton(NEW_HTML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      
      //save and close txt file
      selectEditorTab(0);
      saveAsUsingToolbarButton(NEW_TEXT_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      
      //open files
      openFileFromNavigationTreeWithCodeEditor(NEW_TEXT_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(NEW_HTML_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      //After the step 8: there are changed files "Saved File.xml", 
      //"Saved File.groovy", "Untitled file.html", "Untitled file.txt" in the Content Tab.
      assertEquals(SAVED_FILE_XML, getTabTitle(0));
      assertEquals(SAVED_FILE_GROOVY, getTabTitle(1));
      assertEquals(NEW_TEXT_FILE_NAME, getTabTitle(2));
      assertEquals(NEW_HTML_FILE_NAME, getTabTitle(3));
      
      //end
      selectItemInWorkspaceTree(FOLDER_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(FOLDER_NAME_2);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
   }
   

}
