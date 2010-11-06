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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 
 */
public class CodeOutLineGroovyTemplateTest extends BaseTest
{
   private final static String FILE_NAME = "GroovyTemplateCodeOutline.gtmpl";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
   
   @BeforeClass
   public static void setUp()
   {
      
      String filePath ="src/test/resources/org/exoplatform/ide/operation/edit/outline/GroovyTemplateCodeOutline.gtmpl";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_TEMPLATE, URL);
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
   public static void tearDown() throws Exception
   {
      closeUnsavedFileAndDoNotSave("0");
      cleanDefaultWorkspace();
   }

   // IDE-178:Groovy Template Code Outline
   @Test
   public void testCodeOutLineGroovyTemplate() throws Exception
   {
      //---- 1-2 -----------------
      //open file with text
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //---- 3 -----------------
      //open Outline Panel
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      //---- 4 -----------------
      //check Outline tree
      checkTreeCorrectlyCreated();
      
      //close first node groovy code
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP);
      
      //click on second groovy code node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      
      //check, than cursor go to line
      assertEquals("26 : 1", getCursorPositionUsingStatusBar());
      
      //---- 5 -----------------
      //delete some tags in groovy template file
      for (int i = 0; i < 7; i++)
      {
         runHotkeyWithinEditor(0, true, false, 68);
      }
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("26 : 1", getCursorPositionUsingStatusBar());
      //check outline tree
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      //check selection in outline tree
      checkOutlineTreeNodeSelected(1, "div", true);
      
      //---- 6 -----------------
      //move in editor

      //click on editor
      selenium.clickAt("//body[@class='editbox']", "5,5");
      Thread.sleep(TestConstants.SLEEP);
      
      //press key DOWN to navigate in editor
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      
      //check outline tree
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      //check selection in outline tree
      checkOutlineTreeNodeSelected(2, "a", true);
      assertEquals("27 : 1", getCursorPositionUsingStatusBar());
   }
   
   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check for presence of tab outline
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[ID=isc_OutlineForm_0]/"));
      assertEquals("Outline", selenium.getText("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[index=0]/title"));
      
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      
      //check tree correctly created:
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("a1", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("a2 : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("a2 : boolean", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      assertEquals("a3 : void", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      assertEquals("cTab : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("cName : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      assertEquals("description : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("displayName : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("isSelected : boolean", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      assertEquals("a4 : Integer", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      //check other nodes
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));
      
      //open first div node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check subnodes of div
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      //check other nodes
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[15]/col[0]"));
      
      //open subnode div
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check subnodes of div
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));
      //check other nodes
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[15]/col[0]"));
      assertEquals("div", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[16]/col[0]"));
      
      //open second node div
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[16]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check new nodes added under div
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[17]/col[0]"));
      
      //open node a
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[17]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check new nodes added under a
      assertEquals("groovy code", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[18]/col[0]"));
   }

}
