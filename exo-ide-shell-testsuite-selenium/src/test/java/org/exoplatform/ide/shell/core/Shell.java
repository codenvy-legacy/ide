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
package org.exoplatform.ide.shell.core;

import org.exoplatform.ide.shell.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 26, 2011 3:12:07 PM evgen $
 * 
 */
public class Shell
{
   private static final String SHELL_CONTENT_ID = "shellContent";

   private static final String TERM_CONTENT_ID = "termContent";

   @FindBy(id = SHELL_CONTENT_ID)
   public WebElement shell;

   @FindBy(id = TERM_CONTENT_ID)
   public WebElement term;


   public Shell()
   {
      new WebDriverWait(BaseTest.driver, 10).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return input.findElement(By.id(SHELL_CONTENT_ID)) != null
               && input.findElement(By.id(TERM_CONTENT_ID)) != null;
         }

      });
   }

   public String getContent()
   {
      return shell.getText();
   }
   
   public String getTerm()
   {
      return term.getText();
   }
   
   /**
    * @param command
    * @throws InterruptedException
    */
   public void type(CharSequence command) throws InterruptedException
   {
      term.sendKeys(command);
      Thread.sleep(500);
   }

   /**
    * 
    */
   public void executeCommand()
   {
      final int contentLenth = shell.getText().length();
      term.sendKeys(Keys.RETURN);
      new WebDriverWait(BaseTest.driver, 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
            return shell.getText().length() != contentLenth;
         }

      });

   }

   /**
    * @param string
    * @throws InterruptedException
    */
   public void executeCommand(String command) throws InterruptedException
   {
      type(command);
      executeCommand();
   }

}
