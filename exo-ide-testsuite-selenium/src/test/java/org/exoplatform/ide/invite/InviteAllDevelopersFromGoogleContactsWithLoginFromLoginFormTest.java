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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 2:34:35 PM  Dec 20, 2012 $
 *
 */
public class InviteAllDevelopersFromGoogleContactsWithLoginFromLoginFormTest extends BaseTest
{

   @Test
   public void inviteAllDevelopersFromGoogleContactsWithLoginFromLoginFormTest() throws Exception
   {
      String currentWin = driver.getWindowHandle();

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

      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(FIRST_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(SECOND_USER_FOR_INVITE);
      IDE.INVITE_FORM.waitForUsersFromContactsToInvite(THIRD_USER_FOR_INVITE);
      IDE.INVITE_FORM.clickInviteAll();

      assertTrue(IDE.INVITE_FORM.getTextFomIviteButton().contains("3 developers"));
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

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.GOOGLE.openGoogleAccountPage();
      IDE.GOOGLE.deleteGoogleToken();
   }
}
