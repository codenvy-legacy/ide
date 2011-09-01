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
import org.exoplatform.ide.utils.AbstractTextUtil;

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
    * 1 - number of root node (workspace).
    * @param rowNumber - number of row.
    * @throws Exception
    */
   public void selectRow(int rowNumber) throws Exception
   {
      int size =
         selenium().getXpathCount("//div[@id='" + NAVIGATION_TREE + "']//div[@class='ide-Tree-label']").intValue();
      if (size <= 0)
         return;
      int index = 0;

      for (int i = 1; i <= size; i++)
      {
         if (selenium().isVisible(
            "xpath=(//div[@id='" + NAVIGATION_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]"))
         {
            index++;
         }
         if (index == rowNumber)
         {
            selenium().clickAt(
               "xpath=(//div[@id='" + NAVIGATION_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]", "0");
            break;
         }
      }
   }

   /**
    * Return title of item at row
    * 1 - number of root node (workspace).  
    * @param rowNumber - number of row.
    */
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
         {
            index++;
         }
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
         {
            index++;
         }
         if (index == rowNumber)
         {
            return selenium().getText(
               "xpath=(//div[@id='" + SEARCH_RESULT_TREE + "']//div[@class='ide-Tree-label'])[position()=" + i + "]");
         }
      }
      return null;
   }

   /**
    * Generate item id 
    * @param href of item 
    * @return id of item
    */
   public String getItemId(String href) throws Exception
   {
      System.out.println(TREE_PREFIX_ID + Utils.md5(href));
      return TREE_PREFIX_ID + Utils.md5(href);
   }

   public String getItemIdSearch(String href) throws Exception
   {
      return TREE_PREFIX_SERCH_ID + Utils.md5(href);
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
      //add timeout for reading content from folder (fix for cloud-IDE-assembly)
      IDE().WORKSPACE.waitForItem(itemHref);
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
    * Select and double click on file.
    * 
    * Use instead IDE.WORKSPACE.doubleClickOnFile(fileURL);
    * 
    * @param fileURL URL of file in navigation tree
    * @param checkDefault - is click on checkbox "Use by default"
    * @throws Exception
    */
   @Deprecated
   public void openFileFromNavigationTreeWithCodeEditor(String fileURL, boolean checkDefault) throws Exception
   {
      IDE().WORKSPACE.selectItem(fileURL);
      IDE().WORKSPACE.doubleClickOnFile(fileURL);
   }

   /**
    * Delete selected item in navigation tree.
    * 
    * @throws Exception
    */
   public void deleteSelectedItems() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.File.DELETE);

      //check deletion form
      //assertTrue(selenium().isElementPresent("//div[@view-id='ideDeleteItemsView']"));
      waitForElementPresent("//div[@view-id='ideDeleteItemsView']");
      assertTrue(selenium().isElementPresent("ideDeleteItemFormOkButton"));
      assertTrue(selenium().isElementPresent("ideDeleteItemFormCancelButton"));

      //click Ok button
      selenium().click("ideDeleteItemFormOkButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Selects and refreshes folder in Workspace tree
    * 
    * @param itemURL
    */
   public void selectAndRefreshFolder(String folderURL) throws Exception
   {
      IDE().WORKSPACE.selectItem(folderURL);

      IDE().TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   /**
    * Creates folder with name folderName.
    * 
    * Folder, that will be parent for folderName must be selected before.
    * 
    * Clicks on New button on toolbar, then click on Folder menu from list.
    * 
    * @param folderName folder name
    */
   public void createFolder(String folderName) throws Exception
   {
      IDE().TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FOLDER);
      waitForElementPresent("//div[@view-id='ideCreateFolderForm']");

      //Check creation form elements
      waitForElementPresent("ideCreateFolderFormNameField");
      waitForElementPresent("ideCreateFolderFormCreateButton");
      waitForElementPresent("ideCreateFolderFormCancelButton");

      AbstractTextUtil.getInstance().typeToInput("ideCreateFolderFormNameField", folderName, true);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().click("ideCreateFolderFormCreateButton");
      waitForElementNotPresent("ideCreateFolderForm");
   }

   /**
    * Saves current file.
    * 
    * @throws Exception
    */
   public void saveFile() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      String locator =
         "//div[@id='exoIDEToolbar']//div[@class='exoIconButtonPanel' and @enabled='true' and @title='"
            + ToolbarCommands.File.SAVE + "']";
      waitForElementPresent(locator);
   }

   /**
    * Saves all files.
    * 
    * @throws Exception
    */
   public void saveAllFiles() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_ALL);

      long startTime = System.currentTimeMillis();
      while (true)
      {
         selenium().mouseDown("//td[@class='exo-menuBarItem' and text()='" + MenuCommands.File.FILE + "']");
         Thread.sleep(TestConstants.ANIMATION_PERIOD);

         String locator =
            "//div[@class='exo-popupMenuMain']//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='"
               + MenuCommands.File.SAVE_ALL + "']";
         boolean saveAllDisabled = selenium().isElementPresent(locator);

         String lockLayerLocator = "//div[@class='exo-lockLayer']";
         selenium().mouseDown(lockLayerLocator);
         Thread.sleep(TestConstants.ANIMATION_PERIOD);

         if (saveAllDisabled)
         {
            break;
         }

         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("TimeOut!!!");
         }
      }
   }

   /**
    * Saves file with new name.
    * 
    * @param fileName new name of the file
    * @throws Exception
    */
   public void saveFileAs(String fileName) throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.File.SAVE_AS);

      IDE().ASK_FOR_VALUE_DIALOG.waitForPresent();

      if (fileName != null)
      {
         IDE().ASK_FOR_VALUE_DIALOG.setValue(fileName);
      }

      IDE().ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE().ASK_FOR_VALUE_DIALOG.waitForAskDialogNotPresent();

      String locator =
         "//div[@id='exoIDEToolbar']//div[@class='exoIconButtonPanel' and @enabled='false' and @title='"
            + ToolbarCommands.File.SAVE + "']";
      waitForElementPresent(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public enum Editor {
      CODEMIRROR("CodeMirror"), CKEDITOR("CKEditor");

      /**
       * Name of editor - the title, that displayed in Open With form.
       */
      private String name;

      Editor(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return this.name;
      }

   }

}
