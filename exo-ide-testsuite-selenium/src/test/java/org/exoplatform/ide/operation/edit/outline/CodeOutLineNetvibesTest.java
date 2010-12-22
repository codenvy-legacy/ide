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
 * Test for code outline for netvibes files.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CodeOutLineNetvibesTest extends BaseTest
{
   
   private final static String FILE_NAME = "NetvibesCodeOutline.html";
   
   private final static String FOLDER_NAME = CodeOutLineNetvibesTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {
      String filePath ="src/test/resources/org/exoplatform/ide/operation/edit/outline/NetvibesCodeOutline.html";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL + FOLDER_NAME + "/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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
   
   private void select(int row, int col) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" 
         + String.valueOf(row) + "]/col[" + String.valueOf(col) + "]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   // IDE-175:Html Code Outline
   @Test
   public void testCodeOutLineNetvibes() throws Exception
   {
      //------ 1-3 ------------
      //open file with text
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //------ 4 ------------
      //show Outline
      IDE.toolbar().runCommand(ToolbarCommands.View.SHOW_OUTLINE);

      //------ 5 ------------
      //check Outline tree
      checkTreeCorrectlyCreated();
      
      //------ 6 ------------
      //check navigation in tree
      //click on "p" tag
      select(3, 1);
      
      assertEquals("51 : 1", getCursorPositionUsingStatusBar());
      
      //close "p" tag
      clickOpenImg(3, 1);
      assertEquals("51 : 1", getCursorPositionUsingStatusBar());
      
      //open "p" tag
      clickOpenImg(3, 1);
      assertEquals("51 : 1", getCursorPositionUsingStatusBar());
      checkOutlineTreeNodeSelected(3, "p", true);
      
      goToLine(7);
      Thread.sleep(TestConstants.SLEEP);
      checkOutlineTreeNodeSelected(2, "meta", true);
      
      //press Ctrl+D to delete lines
      selenium.clickAt("//body[@class='editbox']", "5,5");
      for (int i = 0; i < 5; i++)
      {
         runHotkeyWithinEditor(0, true, false, 68);
         Thread.sleep(TestConstants.SLEEP_SHORT*2);
      }
      Thread.sleep(TestConstants.SLEEP);
      
      checkOutlineTreeNodeSelected(2, "link", true);
      
      assertEquals("html", getTitle(0, 0));
      assertEquals("head", getTitle(1, 0));
      
      assertEquals("link", getTitle(2, 0));
      assertEquals("script", getTitle(3, 0));
      assertEquals("title", getTitle(4, 0));
      assertEquals("link", getTitle(5, 0));
      assertEquals("widget:preferences", getTitle(6, 0));
      assertEquals("style", getTitle(7, 0));
      assertEquals("script", getTitle(8, 0));
      assertEquals("body", getTitle(9, 0));

   }
   
   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check for presence of tab outline
      assertTrue(selenium.isElementPresent(Locators.CodeHelperPanel.SC_OUTLINE_TAB_LOCATOR));
      assertEquals("Outline", selenium.getText(Locators.CodeHelperPanel.SC_CODE_HELPER_TABSET_LOCATOR 
         + "/tab[index=0]/title"));
      
      //check tree correctly created:
      //all nodes closed, except root
      assertEquals("html", getTitle(0, 0));
      assertEquals("head", getTitle(1, 0));
      assertEquals("body", getTitle(2, 0));
      
      //open "head" node
      clickOpenImg(1, 0);
      //check new nodes appeard
      assertEquals("meta", getTitle(2, 0));
      assertEquals("meta", getTitle(3, 0));
      assertEquals("meta", getTitle(4, 0));
      assertEquals("meta", getTitle(5, 0));
      assertEquals("meta", getTitle(6, 0));
      assertEquals("link", getTitle(7, 0));
      assertEquals("script", getTitle(8, 0));
      assertEquals("title", getTitle(9, 0));
      assertEquals("link", getTitle(10, 0));
      assertEquals("widget:preferences", getTitle(11, 0));
      assertEquals("style", getTitle(12, 0));
      assertEquals("script", getTitle(13, 0));
      assertEquals("body", getTitle(14, 0));
      
      //open "script" node
      clickOpenImg(13, 0);
      assertEquals("YourWidgetName", getTitle(14, 0));
      assertEquals("body", getTitle(15, 0));
      
      //open "YourWidgetName" node
      clickOpenImg(14, 0);
      assertEquals("argument", getTitle(15, 0));
      assertEquals("body", getTitle(16, 0));
      
      //close "head" node
      clickOpenImg(1, 0);
      
      //open "body" node
      clickOpenImg(2, 0);
      assertEquals("p", getTitle(3, 0));
   }
   
   /**
    * Get title of outline node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @return title in (row, col) position in outline tree
    */
   private String getTitle(int row, int col)
   {
      return selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" 
         + String.valueOf(row) +  "]/col[" + String.valueOf(col) + "]");
   }
   
   /**
    * Click on open icon of outline node.
    * 
    * Can open or close node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @throws Exception
    */
   private void clickOpenImg(int row, int col) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" 
         + String.valueOf(row) + "]/col[" + String.valueOf(col) + "]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
