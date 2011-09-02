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
public class RESTServiceRuntimeErrorTest extends BaseTest
{

   private static final String FOLDER_NAME = "RuntimeError";
   
   private static final String FILE_NAME = "RESTServiceRuntimeErrorTest.groovy";

   @Test
   public void testDeployUndeploy() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      
      IDE.NAVIGATION.createFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      for (int i = 0; i < 10; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      }
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_END);

      IDE.EDITOR.typeTextIntoEditor(0, " / 0");

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.REST_SERVICE.launchRestService();
      
      IDE.REST_SERVICE.selectPathValue("/helloworld/{name}");

      IDE.REST_SERVICE.setMethodFieldValue("GET");

      IDE.REST_SERVICE.sendRequst();

      IDE.OUTPUT.waitForMessageShow(2);

      String mess = IDE.OUTPUT.getOutputMessageText(2);
      assertTrue(mess
         .startsWith("[ERROR]"));
      assertTrue(mess.contains("helloworld/{name} 500 Internal Server Error"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      String url = BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" +FOLDER_NAME+"/"+ FILE_NAME;
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, url);
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
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
