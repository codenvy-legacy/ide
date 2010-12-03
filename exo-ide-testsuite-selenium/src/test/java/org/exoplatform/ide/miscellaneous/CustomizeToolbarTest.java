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
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

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
      Thread.sleep(TestConstants.SLEEP);

      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      String str =
         selenium.getText("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormDeleteButton\"]/");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormCancelButton\"]/");
      // ---------2------------

      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId="
         + str + "]/col[0]"));
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // ------------3-----------
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormDeleteButton\"]");
      assertFalse(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=New $ {new2} ]/col[0]"));
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // ---------5--------
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
//      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      // Control defoult settings
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormRestoreDefaultsButton\"]/");
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=New * [Popup]]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Save]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Save As...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Cut Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Copy Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Paste Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Delete...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Search...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Refresh Selected Folder]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Undo Typing]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Redo Typing]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Format]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Find-Replace...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Show \\ Hide Outline]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Properties]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Show Preview]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Deploy Gadget]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=UnDeploy Gadget]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Set \\ Unset Autoload]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Validate]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Deploy]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Undeploy]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Launch REST Service]/col[0]"));
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // ------6-------
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormMoveDownButton\"]/");
      // control shift
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[2]/col[fieldName=Toolbar||0]"));
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // ---------7-------
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      String new4 =
         selenium.getText("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[2]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[2]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormMoveUpButton\"]/");
      // control shift
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=" + new4
            + "]"));
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // ----------- 8 ---------
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
//      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
//      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Customize Toolbar\")]", "");
//      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[2]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormDeleteButton\"]/");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      // ----------9---------
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
//      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
//      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), \"Customize Toolbar\")]", "");
//      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormDelimeterButton\"]/");
      // control create delimiter
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormMoveDownButton\"]/");
      // control position delimiter
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // ----10-----
      Thread.sleep(TestConstants.SLEEP);

      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      // find delimiter
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormDeleteButton\"]/");
      // control delete delimiter
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // -------11------
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      selenium.click("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]/body/row[1]/col[fieldName=Toolbar||0]");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormAddButton\"]/");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormMoveUpButton\"]/");
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormMoveUpButton\"]/");
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Cut Item(s)]/col[0]"));
      // -----12------
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormOkButton\"]/");
      // -----15------
      Thread.sleep(TestConstants.SLEEP);

      IDE.menu().runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_TOOLBAR);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeToolbarForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCommandItemExListGrid\"]"));
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeToolbarFormRestoreDefaultsButton\"]/");
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=New * [Popup]]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Save]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Save As...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Cut Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Copy Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Paste Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Delete...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Search...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Refresh Selected Folder]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Undo Typing]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Redo Typing]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Format]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Find-Replace...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Show \\ Hide Outline]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Properties]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Show Preview]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Deploy Gadget]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=UnDeploy Gadget]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Set \\ Unset Autoload]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Validate]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Deploy]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Undeploy]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Launch REST Service]/col[0]"));

      // Check default settings
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=New * [Popup]]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Save]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Save As...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Cut Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Copy Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Paste Item(s)]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Delete...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Search...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Refresh Selected Folder]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Undo Typing]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Redo Typing]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Format]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Find-Replace...]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Show \\ Hide Outline]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Properties]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Show Preview]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Deploy Gadget]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=UnDeploy Gadget]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Set \\ Unset Autoload]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Validate]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Deploy]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Undeploy]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideToolbarItemListGrid\"]/body/row[CommandId=Launch REST Service]/col[0]"));

   }
}