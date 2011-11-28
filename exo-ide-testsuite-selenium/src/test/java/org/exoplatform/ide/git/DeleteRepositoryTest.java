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
package org.exoplatform.ide.git;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class DeleteRepositoryTest extends BaseTest
{

   private static final String TEST_FOLDER = DeleteRepositoryTest.class.getSimpleName();

   private static final String TEST_FOLDER2 = DeleteRepositoryTest.class.getSimpleName() + "2";

   private final static String URL = WS_URL + TEST_FOLDER;

   private final static String URL2 = WS_URL + TEST_FOLDER2;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.mkcol(URL2);
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
         VirtualFileSystemUtils.delete(URL);
         VirtualFileSystemUtils.delete(URL2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void deleteRepositoryTest() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(URL + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.DELETE);

      IDE.ASK_DIALOG.waitOpened();
      assertEquals(GIT.DialogTitles.DELETE_DIALOG, IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();

      IDE.OUTPUT.waitForMessageShow(2);
      String message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.endsWith(GIT.Messages.DELETE_SUCCESS));

      selenium().open(URL);
      selenium().waitForPageToLoad("" + 5000);
      assertFalse(selenium().isElementPresent("link=.git"));
      selenium().goBack();
      IDE.WORKSPACE.waitForRootItem();
   }

   @Test
   public void deleteRepositoryFromNonGitFolder() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(URL2 + "/");
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.DELETE);
      IDE.WARNING_DIALOG.waitOpened();
      assertTrue(IDE.WARNING_DIALOG.getWarningMessage().contains("Not a git repository (or any of the parent directories)."));
   }
}
