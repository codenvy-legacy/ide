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

import junit.framework.Assert;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 30, 2011 2:36:11 PM anya $
 *
 */
public class RemoteRepositoriesTest extends BaseTest
{
   private static final String TEST_FOLDER = RemoteRepositoriesTest.class.getSimpleName();

   private static final String REMOTE1_NAME = "remote1";

   private static final String REMOTE2_NAME = "remote2";

   private static final String REMOTE1_URL = "url1";

   private static final String REMOTE2_URL = "url2";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
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
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
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
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectRootItem();

      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, false);

      //Not Git repository:
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, true);
      
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.ERROR_DIALOG.waitIsOpened();
      String message = IDE.ERROR_DIALOG.getMessage();
      Assert.assertEquals(GIT.Messages.NOT_GIT_REPO, message);
      IDE.ERROR_DIALOG.clickOk();
      IDE.ERROR_DIALOG.waitIsClosed();

      //Init repository:
      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(1);
      message = IDE.OUTPUT.getOutputMessageText(1);
      Assert.assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));

      //Check Remove files is available:
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, true);
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);

      IDE.GIT.REMOTES.waitForViewOpened();
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitForViewClosed();
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Remote repositories view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.REMOTES.isViewComponentsPresent());
      Assert.assertEquals(0, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      Assert.assertFalse(IDE.GIT.REMOTES.isDeleteButtonEnabled());
      Assert.assertTrue(IDE.GIT.REMOTES.isAddButtonEnabled());

      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitForViewClosed();
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Remote repositories view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.REMOTES.isViewComponentsPresent());
      Assert.assertEquals(0, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Add remote repository:
      IDE.GIT.REMOTES.clickAddButton();
      IDE.GIT.REMOTES.waitForAddRemoteViewOpened();
      Assert.assertTrue(IDE.GIT.REMOTES.isAddRepositoryViewComponentsPresent());
      Assert.assertFalse(IDE.GIT.REMOTES.isOkButtonEnabled());

      IDE.GIT.REMOTES.typeToNameField(REMOTE1_NAME);
      Assert.assertFalse(IDE.GIT.REMOTES.isOkButtonEnabled());
      IDE.GIT.REMOTES.typeToUrlField(REMOTE1_URL);
      Assert.assertTrue(IDE.GIT.REMOTES.isOkButtonEnabled());
      IDE.GIT.REMOTES.clickOkButton();
      IDE.GIT.REMOTES.waitForAddRemoteViewClosed();

      IDE.GIT.REMOTES.waitForRemotesCount(1);
      Assert.assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Close Remotes view:
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitForViewClosed();

      //Open Remotes view again:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.REMOTES.isViewComponentsPresent());
      Assert.assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Close Remotes view:
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitForViewClosed();
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
      selenium().refresh();
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Open Remote repositories view:
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.REMOTE, MenuCommands.Git.REMOTES);
      IDE.GIT.REMOTES.waitForViewOpened();

      Assert.assertTrue(IDE.GIT.REMOTES.isViewComponentsPresent());
      Assert.assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Add remote repository:
      IDE.GIT.REMOTES.clickAddButton();
      IDE.GIT.REMOTES.waitForAddRemoteViewOpened();

      IDE.GIT.REMOTES.typeToNameField(REMOTE2_NAME);
      IDE.GIT.REMOTES.typeToUrlField(REMOTE2_URL);
      IDE.GIT.REMOTES.clickOkButton();
      IDE.GIT.REMOTES.waitForAddRemoteViewClosed();

      IDE.GIT.REMOTES.waitForRemotesCount(2);
      Assert.assertEquals(2, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Delete second repository:
      IDE.GIT.REMOTES.selectRemoteByName(REMOTE2_NAME);
      Assert.assertTrue(IDE.GIT.REMOTES.isDeleteButtonEnabled());
      IDE.GIT.REMOTES.clickDeleteButton();

      //Check confirmation dialog:
      IDE.ASK_DIALOG.waitForAskDialogOpened();
      Assert.assertEquals(String.format(GIT.Messages.DELETE_REMOTE_QUESTION, REMOTE2_NAME),
         IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitForDialogNotPresent();

      IDE.GIT.REMOTES.waitForRemotesCount(1);
      Assert.assertEquals(1, IDE.GIT.REMOTES.getRemoteRepositoriesCount());

      //Delete second repository:
      IDE.GIT.REMOTES.selectRemoteByName(REMOTE1_NAME);
      Assert.assertTrue(IDE.GIT.REMOTES.isDeleteButtonEnabled());
      IDE.GIT.REMOTES.clickDeleteButton();

      //Check confirmation dialog:
      IDE.ASK_DIALOG.waitForAskDialogOpened();
      Assert.assertEquals(String.format(GIT.Messages.DELETE_REMOTE_QUESTION, REMOTE1_NAME),
         IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitForDialogNotPresent();

      waitForLoaderDissapeared();
      Assert.assertEquals(0, IDE.GIT.REMOTES.getRemoteRepositoriesCount());
      Assert.assertFalse(IDE.GIT.REMOTES.isDeleteButtonEnabled());

      //Close Remotes view:
      IDE.GIT.REMOTES.clickCloseButton();
      IDE.GIT.REMOTES.waitForViewClosed();
   }

}
