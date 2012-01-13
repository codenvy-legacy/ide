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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.paas.heroku.core.Heroku;
import org.exoplatform.ide.paas.heroku.core.Rake;
import org.exoplatform.ide.paas.heroku.core.Rake.Commands;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RakeConsoleTest extends BaseTest
{
   private static final String TEST_FOLDER = RakeConsoleTest.class.getSimpleName();

   private static final String TEST_APP = "test";

   private static final String APP_NAME = String.valueOf(System.currentTimeMillis());

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.upoadZipFolder("src/test/resources/org/exoplatform/ide/paas/heroku/test.zip", WS_URL
            + TEST_FOLDER + "/");
      }
      catch (Exception e)
      {
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
      }
   }
   
   //@Test
   public void testRakeConsole() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_APP + "/");
      
      IDE.HEROKU.CREATE_APP.createApplication(APP_NAME);
      
      IDE.HEROKU.RAKE.openRakeConsole();
      
      assertTrue(selenium().isElementPresent(Rake.COMMAND_FIELD));
      assertTrue(selenium().isElementPresent(Rake.RUN_BUTTON));
      assertTrue(selenium().isElementPresent(Rake.HELP_BUTTON));
      assertTrue(selenium().isElementPresent(Rake.CLOSE_BUTTON));
      
      assertFalse(IDE.HEROKU.RAKE.isRunButtonEnabled());
      
      IDE.HEROKU.RAKE.pressHelpButton();
      IDE.OUTPUT.waitForMessageShow(3);
      String text = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(text.contains(Commands.HELP));
      
      IDE.HEROKU.RAKE.typeCommand("status");
      IDE.HEROKU.RAKE.runCommand();
      IDE.OUTPUT.waitForMessageShow(4);
      text = IDE.OUTPUT.getOutputMessage(4);
      assertTrue(text.contains(Commands.STATUS));
      
      IDE.HEROKU.RAKE.typeCommand(Commands.DB_CREATE);
      IDE.HEROKU.RAKE.runCommand();
      IDE.OUTPUT.waitForMessageShow(5);
      text = IDE.OUTPUT.getOutputMessage(5);
      assertTrue(text.contains(Commands.DB_CREATE));
      
   }
}
