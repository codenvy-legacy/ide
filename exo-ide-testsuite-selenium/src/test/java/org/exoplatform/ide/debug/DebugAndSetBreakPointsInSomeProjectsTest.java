/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class DebugAndSetBreakPointsInSomeProjectsTest extends DebuggerServices
{

   private static final String PROJECT = DebugAndSetBreakPointsInSomeProjectsTest.class.getSimpleName();

   private static final String PROJECT2 = DebugAndSetBreakPointsInSomeProjectsTest.class.getSimpleName() + "2";

   protected static Map<String, Link> project;

   protected static Map<String, Link> project2;

   private static final String USER = "user";

   private static final String SUBMIT = "submit";

   private WebDriver debuger_app_instance = new FirefoxDriver();

   private WebDriverWait wait;

   @Before
   public void before()
   {

      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/debug/change-variable-proj.zip");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      try
      {
         project2 =
            VirtualFileSystemUtils.importZipProject(PROJECT2,
               "src/test/resources/org/exoplatform/ide/debug/change-variable-proj.zip");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }

   @After
   public void tearDown() throws IOException, InterruptedException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      Thread.sleep(1000);
      VirtualFileSystemUtils.delete(WS_URL + PROJECT2);
      Thread.sleep(1000);
      debuger_app_instance.quit();
   }

   @Test
   public void setBreackPointTest() throws Exception
   {
      String currentWin = driver.getWindowHandle();

      //run debug app 2 check link and get link adress
      runDebugApp(PROJECT);

      //step 2 check link and get link location
      IDE.OUTPUT.clickOnOutputTab();
      IDE.OUTPUT.waitLinkWithParticalText("exoplatform");
      IDE.OUTPUT.clickOnAppLinkWithParticalText("exoplatform");
      IDE.DEBUGER.waitOpenedSomeWin();
      switchToDemoAppWindowAndClose(currentWin);
      driver.switchTo().window(currentWin);
      //step 3 return from demo app to IDE window and set breakpoint
      // check setting breackpoint is correctly
      IDE.DEBUGER.waitTabOfDebuger();
      IDE.DEBUGER.clickOnDebugerTab();
      isDebugerButtonsWithoutBreakPoints();
      IDE.DEBUGER.clickOnDebugerTab();
      IDE.DEBUGER.waitOpened();
      IDE.JAVAEDITOR.selectTab("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();

      //step 4 set breakpoint
      IDE.DEBUGER.setBreakPoint(22);
      IDE.DEBUGER.waitToggleBreackPointIsSet(22);
      IDE.DEBUGER.waitBreackPointsTabContainetWithSpecifiedValue("[line :");
      assertTrue(IDE.DEBUGER.getTextFromBreackPointTabContainer().contains(
         "helloworld.GreetingController - [line : 22]"));

      //step 5 switch on debug app, type 'selenium' value, click ok button
      //return close browser window with application and return to ide. Check changes
      openProjectAndClass(PROJECT2);
      IDE.OUTPUT.clickOnOutputTab();
      IDE.OUTPUT.waitOpened();
      IDE.OUTPUT.clickClearButton();
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEBUG_APPLICATION);
      IDE.BUILD.waitOpened();
      String builderMessageSecondPrj = IDE.BUILD.getOutputMessage();
      assertTrue(builderMessageSecondPrj.startsWith(Build.Messages.BUILDING_PROJECT));
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      //check check breakpoint container is empty, set new breakpoint and check, that only new breakpoint is present in panel 
      IDE.OUTPUT.clickOnOutputTab();
      IDE.OUTPUT.waitLinkWithParticalText("exoplatform");
      String lnk = IDE.OUTPUT.getUrlTextText("exoplatform");
      IDE.OUTPUT.clickOnAppLinkWithParticalText("exoplatform");
      IDE.DEBUGER.waitOpenedSomeWin();
      driver.switchTo().window(currentWin);
      IDE.DEBUGER.waitTabOfDebuger();
      IDE.DEBUGER.clickOnDebugerTab();
      IDE.DEBUGER.waitBreackPointsTabContainerIsEmpty();
      IDE.DEBUGER.setBreakPoint(19);
      IDE.DEBUGER.waitToggleBreackPointIsSet(19);
      IDE.DEBUGER.waitBreackPointsTabContainetWithSpecifiedValue("helloworld.GreetingController - [line : 19]");
      assertEquals(IDE.DEBUGER.getTextFromBreackPointTabContainer(), "helloworld.GreetingController - [line : 19]");
      typeAndSendOnDemeApp(lnk);
      IDE.DEBUGER.waitActiveBreackPointIsSet(19);
      IDE.DEBUGER.clickDisconnectBtnClick();
      IDE.DEBUGER.waitDebugerIsClosed();
      IDE.OUTPUT.waitForSubTextPresent("stopped.");
   }

   /**
    * @throws Exception
    */
   private void openProjectAndClass(String project) throws Exception
   {
      IDE.PROJECT.OPEN.openProject(project);
      if (IDE.ASK_DIALOG.isOpened())
      {
         IDE.ASK_DIALOG.clickYes();
         IDE.ASK_DIALOG.waitClosed();
      }
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   /**
    * Type value to field demo application, press enter and close 
    * window 
    * @param value
    * @param winHandle
    * @throws InterruptedException
    */
   private void typeValueAndSubmitDemoappAndExit(String value, String winHandle) throws InterruptedException
   {
      driver.findElement(By.name(USER)).sendKeys(value);
      // driver.findElement(By.name(SUBMIT)).click();
      try
      {
         Robot robot = new Robot();
         Thread.sleep(1000);
         robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
         robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
         Thread.sleep(1000);
         robot.keyPress(java.awt.event.KeyEvent.VK_ESCAPE);
         robot.keyRelease(java.awt.event.KeyEvent.VK_ESCAPE);
      }
      catch (AWTException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public void switchToDemoAppWindowAndClose(String winHandl)
   {
      Set<String> driverWindows = driver.getWindowHandles();
      for (String wins : driverWindows)
      {
         if (!wins.equals(winHandl))
         {
            wait = new WebDriverWait(driver, 10);
            driver.switchTo().window(wins);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(USER)));
            driver.close();
         }
      }
   }

   /**
    * @param lnk
    * @throws InterruptedException 
    */
   private void typeAndSendOnDemeApp(String lnk) throws InterruptedException
   {
      debuger_app_instance.get(lnk);
      new WebDriverWait(debuger_app_instance, 10).until(ExpectedConditions.visibilityOfElementLocated(By.name(USER)));
      new WebDriverWait(debuger_app_instance, 10).until(ExpectedConditions.visibilityOfElementLocated(By.name(SUBMIT)));
      WebElement field_2 = debuger_app_instance.findElement(By.name(USER));
      WebElement submitBtn_2 = debuger_app_instance.findElement(By.name(SUBMIT));
      field_2.sendKeys("selenium");
      try
      {
         Robot robot = new Robot();
         Thread.sleep(1000);
         robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
         robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
      }
      catch (AWTException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }
}
