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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceDeployUndeployTest extends BaseTest
{

   private static final String PROJECT = RESTServiceDeployUndeployTest.class.getSimpleName();

   private static String FILE_NAME = "DeployUndeployTest.grs";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testDeployUndeploy() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");

      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 5);
      String mess = IDE.OUTPUT.getOutputMessage(1);

      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains("/" + PROJECT + "/" + FILE_NAME + " deployed successfully."));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(2, 5);

      assertTrue(IDE.OUTPUT.getOutputMessage(2).contains("/" + PROJECT + "/" + FILE_NAME + " undeployed successfully."));
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);

      IDE.OUTPUT.waitForMessageShow(3, 5);
      mess = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(mess.contains("[ERROR]"));
      assertTrue(mess.contains("/" + PROJECT + "/" + FILE_NAME + " undeploy failed. Error (400: Bad Request)"));

      assertTrue(mess.contains(/*TODO "Can't unbind script " + FOLDER_NAME + "/" + FILE_NAME
                               +*/", not bound or has wrong mapping to the resource class"));
   }

   @After
   public void afterTest() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
