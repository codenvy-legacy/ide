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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class GetItemUrlTest extends BaseTest
{
   private final String WORK_SPACE_LOCATOR = "//div[@ID=\"ideNavigatorItemTreeGrid\"]//div[@class=\"ide-Tree-label\"]/";

   private final String content1 = "<p> Hello!!! </p>";

   private final String content2 = "Hello!!!";

   private final String searchPhrase = "Hello!!!";

   private final String file1Name = "gadget.xml";

   private final String file2Name = "Example.groovy";

   private final String folderName = GetItemUrlTest.class.getSimpleName();

   private final String entrypoint = WEBDAV_CONTEXT + "/repository/";

   @Test
   public void testGetFileUrl() throws Exception
   {
      //Create first file
      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
     IDE.EDITOR.deleteLinesInEditor(7);
      assertEquals("",IDE.EDITOR.getTextFromCodeEditor(0));
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, content1);
      saveAsUsingToolbarButton(file1Name);
      Thread.sleep(TestConstants.SLEEP * 3);

     IDE.EDITOR.closeTab(0);
      IDE.NAVIGATION.assertItemPresent(WS_URL + file1Name);

      createFolder(folderName);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("",IDE.EDITOR.getTextFromCodeEditor(0));
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, content2);
      saveAsUsingToolbarButton(file2Name);
      Thread.sleep(TestConstants.SLEEP);
     IDE.EDITOR.closeTab(0);
      IDE.NAVIGATION.assertItemPresent(WS_URL + folderName + "/" + file2Name);
      
      //Refresh root item
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.TOOLBAR.runCommand("Refresh Selected Folder");
      String workspaceName =
         selenium.getText("//div[@ID=\"ideNavigatorItemTreeGrid\"]//div[@class=\"ide-Tree-label\"]/");
      String url = getSelectedItemUrl();
      assertTrue(url.startsWith(BASE_URL));
      assertTrue(url.endsWith(entrypoint + workspaceName + "/"));

      //Open url and check file and folder are present
      openLink(url);
      assertTrue(selenium.isElementPresent("link=" + file1Name));
      assertTrue(selenium.isElementPresent("link=" + folderName));
      selenium.goBack();
      selenium.waitForPageToLoad("12000");
      Thread.sleep(TestConstants.SLEEP);

      //Check get URL for file in root of the tree
      IDE.NAVIGATION.selectItem(WS_URL + file1Name);
      url = getSelectedItemUrl();
      assertTrue(url.startsWith(BASE_URL));
      assertTrue(url.endsWith(entrypoint + workspaceName + "/" + file1Name));

      //Check get URL for folder in root of the tree
      IDE.NAVIGATION.selectItem(WS_URL + folderName + "/");
      url = getSelectedItemUrl();
      assertTrue(url.startsWith(BASE_URL));
      assertTrue(url.endsWith(entrypoint + workspaceName + "/" + folderName + "/"));

      //Open url and check file and folder are present
      openLink(url);
      assertTrue(selenium.isElementPresent("link=" + file2Name));
      selenium.goBack();
      selenium.waitForPageToLoad("12000");
      Thread.sleep(TestConstants.SLEEP);

      //Check get URL for the file in the folder
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + folderName + "/");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL + folderName + "/" + file2Name);
      url = getSelectedItemUrl();
      assertTrue(url.startsWith(BASE_URL));
      assertTrue(url.endsWith(entrypoint + workspaceName + "/" + folderName + "/" + file2Name));

   }

   /**
    * @throws Exception
    */
   @Test
   public void testGetFileUrlWithSearch() throws Exception
   {
      IDE.NAVIGATION.selectRootOfWorkspace();
      IDE.TOOLBAR.runCommand("Search...");
      Thread.sleep(TestConstants.SLEEP);

      chekAppearSerchForm();
      //Check form inputs
      assertEquals("/",
         selenium.getValue("//table[@id=\"ideSearchFormDynamicForm\"]//div/input[@name=\"ideSearchFormPathField\"]"));
      assertEquals("",
         selenium.getValue("//table[@id=\"ideSearchFormDynamicForm\"]//div/input[@name=\"ideSearchFormContentField\"]"));
      assertEquals("",
         selenium
            .getValue("//table[@id=\"ideSearchFormDynamicForm\"]//tr/td/input[@name=\"ideSearchFormMimeTypeField\"]"));
      //Type content to input
      selenium.click("//table[@id=\"ideSearchFormDynamicForm\"]//div/input[@name=\"ideSearchFormContentField\"]");
      selenium.type("//table[@id=\"ideSearchFormDynamicForm\"]//div/input[@name=\"ideSearchFormContentField\"]",
         searchPhrase);
      //Click "Search" button
      selenium.click("ideSearchFormSearchButton");
      Thread.sleep(TestConstants.SLEEP);

      //Check files are found
      assertElementPresentSearchResultsTree(file1Name);
      assertElementPresentSearchResultsTree(file2Name);

      String workspaceName = selenium.getText(WORK_SPACE_LOCATOR);

      //Check get URL for first file
      IDE.NAVIGATION.selectItemInSerchTree(WS_URL + file1Name);
      String url = getSelectedItemUrl();
      assertTrue(url.startsWith(BASE_URL));
      assertTrue(url.endsWith(entrypoint + workspaceName + "/" + file1Name));

      //Check get URL for second file
      IDE.NAVIGATION.selectItemInSerchTree(WS_URL + folderName + "/" + file2Name);
      url = getSelectedItemUrl();
      assertTrue(url.startsWith(BASE_URL));
      assertTrue(url.endsWith(entrypoint + workspaceName + "/" + folderName + "/" + file2Name));

      selectWorkspaceTab();
      IDE.NAVIGATION.selectItem(WS_URL + folderName + "/");
      IDE.NAVIGATION.deleteSelectedItems();
      IDE.NAVIGATION.selectItem(WS_URL + file1Name);
      IDE.NAVIGATION.deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * method for check Appear Serch Form
    */
   public void chekAppearSerchForm()
   {
      assertTrue(selenium.isElementPresent("//div[@view-id=\"ideSearchView\"]"));
      assertTrue(selenium.isElementPresent("ideSearchFormSearchButton"));
      assertTrue(selenium.isElementPresent("ideSearchFormCancelButton"));
   }

   /**
    * Open link.
    * 
    * @param link link to open
    * @throws Exception 
    */
   private void openLink(String link) throws Exception
   {
      selenium.open(link);
      selenium.waitForPageToLoad("6000");
      Thread.sleep(TestConstants.SLEEP);
   }
}
