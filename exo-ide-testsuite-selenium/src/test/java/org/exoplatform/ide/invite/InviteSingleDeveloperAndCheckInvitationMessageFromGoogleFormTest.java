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

import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class InviteSingleDeveloperAndCheckInvitationMessageFromGoogleFormTest extends BaseTest
{
   final static String INVITATION_MESSAGE = "This is a test message for checking";

   final static String SUBJECT = "You've been invited to use Codenvy";

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.GOOGLE.openGoogleAccountPage();
      IDE.GOOGLE.deleteGoogleToken();
      try
      {
         IDE.MAIL_CHECK.cleanMailBox(SINGLE_USER_FOR_INVITE, USER_PASSWORD);
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
   public void inviteSingleDeveloperAndCheckInvitationMessageFromGoogleFormTest() throws Exception
   {
      String currentWin = driver.getWindowHandle();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.MENU.runCommand(MenuCommands.Share.SHARE, MenuCommands.Share.INVITE_DEVELOPERS);
      IDE.INVITE_FORM.waitForConnectToGoogleForm();
      IDE.INVITE_FORM.clickOnOkButtonOnConnectToGoogleForm();

      switchToGithubLoginWindow(currentWin);

      IDE.GOOGLE.waitOauthPageOpened();
      IDE.GOOGLE.typeLogin(USER_NAME);
      IDE.GOOGLE.typePassword(USER_PASSWORD);
      IDE.GOOGLE.clickSignIn();
      IDE.GOOGLE.waitAllowApplicationButton();
      IDE.GOOGLE.clickOnAllowButton();

      driver.switchTo().window(currentWin);

      IDE.PROJECT.EXPLORER.waitOpened();

      IDE.MENU.runCommand(MenuCommands.Share.SHARE, MenuCommands.Share.INVITE_DEVELOPERS);
      IDE.INVITE_FORM.waitInviteDevelopersOpened();
      IDE.INVITE_FORM.typeEmailForInvite(SINGLE_USER_FOR_INVITE);
      IDE.INVITE_FORM.typeInvitationMeassge(INVITATION_MESSAGE);
      IDE.INVITE_FORM.inviteClick();
      IDE.INVITE_FORM.waitPopUp();
      IDE.INVITE_FORM.clickOkOnPopUp();
      IDE.LOGIN.logout();

      IDE.MAIL_CHECK.waitAndGetInviteLink(SINGLE_USER_FOR_INVITE, USER_PASSWORD);
      IDE.MAIL_CHECK.gotoConfirmInvitePage();
      IDE.PROJECT.EXPLORER.waitOpened();

      // cheking for invitation message
      assertTrue(IDE.MAIL_RECEIVER.getFullMessage(SUBJECT).contains(INVITATION_MESSAGE));
   }

   /**
    * @param currentWin
    */
   private void switchToGithubLoginWindow(String currentWin)
   {
      for (String handle : driver.getWindowHandles())
      {
         if (currentWin.equals(handle))
         {

         }
         else
         {
            driver.switchTo().window(handle);
            break;
         }
      }
   }
}
