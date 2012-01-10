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
import org.openqa.selenium.Keys;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceRuntimeErrorTest extends BaseTest
{

   private static final String PROJECT = "RuntimeError";

   private static final String FILE_NAME = "RESTServiceRuntimeErrorTest.grs";

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
   public void testRunTimeError() throws Exception
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

      IDE.EDITOR.moveCursorDown(0, 10);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor(0, " / 0");
      IDE.EDITOR.waitFileContentModificationMark(FILE_NAME);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(FILE_NAME);

      IDE.REST_SERVICE.deploy(PROJECT + "/" + FILE_NAME, 1);

      IDE.REST_SERVICE.launchRestService();
      IDE.REST_SERVICE.selectPath("/helloworld/{name}");
      IDE.REST_SERVICE.setMethodFieldValue("GET");
      IDE.REST_SERVICE.sendRequest();

      IDE.OUTPUT.waitForMessageShow(2, 5);

      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess.startsWith("[ERROR]"));
      assertTrue(mess.contains("500 Internal Server Error"));
   }

   @After
   public void afterTest() throws Exception
   {
      try
      {
         IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
