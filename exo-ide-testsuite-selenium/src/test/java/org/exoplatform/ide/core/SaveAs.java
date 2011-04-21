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

import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAs extends AbstractTestModule
{

   private static SaveAs instance;

   public static SaveAs getInstance(Selenium selenium)
   {
      if (instance == null)
      {
         instance = new SaveAs(selenium);
      }

      return instance;
   }

   protected SaveAs(Selenium selenium)
   {
      super(selenium);
   }

   public void checkSaveAsIsOpened(boolean isOpened)
   {
      String locator = "//div[@id='ideAskForValueDialog']//div[@class='Caption']/span[text()='Save file as']";
      assertEquals(isOpened, selenium.isElementPresent(locator));
   }

   public String getFileName()
   {
      return "";
   }

   public void setFileName(String fileName)
   {
   }

   public void clickYes()
   {

   }

   public void clickNo()
   {
      String locator = "//div[@id='ideAskForValueDialog']//div[@id='ideAskForValueDialogNoButton']";
      selenium.click(locator);
   }

   public void clickCancel()
   {

   }

}
