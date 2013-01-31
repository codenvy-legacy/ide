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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class InviteAllDevelopersFromGithubWithLoginFromOauthTest extends BaseTest
{
   private static final String PROJECT = "testRepo";

   private static final String INVITATION_MESSAGE = "This is a test message for checking";

   private static final String SUBJECT = "You've been invited to use Codenvy";

   @Override
   @Before
   public void start() throws Exception
   {
   }

   @BeforeClass
   public static void before() throws Exception
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
      IDE.LOGIN.githubBtnClick();
      IDE.GITHUB.waitAuthorizationPageOpened();
      IDE.GITHUB.typeLogin(USER_NAME);
      IDE.GITHUB.typePass(USER_PASSWORD);
      IDE.GITHUB.submit();
      IDE.GITHUB.waitAuthorizeBtn();
      IDE.GITHUB.clickOnAuthorizeBtn();
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.GITHUB.openGithub();
      IDE.GITHUB.deleteGithubToken();

      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         IDE.MAIL_CHECK.cleanMailBox(FIRST_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
         IDE.MAIL_CHECK.cleanMailBox(SECOND_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
         IDE.MAIL_CHECK.cleanMailBox(THIRD_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
      }
      catch (TimeoutException e)
      {
         e.printStackTrace();
      }

      catch (Exception e)
      {
      }
   }

   @Test
   public void inviteAllDevelopersFromGithubWithLoginFromOauthAndCHTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.WELCOME_PAGE.waitImportFromGithubBtnOpened();
      IDE.WELCOME_PAGE.clickImportFromGithub();
      IDE.LOADER.waitClosed();
      IDE.IMPORT_FROM_GITHUB.waitLoadFromGithubFormOpened();
      IDE.LOADER.waitClosed();
      IDE.IMPORT_FROM_GITHUB.selectProjectByName(PROJECT);
      IDE.IMPORT_FROM_GITHUB.finishBtnClick();
      IDE.IMPORT_FROM_GITHUB.waitLoadFromGithubFormClosed();
      IDE.IMPORT_FROM_GITHUB.waitCloningProgressFormClosed();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Share.SHARE, MenuCommands.Share.INVITE_GITHUB_COLLABORATORS);
      IDE.INVITE_FORM.waitInviteDevelopersOpened();

      // there is only checking that collaborators is appeared in invite form.
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(FIRST_GITHUB_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(SECOND_GITHUB_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(THIRD_GITHUB_USER_FOR_INVITE);

      IDE.INVITE_FORM.clickInviteAll();
      IDE.INVITE_FORM.waitCheckboxIsCheckedGithubForm("1");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGithubForm("2");
      IDE.INVITE_FORM.waitCheckboxIsCheckedGithubForm("3");

      IDE.INVITE_FORM.typeInvitationMeassge(INVITATION_MESSAGE);

      IDE.INVITE_FORM.inviteClick();

      IDE.INVITE_FORM.waitPopUp();
      IDE.INVITE_FORM.clickOkOnPopUp();
      IDE.LOGIN.logout();
      // checking email and proceed to invite url
      IDE.MAIL_CHECK.waitAndGetInviteLink(FIRST_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOGIN.logout();

      // checking email and proceed to invite url
      IDE.MAIL_CHECK.waitAndGetInviteLink(SECOND_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOGIN.logout();

      // checking email and proceed to invite url
      IDE.MAIL_CHECK.waitAndGetInviteLink(THIRD_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOGIN.logout();

   }

   @Test
   public void checkInvitationMessageFromGithubForm() throws IOException, MessagingException, TimeoutException
   {
      IDE.MAIL_CHECK.waitAndGetInviteLink(FIRST_GITHUB_USER_FOR_INVITE, USER_PASSWORD);
      assertTrue(IDE.MAIL_RECEIVER.getFullMessage(SUBJECT).contains(INVITATION_MESSAGE));
   }
}
