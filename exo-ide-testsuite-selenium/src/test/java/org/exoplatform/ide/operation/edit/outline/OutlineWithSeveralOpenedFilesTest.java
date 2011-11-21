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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * IDE-170:Test closing Code Outline panel.
 * 
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */

public class OutlineWithSeveralOpenedFilesTest extends BaseTest
{

   ////fix for run tests where new session start after 7 testcases passes (Outline panel  remains is open after the previous test )
   @Before
   public void chekOutliePresentAfterPrevTest() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      if (selenium().isElementPresent("ideOutlineTreeGrid"))
      {
         IDE.OUTLINE.closeOutline();
      }
      else
      {
      }
   }

   @After
   public void tearDown()
   {
      deleteCookies();
   }

   //Check, that Outline tab correctly works, when we
   //try to navigate on different tabs in editor
   @Test
   public void testOutlineWhenSeveralFilesOpen() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      //---- 1 --------------
      //open new javascript file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);

      //no outline panel
      assertFalse(IDE.OUTLINE.isOutlineTreePresent());

      //---- 2 --------------
      //show outline
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent("ideOutlineTreeGrid");

      //check outline appeared
      //we can't use checkOutlineVisibility() method,
      //because when outline appears at first time, element div
      //doesn't have in style attribute visibility attribute
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());

      //---- 3 --------------
      //open new html file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);

      //      //check outline present
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());

      //---- 4 --------------
      //Close Outline tab 
      IDE.OUTLINE.closeOutline();

      waitForElementNotPresent("ideOutlineTreeGrid");
      assertFalse(IDE.OUTLINE.isOutlineTreePresent());

      //---- 5 --------------
      //go to javascript file
      IDE.EDITOR.selectTab(0);
      waitForElementPresent(Locators.EDITOR_LOCATOR);

      //TODO fix problem with selenium click on outline form (after  IDE.OUTLINE.closeOutline();)
      //end
      // IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      // IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }

}
