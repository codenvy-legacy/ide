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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */

public class InviteFromGoogleContactsFormUITest extends BaseTest
{

   @Override
   @Before
   public void start() throws Exception
   {
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
         e.printStackTrace();
      }

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

   @Test
   public void allInviteUserCounterTest() throws Exception
   {
      //step 1 run share menu an wait invite developers
      IDE.MENU.runCommand(MenuCommands.Share.SHARE, MenuCommands.Share.INVITE_DEVELOPERS);
      IDE.INVITE_FORM.waitInviteDevelopersOpened();
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(FIRST_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(SECOND_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(THIRD_USER_FOR_INVITE);

      //step 2 invite all click and check states boxes
      IDE.INVITE_FORM.clickInviteAll();
      assertEquals(IDE.INVITE_FORM.getTextFomIviteButton(), getAmountInvDevelopers(3));
      IDE.INVITE_FORM.waitCheckboxIsCheckedGoogleForm("1");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGoogleForm("2");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGoogleForm("3");

      //step 3 uncheck invite  btn and check states boxes and inv. developers button
      IDE.INVITE_FORM.clickInviteAll();
      IDE.INVITE_FORM.waitChekBoxUnchekedGoogleForm("1");
      IDE.INVITE_FORM.waitChekBoxUnchekedGoogleForm("2");
      IDE.INVITE_FORM.waitChekBoxUnchekedGoogleForm("3");

      assertTrue(IDE.INVITE_FORM.getTextFomIviteButton().contains("Invite developers"));

      //step 4 click on developers 1-3 and check state boxes and inv. developers button
      IDE.INVITE_FORM.clickOnCheckBoxGoogleForm("1");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGoogleForm("1");
      assertEquals(IDE.INVITE_FORM.getTextFomIviteButton(), getAmountInvDevelopers(1));
      IDE.INVITE_FORM.clickOnCheckBoxGoogleForm("2");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGoogleForm("2");
      assertEquals(IDE.INVITE_FORM.getTextFomIviteButton(), getAmountInvDevelopers(2));
      IDE.INVITE_FORM.clickOnCheckBoxGoogleForm("3");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGoogleForm("3");
      assertEquals(IDE.INVITE_FORM.getTextFomIviteButton(), getAmountInvDevelopers(3));

      //step 6 click on developers 1-3 and check unchecked states boxes and inv. developers button
      IDE.INVITE_FORM.clickOnCheckBoxGoogleForm("1");
      IDE.INVITE_FORM.waitChekBoxUnchekedGoogleForm("1");
      assertEquals(IDE.INVITE_FORM.getTextFomIviteButton(), getAmountInvDevelopers(2));
      IDE.INVITE_FORM.clickOnCheckBoxGoogleForm("2");
      IDE.INVITE_FORM.waitChekBoxUnchekedGoogleForm("1");
      assertEquals(IDE.INVITE_FORM.getTextFomIviteButton(), getAmountInvDevelopers(1));
      IDE.INVITE_FORM.clickOnCheckBoxGoogleForm("3");
      IDE.INVITE_FORM.waitChekBoxUnchekedGoogleForm("3");
      assertTrue(IDE.INVITE_FORM.getTextFomIviteButton().contains("Invite developers"));

   }

   public String getAmountInvDevelopers(int numUser)
   {
      String str =
         (numUser > 1) ? (String.format("Invite %s developers", numUser)) : (String.format("Invite %s developer",
            numUser));
      return str;
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.GOOGLE.openGoogleAccountPage();
      IDE.GOOGLE.deleteGoogleToken();
   }
}