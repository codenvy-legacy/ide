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

import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 26, 2010 $
 *
 */
public class RolesWithRESTServiceTest extends BaseTest
{
   private final static String FILE1 = "REST Service";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
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
        IDE.EDITOR.closeTab(0);
         VirtualFileSystemUtils.delete(URL+FILE1);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
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
      waitForRootElement();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      saveAsUsingToolbarButton(FILE1);
      IDE.WORKSPACE.waitForItem(WS_URL + FILE1);
      
      //Check controls available for administrators 
      //and developers
      checkDeployUndeployAllowed(true);
      checkRunService(true);
      checkSetAutoload(true);
      checkSandbox(true);
      checkLaunchService(true);
      checkValidateService(true);
      
     IDE.EDITOR.closeTab(0);
      
      //Logout and login as developer
      logout();
      
      standaloneLogin(TestConstants.Users.JOHN);
      waitForRootElement();
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FILE1, false);
      IDE.EDITOR.waitTabPresent(0);
      //Check deploy/undeploy is not available for developer
      checkDeployUndeployAllowed(false);
      // Check run service is allowed for developer
      checkRunService(true);
      //Check set autoload property is not available for developer
      checkSetAutoload(false);
      //Check deploy/undeploy in sandbox is available for developer
      checkSandbox(true);
      // Check validate service is allowed for developer
      checkValidateService(true);
      // Check launch service is allowed for developer
      checkLaunchService(false);
      
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      
      //Check Launch Rest Service form appears
      IDE.REST_SERVICE.waitForLaunchRestServiceViewOpened();
      IDE.OUTPUT.waitForMessageShow(1);
      String message =  IDE.OUTPUT.getOutputMessageText(1);

      assertTrue(message.contains("[INFO]"));
      assertTrue(message.contains(FILE1 + " validated successfully."));
      
      IDE.OUTPUT.waitForMessageShow(2);
      message =  IDE.OUTPUT.getOutputMessageText(2);

      assertTrue(message.contains("[INFO]"));
      assertTrue(message.contains(FILE1 + " deployed successfully."));
      
      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitForLaunchRestServiceViewClosed();
      
     IDE.EDITOR.closeTab(0);
   }

   /**
    * Tests allowed commands for work with REST services if user has "administrators" role.
    * 
    * @throws Exception
    */
   @Test
   public void testAdminRoleWithRESTService() throws Exception
   {
      selenium.refresh();
      waitForRootElement();
      
      logout();
      
      standaloneLogin(TestConstants.Users.ADMIN);
      waitForRootElement();
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FILE1, false);
      IDE.EDITOR.waitTabPresent(0);
      //Check deploy/undeploy is allowed for administrator
      checkDeployUndeployAllowed(true);
      // Check run service is not available for administrator
      checkRunService(false);
      //Check set autoload property is allowed for administrator
      checkSetAutoload(true);
      //Check deploy/undeploy in sandbox is not available for administrator
      checkSandbox(false);
      // Check validate service is allowed for administrator
      checkValidateService(true);
      // Check launch service is allowed for administrator
      checkLaunchService(true);
      
     IDE.EDITOR.closeTab(0);
   }
   
   /**
    * @param allowed deploy/undeploy allowed
    * @throws Exception
    */
   private void checkDeployUndeployAllowed(boolean allowed) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE, allowed);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE, allowed);

      if (allowed)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE, allowed);
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.UNDEPLOY_GROOVY_SERVICE, allowed);

         //Deploy service:
         IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE);
         IDE.OUTPUT.waitForMessageShow(1);
         //Check successfully deployed message
         IDE.OUTPUT.waitForOutputOpened();
         String message = IDE.OUTPUT.getOutputMessageText(1);
         assertTrue(message.contains("[INFO]"));
         assertTrue(message.contains(FILE1 + " deployed successfully."));

         //Undeploy service
         IDE.TOOLBAR.runCommand(ToolbarCommands.Run.UNDEPLOY_GROOVY_SERVICE);
         IDE.OUTPUT.waitForMessageShow(2);
         //Check successfully undeployed message
         message = IDE.OUTPUT.getOutputMessageText(2);
         assertTrue(message.contains("[INFO]"));
         assertTrue(message.contains(FILE1 + " undeployed successfully."));
      }
   }

   /**
    * Check run service control presence 
    * in top menu and toolbar.
    * 
    * @param allowed allowed to run services
    * @throws Exception
    */
   private void checkRunService(boolean allowed) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, allowed);
      if (allowed)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE, allowed);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, allowed);
      }
   }
   
   /**
    * Check validate service control presence 
    * in top menu and toolbar.
    * 
    * @param allowed allowed to validate services
    * @throws Exception
    */
   private void checkValidateService(boolean allowed) throws Exception
   {
	   IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE, allowed);
	   IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE, allowed);
	   if (allowed)
	   {
	      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE, allowed);
	      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.VALIDATE, allowed);
	   }
   }
   
   /**
    * Check set autoload property control presence 
    * in top menu and toolbar.
    * 
    * @param allowed allowed to set autoload property
    * @throws Exception
    */
   private void checkSetAutoload(boolean allowed) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD, allowed);
	   if (allowed)
	   {
	      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.SET_AUTOLOAD, allowed);
	      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD, allowed);
	   }
   }
   
   /**
    * Check set deploy/undeploy service to sandbox control presence 
    * in top menu and toolbar.
    * 
    * @param allowed allowed to set autoload property
    * @throws Exception
    */
   private void checkSandbox(boolean allowed) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_SANDBOX, allowed);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_SANDBOX, allowed);
      if (allowed)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_SANDBOX, allowed);
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.UNDEPLOY_SANDBOX, allowed);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_SANDBOX, allowed);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_SANDBOX, allowed);
      }
   }
   
   /**
    * Check set launch service control presence 
    * in top menu and toolbar.
    * 
    * @param allowed allowed to set autoload property
    * @throws Exception
    */
   private void checkLaunchService(boolean allowed) throws Exception
   {
      IDE.TOOLBAR.checkButtonExistAtRight(MenuCommands.Run.LAUNCH_REST_SERVICE, allowed);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE, allowed);
      if (allowed)
      {
         IDE.TOOLBAR.assertButtonEnabled(MenuCommands.Run.LAUNCH_REST_SERVICE, allowed);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE, allowed);
      }
   }
}
