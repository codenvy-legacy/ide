/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class WelcomePage extends AbstractTestModule
{

   private interface Locators
   {
      String IMPORTFROM_GITHUB_BTN = "//button[text()='Import from GitHub']";

      String CREATE_A_NEW_PROJECT_FROM_SCRATCH = "//button[text()='Create a New Project from Scratch']";
   }

   @FindBy(xpath = Locators.IMPORTFROM_GITHUB_BTN)
   WebElement inportFromGithub;

   @FindBy(xpath = Locators.CREATE_A_NEW_PROJECT_FROM_SCRATCH)
   WebElement createNewProjectFromScratch;

   /**
    * wait opening button 'Import from github'
    * in 'Welcome' tab
    */
   public void waitImportFromGithubBtnOpened()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return inportFromGithub.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * click on import from github btn in IDE 
    */
   public void clickImportFromGithub()
   {
      inportFromGithub.click();
   }

   /**
    * click on Create a New Project from Scratch on welcome page 
    */
   public void clickCreateNewProjectFromScratch()
   {
      createNewProjectFromScratch.click();
   }

   /**
    * close 'Welcome' tab in IDE 
    */
   public void close()
   {
      try
      {
         driver().findElement(By.cssSelector("div[tab-title=\"Welcome\"]")).click();
      }
      catch (NoSuchElementException e)
      {
         //Nothing to do Welcome page already closed
      }
   }

   /**
    * wait while 'Welcome' tab in IDE is closed
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      IDE().EDITOR.waitTabNotPresent("Welcome");
   }

}
