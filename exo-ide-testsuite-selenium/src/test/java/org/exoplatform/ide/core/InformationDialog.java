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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.TestConstants;

/**
 * Operations with information dialogs.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class InformationDialog extends AbstractTestModule
{
   private static final String INFO_DIALOG_ID = "exoInfoDialog";

   private static final String INFO_BUTTON_OK_ID = "exoInfoDialogOkButton";

   private static final String INFO_MESSAGE_LOCATOR = "//div[@id=\"" + INFO_DIALOG_ID
      + "\"]//div[@class=\"gwt-Label\"]";

   /**
    * Check, is information dialog appeared.
    */
   public void checkIsOpened()
   {
      assertTrue(selenium().isElementPresent(INFO_DIALOG_ID));
      assertTrue(selenium().isElementPresent(INFO_BUTTON_OK_ID));
   }

   /**
    * Check, is information dialog with <code>message</code> appeared.
    * @param message - the message
    * @throws Exception
    */
   public void checkIsOpened(String message) throws Exception
   {
      checkIsOpened();
      assertTrue(selenium().isTextPresent(message));
   }

   /**
    * Click Ok button at information dialog.
    * @throws InterruptedException
    */
   public void clickOk() throws InterruptedException
   {
      selenium().click(INFO_BUTTON_OK_ID);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickYes()
   {
   }

   public void clickNo()
   {
   }

   /**
    * Wait for information dialog.
    * @throws Exception
    */
   public void waitForInfoDialog() throws Exception
   {
      waitForElementPresent(INFO_DIALOG_ID);
      waitForElementPresent(INFO_BUTTON_OK_ID);
   }

   /**
    * Wait for information dialog with <code>message</code>.
    * @param message - the message at information dialog.
    * @throws Exception
    */
   public void waitForInfoDialog(String message) throws Exception
   {
      waitForInfoDialog();
      waitForTextPresent(message);
   }

   public void waitForInfoDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(INFO_DIALOG_ID);
   }

   public String getMessage()
   {
      return selenium().getText(INFO_MESSAGE_LOCATOR);
   }
}
