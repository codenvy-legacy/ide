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
public class SpringApplicationJRebelRunAndUpdateTest extends BaseTest
{
   private static final String PROJECT = SpringApplicationJRebelRunAndUpdateTest.class.getSimpleName();

   private static String CURRENT_WINDOW = null;

   private WebDriverWait wait;

   private static final String CONTENT_FOR_CHANGE = "NEW PROJECT NAME";

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
   public void springApplicationJRebelRunAndUpdateTest() throws Exception
   {
      CURRENT_WINDOW = driver.getWindowHandle();
      wait = new WebDriverWait(driver, 60);

      IDE.WELCOME_PAGE.clickCreateNewProjectFromScratch();
      IDE.CREATE_PROJECT_FROM_SCRATHC.waitCreateProjectFromScratch();
      IDE.CREATE_PROJECT_FROM_SCRATHC.typeProjectName(PROJECT);
      IDE.CREATE_PROJECT_FROM_SCRATHC.selectJavaSpringTechnology();
      IDE.CREATE_PROJECT_FROM_SCRATHC.clickNextButton();
      IDE.CREATE_PROJECT_FROM_SCRATHC.waitProjectTemplateForm();
      IDE.CREATE_PROJECT_FROM_SCRATHC.selectProjectTemplate("Simple Spring application.");
      IDE.CREATE_PROJECT_FROM_SCRATHC.waitForJRebelCheckbox();

      // JRebel inputs shows only before first using.
      // TODO uncomment it after fixing hiding of input fields.
      //      if (IDE.CREATE_PROJECT_FROM_SCRATHC.isJRebelInputsVisible())
      //      {
      IDE.CREATE_PROJECT_FROM_SCRATHC.typeJRebelFirstName("FIRST_NAME");
      IDE.CREATE_PROJECT_FROM_SCRATHC.typeJRebelPhoneName("455435543534");
      IDE.CREATE_PROJECT_FROM_SCRATHC.typeJRebelLastName("LAST_NAME");
      //      }

      IDE.CREATE_PROJECT_FROM_SCRATHC.clickFinishButton();
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROJECT.PACKAGE_EXPLORER.closePackageExplorer();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.RUN_APPLICATION);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.OUTPUT.waitForMessageShow(3, 120);
      IDE.OUTPUT.clickOnAppLinkWithParticalText("exoplatform");

      // switching to application window
      switchToApplicationWindow(CURRENT_WINDOW);

      // checking application
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Enter your name: ']")));
      driver.findElement(By.xpath("//input[@type='text']")).sendKeys("test");
      driver.findElement(By.xpath("//input[@type='submit']")).click();
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Hello, test!']")));

      // changing code and updating application
      driver.switchTo().window(CURRENT_WINDOW);

      changeApplicationContent();

      //updating application
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UPDATE_APPLICATION);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.OUTPUT.waitForMessageShow(6, 120);
      assertTrue(IDE.OUTPUT.getOutputMessage(6).contains("updated"));

      //check that application was updated
      switchToApplicationWindow(CURRENT_WINDOW);
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Enter your name: ']")));
      driver.findElement(By.xpath("//input[@type='text']")).sendKeys("test");
      driver.findElement(By.xpath("//input[@type='submit']")).click();
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='" + CONTENT_FOR_CHANGE
         + " Hello, test!']")));

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

   /**
    * Open related to application UI file and change it for checking updating feature
    * 
    * @throws Exception
    */
   private void changeApplicationContent() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main/java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main/java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main/java/helloworld");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main/java/helloworld");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/src/main/java/helloworld/GreetingController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/src/main/java/helloworld/GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.JAVAEDITOR.moveCursorDown(21);
      IDE.JAVAEDITOR.moveCursorRight(18);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(CONTENT_FOR_CHANGE + " ");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      IDE.JAVAEDITOR.waitNoContentModificationMark("GreetingController.java");
   }
}
