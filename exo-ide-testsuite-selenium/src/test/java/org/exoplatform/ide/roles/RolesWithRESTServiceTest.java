/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.roles;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 26, 2010 $
 *
 */
public class RolesWithRESTServiceTest extends BaseTest
{
   private static final String PROJECT = RolesWithRESTServiceTest.class.getSimpleName();

   private final static String FILE_NAME = "REST Service.grs";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Clear test results.
    * 
    * @throws Exception
    */
   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   /**
    * Tests allowed commands for work with REST services if user has "developers" role.
    * 
    * @throws Exception
    */
   @Test
   public void testDeveloperRoleWithRESTService() throws Exception
   {
      //fix for run tests where new session start after 7 testcases passes   
      //you are logged as root.
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(1);

      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.SAVE_AS, true);
      IDE.EDITOR.saveAs(1, FILE_NAME);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE, true);

      //Check controls available for administrators 
      //and developers
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE));

      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.UNDEPLOY_GROOVY_SERVICE));

      //Deploy service:
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1);
      //Check successfully deployed message
      IDE.OUTPUT.waitOpened();
      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(message.contains("[INFO]"));
      assertTrue(message.contains(FILE_NAME + " deployed successfully."));

      //Undeploy service
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.UNDEPLOY_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(2);
      //Check successfully undeployed message
      message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.contains("[INFO]"));
      assertTrue(message.contains(FILE_NAME + " undeployed successfully."));

      //Check Run service:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE));

      //Check Set Autoload
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.SET_AUTOLOAD));

      //Check Sandbox
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_SANDBOX));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_SANDBOX));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.DEPLOY_SANDBOX));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.UNDEPLOY_SANDBOX));

      //Check Launch Service
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.LAUNCH_REST_SERVICE));

      //Check Validate Service
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE));

      IDE.EDITOR.closeFile(FILE_NAME);

      //Logout and login as developer
      IDE.LOGIN.logout();
      IDE.LOGIN.waitStandaloneLogin();
      IDE.LOGIN.standaloneLogin(TestConstants.Users.DEV, TestConstants.Users.DEV_PASS);

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.waitTabPresent(1);

      //Check deploy/undeploy is not available for developer
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE));
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE));
      // Check run service is allowed for developer
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE));

      //Check set autoload property is not available for developer
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD));
      //Check deploy/undeploy in sandbox is available for developer
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_SANDBOX));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_SANDBOX));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.DEPLOY_SANDBOX));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.UNDEPLOY_SANDBOX));
      //Check validate service is allowed for developer
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE));
      //Check launch service is not allowed for developer
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE));
      assertFalse(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.LAUNCH_REST_SERVICE));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);

      //Check Launch Rest Service form appears
      IDE.REST_SERVICE.waitOpened();
      IDE.OUTPUT.waitForMessageShow(1);
      message = IDE.OUTPUT.getOutputMessage(1);

      assertTrue(message.contains("[INFO]"));
      assertTrue(message.contains(FILE_NAME + " validated successfully."));

      IDE.OUTPUT.waitForMessageShow(2);
      message = IDE.OUTPUT.getOutputMessage(2);

      assertTrue(message.contains("[INFO]"));
      assertTrue(message.contains(FILE_NAME + " deployed successfully."));

      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitClosed();

      IDE.EDITOR.closeFile(FILE_NAME);
   }
}
