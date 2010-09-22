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
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
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
public class GoToErrorInRestServiceTest extends BaseTest
{
   private final static String FILE_WITH_ERROR = "RestServiceWithError.groovy";
   
   private final static String FILE_WITH_ERROR_FOR_CHANGING = "RestServiceWithErrorForChanging.groovy";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         VirtualFileSystemUtils.put(filePath + FILE_WITH_ERROR, MimeType.GROOVY_SERVICE, URL + FILE_WITH_ERROR);
         VirtualFileSystemUtils.put(filePath + FILE_WITH_ERROR_FOR_CHANGING, MimeType.GROOVY_SERVICE, URL + FILE_WITH_ERROR_FOR_CHANGING);
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
   public void testGoToErrorInOpenedFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      openAndValidateRestService();
      
      //---- 1 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //check, cursor go to position
      assertEquals("3 : 9", getCursorPositionUsingStatusBar());
      
   }
   
   @Test
   public void testGoToErrorInClosedFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      openAndValidateRestService();
      //---- 1 -----------------
      //close tab
      closeTabIgnoreChanges("0");
      
      //---- 2 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //file must be opened and cursor must stay on error
      assertEquals(FILE_WITH_ERROR, getTabTitle(0));
      
      assertEquals("3 : 9", getCursorPositionUsingStatusBar());
      
      //---- 3 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      createFileFromToolbar(MenuCommands.New.REST_SERVICE_FILE);
      
      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
   }
   
   @Test
   public void testGoToErrorIfOtherTabSelected() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      openAndValidateRestService();
      //---- 1 -----------------
      //open another tab
      createFileFromToolbar(MenuCommands.New.XML_FILE);
      
      //---- 2 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //check, tab with rest service must be opened
      checkIsEditorTabSelected(FILE_WITH_ERROR, true);
      
      assertEquals("3 : 9", getCursorPositionUsingStatusBar());
      
      //---- 3 -----------------
      //close file
      closeTabIgnoreChanges("1");
      
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      createFileFromToolbar(MenuCommands.New.REST_SERVICE_FILE);
      
      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
   }
   
   @Test
   public void testGoToErrorIfFileIsDeleted() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      openAndValidateRestService();
      //---- 1 -----------------
      //close tab
      closeTabIgnoreChanges("0");
      
      //---- 2 -----------------
      //delete file
      selectItemInWorkspaceTree(FILE_WITH_ERROR);
      deleteSelectedItems();
      
      //---- 3 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //check, error dialog appeared
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      final String textFromErrDialog = selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/");
      assertTrue(textFromErrDialog.contains("404"));
      
      //---- 4 -----------------
      //click Ok button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //---- 5 -----------------
      //open new rest service file and check, that cursor doesn't go to position 3 : 9
      createFileFromToolbar(MenuCommands.New.REST_SERVICE_FILE);
      
      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
   }
   
   @Test
   public void testGoToErrorAfterChangingFile() throws Exception
   {
      //refresh, to clear console and close it
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 1 -----------------
      //open file
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_WITH_ERROR_FOR_CHANGING, false);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 2 -----------------
      //press validate button
      runToolbarButton(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      //check, validation fails
      String validationMsg = selenium.getText("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span");
      assertTrue(validationMsg.contains("validation failed"));
      
      //---- 3 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //fix validation error
      selectIFrameWithEditor(0);
      //go to error
      for (int i = 0; i < 6; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      //delete# unnecessary  space
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_BACK_SPACE);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      
      selectMainFrame();
      
      //---- 4 -----------------
      //press validate button
      runToolbarButton(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      
      //check, validation fails
      validationMsg = selenium.getText("//div[@eventproxy='ideOutputForm']/div[2]/div/table//font[@color='#880000']/span");
      assertTrue(validationMsg.contains("validation failed"));
      
      //---- 5 -----------------
      //click on validation message to go to error
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[2]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //check cursor went to position
      assertEquals("3 : 3", getCursorPositionUsingStatusBar());
      
      //---- 6 -----------------
      //click on first validation message to check, 
      //that cursor can go to previous error (event, it is already fixed)
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //check cursor went to position
      assertEquals("1 : 9", getCursorPositionUsingStatusBar());
      
      //---- 7 -----------------
      //delete some text and check
      //that cursor stays if try to go to nonexistent line
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[2]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      selectIFrameWithEditor(0);
      //select all
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      //delete
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);
      selectMainFrame();
      
      Thread.sleep(TestConstants.SLEEP);
      
      //type some text
      typeTextIntoEditor(0, "public void TestClass(){}");
      
      //go to middle
      selectIFrameWithEditor(0);
      //select all
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selectMainFrame();
      
      assertEquals("1 : 24", getCursorPositionUsingStatusBar());
      
      //when 3d line is deleted, try to go to it
      selenium.clickAt("//div[@eventproxy='ideOutputForm']/div[2]/div/table//font[@color='#880000']/span", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("1 : 24", getCursorPositionUsingStatusBar());
      
      Thread.sleep(TestConstants.SLEEP);

   }
   
   public void closeTabIgnoreChanges(String tabIndex) throws Exception
   {
      //if file is opened, close it
      if (selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + tabIndex + "]/icon"))
      {
         closeTab(tabIndex);
      }

      //check is warning dialog appears
      if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"))
      {
         //click no button
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.SLEEP);
      }
   }
   
   private void openAndValidateRestService() throws Exception
   {
      //---- 1 -----------------
      //open file
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_WITH_ERROR, false);
      Thread.sleep(TestConstants.SLEEP);
      
      //---- 2 -----------------
      //press validate button
      runToolbarButton(ToolbarCommands.Run.VALIDATE_GROOVY_SERVICE);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      //check, validation fails
      final String validationMsg = selenium.getText("//div[@eventproxy='ideOutputForm']/div[1]/div/table//font[@color='#880000']/span");
      assertTrue(validationMsg.contains("validation failed"));
   }
   
   @After
   public void afterMethod() throws Exception
   {
      //check, if opened, close two files
      closeTabIgnoreChanges("0");
      closeTabIgnoreChanges("0");
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FILE_WITH_ERROR);
         VirtualFileSystemUtils.delete(URL + FILE_WITH_ERROR_FOR_CHANGING);
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
