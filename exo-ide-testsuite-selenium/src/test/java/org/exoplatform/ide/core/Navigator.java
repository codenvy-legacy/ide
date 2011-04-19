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

import org.exoplatform.ide.IDE;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 10, 2011 $
 *
 */
public class Navigator extends AbstractTestModule
{

   public interface Locators
   {
      @Deprecated
      public static final String SC_NAVIGATION_TREE = "scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]";

      @Deprecated
      public static final String SC_ROOT_OF_NAVIGATION_TREE = SC_NAVIGATION_TREE + "/body/row[0]/col[1]";

      String NAVIGATION_TREE = "ideNavigatorItemTreeGrid";

      String TREE_PREFIX_ID = "navigation-";
   }

   private IDE ide;

   public Navigator(Selenium selenium, IDE ide)
   {
      super(selenium);
      this.ide = ide;
   }

   /**
    * Get the SmartGWT locator for element in navigation tree by its title.
    * 
    * @param title - the element title
    * @return {@link String}
    */
   public String getScLocator(String title, int col)
   {
      return Locators.SC_NAVIGATION_TREE + "/body/row[name=" + title + "]/col[" + col + "]";
   }

   /**
    * Get the SmartGWT locator for element in navigation tree by its row number and col number.
    * 
    * @param title - the element title
    * @return {@link String}
    */
   public String getScLocator(int row, int col)
   {
      return Locators.SC_NAVIGATION_TREE + "/body/row[" + row + "]/col[" + col + "]";
   }

   /**
    * Select row in navigation tree.
    * 0 - number of root node (workspace).
    * @param rowNumber - number of row.
    * @throws Exception
    */
   public void selectRow(int rowNumber) throws Exception
   {
      selenium.click(Locators.SC_NAVIGATION_TREE + "/body/row[" + rowNumber + "]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public String getRowTitle(int rowNumber)
   {
      return selenium.getText(Locators.SC_NAVIGATION_TREE + "/body/row[" + rowNumber + "]/col[0]");
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
      selenium.clickAt("//div[@id='" + getItemId(folderHref) + "']/table/tbody/tr/td[1]/img", "0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Generate item id 
    * @param href of item 
    * @return id of item
    */
   public String getItemId(String href) throws Exception
   {
      System.out.println("md5 of [" + href + "] is [" + Utils.md5(href) + "]");
      //Thread.sleep(5000);
      return Locators.TREE_PREFIX_ID + Utils.md5(href);
   }

   /**
    * Select item in workspace tree
    * @param itemHref Href of item
    * <h1>Folder href MUST ends with "/"</h1>
    */
   public void selectItem(String itemHref) throws Exception
   {
      selenium.clickAt(getItemId(itemHref), "0");
   }

   /**
    * Check navigation workspace tree contains item.
    * @param itemHref Href of item
    */
   public void assertItemPresent(String itemHref) throws Exception
   {
      assertTrue(selenium.isElementPresent(getItemId(itemHref)));
   }

   /**
    * Check navigation workspace tree doesn't contain item.
    * @param itemHref Href of item
    */
   public void assertItemNotPresent(String itemHref) throws Exception
   {
      assertFalse(selenium.isElementPresent(getItemId(itemHref)));
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
      ide.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);

      String locator = "//table[@id='ideOpenFileWithListGrid']";
      waitForElementPresent(locator);

      if (checkDefault)
      {
         //click on checkbox Use as default editor
         selenium.click("//span[@id='ideOpenFileWithDefaulCheckbox']/input");
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
      }

      selenium.click("ideOpenFileWithOkButton");
      //time remaining to open editor
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }

}
