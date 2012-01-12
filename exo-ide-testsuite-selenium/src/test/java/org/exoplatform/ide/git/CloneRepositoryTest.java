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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.git.core.GIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 23, 2011 2:42:41 PM anya $
 *
 */
public class CloneRepositoryTest extends BaseTest
{
   private static final String PROJECT = CloneRepositoryTest.class.getSimpleName();

   private static final String REPOSITORY = "clone_repository";

   private static final String TEST_FILE1 = "File1.txt";

   private static final String TEST_FILE2 = "File2.txt";

   private static final String DEFAULT_REMOTE_NAME = "origin";

   private static final String ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/clone-test.zip";

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.importZipProject(REPOSITORY, ZIP_PATH);
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void afterTest()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         VirtualFileSystemUtils.delete(WS_URL + REPOSITORY);
      }
      catch (Exception e)
      {
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
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE));

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.CLONE);
      IDE.GIT.CLONE_REPOSITORY.waitOpened();
      assertTrue(IDE.GIT.CLONE_REPOSITORY.isOpened());
      assertFalse(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());
      assertTrue(IDE.GIT.CLONE_REPOSITORY.isCancelButtonEnabled());

      assertFalse(IDE.GIT.CLONE_REPOSITORY.getWorkDirectoryValue().isEmpty());
      assertTrue(IDE.GIT.CLONE_REPOSITORY.getRemoteUriFieldValue().isEmpty());
      assertEquals(DEFAULT_REMOTE_NAME, IDE.GIT.CLONE_REPOSITORY.getRemoteNameFieldValue());

      //Check Clone button is disabled, when remote URI field is empty:
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri(GIT_PATH + "/" + REPO_NAME);
      assertTrue(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri("");
      assertFalse(IDE.GIT.CLONE_REPOSITORY.isCloneButtonEnabled());

      IDE.GIT.CLONE_REPOSITORY.clickCancelButton();
      IDE.GIT.CLONE_REPOSITORY.waitClosed();
   }

   /**
    * @throws Exception
    */
   @Test
   public void testCloneRepository() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.CLONE);
      IDE.GIT.CLONE_REPOSITORY.waitOpened();

      assertTrue(IDE.GIT.CLONE_REPOSITORY.getWorkDirectoryValue().endsWith("/" + PROJECT));
      assertTrue(IDE.GIT.CLONE_REPOSITORY.getRemoteUriFieldValue().isEmpty());
      assertEquals(DEFAULT_REMOTE_NAME, IDE.GIT.CLONE_REPOSITORY.getRemoteNameFieldValue());

      //Check Clone button is disabled, when remote URI field is empty:
      IDE.GIT.CLONE_REPOSITORY.setRemoteUri(GIT_PATH + "/" + REPO_NAME + "/" + WS_NAME + "/" + REPOSITORY);
      IDE.GIT.CLONE_REPOSITORY.clickCloneButton();
      IDE.GIT.CLONE_REPOSITORY.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1, 15);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(message.endsWith(GIT.Messages.CLONE_SUCCESS));

      //Sleep is necessary for files to appear in Davfs:
      Thread.sleep(3000);

      driver.navigate().to(WS_URL + PROJECT);
      new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return driver.findElement(By.partialLinkText(".git")) != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

      assertTrue(driver.findElement(By.partialLinkText(".git")) != null);
      assertTrue(driver.findElement(By.partialLinkText(TEST_FILE1)) != null);
      assertTrue(driver.findElement(By.partialLinkText(TEST_FILE2)) != null);
      driver.navigate().back();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE2);

      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.CLONE));
   }
}
