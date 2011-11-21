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
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 16, 2010 $
 *
 */
//http://jira.exoplatform.org/browse/IDE-417
public class OutlineClosingTest extends BaseTest
{
   private final static String PROJECT = OutlineClosingTest.class.getSimpleName();

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

   @Test
   public void test() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.grs");

      openAndCloseOutline();

      openAndCloseOutline();

      openAndCloseOutline();
   }

   /**
    * @throws Exception
    * @throws InterruptedException
    */
   private void openAndCloseOutline() throws Exception, InterruptedException
   {
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();

      // check for presence of tab outline
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());
      assertTrue(IDE.OUTLINE.isOutlineViewVisible());

      IDE.OUTLINE.closeOutline();
      IDE.OUTLINE.waitClosed();
      assertFalse(IDE.OUTLINE.isOutlineTreePresent());
      assertFalse(IDE.OUTLINE.isOutlineViewVisible());
   }
}
