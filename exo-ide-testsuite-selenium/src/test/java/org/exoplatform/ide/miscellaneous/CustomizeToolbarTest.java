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

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.CodeAssistant.Locators;
import org.junit.Test;

import com.thoughtworks.selenium.Wait;

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
      waitForRootElement();
      //run Customize Toolbar
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
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
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
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
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
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
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      checkAppearCustomizeToolbarForm();
      // Control defoult settings
      selenium.click("ideCustomizeToolbarFormRestoreDefaultsButton");
      //check appear list grid basic elements
      chekIdeToolbarItemListGrid();
      chekTollbarItemExListGrid();
      selenium.click("ideCustomizeToolbarFormOkButton");

      // ------6-------
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      //select first element and move down
      clickOnToolbarElement(2);
      selenium.click("ideCustomizeToolbarFormMoveDownButton");
      //check element after move
      checkElementPresentInItemListGrid("New * [Popup]", 3);
      //confirm selection
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ---------7-------
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      checkAppearCustomizeToolbarForm();
      //check moved element
      checkElementPresentInItemListGrid("New * [Popup]", 3);
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ----------- 8 ---------
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      checkAppearCustomizeToolbarForm();
      clickOnToolbarElement(3);
      //delete moved element
      selenium.click("ideCustomizeToolbarFormDeleteButton");
      selenium.click("ideCustomizeToolbarFormOkButton");
      // ----------9---------
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
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
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      // delete delimiter
      clickOnToolbarElement(3);
      selenium.click("ideCustomizeToolbarFormDeleteButton");
      // control delete delimiter
      checkElementPresentInItemListGrid("Delimiter", 3);
      selenium.click("ideCustomizeToolbarFormOkButton");
      // -------11------
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
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
      waitForRootElement();
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      waitForElementPresent("ideCustomizeToolbarForm");
      //restore default settings and check
      selenium.click("ideCustomizeToolbarFormRestoreDefaultsButton");
      chekTollbarItemExListGrid();
      chekIdeToolbarItemListGrid();
   }

   public void chekTollbarItemExListGrid()
   {
      checkIdeCommandItemExListGrid("New * [Popup]", 2);
      checkIdeCommandItemExListGrid("Upload Zipped Folder...", 6);
      checkIdeCommandItemExListGrid("Download File...", 9);
      checkIdeCommandItemExListGrid("Save As...", 12);
      checkIdeCommandItemExListGrid("Delete...", 16);
      checkIdeCommandItemExListGrid("Cut Item(s)", 21);
      checkIdeCommandItemExListGrid("Find-Replace...", 27);
      checkIdeCommandItemExListGrid("Lock / Unlock File", 30);
      checkIdeCommandItemExListGrid("Version History...", 32);
      checkIdeCommandItemExListGrid("Get URL...", 37);
      checkIdeCommandItemExListGrid("Show / Hide Documentation", 40);
      checkIdeCommandItemExListGrid("Show Gadget Preview", 44);
      checkIdeCommandItemExListGrid("Run in Sandbox", 49);
      checkIdeCommandItemExListGrid("Launch REST Service", 52);
      checkIdeCommandItemExListGrid("Preview node type", 57);
      checkIdeCommandItemExListGrid("Initialize repository", 60);
      checkIdeCommandItemExListGrid("Commit", 65);
      checkIdeCommandItemExListGrid("Status", 68);
      checkIdeCommandItemExListGrid("Workspace...", 70);
      checkIdeCommandItemExListGrid("REST Services Discovery", 75);
      checkIdeCommandItemExListGrid("New POGO", 81);
      checkIdeCommandItemExListGrid("Project Template...", 77);
      checkIdeCommandItemExListGrid("New Data Object", 82);
      checkIdeCommandItemExListGrid("New Netvibes Widget", 91);
   }

   /**
    * 
    */
   public void chekIdeToolbarItemListGrid()
   {
      checkElementPresentInItemListGrid("Delimiter", 1);
      checkElementPresentInItemListGrid("New * [Popup]", 2);
      checkElementPresentInItemListGrid("Delimiter", 3);
      checkElementPresentInItemListGrid("Save", 4);
      checkElementPresentInItemListGrid("Cut Item(s)", 7);
      checkElementPresentInItemListGrid("Undo Typing", 14);
      checkElementPresentInItemListGrid("Redo Typing", 15);
      checkElementPresentInItemListGrid("Format", 16);
      checkElementPresentInItemListGrid("Find-Replace...", 18);
      checkElementPresentInItemListGrid("Lock / Unlock File", 20);
      checkElementPresentInItemListGrid("Show / Hide Outline", 22);
      checkElementPresentInItemListGrid("Show / Hide Documentation", 24);
      checkElementPresentInItemListGrid("Version History...", 26);
      checkElementPresentInItemListGrid("Restore to Version", 30);
      checkElementPresentInItemListGrid("Set / Unset Autoload", 35);
      checkElementPresentInItemListGrid("Deploy", 37);
      checkElementPresentInItemListGrid("Undeploy from Sandbox", 41);
      checkElementPresentInItemListGrid("Deploy Gadget", 44);
      checkElementPresentInItemListGrid("Deploy node type", 48);
   }

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

   public void checkElementPresentInItemListGrid(String elementTitle, int indexElement)
   {
      assertEquals(elementTitle,
         selenium.getText("//table[@ID=\"ideToolbarItemListGrid\"]/tbody/tr[" + indexElement + "]//div"));
   }

}