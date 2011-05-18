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
   private static final String CKEDITOR_NAME = "CKEditor"; 
   
   private static final String CODEMIRROR_NAME = "CodeMirror"; 
   
   private static final String OPEN_WITH_VIEW_ID = "ideOpenFileWithView"; 
   
   private static final String OPEN_WITH_VIEW_LOCATOR = "//div[@view-id='"+OPEN_WITH_VIEW_ID+"']";
   
   public void checkIsOpened()
   {
      fail();
   }

   public void checkIsOpened(boolean isOpened)
   {
      fail();
   }
   
   public void waitForOpenWithDialogClosed() throws Exception
   {
      waitForElementNotPresent(OPEN_WITH_VIEW_LOCATOR);
   }
   
   public void waitForOpenWithDialogOpened() throws Exception
   {
      waitForElementPresent(OPEN_WITH_VIEW_LOCATOR);
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
    * Selects editor by it's position in the list of editors.
    * Note that numbering starts at 1.
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
      String locator = "//table[@id='ideOpenFileWithListGrid']//tr//div[contains(., '"+value+"')]";
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
      waitForOpenWithDialogOpened();
      selectEditor(CODEMIRROR_NAME);
      if (checkDefault)
      {
         clickUseAsDefaultCheckBox();
      }
      clickOpenButton();
      waitForOpenWithDialogClosed();
   }

   /**
    * Open file from navigation tree with CK (WYSIWYG) editor
    * @param fileURL name of file to open
    * @param checkDefault do mark checkbox Use by default
    * @throws Exception
    */
   public void openSelectedFileWithCkEditor(boolean checkDefault)
      throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);
      waitForOpenWithDialogOpened();
      selectEditor(CKEDITOR_NAME);
      if (checkDefault)
      {
         clickUseAsDefaultCheckBox();
      }
      clickOpenButton();
      waitForOpenWithDialogClosed();
   }

}
