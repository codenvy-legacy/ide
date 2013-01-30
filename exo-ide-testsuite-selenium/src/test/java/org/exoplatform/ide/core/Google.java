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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *  The Object containing GoogleAuthPage page`s webelenents
 */
public class Google extends AbstractTestModule
{
   private interface Locators
   {
      String LOGIN_FIELD_ID = "Email";

      String PASS_FIELD_ID = "Passwd";

      String SIGN_IN_BTN_ID = "signIn";

      String APPROVE_ACCESS_BTN_ID = "submit_approve_access";

      String MAIL_BOX_ENTRY = "//span[text()='Gmail']";

      String ALLOW_BUTTON_ID = "submit_approve_access";

      String SECURITU_BUTTON_ID = "nav-security";

      String ACCESS_CONTROL_BUTTON = "//a[contains(@href, 'IssuedAuthSubTokens')]//div[@role='button']";

      String REVOKE_BUTTON = "//form[@action='RevokeAuthSubAccess' and contains(.,'Cloud')]//input[@type='submit']";

   }

   @FindBy(id = Locators.LOGIN_FIELD_ID)
   public WebElement loginField;

   @FindBy(id = Locators.PASS_FIELD_ID)
   public WebElement passField;

   @FindBy(id = Locators.SIGN_IN_BTN_ID)
   public WebElement signInBtn;

   @FindBy(id = Locators.APPROVE_ACCESS_BTN_ID)
   private WebElement approveBtn;

   @FindBy(xpath = Locators.MAIL_BOX_ENTRY)
   private WebElement mailBox;

   @FindBy(id = Locators.ALLOW_BUTTON_ID)
   private WebElement allowBtn;

   @FindBy(id = Locators.SECURITU_BUTTON_ID)
   public WebElement securityBtn;

   @FindBy(xpath = Locators.ACCESS_CONTROL_BUTTON)
   private WebElement accessControlBtn;

   @FindBy(xpath = Locators.REVOKE_BUTTON)
   private WebElement revokeBtn;

   /**
    * wait approve access button
    */
   public void waitMailbox()
   {

      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return mailBox != null && mailBox.isDisplayed();
         }
      });
   }

   /**
    * Wait web elements for login on google.
    */
   public void waitOauthPageOpened() throws Exception
   {

      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return signInBtn.isDisplayed() && passField.isDisplayed() && loginField.isDisplayed();
         }
      });
   }

   /**
    * wait approve access button
    */
   public void waitApproveAccessPage()
   {

      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            return approveBtn != null && approveBtn.isDisplayed();
         }
      });
   }

   /**
    * type login on auth google page 
    * @param login
    */
   public void typeLogin(String login)
   {
      loginField.sendKeys(login);
   }

   /**
    * type password on auth google page 
    * @param password
    */
   public void typePassword(String login)
   {
      passField.sendKeys(login);
   }

   /**
    * click for sign in button
    * @param login
    */
   public void clickSignIn()
   {
      signInBtn.click();
   }

   /**
    * click for approve button
    * @param login
    */
   public void clickApproveBtn()
   {
      approveBtn.click();
   }

   /**
    * Wait for allow button
    */
   public void waitAllowApplicationButton()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.elementToBeClickable(By.id(Locators.ALLOW_BUTTON_ID)));
   }

   /**
    * Click on allow button
    */
   public void clickOnAllowButton()
   {
      allowBtn.click();
   }

   /**
    * Wait for security button
    */
   public void waitSecurityButton()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOf(securityBtn));
   }

   /**
    * Click on security button
    */
   public void clickOnSecurityButton()
   {
      securityBtn.click();
   }

   /**
    * Wait for access control button
    */
   public void waitAccessControlButton()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOf(accessControlBtn));
   }

   /**
    * Click on access control button
    */
   public void clickOnAccessControlButton()
   {
      accessControlBtn.click();
   }

   /**
    * Wait for revoke button
    */
   public void waitRevokeButton()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOf(revokeBtn));
   }

   /**
    * Wait for revoke button disappear
    */
   public void waitRevokeButtonDisappear()
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.invisibilityOfElementLocated(By
         .xpath(Locators.REVOKE_BUTTON)));
   }

   /**
    * Click on revoke button
    */
   public void clickOnRevokeButton()
   {
      revokeBtn.click();
   }

   /**
    * open google account page
    * @throws Exception
    */
   public void openGoogleAccountPage() throws Exception
   {
      driver().get("https://accounts.google.com");
      waitSecurityButton();
   }

   /**
    * Delete google token
    */
   public void deleteGoogleToken()
   {
      waitSecurityButton();
      clickOnSecurityButton();
      waitAccessControlButton();
      clickOnAccessControlButton();
      waitRevokeButton();
      clickOnRevokeButton();
      waitRevokeButtonDisappear();
   }
}
