/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.runner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version 24.12.2012 21:52:57
 *
 */
public class GAEJavaWebApplicationRunTest extends BaseTest
{
   private static final String PROJECT = GAEJavaWebApplicationRunTest.class.getSimpleName();

   private static String CURRENT_WINDOW = null;

   private WebDriverWait wait;

   @AfterClass
   public static void TearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         fail("Can't create test folders");
      }

   }

   @Test
   public void gaeJavaWebApplicationRunTest() throws Exception
   {
      CURRENT_WINDOW = driver.getWindowHandle();
      wait = new WebDriverWait(driver, 60);

      IDE.WELCOME_PAGE.clickCreateNewProjectFromScratch();
      IDE.CREATE_PROJECT_FROM_SCRATHC.waitCreateProjectFromScratch();
      IDE.CREATE_PROJECT_FROM_SCRATHC.typeProjectName(PROJECT);
      IDE.CREATE_PROJECT_FROM_SCRATHC.selectJavaWebApplicationTechnology();
      IDE.CREATE_PROJECT_FROM_SCRATHC.clickNextButton();
      IDE.CREATE_PROJECT_FROM_SCRATHC.waitProjectTemplateForm();
      IDE.CREATE_PROJECT_FROM_SCRATHC
         .selectProjectTemplate("Google App Engine Java project. Illustrates simple examples that use the Search API.");
      IDE.CREATE_PROJECT_FROM_SCRATHC.waitForJRebelCheckbox();
      IDE.CREATE_PROJECT_FROM_SCRATHC.clickOnJRebelCheckbox();
      IDE.CREATE_PROJECT_FROM_SCRATHC.clickFinishButton();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.LOADER.waitClosed();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.RUN_APPLICATION);

      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.OUTPUT.waitForMessageShow(3, 120);
      IDE.OUTPUT.clickOnAppLinkWithParticalText("exoplatform");

      // switching to application window
      switchToApplicationWindow(CURRENT_WINDOW);

      // checking application
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea")));

      driver.findElement(By.xpath("//textarea")).sendKeys("test");
      driver.findElement(By.xpath("//input[@type='submit']")).click();

      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tr[contains(.,(test))]")));
   }

   @Test
   public void stopGAEJavaWebApplicationTest() throws Exception
   {
      // stopping application and check that it stopped
      wait = new WebDriverWait(driver, 60);

      driver.switchTo().window(CURRENT_WINDOW);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.STOP_APPLICATION);

      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.OUTPUT.waitForMessageShow(4, 120);
      assertTrue(IDE.OUTPUT.getOutputMessage(4).contains("stopped"));

      switchToApplicationWindow(CURRENT_WINDOW);

      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea")));
      driver.findElement(By.xpath("//textarea")).sendKeys("test");
      driver.findElement(By.xpath("//input[@type='submit']")).click();
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(.,'404')]")));
   }

   /**
    * @param currentWin
    */
   private void switchToApplicationWindow(String currentWin)
   {
      for (String handle : driver.getWindowHandles())
      {
         if (currentWin.equals(handle))
         {
         }
         else
         {
            driver.switchTo().window(handle);
            break;
         }
      }
   }
}
