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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.paas.heroku.core.CreateApplication;
import org.exoplatform.ide.paas.heroku.core.Heroku;
import org.exoplatform.ide.paas.heroku.core.Heroku.Messages;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CreateApplicationTest extends BaseTest
{
   private static final String TEST_FOLDER = "CreateApplicationTest";

   private static final String APP_NAME = "rwetrdsa324214qefd";

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder(
            "src/test/resources/org/exoplatform/ide/paas/heroku/CreateApplicationTest.zip", WS_URL);
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
         Heroku.logout();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCreateApplication() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      
      IDE.HEROKU.SWITCH_ACCOUNT.loginInHeroku(Messages.LOGIN, Messages.PASSWORD);
      IDE.OUTPUT.waitForMessageShow(1);

      IDE.HEROKU.CREATE_APP.openCreateApplicationForm();
      assertTrue(selenium().isElementPresent(CreateApplication.APP_NAME_FIELD));
      assertTrue(selenium().isElementPresent(CreateApplication.WORK_DIR_FIELD));
      assertTrue(selenium().isElementPresent(CreateApplication.REMOTE_REPO_FIELD));
      assertTrue(selenium().isElementPresent(CreateApplication.CREATE_BUTTON));

      IDE.HEROKU.CREATE_APP.typeAppName(APP_NAME);
      IDE.HEROKU.CREATE_APP.clickCreateApp();

      IDE.OUTPUT.waitForMessageShow(2);

      String message = IDE.OUTPUT.getOutputMessage(2);

      String url = "http://" + APP_NAME + ".heroku.com/";
      assertTrue(message.contains(url));
      
      String git = "git@heroku.com:" + APP_NAME + ".git";
      assertTrue(message.contains(git));
   }

}
