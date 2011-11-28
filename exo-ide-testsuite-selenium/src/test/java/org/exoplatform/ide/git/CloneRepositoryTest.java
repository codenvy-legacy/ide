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
 * @version $Id:  Jun 23, 2011 2:42:41 PM anya $
 *
 */
public class CloneRepositoryTest extends BaseTest
{
   private static final String TEST_FOLDER = CloneRepositoryTest.class.getSimpleName();

   private static final String REPOSITORY = "repository";

   private static final String CLONE_FOLDER = "ForClone";

   private static final String TEST_FILE = "TestFile.txt";

   private static final String TEST_FILE1 = "File1.txt";

   private static final String TEST_FILE2 = "File2.txt";

   private static final String DEFAULT_REMOTE_NAME = "origin";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/clone-test.zip";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.upoadZipFolder(ZIP_PATH, WS_URL);
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
    * Tests the Clone repository view: opens it and checks elements,
    * then closes with Cancel button.
    * 
    * @throws Exception
    */
   @Test
   public void testCloneRepositoryView() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.CLONE);
      IDE.GIT.CLONE_REPOSITORY.waitOpened();
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.isOpened());
      Assert.assertFalse(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.isCancelButtonEnabled());

      Assert.assertFalse(IDE.GIT.CLONE_REPOSITORY.getWorkDirectoryValue().isEmpty());
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.getRemoteUriFieldValue().isEmpty());
      Assert.assertEquals(DEFAULT_REMOTE_NAME, IDE.GIT.CLONE_REPOSITORY.getRemoteNameFieldValue());

      //Check Clone button is disabled, when remote URI field is empty:
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri(GIT_PATH + "/" + REPO_NAME);
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri("");
      Assert.assertFalse(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());

      IDE.GIT.CLONE_REPOSITORY.clickCancelButton();
      IDE.GIT.CLONE_REPOSITORY.waitClosed();
   }

   /**
    * Tests the Clone repository command for workspace. 
    * Must be not allowed.
    * 
    * @throws Exception
    */
   @Test
   public void testCloneRepositoryInWorkspace() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE, true);

      IDE.WORKSPACE.selectRootItem();
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE, false);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE, true);
   }

   /**
    * Tests the Clone repository command for selected file. 
    * Must be not allowed.
    * 
    * @throws Exception
    */
   @Test
   public void testCloneRepositoryWithSelectedFile() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE, true);

      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_FILE);
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE, false);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.MENU.checkCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE, true);
   }

   /**
    * @throws Exception
    */
   @Test
   public void testCloneRepository() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER + "/");

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.CLONE);
      IDE.GIT.CLONE_REPOSITORY.waitOpened();

      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.getWorkDirectoryValue().endsWith(CLONE_FOLDER + "/"));
      Assert.assertTrue(IDE.GIT.CLONE_REPOSITORY.getRemoteUriFieldValue().isEmpty());
      Assert.assertEquals(DEFAULT_REMOTE_NAME, IDE.GIT.CLONE_REPOSITORY.getRemoteNameFieldValue());

      //Check Clone button is disabled, when remote URI field is empty:
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri(GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
         + "/" + REPOSITORY);
      IDE.GIT.CLONE_REPOSITORY.clickCloneButton();
      IDE.GIT.CLONE_REPOSITORY.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertTrue(message.endsWith(GIT.Messages.CLONE_SUCCESS));

      //Sleep is necessary for files to appear in Davfs:
      Thread.sleep(3000);

      selenium().open(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER);
      selenium().waitForPageToLoad("" + 5000);
      Assert.assertTrue(selenium().isElementPresent("link=.git"));
      Assert.assertTrue(selenium().isElementPresent("link=" + TEST_FILE1));
      Assert.assertTrue(selenium().isElementPresent("link=" + TEST_FILE2));
      selenium().goBack();

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER + "/");

      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER + "/" + TEST_FILE1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + CLONE_FOLDER + "/" + TEST_FILE2);
   }

   /**
    * Test Clone Git repository in folder with Git repository.
    * 
    * @throws Exception
    */
   @Test
   public void testCloneRepositoryIfExists() throws Exception
   {
      selenium().refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + REPOSITORY + "/");
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.CLONE);
      IDE.GIT.CLONE_REPOSITORY.waitOpened();
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri(GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER
         + "/" + REPOSITORY);
      IDE.GIT.CLONE_REPOSITORY.clickCloneButton();

      IDE.OUTPUT.waitForMessageShow(1);
      String message = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertTrue(message.startsWith(GIT.Messages.CLONE_REPO_EXISTS));
      Assert.assertTrue(message.endsWith(GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER + "/"
         + REPOSITORY + "/.git"));
   }
}
