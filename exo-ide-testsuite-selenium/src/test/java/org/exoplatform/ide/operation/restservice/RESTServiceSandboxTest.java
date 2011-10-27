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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class RESTServiceSandboxTest extends BaseTest
{

   private static String FILE_NAME = RESTServiceSandboxTest.class.getSimpleName() + ".grs";

   private static String TEST_FOLDER = RESTServiceSandboxTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

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
   }

   /**
    * Test deploy REST service to sandbox 
    * and undeploy from sandbox.
    */
   @Test
   public void testDeployUndeploy() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/");
      //Create REST Service file and save it:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      //Deploy service to sandbox:
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_SANDBOX);
      IDE.OUTPUT.waitForOutputOpened();

      //Check deploy request:
      String mess = IDE.OUTPUT.getOutputMessageText(1);
      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " deployed successfully."));

      //Undeploy service from sandbox:
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_SANDBOX);
      IDE.OUTPUT.waitForMessageShow(2);

      //Check undeploy request:
      mess = IDE.OUTPUT.getOutputMessageText(2);
      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " undeployed successfully."));

      //Try undeploy undeployed service:
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_SANDBOX);
      IDE.OUTPUT.waitForMessageShow(3);

      mess = IDE.OUTPUT.getOutputMessageText(3);
      assertTrue(mess.contains("[ERROR]"));
      assertTrue(mess.contains(FILE_NAME + " undeploy failed. Error (400: Bad Request)"));

      IDE.EDITOR.closeFile(0);
   }

   /**
    * Clear test results.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + TEST_FOLDER + "/" + FILE_NAME);
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
