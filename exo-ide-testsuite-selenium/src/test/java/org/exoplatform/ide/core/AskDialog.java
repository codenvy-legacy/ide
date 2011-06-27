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
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AskDialog Apr 27, 2011 2:28:35 PM evgen $
 *
 */
public class AskDialog extends AbstractTestModule
{

   interface Locators
   {

      String ASK_DIALOG_ID = "exoAskDialog";

      String ASK_TITLE = "//div[@id='" + ASK_DIALOG_ID + "']//div[@class='Caption']/span";

      String QUESTION_LOCATOR = "//div[@id=\"" + ASK_DIALOG_ID + "\"]//div[@class=\"gwt-Label\"]";

   }

   public void waitForAskDialogOpened() throws Exception
   {
      waitForElementPresent(Locators.ASK_DIALOG_ID);
   }

   public void waitForAskDialogClosed() throws Exception
   {
      waitForElementNotPresent(Locators.ASK_DIALOG_ID);
   }

   public void assertOpened(String title)
   {
      assertTrue(isOpened(title));
   }

   public boolean isOpened()
   {
      return selenium().isElementPresent(Locators.ASK_DIALOG_ID);
   }

   public boolean isOpened(String title)
   {
      return selenium().isElementPresent(Locators.ASK_TITLE + "[contains(text(), '" + title + "')]");
   }

   public void clickNo() throws Exception
   {
      selenium().click("exoAskDialogNoButton");
      waitForElementNotPresent(Locators.ASK_DIALOG_ID);
   }

   public void clickYes() throws Exception
   {
      selenium().click("//div[@id='exoAskDialog']//div[@id='exoAskDialogYesButton']");
      waitForElementNotPresent(Locators.ASK_DIALOG_ID);
   }

   public void waitForDialog() throws Exception
   {
      waitForElementPresent(Locators.ASK_DIALOG_ID);
   }

   public void waitForDialog(String message) throws Exception
   {
      waitForDialog();
      waitForTextPresent(message);
   }

   public void waitForDialogNotPresent() throws Exception
   {
      waitForElementNotPresent(Locators.ASK_DIALOG_ID);
   }

   /**
    * Get question' text.
    * 
    * @return {@link String} question
    */
   public String getQuestion()
   {
      return selenium().getText(Locators.QUESTION_LOCATOR);
   }
}
