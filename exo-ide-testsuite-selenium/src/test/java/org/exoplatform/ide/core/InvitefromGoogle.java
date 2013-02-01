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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Object Page for describing main elements
 * Invite developers control
 */

public class InvitefromGoogle extends AbstractTestModule
{
   private final class Locators
   {
      final static String INVITE_DEVELOPERS_ID_ACTIVE =
         "//div [@view-id='ide.inviteGitHubDevelopersView' and @is-active='true']";

      final static String INVITE_BTN =
         "//div[@id='ide.inviteGitHubDevelopersView-window']//td[@class='imageButtonStretch']//tr[contains(.,'Invite')]";

      final static String INVITE_ALL_CHKBOX = "//div[text()='Invite all']/parent::node()//input";

      final static String ENTER_EMAIL_ADRESS_MANYALLY =
         "//div[@id='ide.inviteGitHubDevelopersView-window']//input[@type='text']";

      final static String INVITE_CHECK_BOX_GOOGLE =
         "//div[@id='ide.inviteGitHubDevelopersView-window']/div/table/tbody/tr[2]/td[2]/div/div/div[2]/div/div[2]/div/div[5]/div[1]/div[%s]//input[@type='checkbox']";

      final static String INVITE_CHECK_BOX_GITHUB =
         "//div[@id='ide.inviteGitHubDevelopersView-window']//div/div/div/div/div/div/div/div/div[3]/div/div//span/input[@type='checkbox']";

      final static String INVITATION_MESSAGE = "//div[@id='ide.inviteGitHubDevelopersView-window']//textarea";

      final static String INFO_POP_UP = "ideInformationModalView-window";

      final static String POP_UP_OK_BUTTON = "OkButton";

      final static String USER_FROM_CONTACTS_TO_INVITE =
         "//div[@id='ide.inviteGitHubDevelopersView-window']//div[text()='%s']";

      final static String CONNECT_TO_GOOGLE_VIEW =
         "//div[@id='ideAskModalView-window']//div[text()='You have to be logged in Google account!']";

      final static String OK_BUTTON_ON_CONNECT_TO_GOOGLE_FORM =
         "//div[@id='ideAskModalView-window']//div[@id='YesButton']";
   }

   @FindBy(xpath = Locators.INVITE_DEVELOPERS_ID_ACTIVE)
   private WebElement viewDevelopers;

   @FindBy(xpath = Locators.INVITE_BTN)
   private WebElement invBtn;

   @FindBy(xpath = Locators.INVITE_ALL_CHKBOX)
   private WebElement invAll;

   @FindBy(xpath = Locators.ENTER_EMAIL_ADRESS_MANYALLY)
   private WebElement typeEmailForInvite;

   @FindBy(xpath = Locators.INVITATION_MESSAGE)
   private WebElement invitationMessage;

   @FindBy(id = Locators.INFO_POP_UP)
   private WebElement popUp;

   @FindBy(id = Locators.POP_UP_OK_BUTTON)
   private WebElement popUpOkBtn;

   @FindBy(xpath = Locators.CONNECT_TO_GOOGLE_VIEW)
   private WebElement connectToGoogleView;

   @FindBy(xpath = Locators.OK_BUTTON_ON_CONNECT_TO_GOOGLE_FORM)
   private WebElement okButtonOnConnectToGoogleView;

   /**
    * Wait Available dependencies view opened.
    * 
    * @throws Exception
    */
   public void waitInviteDevelopersOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return viewDevelopers.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * click on invite button
    */
   public void inviteClick()
   {
      invBtn.click();
   }

   /**
    * Click on invite all check box
    */
   public void clickInviteAll()
   {
      invAll.click();
   }

   /**
    * Type email in to form
    * @param value
    */
   public void typeEmailForInvite(String value)
   {
      typeEmailForInvite.sendKeys(value);
   }

   /**
    * Type to personal message
    * @param value
    */
   public void typeInvitationMeassge(String value)
   {
      invitationMessage.sendKeys(value);
   }

   /**
    * get text from invite button
    */
   public String getTextFomIviteButton()
   {
      return invBtn.getText();
   }

   /**
    * wait for information pop up form
    * @throws Exception
    */
   public void waitPopUp() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return popUp != null && popUp.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Click ok on information pop up form
    */
   public void clickOkOnPopUp()
   {
      popUpOkBtn.click();
   }

   /**
    * wait for appearing users from contact in form to invite
    * @throws Exception
    */
   public void waitForUsersFromContactsToInvite(final String user) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement elem =
                  driver().findElement(By.xpath(String.format(Locators.USER_FROM_CONTACTS_TO_INVITE, user)));
               return elem.isDisplayed() && elem != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait for appearing connect to google form
    * @throws Exception
    */
   public void waitForConnectToGoogleForm() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {

               return connectToGoogleView.isDisplayed() && connectToGoogleView != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * click ok button on connect to google form
    */
   public void clickOnOkButtonOnConnectToGoogleForm()
   {
      okButtonOnConnectToGoogleView.click();
   }

   /**
    * wait while checkbox is unchecked on google form
    * @throws Exception
    * @param checkBoxNum
    */
   public void waitCheckboxIsCheckedGoogleForm(final String checkBoxNum) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement box =
                  driver().findElement(By.xpath(String.format(Locators.INVITE_CHECK_BOX_GOOGLE, checkBoxNum)));

               return box.isSelected();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait while chekbox in form unchecked on  google form
    * @param checkBoxNum
    */
   public void waitChekBoxUnchekedGoogleForm(final String checkBoxNum)
   {
      WebElement elem = driver().findElement(By.xpath(String.format(Locators.INVITE_CHECK_BOX_GOOGLE, checkBoxNum)));
      (new WebDriverWait(driver(), 30)).until(ExpectedConditions.elementSelectionStateToBe(elem, false));
   }

   /**
    * click on check box by index google form
    * @param checkBoxNum
    */
   public void clickOnCheckBoxGoogleForm(String checkBoxNum)
   {
      WebElement box = driver().findElement(By.xpath(String.format(Locators.INVITE_CHECK_BOX_GOOGLE, checkBoxNum)));
      box.click();
   }

   /**
    * wait while checkbox is unchecked on github form
    * @throws Exception
    * @param checkBoxNum
    */
   public void waitCheckboxIsCheckedGithubForm(final String checkBoxNum) throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement box =
                  driver().findElement(By.xpath(String.format(Locators.INVITE_CHECK_BOX_GITHUB, checkBoxNum)));

               return box.isSelected();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * wait while chekbox in form unchecked on github form
    * @param checkBoxNum
    */
   public void waitChekBoxUnchekedGithubForm(final String checkBoxNum)
   {
      WebElement elem = driver().findElement(By.xpath(String.format(Locators.INVITE_CHECK_BOX_GITHUB, checkBoxNum)));
      (new WebDriverWait(driver(), 30)).until(ExpectedConditions.elementSelectionStateToBe(elem, false));
   }

   /**
    * click on check box by index github form
    * @param checkBoxNum
    */
   public void clickOnCheckBoxGithubForm(String checkBoxNum)
   {
      WebElement box = driver().findElement(By.xpath(String.format(Locators.INVITE_CHECK_BOX_GITHUB, checkBoxNum)));
      box.click();
   }

   /**
    * Check that invite button is enabled
    */
   public boolean isInviteButtonEnabled()
   {
      return invBtn.isEnabled();
   }

   /**
    * Check that invite button is disabled
    */
   public boolean isInviteButtonDisabled()
   {
      return !invBtn.isEnabled();
   }
}
