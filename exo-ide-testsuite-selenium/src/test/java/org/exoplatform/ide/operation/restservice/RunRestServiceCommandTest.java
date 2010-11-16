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

import static org.junit.Assert.*;

import org.exoplatform.common.http.client.ModuleException;
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

import java.io.IOException;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class RunRestServiceCommandTest extends BaseTest
{
   private final static String SIMPLE_FILE_NAME = "RestServiceExample.groovy";

   private final static String NON_VALID_FILE_NAME = "RestServiceValidationWrongExample.groovy";

   private final static String FILE_FOR_CHANGE_CONTENT_NAME = "RestServiceChangeContent.groovy";

   private final static String NEW_FILE_NAME = "NewRestService";
   
   private final static String FOLDER_NAME = "RunRestServiceCommandTest";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath + SIMPLE_FILE_NAME, MimeType.GROOVY_SERVICE, URL + SIMPLE_FILE_NAME);
         VirtualFileSystemUtils.put(filePath + NON_VALID_FILE_NAME, MimeType.GROOVY_SERVICE, URL + NON_VALID_FILE_NAME);
         VirtualFileSystemUtils.put(filePath + FILE_FOR_CHANGE_CONTENT_NAME, MimeType.GROOVY_SERVICE, URL
            + FILE_FOR_CHANGE_CONTENT_NAME);
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
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testSavedFileRunRestService() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //---- 1 -----------------
      //open file
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      
      openFileFromNavigationTreeWithCodeEditor(SIMPLE_FILE_NAME, false);

      //---- 2 -----------------
      //check Run Groovy Service button and menu
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkToolbarButtonState(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 3 -----------------
      //call Run Groovy Service command
      runToolbarButton(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP * 2);

      //check Launch Rest Service form appears
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      //---- 4 -----------------
      //close
      selenium.click("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]/closeButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 5 -----------------
      //check messages
      assertEquals(
         "[INFO] " + SIMPLE_FILE_NAME + " validated successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[1]//font[@color='#007700']"));
      assertEquals(
         "[INFO] " + BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT +"/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/" + SIMPLE_FILE_NAME
            + " deployed successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[2]//font[@color='#007700']"));

      //---- 6 -----------------
      //check, that hanlders removed, and after validation and deploying 
      //Launt Rest Service form doesn't appear

      runToolbarButton(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //check Launch Rest Service form doesn't appear
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      runToolbarButton(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      //check Launch Rest Service form doesn't appear
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

   }

   @Test
   public void testRunGroovyServiceWithNonValidFile() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      //---- 1 -----------------
      //open file
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(NON_VALID_FILE_NAME, false);

      //---- 2 -----------------
      //check Run Groovy Service button and menu
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkToolbarButtonState(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 3 -----------------
      //call Run Groovy Service command
      runToolbarButton(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 4 -----------------
      //check that validation fails message appears.
      String msg = selenium.getText("//font[@color='#880000']");
      assertTrue(msg.contains("[ERROR] " + NON_VALID_FILE_NAME + " validation failed. Error (400: Bad Request)"));

      //---- 5 -----------------
      //fix file
      selectIFrameWithEditor(0);
      selenium.clickAt("//body[@class='editbox']", "5,5");
      selectMainFrame();

      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_D);
      Thread.sleep(TestConstants.SLEEP);

      //---- 6 -----------------
      //validate file and check, that Launch Rest Service form doesn't appear
      runToolbarButton(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 7 -----------------
      //check messages
      assertEquals(
         "[INFO] " + NON_VALID_FILE_NAME + " validated successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[2]//font[@color='#007700']"));

      //check Launch Rest Service form doesn't appear
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

   }

   @Test
   public void testRunGroovyServiceWithChangedFile() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      //---- 1 -----------------
      //open file
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_FOR_CHANGE_CONTENT_NAME, false);

      //---- 2 -----------------
      //type some text
      typeTextIntoEditor(0, "//modified file\n");

      //---- 3 -----------------
      //check Run Groovy Service button and menu
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkToolbarButtonState(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      //---- 4 -----------------
      //call Run Groovy Service command
      runToolbarButton(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //check Launch Rest Service form appears
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      //---- 5 -----------------
      //close
      selenium.click("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]/closeButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 6 -----------------
      //check messages
      assertEquals(
         "[INFO] " + FILE_FOR_CHANGE_CONTENT_NAME + " validated successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[1]//font[@color='#007700']"));
      assertEquals(
         "[INFO] " + BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/"
            + FILE_FOR_CHANGE_CONTENT_NAME + " deployed successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[2]//font[@color='#007700']"));

   }

   @Test
   public void testRunGroovyServiceWithNewFile() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      //---- 1 -----------------
      //open file
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.REST_SERVICE_FILE);

      //---- 2 -----------------
      //check Run Groovy Service button and menu
      checkToolbarButtonPresentOnRightSide(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkToolbarButtonState(ToolbarCommands.Run.RUN_GROOVY_SERVICE, true);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.RUN_GROOVY_SERVICE, true);

      selectItemInWorkspaceTree(FOLDER_NAME);
      //---- 3 -----------------
      //call Run Groovy Service command
      runToolbarButton(ToolbarCommands.Run.RUN_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 5 -----------------
      //save as dialog appears
      SaveFileUtils.checkSaveAsDialogAndSave(NEW_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      //check Launch Rest Service form appears
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      //---- 6 -----------------
      //close
      selenium.click("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]/closeButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //---- 7 -----------------
      //check messages
      assertEquals(
         "[INFO] " + NEW_FILE_NAME + " validated successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[1]//font[@color='#007700']"));
      assertEquals(
         "[INFO] " + BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT +"/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/" + NEW_FILE_NAME
            + " deployed successfully.",
         selenium
            .getText("//div[@eventproxy='ideOperationFormTabSet']/div[2][@class='tabSetContainer']/div/div/div/div[1]/div/div[2]//font[@color='#007700']"));

   }

   @After
   public void afterMethod() throws Exception
   {
      //if file is opened, close it
      if (selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/icon"))
      {
         closeTab("0");
      }

      //check is warning dialog appears
      if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"))
      {
         //click no button
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.SLEEP);
      }
   }

}
