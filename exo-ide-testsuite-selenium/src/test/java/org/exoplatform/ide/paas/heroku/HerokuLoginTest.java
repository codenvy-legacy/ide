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
package org.exoplatform.ide.paas.heroku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.paas.heroku.core.Heroku.Messages;
import org.exoplatform.ide.paas.heroku.core.SwitchAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class HerokuLoginTest extends BaseTest
{

   private static final String TEST_FOLDER = HerokuLoginTest.class.getSimpleName();
   
   private static final String LOGIN = "test@test.com";
   
   private static final String PASSWORD = "test";
   
   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testLogin() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      
      IDE.HEROKU.SWITCH_ACCOUNT.openLoginForm();
      assertTrue(selenium.isElementPresent(SwitchAccount.PASSWORD_FIELD));
      assertTrue(selenium.isElementPresent(SwitchAccount.EMAIL_FIELD));
      assertTrue(selenium.isElementPresent(SwitchAccount.LOGIN_BUTTON));
      assertTrue(selenium.isElementPresent(SwitchAccount.CANCEL_BUTTON));
      assertTrue(selenium.isElementPresent(SwitchAccount.TRY_AS_DEMO_BUTTON));
      assertFalse(IDE.HEROKU.SWITCH_ACCOUNT.isLoginButtonEnabled());
      IDE.HEROKU.SWITCH_ACCOUNT.typeLogin(LOGIN);
      IDE.HEROKU.SWITCH_ACCOUNT.typePassword(PASSWORD);
      
      assertTrue(IDE.HEROKU.SWITCH_ACCOUNT.isLoginButtonEnabled());
      IDE.HEROKU.SWITCH_ACCOUNT.clickLoginButton();
      
      assertFalse(IDE.WARNING_DIALOG.isDialogOpened());
      IDE.OUTPUT.waitForMessageShow(1);
      
      assertEquals(Messages.LOGED_IN, IDE.OUTPUT.getOutputMessageText(1));
   }
   
   @Test
   public void testLoginWithInvalidPassword() throws Exception
   {
      refresh();
      
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      
      IDE.HEROKU.SWITCH_ACCOUNT.openLoginForm();
      
      IDE.HEROKU.SWITCH_ACCOUNT.typeLogin(LOGIN);
      IDE.HEROKU.SWITCH_ACCOUNT.typePassword(PASSWORD + System.currentTimeMillis());
      IDE.HEROKU.SWITCH_ACCOUNT.clickLoginButton();
      
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      assertTrue(IDE.WARNING_DIALOG.isDialogOpened());
   }
   
}
