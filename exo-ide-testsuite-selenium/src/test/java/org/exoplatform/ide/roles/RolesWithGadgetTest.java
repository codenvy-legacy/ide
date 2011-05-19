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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 27, 2010 $
 *
 */
public class RolesWithGadgetTest extends BaseTest
{
   private final static String FILE1 = "Gadget";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = RolesWithGadgetTest.class.getSimpleName();
   
   /**
    * Create test folder.
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
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
    * Clear test results.
    */
   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
    	  VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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
    * Tests allowed commands for work with gadget if user has "developers" role.
    * 
    * @throws Exception
    */
   @Test
   public void testDeveloperRoleWithGadget() throws Exception
   {
      selenium.refresh();
      waitForRootElement();
      
      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);   
      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      saveAsUsingToolbarButton(FILE1);
      
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE1);
      //Check deploy/undeploy is available for administrator
      checkDeployUndeployAllowed(true);

     IDE.EDITOR.closeFile(0);
      
      //Logout and login as developer
      logout();

      standaloneLogin(TestConstants.Users.JOHN);
      waitForRootElement();
      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);     
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE1);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE1, false);
      IDE.EDITOR.waitTabPresent(0);
      //Check deploy/undeploy is not available for developer
      checkDeployUndeployAllowed(false);

     IDE.EDITOR.closeFile(0);
   }

   /**
    * Tests allowed commands for work with gadget if user has "administrators" role.
    * 
    * @throws Exception
    */
   @Test
   public void testAdminRoleWithGadget() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      logout();

      standaloneLogin(TestConstants.Users.ADMIN);
      waitForRootElement();
      // open folder
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);   
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE1);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE1, false);
      IDE.EDITOR.waitTabPresent(0);
      //Check deploy/undeploy is available for administrator
      checkDeployUndeployAllowed(true);
      
     IDE.EDITOR.closeFile(0);
   }
   
   /**
    * Checks the controls for deploy/undeploy gadget presence 
    * in top menu and toolbar.
    * 
    * @param allowed is deploy/undeploy allowed
    * @throws Exception
    */
   private void checkDeployUndeployAllowed(boolean allowed) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, allowed);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, allowed);

      if (allowed)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_GADGET, allowed);
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.UNDEPLOY_GADGET, allowed);
         //Check deploy/undeploy gadget functionality only when in portal:
         if (IdeAddress.PORTAL.getApplicationUrl().equals(APPLICATION_URL))
         {
            //Deploy gadget:
            IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_GADGET);
            //Check successfully deployed message
            IDE.OUTPUT.waitForOutputOpened();
            IDE.OUTPUT.waitForMessageShow(1);
            String message = IDE.OUTPUT.getOutputMessageText(1);
            assertTrue(message.contains("[INFO]"));
            assertTrue(message.contains(FILE1 + " deployed successfully."));

            //Undeploy gadget
            IDE.TOOLBAR.runCommand(ToolbarCommands.Run.UNDEPLOY_GADGET);
            //Check successfully undeployed message
            IDE.OUTPUT.waitForMessageShow(2);
            message = IDE.OUTPUT.getOutputMessageText(2);
            assertTrue(message.contains("[INFO]"));
            assertTrue(message.contains(FILE1 + " undeployed successfully."));
         }
      }
   }
}
