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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class Navigation extends AbstractTestModule
{

   public static final String NAVIGATION_TREE = "ideNavigatorItemTreeGrid";

   public static final String SEARCH_RESULT_TREE = "ideSearchResultItemTreeGrid";

   static final String TREE_PREFIX_ID = "navigation-";

   static final String TREE_PREFIX_SERCH_ID = "search-";

   /**
    * Select row in navigation tree.
    * 0 - number of root node (workspace).
    * @param rowNumber - number of row.
    * @throws Exception
    */
   public void selectRow(int rowNumber) throws Exception
   {
      fail();
      //      selenium.click(Locators.SC_NAVIGATION_TREE + "/body/row[" + rowNumber + "]/col[1]");
      //      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public String getRowTitle(int rowNumber)
   {
      int size =
         selenium().getXpathCount("//div[@id='" + NAVIGATION_TREE + "']//div[@class='ide-Tree-label']").intValue();
      if (size <= 0)
         return null;
      int index = 0;

      for (int i = 1; i <= size; i++)
      {
         if (selenium().isVisible(
            "xpath=(//div[@id='" + NAVIGATION_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]"))
            ;
         index++;
         if (index == rowNumber)
         {
            return selenium().getText(
               "xpath=(//div[@id='" + NAVIGATION_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]");
         }
      }
      return null;
   }

   public String getRowTitleInSearchTree(int rowNumber)
   {
      int size =
         selenium().getXpathCount("//div[@id='" + SEARCH_RESULT_TREE + "']//div[@class='ide-Tree-label']").intValue();
      if (size <= 0)
         return null;
      int index = 0;

      for (int i = 1; i <= size; i++)
      {
         if (selenium().isVisible(
            "xpath=(//div[@id='" + SEARCH_RESULT_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]"))
            ;
         index++;
         if (index == rowNumber)
         {
            return selenium().getText(
               "xpath=(//div[@id='" + SEARCH_RESULT_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]");
         }
      }
      return null;
   }

   /**
    * Click open icon of folder in navigation tree.
    * If folder is closed, it will be opened,
    * if it is opened, it will be closed.
    * 
    * @param folderHref - the folder href.
    * @throws Exception
    */
   public void clickOpenIconOfFolder(String folderHref) throws Exception
   {
      //      selenium.click(getScLocator(folderHref, 0) + "/open");
      selenium().clickAt("//div[@id='" + getItemId(folderHref) + "']/table/tbody/tr/td[1]/img", "0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Generate item id 
    * @param href of item 
    * @return id of item
    */
   public String getItemId(String href) throws Exception
   {
      return TREE_PREFIX_ID + Utils.md5(href);
   }

   public String getItemIdSearch(String href) throws Exception
   {
      return TREE_PREFIX_SERCH_ID + Utils.md5(href);
   }

   /**
    * Select item in workspace tree
    * @param itemHref Href of item
    * <h1>Folder href MUST ends with "/"</h1>
    */
   public void selectItem(String itemHref) throws Exception
   {
      selenium().clickAt(getItemId(itemHref), "0");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Select item in search tree
    * @param itemHref
    * @throws Exception
    */
   public void selectItemInSearchTree(String itemHref) throws Exception
   {
      selenium().clickAt(getItemIdSearch(itemHref), "0");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Assert item is shown in search tree.
    * @param itemHref
    * @throws Exception
    */
   public void assertItemVisibleInSearchTree(String itemHref) throws Exception
   {
      String id = getItemIdSearch(itemHref);
      assertTrue(selenium().isElementPresent(id));
      assertTrue(selenium().isVisible(id));
   }

   /**
    * Assert item is not shown in search tree.
    * @param itemHref
    * @throws Exception
    */
   public void assertItemNotVisibleInSearchTree(String itemHref) throws Exception
   {
      String id = getItemIdSearch(itemHref);
      if (selenium().isElementPresent(id))
      {
         assertFalse(selenium().isVisible(id));
      }
   }

   /**
    * Check navigation workspace tree contains item.
    * @param itemHref Href of item
    */
   public void assertItemVisible(String itemHref) throws Exception
   {
      String id = getItemId(itemHref);
      assertTrue(selenium().isElementPresent(id));
      assertTrue(selenium().isVisible(id));
   }

   /**
    * Check navigation workspace tree doesn't contain item.
    * @param itemHref Href of item
    */
   public void assertItemNotVisible(String itemHref) throws Exception
   {
      String id = getItemId(itemHref);
      if (selenium().isElementPresent(id))
      {
         assertFalse(selenium().isVisible(id));
      }
   }

   /**
    * Open selected file with code mirror.
    * 
    * Method doesn't check is selected item in navigation tree is file.
    * It will fail, while calling "Open with" command.
    * 
    * @param checkDefault - is click on checkbox "Use by default"
    * @throws Exception
    */
   public void openSelectedFileWithCodeEditor(boolean checkDefault) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);

      String locator = "//table[@id='ideOpenFileWithListGrid']";
      waitForElementPresent(locator);

      if (checkDefault)
      {
         //click on checkbox Use as default editor
         selenium().click("//span[@id='ideOpenFileWithDefaulCheckbox']/input");
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
      }

      selenium().click("ideOpenFileWithOkButton");
      //time remaining to open editor
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

   /**
    * Open file from navigation tree with code mirror.
    * 
    * @param fileURL URL of file in navigation tree
    * @param checkDefault - is click on checkbox "Use by default"
    * @throws Exception
    */
   public void openFileFromNavigationTreeWithCodeEditor(String fileURL, boolean checkDefault) throws Exception
   {
      selectItem(fileURL);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      openSelectedFileWithCodeEditor(checkDefault);
   }

   /**
    * Delete selected item in navigation tree.
    * 
    * @throws Exception
    */
   public void deleteSelectedItems() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.File.DELETE, false);

      //check deletion form
      //assertTrue(selenium.isElementPresent("//div[@view-id='ideDeleteItemsView']"));
      waitForElementPresent("//div[@view-id='ideDeleteItemsView']");
      assertTrue(selenium().isElementPresent("ideDeleteItemFormOkButton"));
      assertTrue(selenium().isElementPresent("ideDeleteItemFormCancelButton"));

      //click Ok button
      selenium().click("ideDeleteItemFormOkButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Select the root workspace item in workspace tree.
    * 
    * @param name
    * @throws Exception
    */
   public void selectRootOfWorkspace() throws Exception
   {
      selectItem(IDE().getWorkspaceURL());
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Wait for item present in workspace tree
    * @param itemHref Href of the item
    * @throws Exception 
    */
   public void waitForItem(String itemHref) throws Exception
   {
      waitForElementPresent(getItemId(itemHref));
   }

}
