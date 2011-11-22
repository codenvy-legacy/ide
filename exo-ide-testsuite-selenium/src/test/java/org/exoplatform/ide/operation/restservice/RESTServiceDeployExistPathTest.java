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
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */

//IDE-133
public class RESTServiceDeployExistPathTest extends BaseTest
{

   /**
    * 
    */
   private static final String FOLDER_NAME = "Test";

   private static final String FIRST_NAME = System.currentTimeMillis() + ".groovy";

   private static final String SECOND_NAME = System.currentTimeMillis() + "copy.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @Test
   public void testDeployExistPath() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      //TODO*****************change**************change add folder for locked file
      IDE.NAVIGATION.createFolder(FOLDER_NAME);
      //*************************************
      IDE.TOOLBAR.runCommandFromNewPopupMenu("REST Service");
      //createFileFromToolbar("REST Service");
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(FIRST_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);

      saveAsUsingToolbarButton(SECOND_NAME);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FOLDER_NAME + "/" + FIRST_NAME, false);

      IDE.EDITOR.selectTab(0);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess.startsWith("[ERROR]"));
      assertTrue(mess.contains(SECOND_NAME + " deploy failed. Error (400: Bad Request)"));

      //***************fix GOTO static string message****************     
      //      assertTrue(mess
      //         .contains("Can't bind script " + SECOND_NAME + ", it is not root resource or root resource with the same URI pattern already registered"));

      IDE.EDITOR.selectTab(1);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.EDITOR.selectTab(0);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      mess = IDE.OUTPUT.getOutputMessage(4);

      assertTrue(mess.contains("[INFO]"));

      assertTrue(mess.contains(SECOND_NAME + " deployed successfully."));
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FOLDER_NAME + "/" + FIRST_NAME);
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FOLDER_NAME + "/" + SECOND_NAME);
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
