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
 * @version $Id: Oct 27, 2010 $
 *
 */
public class RolesWithGadgetTest extends BaseTest
{
   private final String DEVELOPER = "john";

   private final String USER = "demo";

   private final static String FILE1 = "Gadget";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         closeTab("0");
         VirtualFileSystemUtils.delete(URL + FILE1);
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
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GOOGLE_GADGET_FILE);
      saveAsUsingToolbarButton(FILE1);

      Thread.sleep(TestConstants.SLEEP);
      //Check deploy/undeploy is available for administrator
      checkDeployUndeployAllowed(true);

      closeTab("0");

      //Logout and login as developer
      logout();

      standaloneLogin(DEVELOPER);
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      openFileFromNavigationTreeWithCodeEditor(FILE1, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //Check deploy/undeploy is not available for developer
      checkDeployUndeployAllowed(false);

      closeTab("0");

      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Tests allowed commands for work with gadget if user has "users" role.
    * 
    * @throws Exception
    */
   @Test
   public void testUserRoleWithGadget() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      logout();

      standaloneLogin(USER);
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      //Double click on item :
      //TODO not works in Windows
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + FILE1 + "]/col[1]");
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + FILE1 + "]/col[1]");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //Check Run menu is not available for user
      assertFalse(selenium.isElementPresent("//td[@class='exo-menuBarItem' and @menubartitle='" + MenuCommands.Run.RUN
         + "']"));

      closeTab("0");
   }

   private void checkDeployUndeployAllowed(boolean allowed) throws Exception
   {
      checkMenuCommandPresent(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, allowed);
      checkMenuCommandPresent(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, allowed);

      if (allowed)
      {
         checkToolbarButtonState(ToolbarCommands.Run.DEPLOY_GADGET, allowed);
         checkToolbarButtonState(ToolbarCommands.Run.UNDEPLOY_GADGET, allowed);

         //Deploy gadget:
         runToolbarButton(ToolbarCommands.Run.DEPLOY_GADGET);
         Thread.sleep(TestConstants.SLEEP);
         //Check successfully deployed message
         assertTrue(selenium.isElementPresent("scLocator=//VLayout[ID=\"ideOutputForm\"]/"));
         String message = selenium.getText("//div[contains(@eventproxy,'Record_0')]");
         assertTrue(message.contains("[INFO]"));
         assertTrue(message.contains(FILE1 + " deployed successfully."));

         //Undeploy gadget
         runToolbarButton(ToolbarCommands.Run.UNDEPLOY_GADGET);
         Thread.sleep(TestConstants.SLEEP);
         //Check successfully undeployed message
         assertTrue(selenium.isElementPresent("scLocator=//VLayout[ID=\"ideOutputForm\"]/"));
         message = selenium.getText("//div[contains(@eventproxy,'Record_1')]");
         assertTrue(message.contains("[INFO]"));
         assertTrue(message.contains(FILE1 + " undeployed successfully."));
      }
   }
}
