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
public class RESTServiceDeployUndeployTest extends BaseTest
{

   private static final String FOLDER_NAME = System.currentTimeMillis() + "RESTService";

   private static String FILE_NAME = "DeployUndeployTest.groovy";

   private final static String FILE_URL = WS_URL + FOLDER_NAME + "/" + FILE_NAME;

   @Test
   public void testDeployUndeploy() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      //TODO*******************change add folder for locked file
      IDE.NAVIGATION.createFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //*****************************
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.EDITOR.closeFile(0);

      System.out.println(FILE_URL);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_URL, false);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitOpened();
      
      assertTrue(IDE.OUTPUT.isOpened());

      String mess = IDE.OUTPUT.getOutputMessage(1);

      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " deployed successfully."));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //**********fix TODO static string message 
      assertTrue(IDE.OUTPUT.getOutputMessage(2).contains(FOLDER_NAME + "/" + FILE_NAME + " undeployed successfully."));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      mess = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(mess.contains("[ERROR]"));
      assertTrue(mess.contains(FILE_NAME + " undeploy failed. Error (400: Bad Request)"));

      //**********fix TODO static string message
      assertTrue(mess.contains("Can't unbind script " + FOLDER_NAME +"/" + FILE_NAME
         + ", not bound or has wrong mapping to the resource class"));
      //****************************************
      IDE.EDITOR.closeFile(0);

      IDE.NAVIGATION.assertItemVisible(FILE_URL);

   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, FILE_URL);
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
