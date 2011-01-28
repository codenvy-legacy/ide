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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @version $Id:
 *
 */

public class CodeOutlineXmlTest extends BaseTest
{
   private final static String FILE_NAME = "XmlCodeOutline.xml";
   
   private final static String TEST_FOLDER = CodeOutlineXmlTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {
      
      String filePath ="src/test/resources/org/exoplatform/ide/operation/edit/outline/XmlCodeOutline.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, URL+TEST_FOLDER + "/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(URL+TEST_FOLDER);
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

   /**
    * IDE-174:XML Code Outline
    * @throws Exception
    */
   @Test
   public void testXmlCodeOutline() throws Exception
   {
      waitForRootElement();
      
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      /*
       * 1. Open file with text
       */
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      /*
       * 2. Open outline
       */
      IDE.toolbar().runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      
      /*
       * Check Outline Panel appeared
       */
      assertTrue(selenium.isElementPresent(Locators.CodeHelperPanel.SC_OUTLINE_TAB_LOCATOR));
      //"scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]/tab[ID=isc_OutlineForm_0]/"));
      
      checkTreeCorrectlyCreated();
      
      checkTreeNavigation();
      
      checkCodeNavigation();
      
      checkUpdatingTreeAfterFileChanging();
      
      Thread.sleep(TestConstants.SLEEP);
     
      IDE.editor().closeFileTabIgnoreChanges(0);
   }
   
   private void checkCodeNavigation() throws Exception
   {
      /*
       * Click on editor
       */
      IDE.editor().clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      /*
       * Press key DOWN to navigate in editor
       */
      for (int i = 0; i < 4; i++){
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
     
      Thread.sleep(TestConstants.SLEEP);
     
      IDE.outline().checkOutlineTreeNodeSelected(1, "display-name", true);
      assertEquals("5 : 1", getCursorPositionUsingStatusBar());
      
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.outline().checkOutlineTreeNodeSelected(1, "display-name", false);
      IDE.outline().checkOutlineTreeNodeSelected(3, "param-name", true);
      assertEquals("9 : 1", getCursorPositionUsingStatusBar());
      Thread.sleep(TestConstants.SLEEP);
      
      //press Ctrl+End to go to the end of document
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      //check selection in tree
      IDE.outline().checkOutlineTreeNodeSelected(3, "param-name", false);
      IDE.outline().checkOutlineTreeNodeSelected(0, "web-app", true);
      //check cursor position
      assertEquals("32 : 11", getCursorPositionUsingStatusBar());
      
      //press Ctrl+HOME to go to the end of document
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_HOME);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      //check selection in tree
      IDE.outline().checkOutlineTreeNodeSelected(0, "web-app", true);
      //check cursor position
      assertEquals("1 : 1", getCursorPositionUsingStatusBar());
      
      //check, that selection in tree will redraw after
      //2 seconds, when cursor activity stop
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.outline().checkOutlineTreeNodeSelected(0, "web-app", true);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.outline().checkOutlineTreeNodeSelected(0, "web-app", true);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.outline().checkOutlineTreeNodeSelected(0, "web-app", true);
      Thread.sleep(TestConstants.SLEEP);
      
      //check selection in tree
      IDE.outline().checkOutlineTreeNodeSelected(0, "web-app", false);
      IDE.outline().checkOutlineTreeNodeSelected(1, "display-name", true);
      //check cursor position
      assertEquals("4 : 1", getCursorPositionUsingStatusBar());
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void checkUpdatingTreeAfterFileChanging() throws Exception
   {
      //---- 1----- 
      //add new node and check, that tree will updated, after 2 seconds
      //when stop typing text
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      
      //type text
      typeTextIntoEditor(0, "\n<settings>\n");
      //check, that after typing outline tree is the same
      typeTextIntoEditor(0, "<value>value</value>\n");
      Thread.sleep(TestConstants.SLEEP);
      //check, that after typing outline tree is the same
      typeTextIntoEditor(0, "</settings>\n");
      //check, that after typing outline tree is the same

      //pause
      Thread.sleep(TestConstants.SLEEP);
      
      //check, that after 2 seconds tree is updated
      assertEquals("web-app", IDE.outline().getTitle(0, 0));
      assertEquals("display-name", IDE.outline().getTitle(1, 0));
      //new nodes added
      assertEquals("settings", IDE.outline().getTitle(2, 0));
      assertEquals("value", IDE.outline().getTitle(3, 0));
      assertEquals("context-param", IDE.outline().getTitle(4, 0));
      assertEquals("context-param", IDE.outline().getTitle(5, 0));
      assertEquals("CDATA", IDE.outline().getTitle(6, 0));
      assertEquals("filter", IDE.outline().getTitle(7, 0));
      
      //close node settings
      IDE.outline().clickOpenImg(2, 0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check, than node value is hidden
      assertEquals("settings", IDE.outline().getTitle(2, 0));
      assertEquals("context-param", IDE.outline().getTitle(3, 0));
      assertEquals("context-param", IDE.outline().getTitle(4, 0));
      assertEquals("CDATA", IDE.outline().getTitle(5, 0));
      assertEquals("filter", IDE.outline().getTitle(6, 0));
      
      //check cursor position
      assertEquals("10 : 3", getCursorPositionUsingStatusBar());
      
      //---- 2 ----
      //navigate in editor and check, that tree
      //works correctly with new node
      
      //click on node
      IDE.outline().select(3);
      
      //click on editor
      IDE.editor().clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //go up and check, that node settings opened
      //and node value selected
      for (int i = 0; i < 7; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      
      Thread.sleep(TestConstants.SLEEP);
      
      //check, that after 2 seconds tree is updated
      assertEquals("web-app", IDE.outline().getTitle(0, 0));
      assertEquals("display-name", IDE.outline().getTitle(1, 0));
      //new nodes added
      assertEquals("settings", IDE.outline().getTitle(2, 0));
      assertEquals("value", IDE.outline().getTitle(3, 0));
      assertEquals("context-param", IDE.outline().getTitle(4, 0));
      assertEquals("context-param", IDE.outline().getTitle(5, 0));
      assertEquals("CDATA", IDE.outline().getTitle(6, 0));
      assertEquals("filter", IDE.outline().getTitle(7, 0));
      
      IDE.outline().checkOutlineTreeNodeSelected(3, "value", true);
      
      //check cursor position
      assertEquals("8 : 1", getCursorPositionUsingStatusBar());
      
      
      //---- 4 ----
      //check, that if you stay on closing tag,
      //outline tree should highlight according node
      
      
      //go one line lower on </settings> tag
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      //pause
      Thread.sleep(TestConstants.SLEEP);
      
      //check, that tree is the same, as on the previous step 
      assertEquals("web-app", IDE.outline().getTitle(0, 0));
      assertEquals("display-name", IDE.outline().getTitle(1, 0));
      assertEquals("settings", IDE.outline().getTitle(2, 0));
      assertEquals("value", IDE.outline().getTitle(3, 0));
      assertEquals("context-param", IDE.outline().getTitle(4, 0));
      assertEquals("context-param", IDE.outline().getTitle(5, 0));
      assertEquals("CDATA", IDE.outline().getTitle(6, 0));
      assertEquals("filter", IDE.outline().getTitle(7, 0));
      
      //check, that settings node is selected
      IDE.outline().checkOutlineTreeNodeSelected(2, "settings", true);
      IDE.outline().checkOutlineTreeNodeSelected(3, "value", false);
      assertEquals("9 : 1", getCursorPositionUsingStatusBar());
      
      //now click on node "settings" in tree
      //and cursor must go to <settings> tab in editor: line 7, column 1
      IDE.outline().select(2);
      Thread.sleep(TestConstants.SLEEP);
      IDE.outline().checkOutlineTreeNodeSelected(2, "settings", true);
      assertEquals("7 : 1", getCursorPositionUsingStatusBar());
   }
   
   private void checkTreeNavigation() throws Exception
   {
      //click on node
      IDE.outline().select(4);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //cursor jump to the node in editor
      assertEquals("12 : 1", getCursorPositionUsingStatusBar());
      
      //close filter node
      IDE.outline().clickOpenImg(9, 1);
      //cursor stay at the same position
      assertEquals("12 : 1", getCursorPositionUsingStatusBar());
      
      //click on CDATA node
      IDE.outline().select(8);
      //cursor jump to the node in editor
      assertEquals("24 : 1", getCursorPositionUsingStatusBar());
      
      //click on root node
      IDE.outline().select(0);
      //cursor jump to the node in editor
      assertEquals("2 : 1", getCursorPositionUsingStatusBar());
   }
   
   /**
    * Walk through tree and check all nodes.
    * 
    * Open all nodes and check children.
    * 
    * When method continues work, it doesn't close nodes
    *  
    * @throws Exception
    */
   private void checkTreeCorrectlyCreated() throws Exception
   {
      // check when all nodex closed, except root
      assertEquals("web-app", IDE.outline().getTitle(0, 0));
      assertEquals("display-name", IDE.outline().getTitle(1, 0));
      assertEquals("context-param", IDE.outline().getTitle(2, 0));
      assertEquals("context-param", IDE.outline().getTitle(3, 0));
      assertEquals("CDATA", IDE.outline().getTitle(4, 0));
      assertEquals("filter", IDE.outline().getTitle(5, 0));
      
      // open first context-param
      IDE.outline().clickOpenImg(2, 0);
      // check two nodes under context-param added
      assertEquals("param-name", IDE.outline().getTitle(3, 0));
      assertEquals("param-value", IDE.outline().getTitle(4, 0));
      // check other nodes do down on two positions
      assertEquals("context-param", IDE.outline().getTitle(5, 0));
      assertEquals("CDATA", IDE.outline().getTitle(6, 0));
      assertEquals("filter", IDE.outline().getTitle(7, 0));
      // open second node context-param
      IDE.outline().clickOpenImg(5, 0);
      // check two nodes under context-param added
      assertEquals("param-name", IDE.outline().getTitle(6, 0));
      assertEquals("param-value", IDE.outline().getTitle(7, 0));
      // check other nodes do down on two positions
      assertEquals("CDATA", IDE.outline().getTitle(8, 0));
      assertEquals("filter", IDE.outline().getTitle(9, 0));
      // open filter node
      IDE.outline().clickOpenImg(9, 0);
      // check two nodes under filter node added
      assertEquals("filter-name", IDE.outline().getTitle(10, 0));
      assertEquals("filter-class", IDE.outline().getTitle(11, 0));
   }
}
