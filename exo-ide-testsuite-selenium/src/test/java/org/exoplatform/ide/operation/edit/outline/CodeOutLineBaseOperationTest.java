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
 * 
 * 
 * @author <a href="musienko.maxim@gmail.com">Musienko Maksim</a>
 * @version $Id:   ${date} ${time}
  */
public class CodeOutLineBaseOperationTest extends BaseTest
{
   
   private final static String FILE_NAME = "GroovyTemplateCodeOutline.gtmpl";

   private final static String TEST_FOLDER = CodeOutLineBaseOperationTest.class.getSimpleName();

   private static final String WAIT_FOR_PARSING_TEST_LOCATOR =
      "//html[@style='border-width: 0pt;']//body[@class='editbox']//span[284][@class='xml-tagname']";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GroovyTemplateCodeOutline.gtmpl";
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_TEMPLATE, WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   // IDE-178:Groovy Template Code Outline
   @Test
   public void testNavigationOnOutLineGroovyTemplate() throws Exception
   {
      //---- 1-2 -----------------
      //open file with text
      // Open groovy file with test content
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);

      waitForElementPresent(WAIT_FOR_PARSING_TEST_LOCATOR);

      //---- 3 -----------------
      //open Outline Panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent("ideOutlineTreeGrid");
      //click on second groovy code node
      IDE.OUTLINE.selectRow(2);

      //check, than cursor go to line
      assertEquals("26 : 1", IDE.STATUSBAR.getCursorPosition());

      //---- 4 -----------------
      //delete some tags in groovy template file
      for (int i = 0; i < 7; i++)
      {
         IDE.EDITOR.runHotkeyWithinEditor(0, true, false, 68);
      }
      
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("26 : 1", getCursorPositionUsingStatusBar());
      //check outline tree
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(1));
      IDE.OUTLINE.doubleClickItem(1);
      assertEquals("div", IDE.OUTLINE.getItemLabel(12));
      assertEquals("a1 : Object", IDE.OUTLINE.getItemLabel(2));
      //check selection in outline tree
      IDE.OUTLINE.checkOutlineTreeNodeSelected(1, "groovy code", true);

      //---- 5 -----------------
      //click on editor
      goToLine(27);
      assertEquals("27 : 1", getCursorPositionUsingStatusBar());
      
      //check outline tree
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(1));
      assertEquals("div", IDE.OUTLINE.getItemLabel(12));

      assertEquals("a", IDE.OUTLINE.getItemLabel(13));
      assertEquals("groovy code", IDE.OUTLINE.getItemLabel(14));
      //check selection in outline tree
      IDE.OUTLINE.checkOutlineTreeNodeSelected(14, "groovy code", true);

   }
}