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

import org.exoplatform.ide.TestConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:mmusienko@exoplatform.com">Musienko Maksim</a>
 * @version $
 */

public class ErrorMarks extends AbstractTestModule
{
   private interface Locators
   {

      String ERROR_MARKER_PREFIX = "//div[@class='CodeMirror-line-numbers']/div[text() = '%s' and @title]";

      String ERROR_MARKER_LABEL = "//div[@class='CodeMirror-line-numbers']//div[@title=\"%s\" and text()=%s]";

      String DECLARATION_FORM_ID = "ideAssistImportDeclarationForm";

      String DECLARATION_FORM = "//div[@id='ideAssistImportDeclarationForm']//div[@class='gwt-Label' and text()='%s']";

      String LINE_NUMBERS_CLASS = "//div[@class='CodeMirror-line-numbers']/div[text()='%s']";

      String FQN_PREFIX = "//div[@id='ideAssistImportDeclarationForm']//div[text()='%s']";

      String MARKER_EDITOR_LOCATOR = "//div[@panel-id='editor' and @view-id='editor-%s' ]//iframe";
   }

   /** wait appearance of the error in editor 
    * @param numEditor
    * @param numLine
    */
   public void waitLineNumberAppearance(final int numLine)
   {
      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver d)
         {  WebElement line = driver().findElement(By.xpath(String.format(Locators.LINE_NUMBERS_CLASS, numLine)));
            return line != null && line.isDisplayed();
         }

      });
   }

   /**
    * checks the appearance of the marker
    * @param numString
    * @return
    */
   public boolean isErrorMarkerShow(int numString)
   {
      try
      {
         WebElement mark = driver().findElement(By.xpath(String.format(Locators.ERROR_MARKER_PREFIX, numString)));
         return mark != null && mark.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * wait open declaration form
    * @param numString
    * @return
    */
   public void waitDeclarationFormOpen(final String declaration)
   {

      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               WebElement mark = driver().findElement(By.xpath(String.format(Locators.DECLARATION_FORM, declaration)));
               return mark != null && mark.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait marker is appear
    * @param numString
    * @return
    */
   public void waitErrorMarkerIsAppear(final int numMark)
   {

      (new WebDriverWait(driver(), 5)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               WebElement mark = driver().findElement(By.xpath(String.format(Locators.ERROR_MARKER_PREFIX, numMark)));
               return mark != null && mark.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait marker is disappear
    * @param numString
    * @return
    */
   public void waitErrorMarkerIsDisAppear(final int numMark)
   {

      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               WebElement mark = driver().findElement(By.xpath(String.format(Locators.ERROR_MARKER_PREFIX, numMark)));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   /**
    * wait setting changes in error marker
    * after fix or other changes in code 
    * @param markLabel
    * @param numMarker
    */
   public void waitChangesInErrorMarker(final String markLabel, final int numMarker)
   {

      (new WebDriverWait(driver(), 5)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               WebElement mark =
                  driver().findElement(By.xpath(String.format(Locators.ERROR_MARKER_LABEL, markLabel, numMarker)));
               return true;
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait fqn declaration is appear
    * @param numString
    * @return
    */
   public void waitFqnDeclarationIsAppear(final String fqnName)
   {

      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {
            try
            {
               WebElement fqn = driver().findElement(By.xpath(String.format(Locators.FQN_PREFIX, fqnName)));
               return fqn != null && fqn.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * click on declaration with specified name
    * @param declaration
    */
   public void selectAndInsertDeclaration(String declaration)
   {
      WebElement decl = driver().findElement(By.xpath(String.format(Locators.DECLARATION_FORM, declaration)));
      new Actions(driver()).doubleClick(decl).build().perform();
   }

   /**
    * click on declaration with specified name
    * @param declaration
    */
   public void selectAndInsertFqn(String fqn)
   {
      WebElement declFqn = driver().findElement(By.xpath(String.format(Locators.FQN_PREFIX, fqn)));
      new Actions(driver()).doubleClick(declFqn).build().perform();
   }

   /**
    * wait closed declaration form
    * @param numString
    * @return
    */
   public void waitDeclarationFormClosed()
   {
      (new WebDriverWait(driver(), 10)).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver d)
         {

            try
            {
               WebElement mark = driver().findElement(By.id(Locators.DECLARATION_FORM_ID));
               return false;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });
   }

   /**
    * get title text from error marker 
    * @param numString
    * @return
    */
   public String getTextFromErorMarker(int numString)
   {
      WebElement mark = driver().findElement(By.xpath(String.format(Locators.ERROR_MARKER_PREFIX, numString)));
      return mark.getAttribute("title");
   }

   /**
    * get title text from error marker 
    * @param numString
    * @return
    */
   public void clickOnErorMarker(int numString)
   {
      WebElement mark = driver().findElement(By.xpath(String.format(Locators.ERROR_MARKER_PREFIX, numString)));
      mark.click();
   }

   /**
    * move cursor to down in declaration
    * specified number of times 
    * @param numClick
    * @throws InterruptedException 
    */
   public void downMoveCursorInDeclForm(int numClick) throws InterruptedException
   {
      WebElement mark = driver().findElement(By.id(Locators.DECLARATION_FORM_ID));

      for (int i = 0; i < numClick; i++)
      {
         mark.click();
         new Actions(driver()).sendKeys(Keys.ARROW_DOWN.toString()).build().perform();
         //sendKeys(Keys.ARROW_DOWN.toString());
         //delay for emulation of the user input
         Thread.sleep(TestConstants.REDRAW_PERIOD);
      }
   }

   /**
    * move up cursor in declaration
    * specified number of times 
    * @param numClick
    * @throws InterruptedException 
    */
   public void upMoveCursorInDeclForm(int numClick) throws InterruptedException
   {
      WebElement mark = driver().findElement(By.id(Locators.DECLARATION_FORM_ID));

      for (int i = 0; i < numClick; i++)
      {
         mark.sendKeys(Keys.ARROW_UP.toString());
         //delay for emulation of the user input
         Thread.sleep(TestConstants.REDRAW_PERIOD);
      }
   }

   /**
    * select iframe with error markers panel
    * @param numEditor
    */
   public void selectIframeWitErrorMarks(int numEditor)
   {
      WebElement frameWithMarkers =
         driver().findElement(By.xpath(String.format(Locators.MARKER_EDITOR_LOCATOR, numEditor)));
      driver().switchTo().frame(frameWithMarkers);
   }

}
