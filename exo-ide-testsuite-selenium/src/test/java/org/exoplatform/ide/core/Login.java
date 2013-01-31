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

import org.exoplatform.ide.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Login.java Dec 27, 2011 4:42:12 PM vereshchaka $
 *
 */
public class Login extends BaseTest
{

   private interface Locators
   {

      String USERNAME = "username";

      String PASSWORD = "password";

      String LOGIN_ON_TENANT_BTN = "submitButton";

      String LOGIN_STANDALONE = "loginButton";

      String GOOGLE_OAUTH_BUTTON = "a.LoginGoogle";

      String GITHUB_OAUTH_BUTTON = "a.LoginGithub";

      String IDE_LOGOUT = "//td[@class='exo-popupMenuTitleField' and text()='Logout']";
   }

   @FindBy(name = Locators.USERNAME)
   private WebElement name;

   @FindBy(id = Locators.LOGIN_ON_TENANT_BTN)
   private WebElement loginTenantButton;

   @FindBy(name = Locators.PASSWORD)
   private WebElement password;

   @FindBy(id = Locators.LOGIN_STANDALONE)
   private WebElement loginButton;

   @FindBy(xpath = Locators.IDE_LOGOUT)
   private WebElement logoutButton;

   @FindBy(linkText = "IDE")
   private WebElement cloudIdeAdditionMenu;

   @FindBy(css = Locators.GOOGLE_OAUTH_BUTTON)
   private WebElement googleOauthBtn;

   @FindBy(css = Locators.GITHUB_OAUTH_BUTTON)
   private WebElement gitHubOauthBtn;

   /**
    * click on google button in tenant authorization form
    */
   public void googleOauthBtnClick()
   {
      googleOauthBtn.click();
   }

   /**
    * click on github button in tenant authorization form
    */
   public void githubBtnClick()
   {
      gitHubOauthBtn.click();
   }

   /**
    * Logout from IDE click on IDE - > Logout
    * @throws Exception 
    */
   public void logout() throws Exception
   {
      openCloudIdeAdditionMenu();
      waitLogoutIDEMenu();
      logoutButton.click();
      waitIdeLoginPage();
   }

   /**
    * Open IDE addition menu where "Logout" is sub-menu item
    */
   private void openCloudIdeAdditionMenu()
   {
      if (cloudIdeAdditionMenu != null)
      {
         cloudIdeAdditionMenu.click();
      }
   }

   /**
    * @param userName
    * @param password
    * @throws Exception
    */
   public void standaloneLogin(String userName, String password) throws Exception
   {
      IDE.INPUT.typeToElement(name, userName, true);
      IDE.INPUT.typeToElement(this.password, password, true);
      login();
   }

   /**
   * login on tenant page
   * @param userName
   * @param password
   * @throws Exception
   */
   public void tenantLogin(String userName, String password) throws Exception
   {
      IDE.INPUT.typeToElement(name, userName, true);
      IDE.INPUT.typeToElement(this.password, password, true);
      tenantLogin();
   }

   /**
    * 
    */
   public void waitStandaloneLogin()
   {
      new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.name(Locators.USERNAME));
               return true;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   public void waitLogoutIDEMenu()
   {
      new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Locators.IDE_LOGOUT)));
   }

   /**
    * wait login jsp page on cloud-ide
    */
   public void waitTenantLoginPage()
   {
      waitTenantAllLoginPage();
   }

   /**
    * wait login jsp page on cloud-ide
    */
   public void waitStandaloneLoginPage()
   {
      new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return name != null && name.isDisplayed() && password != null && password.isDisplayed()
                  && loginButton != null && loginButton.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait login jsp page on cloud-ide
    */
   public void waitTenantAllLoginPage()
   {
      new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return name != null && name.isDisplayed() && password != null && password.isDisplayed()
                  && loginTenantButton != null && loginTenantButton.isDisplayed() && gitHubOauthBtn.isDisplayed()
                  && googleOauthBtn.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * click on button Login
    */
   public void tenantLogin()
   {
      loginTenantButton.click();
      try
      {
         IDE.PROJECT.EXPLORER.waitOpened();
      }
      catch (InterruptedException e)
      {
      }
   }

   /**
    * click on button Login
    */
   public void login()
   {
      loginButton.click();
      try
      {
         IDE.PROJECT.EXPLORER.waitOpened();
      }
      catch (InterruptedException e)
      {
      }
   }

   /**
    * login as invite user on cloud page
    * @throws Exception 
    */
   public void loginAsTenantUser() throws Exception
   {
      tenantLogin(BaseTest.NOT_ROOT_USER_NAME, BaseTest.NOT_ROOT_USER_PASSWORD);
   }

   /**
    * login as root user on cloud page
    * @throws Exception 
    */
   public void loginAsTenantRoot() throws Exception
   {
      tenantLogin(BaseTest.USER_NAME, BaseTest.USER_PASSWORD);
   }

   /**
    * login as invite user on standalone page
    * @throws Exception 
    */
   public void loginAsStandaloneUser() throws Exception
   {
      standaloneLogin(BaseTest.NOT_ROOT_USER_NAME, BaseTest.NOT_ROOT_USER_PASSWORD);
   }

   /**
    * login as root user on standalone page
    * @throws Exception 
    */
   public void loginAsStandaloneRoot() throws Exception
   {
      standaloneLogin(BaseTest.USER_NAME, BaseTest.USER_PASSWORD);
   }

}
