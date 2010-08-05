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

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * IDE-96 Go to folder test
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GoToFolderTest extends BaseTest
{
   private final String folder1Name = "Test1";

   private final String folder2Name = "Test2";

   private final String file1Name = "testFile1.xml";

   private final String file2Name = "testFile2.xml";

   @Test
   public void testGoToFolder() throws Exception
   {
      Thread.sleep(1000);

      //Create first folder with xml file in it and close file.
      createFolder(folder1Name);
      openNewFileFromToolbar("XML File");
      saveAsByTopMenu(file1Name);
      Thread.sleep(2000);
      closeTab("0");

      //Create second folder with xml file in it and close file.
      selectRootOfWorkspaceTree();
      createFolder(folder2Name);
      openNewFileFromToolbar("XML File");
      saveAsUsingToolbarButton(file2Name);
      Thread.sleep(2000);
      closeTab("0");

      //Open first folder and file in it 
      openOrCloseFolder(folder1Name);
      Thread.sleep(1000);
      openFileFromNavigationTreeWithCodeEditor(file1Name);
      Thread.sleep(1000);
      //Close first folder
      openOrCloseFolder(folder1Name);
      assertElementNotPresentInWorkspaceTree(file1Name);

      Thread.sleep(1000);
      //Select second file
      selectItemInWorkspaceTree(file2Name);

      //Go to folder with first file
      selectTopMenuCommand("View", "Go to Folder");
      Thread.sleep(1000);

      //Check file is shown in tree
      //TODO check selected state
      assertElementPresentInWorkspaceTree(file1Name);

      selectRootOfWorkspaceTree();
      clickOnToolbarButton("Refresh Selected Folder");

      openOrCloseFolder(folder2Name);
      openFileFromNavigationTreeWithCodeEditor(file2Name);
      Thread.sleep(1000);
      //Go to folder with first file
      selectTopMenuCommand("View", "Go to Folder");
      Thread.sleep(1000);
      //TODO check selected state
      assertElementPresentInWorkspaceTree(file2Name);

      //Close opened tabs
      closeTab("0");
      closeTab("0");
   }

   @Test
   public void testGoToFolderSearchPanel() throws Exception
   {
      //Close root workspace folder
      selectRootOfWorkspaceTree();
      openCloseRootWorkspace();
      assertElementNotPresentInWorkspaceTree(file1Name);
      assertElementNotPresentInWorkspaceTree(file2Name);
      assertElementNotPresentInWorkspaceTree(folder1Name);
      assertElementNotPresentInWorkspaceTree(folder2Name);

      clickOnToolbarButton("Search...");
      Thread.sleep(1000);
      
      //Check search form appears
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSearchForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormPathField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormContentField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormMimeTypeField]/element"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSearchFormSearchButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSearchFormCancelButton\"]"));
      //Check form inputs
      assertEquals("/", selenium.getValue("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormPathField]/element"));
      assertEquals("", selenium.getValue("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormContentField]/element"));
      assertEquals("", selenium.getValue("scLocator=//DynamicForm[ID=\"ideSearchFormDynamicForm\"]/item[name=ideSearchFormMimeTypeField]/element"));
      //Click "Search" button
      selenium.click("scLocator=//IButton[ID=\"ideSearchFormSearchButton\"]");
      Thread.sleep(1000);
      //Check files are found
      assertElementPresentSearchResultsTree(file1Name);
      assertElementPresentSearchResultsTree(file2Name);
      //Open second file
      selectItemInSearchResultsTree(file2Name);
      openFileFromSearchResultsWithCodeEditor(file2Name);
      //Go to folder with second file
      Thread.sleep(1000);
      selectTopMenuCommand("View", "Go to Folder");
      Thread.sleep(1000);
      //TODO check selected
      assertElementPresentInWorkspaceTree(file2Name);
      assertElementNotPresentInWorkspaceTree(file1Name);
      assertElementPresentInWorkspaceTree(folder1Name);
      assertElementPresentInWorkspaceTree(folder2Name);

      //Clear results
      selectItemInWorkspaceTree(folder1Name);
      deleteSelectedItem();
      selectItemInWorkspaceTree(folder2Name);
      deleteSelectedItem();
   }
}
