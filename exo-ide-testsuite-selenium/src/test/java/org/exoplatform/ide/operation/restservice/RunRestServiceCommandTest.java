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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class RunRestServiceCommandTest extends BaseTest
{
   private final static String SIMPLE_FILE_NAME = "RestServiceExample.grs";

   private final static String NON_VALID_FILE_NAME = "RestServiceValidationWrongExample.grs";

   private final static String FILE_FOR_CHANGE_CONTENT_NAME = "RestServiceChangeContent.grs";

   private final static String NEW_FILE_NAME = "NewRestService.grs";

   private final static String PROJECT = RunRestServiceCommandTest.class.getSimpleName();

   @Before
   public void beforeTest()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, SIMPLE_FILE_NAME, MimeType.GROOVY_SERVICE, filePath
            + "RestServiceExample.grs");
         VirtualFileSystemUtils.createFileFromLocal(link, NON_VALID_FILE_NAME, MimeType.GROOVY_SERVICE, filePath
            + "RestServiceValidationWrongExample.groovy");
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_FOR_CHANGE_CONTENT_NAME, MimeType.GROOVY_SERVICE,
            filePath + "RestServiceChangeContent.groovy");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void afterMethod()
   {
      try
      {
         /*  Utils.undeployService(BASE_URL, REST_CONTEXT, URL + NEW_FILE_NAME);
           Utils.undeployService(BASE_URL, REST_CONTEXT, URL + NON_VALID_FILE_NAME);
           Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FILE_FOR_CHANGE_CONTENT_NAME);
           Utils.undeployService(BASE_URL, REST_CONTEXT, URL + SIMPLE_FILE_NAME);*/
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testSavedFileRunRestService() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SIMPLE_FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + SIMPLE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SIMPLE_FILE_NAME);

      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE));

      IDE.REST_SERVICE.runRESTServiceInSanbox();

      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitClosed();

      assertEquals("[INFO] " + SIMPLE_FILE_NAME + " validated successfully.", IDE.OUTPUT.getOutputMessage(1));
      assertEquals("[INFO] " + "/" + PROJECT + "/" + SIMPLE_FILE_NAME + " deployed successfully.",
         IDE.OUTPUT.getOutputMessage(2));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);

      assertFalse(IDE.REST_SERVICE.isFormOpened());

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(3, 5);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.UNDEPLOY_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(4, 5);

      assertFalse(IDE.REST_SERVICE.isFormOpened());
   }

   @Test
   public void testRunGroovyServiceWithNonValidFile() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NON_VALID_FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + NON_VALID_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + NON_VALID_FILE_NAME);

      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      IDE.OUTPUT.waitOpened();
      IDE.OUTPUT.waitForMessageShow(1, 5);

      String msg = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(msg.contains("[ERROR] " + NON_VALID_FILE_NAME + " validation failed."));

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "d");
      IDE.EDITOR.waitFileContentModificationMark(NON_VALID_FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      IDE.OUTPUT.waitForMessageShow(2, 5);

      assertEquals("[INFO] " + NON_VALID_FILE_NAME + " validated successfully.", IDE.OUTPUT.getOutputMessage(2));
      assertFalse(IDE.REST_SERVICE.isFormOpened());
   }

   @Test
   public void testRunGroovyServiceWithChangedFile() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_FOR_CHANGE_CONTENT_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_FOR_CHANGE_CONTENT_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_FOR_CHANGE_CONTENT_NAME);

      //---- 2 -----------------
      //type some text
      IDE.EDITOR.typeTextIntoEditor(0, "//modified file\n");

      //---- 3 -----------------
      //check Run Groovy Service button and menu
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE));

      //---- 4 -----------------
      IDE.REST_SERVICE.runRESTServiceInSanbox();

      //---- 5 -----------------
      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitClosed();

      //---- 6 -----------------
      //check messages
      assertEquals("[INFO] " + FILE_FOR_CHANGE_CONTENT_NAME + " validated successfully.",
         IDE.OUTPUT.getOutputMessage(1));
      assertEquals("[INFO] " + "/" + PROJECT + "/" + FILE_FOR_CHANGE_CONTENT_NAME + " deployed successfully.",
         IDE.OUTPUT.getOutputMessage(2));
   }

   @Test
   public void testRunGroovyServiceWithNewFile() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");

      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE));

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);

      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.setValue(NEW_FILE_NAME);
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NEW_FILE_NAME);

      IDE.REST_SERVICE.waitOpened();

      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitClosed();

      assertEquals("[INFO] " + NEW_FILE_NAME + " validated successfully.", IDE.OUTPUT.getOutputMessage(1));
      assertTrue(IDE.OUTPUT.getOutputMessage(2).contains(NEW_FILE_NAME + " deployed successfully."));
   }
}
