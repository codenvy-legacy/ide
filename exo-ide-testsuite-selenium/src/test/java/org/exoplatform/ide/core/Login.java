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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Login.java Dec 27, 2011 4:42:12 PM vereshchaka $
 *
 */
public class Login extends AbstractTestModule
{
   
   private static final String LOGOUT_LOCATOR = "//node()[@id='logoutButton']";
   
   private static final String USERNAME = "j_username";
   
   private static final String PASSWORD = "j_password";
   
   private static final String LOGIN_BUTTON = "//node()[@id='loginButton']";
   
   @FindBy(xpath = LOGOUT_LOCATOR)
   private WebElement logout;
   
   @FindBy(name = USERNAME)
   private WebElement name;
   
   @FindBy(name = PASSWORD)
   private WebElement password;
   
   @FindBy(xpath = LOGIN_BUTTON)
   private WebElement login;
   
   public void logout()
   {
      logout.click();
   }
   
   public void standaloneLogin(String userName, String password) throws Exception
   {
      IDE().INPUT.typeToElement(name, userName, true);
      IDE().INPUT.typeToElement(this.password, password, true);
      login.click();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
   }
   
   public void waitStandaloneLogin()
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
         {
            @Override
            public Boolean apply(WebDriver input)
            {
               try
               {
                  input.findElement(By.name(USERNAME));
                  return true;
               }
               catch (NoSuchElementException e)
               {
                  return false;
               }
            }
         });
   }

}
