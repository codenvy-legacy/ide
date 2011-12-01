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
import org.exoplatform.ide.git.core.InitRepository;
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
 * @version $Id:  Jun 23, 2011 9:55:53 AM anya $
 *
 */
public class InitRepositoryTest extends BaseTest
{
   private static final String PROJECT = InitRepositoryTest.class.getSimpleName();

   private static final String TEST_FILE = "TestFile.html";

   @Before
   public void before()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Thread.sleep(2000);
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
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Tests the Init repository view: opens it and checks elements,
    * then closes with Cancel button.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryView() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      assertTrue(IDE.GIT.INIT_REPOSITORY.isOpened());
      assertTrue(IDE.GIT.INIT_REPOSITORY.isInitButtonEnabled());
      assertTrue(IDE.GIT.INIT_REPOSITORY.isCancelButtonEnabled());
      assertEquals("/" + PROJECT, IDE.GIT.INIT_REPOSITORY.getWorkDirectoryValue());

      //Check element's titles:
      assertEquals(InitRepository.Titles.INIT_BUTTON, IDE.GIT.INIT_REPOSITORY.getInitButtonTitle());
      assertEquals(InitRepository.Titles.CANCEL_BUTTON, IDE.GIT.INIT_REPOSITORY.getCancelButtonTitle());

      IDE.GIT.INIT_REPOSITORY.clickCancelButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();
   }

   /**
    * Tests the Init repository command for selected file. 
    * Must be not allowed.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryWithSelectedFile() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");
      IDE.EDITOR.saveAs(1, TEST_FILE);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.waitTabNotPresent(TEST_FILE);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_FILE);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Git.GIT, MenuCommands.Git.INIT));

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      assertTrue(IDE.GIT.INIT_REPOSITORY.isOpened());
      assertTrue(IDE.GIT.INIT_REPOSITORY.isInitButtonEnabled());
      assertTrue(IDE.GIT.INIT_REPOSITORY.isCancelButtonEnabled());
      assertFalse(IDE.GIT.INIT_REPOSITORY.getWorkDirectoryValue().isEmpty());

      IDE.GIT.INIT_REPOSITORY.clickCancelButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();
   }

   /**
    * Test init Git repository.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepository() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      assertTrue(IDE.GIT.INIT_REPOSITORY.getWorkDirectoryValue().endsWith(PROJECT));

      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));

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

      driver.navigate().back();
      IDE.PROJECT.EXPLORER.waitOpened();
   }

   /**
    * Test init Git repository in folder with Git repository.
    * 
    * @throws Exception
    */
   @Test
   public void testInitRepositoryIfExists() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.GIT.INIT_REPOSITORY.initRepository();

      IDE.OUTPUT.waitForMessageShow(1, 10);
      String message = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(message.endsWith(GIT.Messages.INIT_SUCCESS));

      IDE.GIT.INIT_REPOSITORY.initRepository();
      IDE.OUTPUT.waitForMessageShow(2, 10);
      message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.startsWith(GIT.Messages.REPOSITORY_EXISTS));
      assertTrue(message.contains(PROJECT));
   }
}
