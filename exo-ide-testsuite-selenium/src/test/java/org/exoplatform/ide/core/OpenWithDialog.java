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

import static org.junit.Assert.fail;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenWithDialog extends AbstractTestModule
{

   public void checkIsOpened()
   {
      fail();
   }

   public void checkIsOpened(boolean isOpened)
   {
      fail();
   }

   public void callFromMenu() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);

      waitForElementPresent("//div[@view-id='ideOpenFileWithView']");
      String locator = "//table[@id='ideOpenFileWithListGrid']";
      waitForElementPresent(locator);
   }

   public String getSelectedEditor() throws Exception
   {
      String locator = "//table[@id='ideOpenFileWithListGrid']/tbody/tr/td/div[@tabindex='0']";
      return selenium().getText(locator);
   }

   /**
    * Selects editor by it's name in the list of editors. <p/>
    * 
    * Note, that name of editor - it is not the whole title of 
    * row in the list. The name - it can be part of row, that can uniquely identify
    * editor in list.
    * @param name - the name of editor
    */
   public void selectEditor(String name)
   {
      IDE().selectMainFrame();
      String locator = "//table[@id='ideOpenFileWithListGrid']/tbody/tr/td/div[contains(text(), '" + name + "')]";
      selenium().click(locator);
   }

   public void clickOpenButton() throws Exception
   {
      selenium().click("ideOpenFileWithOkButton");
   }

   public void clickCancelButton()
   {
      selenium().click("ideOpenFileWithCancelButton");
   }
   
   public void clickUseAsDefaultCheckBox() throws Exception {
      String locator = "//input[@name='ideOpenWithIsDefault']";
      selenium().click(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
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
    * Open file from navigation tree with CK (WYSIWYG) editor
    * @param fileURL name of file to open
    * @param checkDefault do mark checkbox Use by default
    * @throws Exception
    */
   public void openFileFromNavigationTreeWithCkEditor(String fileURL, String typeFile, boolean checkDefault)
      throws Exception
   {
      //TODO add check form
      IDE().WORKSPACE.selectItem(fileURL);
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);
      selenium().click(
         "//table[@id='ideOpenFileWithListGrid']//tbody//tr//div[text()=" + "'" + "CKEditor" + " " + typeFile + " "
            + "editor" + "'" + "]");
      if (checkDefault)
      {
         //click on checkbox Use as default editor
         selenium()
            .click(
               "scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
         Thread.sleep(TestConstants.SLEEP);
      }
      selenium().click("ideOpenFileWithOkButton");
      Thread.sleep(TestConstants.SLEEP);
      //time remaining to open CK editor
      Thread.sleep(TestConstants.SLEEP);
      //TODO add check that editor opened
   }

}
