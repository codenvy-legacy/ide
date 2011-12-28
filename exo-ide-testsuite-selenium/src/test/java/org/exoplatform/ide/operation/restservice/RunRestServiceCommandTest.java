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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

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

   private final static String NEW_FILE_NAME = "NewRestService";

   private final static String FOLDER_NAME = RunRestServiceCommandTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp()
   {

      final String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath + "RestServiceExample.groovy", MimeType.GROOVY_SERVICE, URL
            + SIMPLE_FILE_NAME);
         VirtualFileSystemUtils.put(filePath + "RestServiceValidationWrongExample.groovy", MimeType.GROOVY_SERVICE, URL
            + NON_VALID_FILE_NAME);
         VirtualFileSystemUtils.put(filePath + "RestServiceChangeContent.groovy", MimeType.GROOVY_SERVICE, URL
            + FILE_FOR_CHANGE_CONTENT_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + NEW_FILE_NAME);
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + NON_VALID_FILE_NAME);
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FILE_FOR_CHANGE_CONTENT_NAME);
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + SIMPLE_FILE_NAME);
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testSavedFileRunRestService() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      
      //---- 1 -----------------
      //open file
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + SIMPLE_FILE_NAME);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + SIMPLE_FILE_NAME, false);

      //---- 2 -----------------
      //check Run Groovy Service button and menu
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 3 -----------------
      //call Run Groovy Service command
      IDE.REST_SERVICE.runRESTServiceInSanbox();

      //---- 4 -----------------
      //close
      IDE.REST_SERVICE.closeForm();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 5 -----------------
      //check messages
      assertEquals("[INFO] " + SIMPLE_FILE_NAME + " validated successfully.", IDE.OUTPUT.getOutputMessage(1));
      assertEquals("[INFO] " + BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/"
         + FOLDER_NAME + "/" + SIMPLE_FILE_NAME + " deployed successfully.", IDE.OUTPUT.getOutputMessage(2));

      //---- 6 -----------------
      //check, that hanlders removed, and after validation and deploying 
      //Launt Rest Service form doesn't appear

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //check Launch Rest Service form doesn't appear
      IDE.REST_SERVICE.isFormNotOpened();

      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //check Launch Rest Service form doesn't appear
      IDE.REST_SERVICE.isFormNotOpened();

   }

   @Test
   public void testRunGroovyServiceWithNonValidFile() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      IDE.WORKSPACE.waitForRootItem();
      //---- 1 -----------------
      //open file
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + NON_VALID_FILE_NAME);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + NON_VALID_FILE_NAME, false);

      //---- 2 -----------------
      //check Run Groovy Service button and menu
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 3 -----------------
      //call Run Groovy Service command
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      IDE.OUTPUT.waitOpened();
      IDE.OUTPUT.waitForMessageShow(1);
      //---- 4 -----------------
      //check that validation fails message appears.
      String msg = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(msg.contains("[ERROR] " + NON_VALID_FILE_NAME + " validation failed. Error (400: Bad Request)"));

      //---- 5 -----------------
      //fix file
      IDE.EDITOR.clickOnEditor(0);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + "d");
      Thread.sleep(TestConstants.SLEEP);

      //---- 6 -----------------
      //validate file and check, that Launch Rest Service form doesn't appear
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 7 -----------------
      //check messages
      assertEquals("[INFO] " + NON_VALID_FILE_NAME + " validated successfully.", IDE.OUTPUT.getOutputMessage(2));

      //check Launch Rest Service form doesn't appear
      IDE.REST_SERVICE.isFormNotOpened();

   }

   @Test
   public void testRunGroovyServiceWithChangedFile() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      IDE.WORKSPACE.waitForRootItem();
      //---- 1 -----------------
      //open file
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + FILE_FOR_CHANGE_CONTENT_NAME);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_FOR_CHANGE_CONTENT_NAME, false);

      //---- 2 -----------------
      //type some text
      IDE.EDITOR.typeTextIntoEditor(0, "//modified file\n");

      //---- 3 -----------------
      //check Run Groovy Service button and menu
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 4 -----------------
      //call Run Groovy Service command
      IDE.REST_SERVICE.runRESTServiceInSanbox();

      //---- 5 -----------------
      //close
      IDE.REST_SERVICE.closeForm();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 6 -----------------
      //check messages
      assertEquals("[INFO] " + FILE_FOR_CHANGE_CONTENT_NAME + " validated successfully.",
         IDE.OUTPUT.getOutputMessage(1));
      assertEquals("[INFO] " + BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/"
         + FOLDER_NAME + "/" + FILE_FOR_CHANGE_CONTENT_NAME + " deployed successfully.",
         IDE.OUTPUT.getOutputMessage(2));

   }

   @Test
   public void testRunGroovyServiceWithNewFile() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      IDE.WORKSPACE.waitForRootItem();
      //---- 1 -----------------
      //open file
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.selectItem(URL);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);

      //---- 2 -----------------
      //check Run Groovy Service button and menu
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.RUN_GROOVY_SERVICE));
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 3 -----------------
      //call Run Groovy Service command
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 5 -----------------
      //save as dialog appears
      SaveFileUtils.checkSaveAsDialogAndSave(NEW_FILE_NAME, true);
      Thread.sleep(TestConstants.SLEEP);

      //check Launch Rest Service form appears
      IDE.REST_SERVICE.isFormNotOpened();

      //---- 6 -----------------
      //close
      IDE.REST_SERVICE.closeForm();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 7 -----------------
      //check messages
      assertEquals("[INFO] " + NEW_FILE_NAME + " validated successfully.", IDE.OUTPUT.getOutputMessage(1));
      assertEquals("[INFO] " + BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/"
         + FOLDER_NAME + "/" + NEW_FILE_NAME + " deployed successfully.", IDE.OUTPUT.getOutputMessage(2));

   }

   @After
   public void afterMethod() throws Exception
   {
    //TODO this block should be remove after fix problem in issue IDE-804. File does not should be modified  
      if (IDE.EDITOR.isFileContentChanged(0)){
       
       IDE.EDITOR.closeTabIgnoringChanges(0);
      }
      else
        IDE.EDITOR.closeFile(0);    
   }

}
