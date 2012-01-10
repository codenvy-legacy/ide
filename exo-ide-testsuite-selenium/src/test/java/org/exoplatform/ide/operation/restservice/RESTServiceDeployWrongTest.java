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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceDeployWrongTest extends BaseTest
{

   private static String FILE_NAME = "DeployWrongTest.grs";

   private final static String PROJECT = RESTServiceDeployWrongTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
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
   public void testDeployWrong() throws Exception
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

      IDE.EDITOR.moveCursorDown(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "1");
      IDE.EDITOR.waitFileContentModificationMark(FILE_NAME);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 5);

      String mess = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(mess.startsWith("[ERROR]"));
      assertTrue(mess.contains("/" + PROJECT + "/" + FILE_NAME + " deploy failed."));
   }

   @AfterClass
   public static void tearDown()
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
