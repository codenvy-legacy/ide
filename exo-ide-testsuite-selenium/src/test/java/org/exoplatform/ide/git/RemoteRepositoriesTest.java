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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 30, 2011 2:36:11 PM anya $
 *
 */
public class RemoteRepositoriesTest extends BaseTest
{
   private static final String PROJECT = RemoteRepositoriesTest.class.getSimpleName();

   private static final String REMOTE1_NAME = "remote1";

   private static final String REMOTE2_NAME = "remote2";

   private static final String REMOTE1_URL = "url1";

   private static final String REMOTE2_URL = "url2";

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void before()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(PROJECT, EMPTY_ZIP_PATH);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void after()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Test command is not available for Remote repositories in not Git repository.
    * @throws Exception 
    */
   @Test
   public void testRemotesCommand() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      //Check Remove files is available:
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE));
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);

      IDE.GIT.REMOTES.waitOpened();
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitClosed();
   }

   /**
    * Test the Remote repositories view.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testRemoteRepositoriesView() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      //Open Remote repositories view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitOpened();

      assertTrue(IDE.GIT.REMOTES.isOpened());
      assertEquals(0, IDE.GIT.REMOTES.getRemoteRepositoriesCount());
      assertFalse(IDE.GIT.REMOTES.isDeleteButtonEnabled());
      assertTrue(IDE.GIT.REMOTES.isAddButtonEnabled());

      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitClosed();
   }

   /**
    * Test Add remote repository.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testAddRemoteRepository() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      //Open Remote repositories view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitOpened();

      assertTrue(IDE.GIT.REMOTES.isOpened());
      assertEquals(0, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Add remote repository:
      IDE.GIT.REMOTES.clickAddButton();
      IDE.GIT.REMOTES.waitAddRemoteViewOpened();
      assertTrue(IDE.GIT.REMOTES.isAddRepositoryOpened());
      assertFalse(IDE.GIT.REMOTES.isOkButtonEnabled());

      IDE.GIT.REMOTES.typeToNameField(REMOTE1_NAME);
      assertFalse(IDE.GIT.REMOTES.isOkButtonEnabled());
      IDE.GIT.REMOTES.typeToUrlField(REMOTE1_URL);
      assertTrue(IDE.GIT.REMOTES.isOkButtonEnabled());
      IDE.GIT.REMOTES.clickOkButton();
      IDE.GIT.REMOTES.waitAddRemoteViewClosed();

      IDE.GIT.REMOTES.waitForRemotesCount(1);
      assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Close Remotes view:
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitClosed();

      //Open Remotes view again:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitOpened();

      Assert.assertTrue(IDE.GIT.REMOTES.isOpened());
      Assert.assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Close Remotes view:
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitClosed();
   }

   /**
    * Test Delete remote repository.
    * 
    * @throws Exception 
    * 
    */
   @Test
   public void testDeleteRemoteRepository() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      
      IDE.GIT.REMOTES.addRemoteRepository(REMOTE1_NAME, REMOTE1_URL);

      //Open Remote repositories view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitOpened();

      assertTrue(IDE.GIT.REMOTES.isOpened());
      assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Add remote repository:
      IDE.GIT.REMOTES.clickAddButton();
      IDE.GIT.REMOTES.waitAddRemoteViewOpened();

      IDE.GIT.REMOTES.typeToNameField(REMOTE2_NAME);
      IDE.GIT.REMOTES.typeToUrlField(REMOTE2_URL);
      IDE.GIT.REMOTES.clickOkButton();
      IDE.GIT.REMOTES.waitAddRemoteViewClosed();

      IDE.GIT.REMOTES.waitForRemotesCount(2);
      assertEquals(2, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Delete second repository:
      IDE.GIT.REMOTES.selectRemoteByName(REMOTE2_NAME);
      assertTrue(IDE.GIT.REMOTES.isDeleteButtonEnabled());
      IDE.GIT.REMOTES.clickDeleteButton();

      //Check confirmation dialog:
      IDE.ASK_DIALOG.waitOpened();
      assertEquals(String.format(GIT.Messages.DELETE_REMOTE_QUESTION, REMOTE2_NAME), IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();

      IDE.GIT.REMOTES.waitForRemotesCount(1);
      Assert.assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Delete second repository:
      IDE.GIT.REMOTES.selectRemoteByName(REMOTE1_NAME);
      Assert.assertTrue(IDE.GIT.REMOTES.isDeleteButtonEnabled());
      IDE.GIT.REMOTES.clickDeleteButton();

      //Check confirmation dialog:
      IDE.ASK_DIALOG.waitOpened();
      Assert.assertEquals(String.format(GIT.Messages.DELETE_REMOTE_QUESTION, REMOTE1_NAME),
         IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();

      IDE.GIT.REMOTES.waitForRemotesCount(0);
      assertEquals(0, IDE.GIT.REMOTES.getRemoteRepositoriesCount());
      assertFalse(IDE.GIT.REMOTES.isDeleteButtonEnabled());

      //Close Remotes view:
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitClosed();
   }

}
