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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-170:Test closing Code Outline panel.
 * 
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */

public class ClosingOutlinePanelTest extends BaseTest
{

   private final static String JAVASCRIPT_FILE_NAME = "TestJavaScriptFile.js";

   private final static String TEXT_FILE_NAME = "SampleTextFile.txt";

   private final static String FOLDER_NAME = ClosingOutlinePanelTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      String javaScriptFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/TestJavaScriptFile.js";
      String textFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/SampleTextFile.txt";
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(javaScriptFilePath, MimeType.APPLICATION_JAVASCRIPT, WS_URL + FOLDER_NAME + "/" + JAVASCRIPT_FILE_NAME);
         VirtualFileSystemUtils.put(textFilePath, MimeType.TEXT_PLAIN, WS_URL + FOLDER_NAME + "/" + TEXT_FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void afterMethod()
   {
      deleteCookies();
      cleanRegistry();
      cleanDefaultWorkspace();
   }

   //IDE-170:Test closing Code Outline panel
   @Test
   public void testClosingOutlinePanel() throws Exception
   {
      //----- 1 -------------
      //open JavaScript file.
      
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + JAVASCRIPT_FILE_NAME, false);

      //----- 2 -------------
      //show Code Outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);

      waitForElementPresent("ideOutlineTreeGrid");
      //check Code Outline present
      IDE.OUTLINE.assertOutlineTreePresent();

      //----- 3 -------------
      //open text file.
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEXT_FILE_NAME, false);

      //check Code Outline in Not Present
      IDE.OUTLINE.checkOtlineTreeIsNotPresent();

      //return to the tab with JavaScript.
      IDE.EDITOR.selectTab(0);
      //check Code Outline present
      IDE.OUTLINE.assertOutlineTreePresent();

      //----- 4 -------------
      //close code outline panel.

      IDE.EDITOR.closeFile(0);
      //check Code Outline in Not Present
      IDE.OUTLINE.checkOtlineTreeIsNotPresent();

      //----- 5 -------------
      //refresh page
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      IDE.WORKSPACE.waitForRootItem();
      
      //check outline is not present
      IDE.OUTLINE.checkOtlineTreeIsNotPresent();

   }

   //---- implementation -----------

   private void checkCodeHelperPanelPresent(boolean isPresent)
   {
      if (isPresent)
      {
         assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/"));
      }
   }

   private void checkCodeHelperPanelVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCodeHelperPanel' and contains(@style, 'visibility: inherit')]/div[@eventproxy='ideCodeHelperPanel']"));
         assertFalse(selenium
            .isElementPresent("//div[@eventproxy='ideCodeHelperPanel' and contains(@style, 'visibility: hidden')]/div[@eventproxy='ideCodeHelperPanel']"));
      }
      else
      {
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCodeHelperPanel' and contains(@style, 'visibility: hidden')]/div[@eventproxy='ideCodeHelperPanel']"));
         assertFalse(selenium
            .isElementPresent("//div[@eventproxy='ideCodeHelperPanel' and contains(@style, 'visibility: inherit')]/div[@eventproxy='ideCodeHelperPanel']"));
      }
   }

}