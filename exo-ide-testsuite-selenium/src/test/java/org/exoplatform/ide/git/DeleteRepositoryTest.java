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
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class DeleteRepositoryTest extends BaseTest
{

   private static final String PROJECT = DeleteRepositoryTest.class.getSimpleName();

   @Before
   public void before()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
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

   @Test
   public void deleteRepositoryTest() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.INIT);
      IDE.GIT.INIT_REPOSITORY.waitOpened();
      IDE.GIT.INIT_REPOSITORY.clickInitButton();
      IDE.GIT.INIT_REPOSITORY.waitClosed();

      IDE.OUTPUT.waitForMessageShow(1, 10);

      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.DELETE);

      IDE.ASK_DIALOG.waitOpened();
      assertEquals(GIT.DialogTitles.DELETE_DIALOG, IDE.ASK_DIALOG.getQuestion());
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();

      IDE.OUTPUT.waitForMessageShow(2, 10);
      String message = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(message.endsWith(GIT.Messages.DELETE_SUCCESS));

      driver.navigate().to(WS_URL + PROJECT);

      new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               return driver.findElement(By.partialLinkText(".project")) != null;
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
      try
      {
         assertTrue(driver.findElement(By.partialLinkText(".git")) == null);
      }
      catch (NoSuchElementException e)
      {
      }
      driver.navigate().back();
      IDE.PROJECT.EXPLORER.waitOpened();
   }

   @Test
   public void deleteRepositoryFromNonGitFolder() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.DELETE);
      IDE.WARNING_DIALOG.waitOpened();
      assertTrue(IDE.WARNING_DIALOG.getWarningMessage().contains(
         "Not a git repository (or any of the parent directories)."));
   }
}
