/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Workspace extends AbstractTestModule
{

   static final String TREE_PREFIX_ID = "navigation-";

   /**
    * Select item in workspace tree
    * @param itemHref Href of item
    * <h1>Folder href MUST ends with "/"</h1>
    */
   public void selectItem(String itemHref) throws Exception
   {
      waitForItem(itemHref);
      selenium().clickAt(getItemId(itemHref), "0");
   }

   /**
    * Selects root item in Workspace tree.
    * 
    * @throws Exception
    */
   public void selectRootItem() throws Exception
   {
      //wait for appear root folder (fix foc cloud-ide assembly)
      waitForRootItem();
      //wait for select root folder (fix foc cloud-ide assembly)
      Thread.sleep(TestConstants.ANIMATION_PERIOD * 60);
      selectItem(IDE().getWorkspaceURL());
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   public void waitForRootItem() throws Exception
   {
      System.out.println("\r\n\r\nwaiting for ITEM >>> [" + IDE().getWorkspaceURL() +"]\r\n\r\n");
      String locator = getItemId(IDE().getWorkspaceURL());
      System.out.println("\r\n\r\nWORKSPACE URL LOCATOR [ " + locator + " ]\r\n\r\n");
      waitForElementPresent(locator);
   }
   
   /**
    * Generate item id 
    * @param href of item 
    * @return id of item
    */
   public String getItemId(String href) throws Exception
   {
//      String itemId = href.substring(BaseTest.WS_URL.length());
//      if (itemId.endsWith("/"))
//         itemId = itemId.substring(0, itemId.length() - 1);
//      itemId = itemId.replaceAll("/", "-");
//      itemId = itemId.replaceAll(" ", "_");
//      return TREE_PREFIX_ID + itemId;      
      return TREE_PREFIX_ID + Utils.md5(href);
   }

   public void doubleClickOnFolder(String folderURL) throws Exception
   {
      //add timeout for reading content from folder (fix for cloud-IDE-assembly)
      IDE().WORKSPACE.waitForItem(folderURL);
      String locator = "//div[@id='" + getItemId(folderURL) + "']/table/tbody/tr/td[2]";

      selenium().mouseDown(locator);
      selenium().mouseUp(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().doubleClick(locator);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   public void doubleClickOnFile(String fileURL) throws Exception
   {
      //add timeout for reading content from folder (fix for cloud-IDE-assembly)
      IDE().WORKSPACE.waitForItem(fileURL);
      String locator = "//div[@id='" + getItemId(fileURL) + "']/div/table/tbody/tr/td[2]";

      selenium().mouseDown(locator);
      selenium().mouseUp(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().doubleClick(locator);
      IDE().EDITOR.waitEditorFileOpened();
   }

   public void doubleClickOnFileFromSearchTab(String fileURL) throws Exception
   {
      IDE().NAVIGATION.selectItemInSearchTree(fileURL);
      String itemId = IDE().NAVIGATION.getItemIdSearch(fileURL);
      String locator = "//div[@id='" + itemId + "']/div/table/tbody/tr/td[2]";

      selenium().mouseDown(locator);
      selenium().mouseUp(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium().doubleClick(locator);
      IDE().EDITOR.waitEditorFileOpened();
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
      waitForItem(folderHref);
      System.out.println("Click on Open Icon > " + folderHref);
      String locator = "//div[@id='" + getItemId(folderHref) + "']/table/tbody/tr/td[1]/img";
      System.out.println("Locator [" + locator + "]");

      selenium().clickAt(locator, "0");
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
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

   /**
    * Wait for item not present in workspace tree
    * @param itemHref Href of the item
    * @throws Exception 
    */
   public void waitForItemNotPresent(String itemHref) throws Exception
   {
      waitForElementNotPresent(getItemId(itemHref));
   }

}
