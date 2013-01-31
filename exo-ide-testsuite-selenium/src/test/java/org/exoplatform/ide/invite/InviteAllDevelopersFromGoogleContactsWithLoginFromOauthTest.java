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
package org.exoplatform.ide.invite;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.IDE;
import org.exoplatform.ide.MenuCommands;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 2:34:35 PM  Dec 20, 2012 $
 *
 */
public class InviteAllDevelopersFromGoogleContactsWithLoginFromOauthTest extends BaseTest
{

   /**
    * login using oauth
    * @see org.exoplatform.ide.BaseTest#start()
    */
   @Override
   @Before
   public void start()
   {
      //Choose browser Web driver:
      switch (BROWSER_COMMAND)
      {
         case GOOGLE_CHROME :
            service =
               new ChromeDriverService.Builder().usingDriverExecutable(new File("src/test/resources/chromedriver"))
                  .usingAnyFreePort().build();
            try
            {
               service.start();
            }
            catch (IOException e1)
            {
               e1.printStackTrace();
            }

            // For use with ChromeDriver:
            driver = new ChromeDriver(service);
            break;
         case IE_EXPLORE_PROXY :
            driver = new InternetExplorerDriver();
            break;
         default :
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
      }

      IDE = new IDE(ENTRY_POINT_URL + WS_NAME + "/", driver);
      try
      {

         driver.manage().window().maximize();
         driver.get(APPLICATION_URL);
      }
      catch (Exception e)
      {
      }

      try
      {
         IDE.LOGIN.waitTenantAllLoginPage();
         IDE.LOGIN.googleOauthBtnClick();
         IDE.GOOGLE.waitOauthPageOpened();
         IDE.GOOGLE.typeLogin(USER_NAME);
         IDE.GOOGLE.typePassword(USER_PASSWORD);
         IDE.GOOGLE.clickSignIn();
         IDE.GOOGLE.waitAllowApplicationButton();
         IDE.GOOGLE.clickOnAllowButton();
         IDE.PROJECT.EXPLORER.waitOpened();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.GOOGLE.openGoogleAccountPage();
      IDE.GOOGLE.deleteGoogleToken();

      try
      {
         IDE.MAIL_CHECK.cleanMailBox(FIRST_USER_FOR_INVITE, USER_PASSWORD);
         IDE.MAIL_CHECK.cleanMailBox(SECOND_USER_FOR_INVITE, USER_PASSWORD);
         IDE.MAIL_CHECK.cleanMailBox(THIRD_USER_FOR_INVITE, USER_PASSWORD);
      }
      catch (TimeoutException e)
      {
         e.printStackTrace();
      }
      catch (MessagingException e)
      {
         e.printStackTrace();
      }

   }

   @Test
   public void inviteAllDevelopersFromGoogleContactsWithLoginFromOauthTest() throws Exception
   {
      IDE.MENU.runCommand(MenuCommands.Share.SHARE, MenuCommands.Share.INVITE_DEVELOPERS);
      IDE.INVITE_FORM.waitInviteDevelopersOpened();
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(FIRST_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(SECOND_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(THIRD_USER_FOR_INVITE);
      IDE.INVITE_FORM.clickInviteAll();

      assertTrue(IDE.INVITE_FORM.getTextFomIviteButton().contains("3 developers"));

      IDE.INVITE_FORM.inviteClick();
      IDE.INVITE_FORM.waitPopUp();
      IDE.INVITE_FORM.clickOkOnPopUp();
      IDE.LOGIN.logout();

      // checking email and proceed to invite url
      IDE.MAIL_CHECK.waitAndGetInviteLink(FIRST_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOGIN.logout();

      // checking email and proceed to invite url
      IDE.MAIL_CHECK.waitAndGetInviteLink(SECOND_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOGIN.logout();

      // checking email and proceed to invite url
      IDE.MAIL_CHECK.waitAndGetInviteLink(THIRD_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOGIN.logout();
   }
}
