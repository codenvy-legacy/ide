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

import org.everrest.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceDeployWrongTest extends BaseTest
{

   private static String FILE_NAME = "DeployWrongTest.grs";

   private final static String TEST_FOLDER = RESTServiceDeployWrongTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

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

   @Test
   public void testDeployUndeploy() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_END);

      IDE.EDITOR.typeTextIntoEditor(0, "1");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.runCommand("Run", MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.OUTPUT.checkOutputOpened();

      String mess = IDE.OUTPUT.getOutputMessageText(1);

      assertTrue(mess.startsWith("[ERROR]"));
      assertTrue(mess.contains(FILE_NAME + " deploy failed. Error (400: Bad Request)"));

   }

   @AfterClass
   public static void tearDown()
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

}
