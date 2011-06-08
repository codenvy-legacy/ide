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

   @Test
   public void CustomizeToolbartest() throws Exception
   {

      // --------1----------
      IDE.WORKSPACE.waitForRootItem();
      //run Customize Toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      //wait for draw panel
      waitForElementPresent("ideCustomizeToolbarForm");
      /**
       * 
       *
       */
      checkAppearCustomizeToolbarForm();
      //store first element for chek appear after close coostomize toolbar
      String storeTextFirstElementGrid = selenium.getText("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[1]/td");
      //click on first element, and cancel not save changes
      selenium.click("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[1]/td");
      selenium.click("ideCustomizeToolbarFormDeleteButton");
      selenium.click("ideCustomizeToolbarFormCancelButton");
      checkDisAppearCustomizeToolbarForm();
      // ---------2------------
      //run customize toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      checkAppearCustomizeToolbarForm();
      //store first element into second variable for chek appear after close coostomize toolbar
      String storeTextFirstElementGridAfterCloseForm =
         selenium.getText("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[1]/td");
      //check first element
      assertEquals(storeTextFirstElementGrid, storeTextFirstElementGridAfterCloseForm);
      selenium.click("ideCustomizeToolbarFormOkButton");
      checkDisAppearCustomizeToolbarForm();
      // ------------3-----------
      //run customize toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      checkAppearCustomizeToolbarForm();
      //delete first element
      selenium.click("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[1]/td");
      selenium.click("ideCustomizeToolbarFormDeleteButton");
      //check deleted element
      assertFalse(storeTextFirstElementGrid == selenium
         .getText("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[1]/td"));
      selenium.click("ideCustomizeToolbarFormOkButton");
      checkDisAppearCustomizeToolbarForm();

      // ---------5--------
      //run customize toolbar
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      // Control defoult settings
      selenium.click("ideCustomizeToolbarFormRestoreDefaultsButton");
      //check appear list grid basic elements
      checkAppearCustomizeToolbarForm();
      chekIdeToolbarItemListGrid();
      chekToollbarItemExListGrid();
      selenium.click("ideCustomizeToolbarFormOkButton");

      // ------6-------
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      //select first element and move down
      clickOnToolbarElement(2);
      selenium.click("ideCustomizeToolbarFormMoveDownButton");
      //check element after move
      checkElementPresentInItemListGrid("New * [Popup]", 3);
      //confirm selection
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ---------7-------
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      checkAppearCustomizeToolbarForm();
      //check moved element
      checkElementPresentInItemListGrid("New * [Popup]", 3);
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ----------- 8 ---------
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      checkAppearCustomizeToolbarForm();
      clickOnToolbarElement(3);
      //delete moved element
      selenium.click("ideCustomizeToolbarFormDeleteButton");
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ----------9---------
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      //chek element after delete (after delete should be "Save" instead of "New * [Popup]")
      checkElementPresentInItemListGrid("Save", 3);
      //click on first element and push delimitier
      clickOnToolbarElement(3);
      selenium.click("ideCustomizeToolbarFormDelimeterButton");
      // control create delimiter
      checkElementPresentInItemListGrid("Delimiter", 4);
      clickOnToolbarElement(2);
      selenium.click("ideCustomizeToolbarFormMoveDownButton");
      // control position delimiter
      checkElementPresentInItemListGrid("Delimiter", 3);
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ----10-----
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      // delete delimiter
      clickOnToolbarElement(3);
      selenium.click("ideCustomizeToolbarFormDeleteButton");
      // control delete delimiter
      checkElementPresentInItemListGrid("Delimiter", 3);
      selenium.click("ideCustomizeToolbarFormOkButton");
      // -------11------
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      //add new element on toolbar
      clickOnComandToolbarElement(2);
      clickOnToolbarElement(2);
      //change position new element
      selenium.click("ideCustomizeToolbarFormAddButton");
      selenium.click("ideCustomizeToolbarFormMoveDownButton");
      selenium.click("ideCustomizeToolbarFormMoveDownButton");
      //check new element in new position
      checkElementPresentInItemListGrid("New * [Popup]", 5);
      // -----12------
      selenium.click("ideCustomizeToolbarFormOkButton");
      // -----15------
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      //restore default settings and check
      selenium.click("ideCustomizeToolbarFormRestoreDefaultsButton");
      chekToollbarItemExListGrid();
      chekIdeToolbarItemListGrid();
   }

   /**
   * check main elements on TollbarItem
    * @throws InterruptedException 
   */
   public void chekToollbarItemExListGrid() throws InterruptedException
   {
      List<String> items = getCommands("ideCommandItemExListGrid");
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
      assertTrue(items.contains("Initialize repository"));
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
      List<String> items = getCommands("ideToolbarItemListGrid");

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
      waitForElementPresent("ideCustomizeToolbarForm");
      assertTrue(selenium.isElementPresent("ideCustomizeToolbarForm"));
      assertTrue(selenium.isElementPresent("//div//span[\"Customize Toolbar\"]"));
      assertTrue(selenium.isElementPresent("ideToolbarItemListGrid"));
      assertTrue(selenium.isElementPresent("ideCustomizeToolbarFormCancelButton"));
      assertTrue(selenium.isElementPresent("ideCustomizeToolbarFormRestoreDefaultsButton"));
      assertTrue(selenium.isElementPresent("ideCustomizeToolbarFormOkButton"));
   }

   /**
   * @throws InterruptedException
   */
   public void checkDisAppearCustomizeToolbarForm() throws InterruptedException
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertFalse(selenium.isElementPresent("ideCustomizeToolbarForm"));
      assertFalse(selenium.isElementPresent("//div//span[\"Customize Toolbar\"]"));
      assertFalse(selenium.isElementPresent("ideToolbarItemListGrid"));
      assertFalse(selenium.isElementPresent("ideCustomizeToolbarFormCancelButton"));
      assertFalse(selenium.isElementPresent("ideCustomizeToolbarFormRestoreDefaultsButton"));
      assertFalse(selenium.isElementPresent("ideCustomizeToolbarFormOkButton"));
   }

   /**
   * @param indexElement
   */
   public void clickOnComandToolbarElement(int indexElement)
   {

      selenium.click("//table[@ID=\"ideCommandItemExListGrid\"]/tbody/tr[" + indexElement + "]/td");
   }

   /**
    * 
    * 
    * @param indexElement 
    */
   public void clickOnToolbarElement(int indexElement)
   {

      selenium.click("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[" + indexElement + "]/td");
   }

   private List<String> getCommands(String tableId)
   {
      List<String> res = new ArrayList<String>();
      Number count = selenium.getXpathCount("//table[@id=\"" + tableId + "\"]/tbody/tr");
      for (int i = 1; i < count.intValue(); i++)
      {
         String text = selenium.getText("//table[@id=\"" + tableId + "\"]//tr[position()=" + i + "]");
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
         selenium.getText("//table[@ID=\"ideCommandItemExListGrid\"]/tbody/tr[" + indexElement + "]//div"));
   }

   /**
   * @param elementTitle
   * @param indexElement
   */
   public void checkElementPresentInItemListGrid(String elementTitle, int indexElement)
   {
      assertEquals(elementTitle,
         selenium.getText("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[" + indexElement + "]//div"));
   }

}