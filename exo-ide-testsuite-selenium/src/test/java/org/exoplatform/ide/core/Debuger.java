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

import org.exoplatform.ide.MenuCommands.New;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Debuger extends AbstractTestModule
{
   private interface Locators
   {
      String DEBUGER_VIEW_PANEL = "//div[@view-id='ideDebuggerView']";

      String CODEMIRROR_LINE_POINT = "//div[@class='CodeMirror-line-numbers']/div[%s]";

      String BREAK_POINT_IMAGE = "//div[@class='CodeMirror-line-numbers']/div[@title='Breakpoint' and text()=%s]";

      String CHANGE_VAR_FIELD = "ideChangeVariableValueViewExpressionField";

      String CONFIRM_CHANGE_VAR_BTN = "ideChangeVariableValueViewChangeButton";

      String RESUME_BTN = "//div[@view-id='ideDebuggerView']//div[@class='exoToolbarPanel']/div[1]/div";

      String CHANGE_VAR_BTN = "//div[@view-id='ideDebuggerView']//div[@class='exoToolbarPanel']/div[7]/div";

   }

   @FindBy(xpath = Locators.CHANGE_VAR_BTN)
   private WebElement changeVarBtn;

   @FindBy(xpath = Locators.DEBUGER_VIEW_PANEL)
   private WebElement view;

   @FindBy(name = Locators.CHANGE_VAR_FIELD)
   private WebElement changeField;

   @FindBy(id = Locators.CONFIRM_CHANGE_VAR_BTN)
   private WebElement confirmChange;

   /**
    * click on change variablr btn
    * 
    * @throws Exception
    */
   public void changeVarBtnClick() throws Exception
   {
      changeVarBtn.click();
   }

   /**
    * Wait build project view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 60).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed();
         }
      });
   }

   /**
    * Wait change variable field
    * 
    * @throws Exception
    */
   public void waitShangeWarField() throws Exception
   {
      new WebDriverWait(driver(), 60).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return changeField != null && changeField.isDisplayed();
         }
      });
   }

   /**
    * Wait change variable field
    * 
    * @throws Exception
    */
   public void typeToChangeVariableField(String value) throws Exception
   {
      changeField.sendKeys(value);
   }

   /**
    * Wait change variable field
    * 
    * @throws Exception
    */
   public void confirmChangeBtnClick() throws Exception
   {
      confirmChange.click();
   }

   /**
    * click on resume btn
    * 
    * @throws Exception
    */
   public void resumeBtnClick() throws Exception
   {
      driver().findElement(By.xpath(Locators.RESUME_BTN)).click();
   }

   /**
    * Wait build project view opened.
    * 
    * @throws Exception
    */
   public void waitSetBreakPoint(final int numLine) throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            WebElement setImg = driver().findElement(By.xpath(String.format(Locators.BREAK_POINT_IMAGE, numLine)));
            return setImg != null && setImg.isDisplayed();
         }
      });
   }

   /**
    * Wait build project view opened.
    * 
    * @throws Exception
    */
   public void waitUnSetBreakPoint(final int numLine) throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement setImg = driver().findElement(By.xpath(String.format(Locators.BREAK_POINT_IMAGE, numLine)));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   public void setBrackPoint(int numLine) throws Exception
   {
      WebElement set = driver().findElement(By.xpath(String.format(Locators.CODEMIRROR_LINE_POINT, numLine)));
      new Actions(driver()).doubleClick(set).build().perform();
      waitSetBreakPoint(numLine);
   }

   public void unSetBrackPoint(int numLine) throws Exception
   {
      WebElement set = driver().findElement(By.xpath(String.format(Locators.CODEMIRROR_LINE_POINT, numLine)));
      new Actions(driver()).doubleClick(set).build().perform();
      waitUnSetBreakPoint(numLine);
   }

}
