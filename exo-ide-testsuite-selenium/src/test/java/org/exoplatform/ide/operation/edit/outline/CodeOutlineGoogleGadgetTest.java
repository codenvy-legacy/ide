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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeOutlineGoogleGadgetTest extends BaseTest
{
   private final static String FILE_NAME = "GoogleGadgetCodeOutline.xml";
   
   private final static String TEST_FOLDER = CodeOutlineGoogleGadgetTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GoogleGadgetCodeOutline.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.GOOGLE_GADGET, URL + TEST_FOLDER+ "/" + FILE_NAME);
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

   //IDE-173 : Google Gadget Code Outline
   @Test
   public void testCodeOutlineGoogleGadget() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //---- 1-2 -----------------
      //open file with text
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //---- 3 -----------------
      //open Outline Panel
      IDE.toolbar().runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);
      //press key DOWN and key UP 
      //TODO: check why code outline works incorrectly without this
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
     
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.SLEEP);

      //---- 4 -----------------
      //check outline tree correctly created
      checkTreeCreatedCorrectly();

      //---- 5 -----------------
      //check tree navigation
      //click on first br node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]");
      Thread.sleep(TestConstants.SLEEP);
      //check cursor go to line
      assertEquals("32 : 1", getCursorPositionUsingStatusBar());

      //---- 6 -----------------
      //check selecting of tree node, when cursor changes position

      //click on editor
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //press key DOWN to navigate in editor
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("33 : 1", getCursorPositionUsingStatusBar());
      IDE.outline().checkOutlineTreeNodeSelected(8, "br", false);
      IDE.outline().checkOutlineTreeNodeSelected(9, "br", true);
      
      //go to script tag by pressing key DOWN
      IDE.editor().clickOnEditor();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //Go to script tag in editor
      for (int i = 0; i < 39; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      
      Thread.sleep(TestConstants.SLEEP*2);
      //check tree
      IDE.outline().checkOutlineTreeNodeSelected(11, "script", true);

      //delete script node
      runHotkeyWithinEditor(0, true, false, 68);
      Thread.sleep(300);
      runHotkeyWithinEditor(0, true, false, 68);
      Thread.sleep(300);
      runHotkeyWithinEditor(0, true, false, 68);
      Thread.sleep(TestConstants.SLEEP);

      IDE.outline().checkOutlineTreeNodeSelected(6, "body", true);

      //check, that there are no nodes script or b in Outline tree
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));

      //close file      
      IDE.editor().closeTab(0);

      //close dialog window by pressing "No"
      selenium
         .click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/member[Class=Canvas||index=0||length=2||classIndex=0||classLength=1]/");
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(TestConstants.SLEEP);

   }

   private void checkTreeCreatedCorrectly() throws Exception
   {
      //check for presence of tab outline
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]/tab[ID=isc_OutlineForm_0]/"));
      assertEquals("Outline", selenium.getText("scLocator=//TabSet[ID=\"ideCodeHelperPanel\"]/tab[index=0]/title"));

      //check tree correctly created:
      //all nodes closed, except root
      assertEquals("Module", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("ModulePrefs", selenium
         .getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("Content", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));

      //open Content node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new node appeared
      assertEquals("CDATA", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));

      //open CDATA node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new node appeared
      assertEquals("html", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));

      //open html node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("head", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("body", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));

      //open head node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]/open");
      Thread.sleep(TestConstants.SLEEP);
      
      //new nodes appeared
      assertEquals("meta", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      assertEquals("link", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("title", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      //check other nodes
      assertEquals("body", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));

      //open script node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("a", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      //check other nodes
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("body", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));

      //close head node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //open body node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("table", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));

      //open table node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("thead", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]"));
      assertEquals("tbody", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      //check other nodes
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));

      //open thead node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("tr", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]"));
      //check other nodes
      assertEquals("tbody", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));

      //open tr node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("td", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      //check other nodes
      assertEquals("tbody", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[15]/col[0]"));

      //close thead node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[8]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //open tbody node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("tr", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]"));
      //check other nodes
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));

      //open tr node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //new nodes appeared
      assertEquals("td", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]"));
      assertEquals("td", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));
      assertEquals("td", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[13]/col[0]"));
      //check other nodes
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[14]/col[0]"));
      assertEquals("br", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[15]/col[0]"));
      assertEquals("style", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[16]/col[0]"));
      assertEquals("script", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[17]/col[0]"));

      //close tr node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[10]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //close tbody node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[9]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //close table node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //open script node
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[11]/col[0]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //node node appeared
      assertEquals("b", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[12]/col[0]"));

      Thread.sleep(TestConstants.SLEEP);
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
}