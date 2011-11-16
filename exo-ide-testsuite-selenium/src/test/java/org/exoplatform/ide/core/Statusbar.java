/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Statusbar extends AbstractTestModule
{
   interface Locators
   {
      String CURSOR_POSITION_LOCATOR =
         "//div[@control-id='__editor_cursor_position']//table[@class='exo-statusText-table']//td[@class='exo-statusText-table-middle']";
   }

   @FindBy(xpath = Locators.CURSOR_POSITION_LOCATOR)
   private WebElement cursorPosition;

   /**
    * Get cursor position.
    * @return {@link String}
    */
   public String getCursorPosition()
   {
      return cursorPosition.getText();
   }

   /**
    * Click on cursor position control of status bar.
    * @throws Exception 
    */
   public void clickOnCursorPositionControl()
   {
      cursorPosition.click();
   }

   public void waitCursorPositionControl()
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver arg0)
         {
            return (cursorPosition != null && cursorPosition.isDisplayed());
         }
      });
   }

   /**
    * Get text shown in status bar.
    * 
    * @return {@link String} text
    */
   @Deprecated
   public String getStatusbarText()
   {
      return selenium()
         .getText(
            "//div[@id='exoIDEStatusbar']//div[@class='exoToolbarElementLeft']//table[@class='exo-statusText-table']//td[@class='exo-statusText-table-middle']");
   }
}
