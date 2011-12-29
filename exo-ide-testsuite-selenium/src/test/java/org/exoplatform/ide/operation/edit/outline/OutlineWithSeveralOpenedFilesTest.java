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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
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

public class OutlineWithSeveralOpenedFilesTest extends BaseTest
{

   private final static String PROJECT = OutlineWithSeveralOpenedFilesTest.class.getSimpleName();

   @BeforeClass
   public static void setUp() throws Exception
   {
      VirtualFileSystemUtils.createDefaultProject(PROJECT);
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

   //Check, that Outline tab correctly works, when we
   //try to navigate on different tabs in editor
   @Test
   public void testOutlineWhenSeveralFilesOpen() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      //---- 1 --------------
      //open new javascript file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.js");

      //no outline panel
      assertFalse(IDE.OUTLINE.isOutlineTreePresent());

      //---- 2 --------------
      //show outline
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      //---- 3 --------------
      //open new html file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.html");

      //check outline present
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());

      //---- 4 --------------
      //Close Outline tab 
      IDE.OUTLINE.closeOutline();
      IDE.OUTLINE.waitClosed();
      assertFalse(IDE.OUTLINE.isOutlineTreePresent());

      //---- 5 --------------
      //go to javascript file
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.js");

      assertFalse(IDE.OUTLINE.isOutlineTreePresent());
   }
}
