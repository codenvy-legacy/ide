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
import static org.junit.Assert.fail;

import org.exoplatform.ide.TestConstants;

/**
 * This class provides methods for working with Warning dialog.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WarningDialog extends AbstractTestModule
{

   public static final String WARNING_DIALOG_ID = "exoWarningDialog";
   
   public static final String OK_BUTTON_ID = "exoWarningDialog";
   
   /**
    * <b>Use instead assert methods in your test.</b>
    * <p/>
    * Check whether the Warning dialog is opened.
    */
   @Deprecated
   public void checkIsOpened()
   {
      assertTrue(selenium().isElementPresent(WARNING_DIALOG_ID));
      assertTrue(selenium().isElementPresent(OK_BUTTON_ID));
   }

   /**
    * Check whether the Warning dialog is opened and contains specified message.
    * 
    * @param message message
    */
   public void checkIsOpened(String message)
   {
      checkIsOpened();
      assertTrue(selenium().isTextPresent(message));
   }
   
   /**
    * Wait for Warning dialog opened
    * @throws Exception
    */
   public void waitForWarningDialogOpened() throws Exception
   {
      waitForElementPresent(WARNING_DIALOG_ID);
   }

   /**
    * Gets 
    * 
    * @return
    */
   public boolean isDialogOpened()
   {
      fail();
      return false;
   }
   
   public boolean isDialogOpened(String message) {
      fail();
      return false;
   }

   public void clickOk() throws Exception
   {
      selenium().click("//div[@id='" + WARNING_DIALOG_ID + "']//div[@id='" + OK_BUTTON_ID + "']");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickYes() throws Exception
   {
      fail();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickNo() throws Exception
   {
      fail();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickCancel() throws Exception
   {
      fail();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
