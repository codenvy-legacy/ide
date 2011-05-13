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

public class OpenWith extends AbstractTestModule
{

   public void checkIsOpened()
   {
      fail();
   }

   public void checkIsOpened(boolean isOpened)
   {
      fail();
   }

   public void open() throws Exception
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
    * Selects editor by it's position in the list of editors.
    * Note that numbering begins at one.
    * 
    * @param index
    */
   public void selectEditorByIndex(int index)
   {
      IDE().selectMainFrame();
      String locator = "//table[@id='ideOpenFileWithListGrid']/tbody[1]/tr[" + index + "]/td/div";
      selenium().click(locator);
   }

   public void selectEditor(String value)
   {
      IDE().selectMainFrame();
      String locator = "//table[@id='ideOpenFileWithListGrid']/tbody/tr/td/div[text()='" + value + "']";
      selenium().click(locator);
   }

   public void clickOpen() throws Exception
   {
      selenium().click("ideOpenFileWithOkButton");
   }

   public void clickCancel()
   {
      selenium().click("ideOpenFileWithCancelButton");
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
   private void openSelectedFileWithCodeEditor(boolean checkDefault) throws Exception
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
   protected void openFileFromNavigationTreeWithCkEditor(String fileURL, String typeFile, boolean checkDefault)
      throws Exception
   {
      //TODO add check form
      IDE().NAVIGATION.selectItem(fileURL);
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
