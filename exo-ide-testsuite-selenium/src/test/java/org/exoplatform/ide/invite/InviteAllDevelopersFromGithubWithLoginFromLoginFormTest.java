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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 2:34:35 PM  Dec 20, 2012 $
 *
 */
public class InviteAllDevelopersFromGithubWithLoginFromLoginFormTest extends BaseTest
{

   private static final String PROJECT = "testRepo";

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.GITHUB.openGithub();
      IDE.GITHUB.deleteGithubToken();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   @Test
   public void inviteAllDevelopersFromGithubWithLoginFromLoginFormTest() throws Exception
   {
      // current window handle
      String currentWin = driver.getWindowHandle();

      IDE.WELCOME_PAGE.waitImportFromGithubBtnOpened();
      IDE.WELCOME_PAGE.clickImportFromGithub();
      IDE.LOADER.waitClosed();
      IDE.IMPORT_FROM_GITHUB.waitLoginFormToGithub();
      IDE.IMPORT_FROM_GITHUB.clickOkOnLoginForm();

      switchToGithubLoginWindow(currentWin);

      IDE.GITHUB.waitAuthorizationPageOpened();
      IDE.GITHUB.typeLogin(USER_NAME);
      IDE.GITHUB.typePass(USER_PASSWORD);
      IDE.GITHUB.submit();
      IDE.GITHUB.waitAuthorizeBtn();
      IDE.GITHUB.clickOnAuthorizeBtn();

      driver.switchTo().window(currentWin);

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
