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

import org.exoplatform.ide.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *  The Object containing GitHubAuthPage page`s webelenents
 */
public class GitHub extends BaseTest

{
   private interface Locators
   {

      String LOGIN_FIELD = "login_field";

      String PASS_FIELD = "password";

      String SIGN_IN_BTN = "input[value='Sign in']";

      String ACCOUNT_SETTINGS = "account_settings";

      String APPLICATIONS_BTN = "//*[@id='settings-nav']//a[contains(.,'Applications')]";

      String REVOKE_BTN = "//div[@id='page-settings']//li[contains(.,'envy')]/a[@data-method='delete']";

      String AUTHORIZE_BUTTON = "//button[@class='button primary' and @type='submit']";

      String LOGOUT_BTN = "logout";
   }

   @FindBy(id = Locators.LOGIN_FIELD)
   public WebElement loginField;

   @FindBy(id = Locators.PASS_FIELD)
   public WebElement passField;

   @FindBy(css = Locators.SIGN_IN_BTN)
   public WebElement signInBtn;

   @FindBy(id = Locators.ACCOUNT_SETTINGS)
   public WebElement accountSettings;

   @FindBy(xpath = Locators.APPLICATIONS_BTN)
   public WebElement applicationsBtn;

   @FindBy(xpath = Locators.REVOKE_BTN)
   public WebElement revokeBtn;

   @FindBy(xpath = Locators.AUTHORIZE_BUTTON)
   public WebElement authorizeBtn;

   @FindBy(id = Locators.LOGOUT_BTN)
   public WebElement logoutBtn;

   /**
    * Wait web elements for login on google.
    */
   public void waitAuthorizationPageOpened() throws Exception
   {

      new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {

            return loginField.isDisplayed() && passField.isDisplayed() && signInBtn.isDisplayed();

         }
      });
   }

   /**
    *  type login to login field
   * @param Login
   */
   public void typeLogin(String login)
   {
      loginField.sendKeys(login);
   }

   /**
    *  type password to password field
   * @param Login
   */
   public void typePass(String pass)
   {
      passField.sendKeys(pass);
   }

   /**
    * click on submit btn on github auth form
    */
   public void submit()
   {
      signInBtn.click();
   }

   public void openGithub() throws Exception
   {
      driver.get("https://github.com/login");
      waitAccountSettingsBtn();
   }

   public void openGithubAndLogin(String login, String pass) throws Exception
   {
      driver.get("https://github.com/login");
      waitAuthorizationPageOpened();
      typeLogin(login);
      typePass(pass);
      submit();
      waitAccountSettingsBtn();
   }

   /**
    * wait for appearing account settings button
    */
   public void waitAccountSettingsBtn()
   {
      new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .id(Locators.ACCOUNT_SETTINGS)));
   }

   /**
    * click on account settings button
    */
   public void clickOnAccountSettingsButton()
   {
      accountSettings.click();
   }

   /**
    * wait for appearing applications button in settings menu.
    */
   public void waitApplicationsBtn()
   {
      new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.APPLICATIONS_BTN)));
   }

   /**
    * click on applications button in settings menu.
    */
   public void clickOnApplicationsButton()
   {
      applicationsBtn.click();
   }

   /**
    * wait for appearing Revoke button in applications menu.
    */
   public void waitRevokeBtn()
   {
      new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Locators.REVOKE_BTN)));
   }

   /**
    * wait for disappearing Revoke button in applications menu.
    */
   public void waitRevokeBtnDisappear()
   {
      new WebDriverWait(driver, 30)
         .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Locators.REVOKE_BTN)));
   }

   /**
    * click on Revoke button in applications menu.
    */
   public void clickOnRevokeButton()
   {
      revokeBtn.click();
   }

   /**
    * Open Github, login, open settings and delete token
    * @throws Exception 
    */
   public void deleteGithubToken() throws Exception
   {
      clickOnAccountSettingsButton();
      waitApplicationsBtn();
      clickOnApplicationsButton();
      waitRevokeBtn();
      clickOnRevokeButton();
      waitRevokeBtnDisappear();
      clickOnLogoutBtn();
   }

   /**
    * wait for authorize button.
    */
   public void waitAuthorizeBtn()
   {
      new WebDriverWait(driver, 30).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath(Locators.AUTHORIZE_BUTTON)));
   }

   /**
    * click on authorize button
    */
   public void clickOnAuthorizeBtn()
   {
      authorizeBtn.click();
   }

   /**
    * logout from github
    */
   public void clickOnLogoutBtn()
   {
      logoutBtn.click();
   }

}
