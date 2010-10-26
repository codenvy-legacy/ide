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
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLineGroovyTest extends BaseTest
{

   private final static String FILE_NAME = "GroovyCodeOutline.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FILE_NAME;

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GroovyCodeOutline.groovy";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.APPLICATION_GROOVY, URL);
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
   public void testCodeOutLineGroovy() throws Exception
   {
      //Open groovy file with content
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      //Open outline panel:
      runToolbarButton(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      //Check ouline tree nodes:
      checkOutlineTree();
      
      //Click name field
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("4 : 1", getCursorPositionUsingStatusBar());
      
    //Click age field
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("5 : 1", getCursorPositionUsingStatusBar());
      
      //Click address field
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("6 : 1", getCursorPositionUsingStatusBar());
      
      //Click greet method
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("17 : 1", getCursorPositionUsingStatusBar());
      
      //Click addYear method
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("12 : 1", getCursorPositionUsingStatusBar());
      
      Thread.sleep(30000);
   }
   
   private void  checkOutlineTree() throws Exception
   {
      //check for presence of tab outline
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]"));
      assertEquals("Outline", selenium.getText("scLocator=//TabSet[ID=\"ideCodeHelperTabSet\"]/tab[index=0]/title"));
      
      //check tree correctly created:
      assertEquals("Dep", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[0]/col[0]"));
      assertEquals("name : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[1]/col[0]"));
      assertEquals("age : int", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("address : int", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[3]/col[0]"));
      assertEquals("getAge : int", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[4]/col[0]"));
      assertEquals("addYear", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[5]/col[0]"));
      assertEquals("i : int", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[6]/col[0]"));
      assertEquals("greet : String", selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[7]/col[0]"));
   }
}
