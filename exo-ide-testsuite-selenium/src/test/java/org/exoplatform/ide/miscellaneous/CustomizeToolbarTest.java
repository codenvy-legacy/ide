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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class CustomizeToolbarTest extends BaseTest
{
   
   private static class CustomizeToolbarViewLocators
   {
      
      static String VIEW = "ide.core.customize-toolbar-window";
      
      static String COMMANDS_LIST_GRID = "ide.core.customize-toolbar.commands-list";
      
      static String TOOLBARITEMS_LIST_GRID = "ide.core.customize-toolbar.toolbar-items-list";
      
      static String BUTTON_OK = "ide.core.customize-toolbar.ok-button";
      
      static String BUTTON_CANCEL = "ide.core.customize-tolbar.cancel-button";
      
      static String BUTTON_DEFAULTS = "ide.core.customize-toolbar.defaults-button";
      
      static String BUTTON_ADD_COMMAND = "ide.core.customize-toolbar.add-button";
      
      static String BUTTON_ADD_DELIMITER = "ide.core.customize-toolbar.delimiter-button";
      
      static String BUTTON_DELETE = "ide.core.customize-toolbar.delete-button";
      
      static String BUTTON_MOVE_UP = "ide.core.customize-toolbar.move-up-button";
      
      static String BUTTON_MOVE_DOWN = "ide.core.customize-toolbar.move-down-button";
      
   }

   @Test
   public void CustomizeToolbartest() throws Exception
   {

      // --------1----------
      IDE.WORKSPACE.waitForRootItem();
      //run Customize Toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      //wait for draw panel
      waitForElementPresent(CustomizeToolbarViewLocators.VIEW);

      checkAppearCustomizeToolbarForm();
      //store first element for chek appear after close coostomize toolbar
      String storeTextFirstElementGrid = selenium().getText("//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID+ "\"]/tbody/tr[1]/td");
      System.out.println();
      System.out.println();
      System.out.println("Text1 > " + storeTextFirstElementGrid);
      System.out.println();
      System.out.println();
      //click on first element, and cancel not save changes
      selenium().click("//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID + "\"]/tbody/tr[1]/td");
      selenium().click(CustomizeToolbarViewLocators.BUTTON_DELETE);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_CANCEL);
      checkDisAppearCustomizeToolbarForm();

      // ---------2------------
      //run customize toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //store first element into second variable for chek appear after close coostomize toolbar
      String storeTextFirstElementGridAfterCloseForm =
         selenium().getText("//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID + "\"]/tbody/tr[1]/td");
      System.out.println();
      System.out.println();
      System.out.println("Text2 > " + storeTextFirstElementGridAfterCloseForm);
      System.out.println();
      System.out.println();
      //check first element
      assertEquals(storeTextFirstElementGrid, storeTextFirstElementGridAfterCloseForm);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);
      checkDisAppearCustomizeToolbarForm();
      
      // ------------3-----------
      //run customize toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //delete first element
      selenium().click("//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID + "\"]/tbody/tr[1]/td");
      selenium().click(CustomizeToolbarViewLocators.BUTTON_DELETE);
      //check deleted element
      assertFalse(storeTextFirstElementGrid == selenium().getText(
         "//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID + "\"]/tbody/tr[1]/td"));
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);
      checkDisAppearCustomizeToolbarForm();

      // ---------5--------
      //run customize toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      // Control defoult settings
      selenium().click(CustomizeToolbarViewLocators.BUTTON_DEFAULTS);
      //check appear list grid basic elements
      chekIdeToolbarItemListGrid();
      chekToollbarItemExListGrid();
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      
      // ------6-------
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //select first element and move down
      clickOnToolbarElement(2);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_MOVE_DOWN);
      //check element after move
      checkElementPresentInItemListGrid("New * [Popup]", 3);
      //confirm selection
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      // ---------7-------
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //check moved element
      checkElementPresentInItemListGrid("New * [Popup]", 3);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      // ----------- 8 ---------
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      clickOnToolbarElement(3);
      //delete moved element
      selenium().click(CustomizeToolbarViewLocators.BUTTON_DELETE);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      // ----------9---------
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //chek element after delete (after delete should be "Save" instead of "New * [Popup]")
      checkElementPresentInItemListGrid("Save", 3);
      //click on first element and push delimitier
      clickOnToolbarElement(3);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_ADD_DELIMITER);
      // control create delimiter
      checkElementPresentInItemListGrid("Delimiter", 4);
      clickOnToolbarElement(2);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_MOVE_DOWN);
      // control position delimiter
      checkElementPresentInItemListGrid("Delimiter", 3);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      // ----10-----
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      // delete delimiter
      clickOnToolbarElement(3);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_DELETE);
      // control delete delimiter
      checkElementPresentInItemListGrid("Delimiter", 3);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      // -------11------
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //add new element on toolbar
      clickOnComandToolbarElement(2);
      clickOnToolbarElement(2);
      //change position new element
      selenium().click(CustomizeToolbarViewLocators.BUTTON_ADD_COMMAND);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_MOVE_DOWN);
      selenium().click(CustomizeToolbarViewLocators.BUTTON_MOVE_DOWN);
      //check new element in new position
      checkElementPresentInItemListGrid("New * [Popup]", 5);
      // -----12------
      selenium().click(CustomizeToolbarViewLocators.BUTTON_OK);

      // -----15------
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      //restore default settings and check
      selenium().click(CustomizeToolbarViewLocators.BUTTON_DEFAULTS);
      chekToollbarItemExListGrid();
      chekIdeToolbarItemListGrid();
   }

   /**
   * check main elements on TollbarItem
    * @throws InterruptedException 
   */
   public void chekToollbarItemExListGrid() throws InterruptedException
   {
      List<String> items = getCommands(CustomizeToolbarViewLocators.COMMANDS_LIST_GRID);
      assertTrue(items.contains("New * [Popup]"));
      assertTrue(items.contains("Upload Zipped Folder..."));
      assertTrue(items.contains("Download File..."));
      assertTrue(items.contains("Save As..."));
      assertTrue(items.contains("Delete..."));
      assertTrue(items.contains("Cut Item(s)"));
      assertTrue(items.contains("Find-Replace..."));
      assertTrue(items.contains("Lock / Unlock File"));
      assertTrue(items.contains("Version History..."));
      assertTrue(items.contains("Get URL..."));
      assertTrue(items.contains("Show / Hide Documentation"));
      assertTrue(items.contains("Show Preview"));
      assertTrue(items.contains("Run in Sandbox"));
      assertTrue(items.contains("Launch REST Service"));
      assertTrue(items.contains("Preview node type"));
      assertTrue(items.contains("Initialize Repository"));
      assertTrue(items.contains("Push..."));
      assertTrue(items.contains("Workspace..."));
      assertTrue(items.contains("REST Services Discovery"));
      assertTrue(items.contains("New POGO"));
      assertTrue(items.contains("Project Template..."));
      assertTrue(items.contains("New Data Object"));
      assertTrue(items.contains("New Netvibes Widget"));
   }

   /**
    * @chek main elements on ListGridItem
    * 
    */
   public void chekIdeToolbarItemListGrid()
   {
      List<String> items = getCommands(CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID);

      assertTrue(items.contains("Delimiter"));
      assertTrue(items.contains("New * [Popup]"));
      assertTrue(items.contains("Delimiter"));
      assertTrue(items.contains("Save"));
      assertTrue(items.contains("Cut Item(s)"));
      assertTrue(items.contains("Undo Typing"));
      assertTrue(items.contains("Redo Typing"));
      assertTrue(items.contains("Format"));
      assertTrue(items.contains("Find-Replace..."));
      assertTrue(items.contains("Lock / Unlock File"));
      assertTrue(items.contains("Show / Hide Outline"));
      assertTrue(items.contains("Show / Hide Documentation"));
      assertTrue(items.contains("Version History..."));
      assertTrue(items.contains("Restore to Version"));
      assertTrue(items.contains("Set / Unset Autoload"));
      assertTrue(items.contains("Deploy"));
      assertTrue(items.contains("Undeploy from Sandbox"));
      assertTrue(items.contains("Deploy widget"));
      assertTrue(items.contains("Deploy node type"));
   }

   /**
   * @throws Exception
   */
   public void checkAppearCustomizeToolbarForm() throws Exception
   {
      waitForElementPresent(CustomizeToolbarViewLocators.VIEW);
      assertTrue(selenium().isElementPresent("//div//span[\"Customize Toolbar\"]"));
      assertTrue(selenium().isElementPresent(CustomizeToolbarViewLocators.COMMANDS_LIST_GRID));
      assertTrue(selenium().isElementPresent(CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID));
      
      assertTrue(selenium().isElementPresent(CustomizeToolbarViewLocators.BUTTON_OK));
      assertTrue(selenium().isElementPresent(CustomizeToolbarViewLocators.BUTTON_CANCEL));
      assertTrue(selenium().isElementPresent(CustomizeToolbarViewLocators.BUTTON_DEFAULTS));
   }

   /**
   * @throws InterruptedException
   */
   public void checkDisAppearCustomizeToolbarForm() throws Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      waitForElementNotPresent(CustomizeToolbarViewLocators.VIEW);
      assertFalse(selenium().isElementPresent("//div//span[\"Customize Toolbar\"]"));
      assertFalse(selenium().isElementPresent(CustomizeToolbarViewLocators.COMMANDS_LIST_GRID));      
      assertFalse(selenium().isElementPresent(CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID));

      assertFalse(selenium().isElementPresent(CustomizeToolbarViewLocators.BUTTON_OK));
      assertFalse(selenium().isElementPresent(CustomizeToolbarViewLocators.BUTTON_CANCEL));
      assertFalse(selenium().isElementPresent(CustomizeToolbarViewLocators.BUTTON_DEFAULTS));
   }

   /**
   * @param indexElement
   */
   public void clickOnComandToolbarElement(int indexElement)
   {

      selenium().click("//table[@ID=\"" + CustomizeToolbarViewLocators.COMMANDS_LIST_GRID + "\"]/tbody/tr[" + indexElement + "]/td");
   }

   /**
    * 
    * 
    * @param indexElement 
    */
   public void clickOnToolbarElement(int indexElement)
   {

      selenium().click("//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID + "\"]/tbody/tr[" + indexElement + "]/td");
   }

   private List<String> getCommands(String tableId)
   {
      List<String> res = new ArrayList<String>();
      Number count = selenium().getXpathCount("//table[@id=\"" + tableId + "\"]/tbody/tr");
      for (int i = 1; i < count.intValue(); i++)
      {
         String text = selenium().getText("//table[@id=\"" + tableId + "\"]//tr[position()=" + i + "]");
         res.add(text);
      }
      return res;
   }

   /**
    * 
    * 
    * @param elementTitle
    * @param indexElement
    */
   public void checkIdeCommandItemExListGrid(String elementTitle, int indexElement)
   {
      assertEquals(elementTitle,
         selenium().getText("//table[@ID=\"" + CustomizeToolbarViewLocators.COMMANDS_LIST_GRID + "\"]/tbody/tr[" + indexElement + "]//div"));
   }

   /**
   * @param elementTitle
   * @param indexElement
   */
   public void checkElementPresentInItemListGrid(String elementTitle, int indexElement)
   {
      assertEquals(elementTitle,
         selenium().getText("//table[@ID=\"" + CustomizeToolbarViewLocators.TOOLBARITEMS_LIST_GRID + "\"]/tbody/tr[" + indexElement + "]//div"));
   }

}