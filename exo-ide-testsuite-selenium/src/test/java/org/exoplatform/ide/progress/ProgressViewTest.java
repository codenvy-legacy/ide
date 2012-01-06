/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.progress;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 20, 2011 evgen $
 *
 */
public class ProgressViewTest extends BaseTest
{
   private static final String PROJECT = ProgressViewTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         String projectPath = "src/test/resources/org/exoplatform/ide/progress/progress-repo.zip";
         VirtualFileSystemUtils.importZipProject(PROJECT, projectPath);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testProgress() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      
      IDE.MENU.waitForMenuItemPresent(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();

      IDE.PROGRESS_BAR.waitProgressBarControl();

      IDE.PROGRESS_BAR.clickProgressBarControl();
      IDE.PROGRESS_BAR.waitProgressBarView();

      String text = IDE.PROGRESS_BAR.getViewText();
      
      assertTrue(text.contains("Initializing "));
   }
}
