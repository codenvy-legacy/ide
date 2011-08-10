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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceSaveAutoloadPropertyTest extends BaseTest
{

   /**
    * 
    */
   private static final String FOLDER_NAME = "Autoload";

   private static final String FILE_NAME = System.currentTimeMillis() + ".groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/" + FILE_NAME;

   
   /**
    * Test added to Ignore, because at the moment the autoload function dosen't work in IDE.
    * @throws Exception
    */
   
   @Ignore
   @Test
   public void testAutoload() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.NAVIGATION.createFolder(FOLDER_NAME);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      saveAsUsingToolbarButton(FILE_NAME);
      IDE.WORKSPACE.waitForItem(URL);

      IDE.EDITOR.closeFile(0);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL, false);

      IDE.MENU.runCommand("Run", MenuCommands.Run.UNDEPLOY_REST_SERVICE);

      String mess = IDE.OUTPUT.getOutputMessageText(1);

      assertTrue(mess.contains("[ERROR]"));
      assertTrue(mess.contains(FILE_NAME + " undeploy failed. Error (400: Bad Request)"));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD);
      Thread.sleep(TestConstants.SLEEP);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.UNSET_AUTOLOAD, true);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);

      assertEquals("true", IDE.PROPERTIES.getAutoloadProperty());

//      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "/// test comment 1\n");
      IDE.EDITOR.typeTextIntoEditor(0, "/// test comment 1\n");

      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);

      IDE.EDITOR.closeFile(0);

      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.waitForItem(URL);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL, false);
      IDE.EDITOR.checkCodeEditorOpened(0);
      
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);

      mess = IDE.OUTPUT.getOutputMessageText(2);

      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " undeployed successfully."));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL);
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
