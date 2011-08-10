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

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RenameDialog extends AbstractTestModule
{

   private static String RENAME_BUTTON_ID = "ideRenameItemFormRenameButton";

   private static String CANCEL_BUTTON_ID = "ideRenameItemFormCancelButton";

   private static String FORM_LOCATOR = "//div[@view-id='ideRenameItemForm']";

   private static String RENAME_TO_LOCATOR = FORM_LOCATOR + "//input[@name='ideRenameItemFormRenameField']";

   private static String MIME_TYPE_LOCATOR = FORM_LOCATOR + "//input[@name='ideRenameItemFormMimeTypeField']";

   private static String MIME_TYPE_DISABLED_LOCATOR = FORM_LOCATOR
      + "//input[@name='ideRenameItemFormMimeTypeField' and @disabled='']";

   private static String WARNING_MESSAGE_LOCATOR = FORM_LOCATOR + "//div[contains(@class, 'exo-rename-warning-msg')]";
   
   public void waitForRenameDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(FORM_LOCATOR);
   }

   /**
    * Runs menu command "File > Rename" and waits for Rename dialog is appear.
    * 
    * @throws Exception
    */
   public void callFromMenu() throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);

      waitForElementPresent(FORM_LOCATOR);
      waitForElementPresent(RENAME_TO_LOCATOR);
   }

   /**
    * Gets Warning message
    * 
    * @return
    */
   public String getWarningMessage()
   {
      return selenium().getText(WARNING_MESSAGE_LOCATOR);
   }

   /**
    * Gets file name
    * 
    * @return
    */
   public String getFileName()
   {
      return selenium().getText(RENAME_TO_LOCATOR);
   }

   /**
    * Sets new file name
    * 
    * @param newFileName
    */
   public void setFileName(String newFileName)
   {
      selenium().type(RENAME_TO_LOCATOR, newFileName);
   }

   /**
    * Gets mime type
    * 
    * @return
    */
   public String getMimeType()
   {
      return selenium().getText(MIME_TYPE_LOCATOR);
   }

   /**
    * Sets new mime type
    * 
    * @param mimeType
    */
   public void setMimeType(String mimeType)
   {
      selenium().type(MIME_TYPE_LOCATOR, mimeType);
   }

   /**
    * Clicks on Rename button
    * 
    * @throws Exception
    */
   public void clickRenameButton() throws Exception
   {
      selenium().click(FORM_LOCATOR + "//div[@id='" + RENAME_BUTTON_ID + "']");
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   /**
    * Clicks on Cancel button
    * 
    * @throws Exception
    */
   public void clickCancelButton() throws Exception
   {
      selenium().click(FORM_LOCATOR + "//div[@id='" + CANCEL_BUTTON_ID + "']");
      waitForElementNotPresent(FORM_LOCATOR);
   }

}
