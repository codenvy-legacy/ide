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

   // Check, that Outline tab correctly works, when we
   // try to navigate on different tabs in editor
   @Test
   public void testOutlineWhenSeveralFilesOpen() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      // ---- 1 --------------
      // open new javascript file
      //TODO IDE - 2155 on this moment outline for javascript file does not works.
      //After fix we need uncoment this block 
      /* IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
       IDE.EDITOR.waitActiveFile();

       // no outline panel
       assertFalse(IDE.OUTLINE.isOutlineTreePresent());

       // ---- 2 --------------
       // show outline
       IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
       IDE.OUTLINE.waitOpened();
       IDE.OUTLINE.waitOutlineTreeVisible();*/

      // ---- 3 --------------
      // open new html file

      //TODO after resolve issue 2155 block str 87-98 can be removed
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile();

      //TODO Pause for build outline tree
      //after implementation method for check ready state, should be remove
      Thread.sleep(4000);

      // no outline panel
      IDE.OUTLINE.waitOutlineTreeNotVisible();

      // ---- 2 --------------
      // show outline
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile();

      // check outline present
      IDE.OUTLINE.waitOutlineTreeVisible();

      // ---- 4 --------------
      // Close Outline tab
      IDE.OUTLINE.closeOutline();
      IDE.OUTLINE.waitClosed();
      IDE.OUTLINE.waitOutlineTreeNotVisible();

      // ---- 5 --------------
      // go to javascript file
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitActiveFile();
      IDE.OUTLINE.waitOutlineTreeNotVisible();
   }
}
