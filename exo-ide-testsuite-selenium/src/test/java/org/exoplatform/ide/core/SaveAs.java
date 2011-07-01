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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exoplatform.ide.TestConstants;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@Deprecated
public class SaveAs extends AbstractTestModule
{
   public static final String SAVE_AS_DIALOG_LOCATOR =
      "//div[@id='ideAskForValueView-window']//div[@class='Caption']/span[text()='Save file as']";

   public void checkIsOpened(boolean isOpened)
   {
      assertEquals(isOpened, selenium().isElementPresent(SAVE_AS_DIALOG_LOCATOR));
   }

   public String getFileName()
   {
      fail();
      return "";
   }

   public void setFileName(String fileName)
   {
      fail();
   }

   public void clickYes() throws Exception
   {
      String locator = "//div[@id='ideAskForValueView-window']//div[@id='ideAskForValueViewYesButton']";
      selenium().click(locator);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   public void clickNo() throws Exception
   {
      String locator = "//div[@id='ideAskForValueView-window']//div[@id='ideAskForValueViewNoButton']";
      selenium().click(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void clickCancel() throws Exception
   {
      String locator = "//div[@id='ideAskForValueView-window']//div[@id='ideAskForValueViewCancelButton']";
      selenium().click(locator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   public void waitForDialog() throws Exception
   {
      waitForElementPresent(SAVE_AS_DIALOG_LOCATOR);
   }

}
