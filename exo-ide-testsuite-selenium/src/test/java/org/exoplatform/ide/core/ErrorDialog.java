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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ErrorDialog extends AbstractTestModule
{
   private final String WARNING_DIALOG_ID = "exoWarningDialog";

   private final String WARNING_MESSAGE_LOCATOR = "//div[@id=\"" + WARNING_DIALOG_ID + "\"]//div[@class=\"gwt-Label\"]";

   private final String OK_BUTTON_ID = "OkButton";

   /**
    * Wait error dialog is opened.
    * 
    * @throws Exception
    */
   public void waitIsOpened() throws Exception
   {
      waitForElementPresent(WARNING_DIALOG_ID);
   }

   public void waitIsClosed() throws Exception
   {
      waitForElementNotPresent(WARNING_DIALOG_ID);
   }

   /**
    * @param message
    */
   public void checkIsOpened(String message)
   {
      fail();
   }

   /**
    * Check message from error dialog equals the pointed one.
    * 
    * @param message message to compare
    */
   public void checkMessageEquals(String message)
   {
      assertEquals(message, selenium().getText(WARNING_MESSAGE_LOCATOR));
   }

   /**
    * Get warning message
    * 
    * @return {@link String} warning message
    */
   public String getMessage()
   {
      return selenium().getText(WARNING_MESSAGE_LOCATOR);
   }

   /**
    * Check message from error dialog contains the pointed one.
    * 
    * @param message message to be contained
    */
   public void checkMessageContains(String message)
   {
      String text = selenium().getText(WARNING_MESSAGE_LOCATOR);
      assertTrue(text.contains(message));
   }

   /**
    * Click "Ok" button on error dialog.
    */
   public void clickOk()
   {
      selenium().click(OK_BUTTON_ID);
   }

}
