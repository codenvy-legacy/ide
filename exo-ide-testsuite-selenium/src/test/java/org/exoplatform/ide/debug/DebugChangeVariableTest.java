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
package org.exoplatform.ide.debug;

import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DebugChangeVariableTest extends DebuggerServices
{

   private static final String PROJECT = DebugChangeVariableTest.class.getSimpleName();

   protected static Map<String, Link> project;

   private static final String USER = "user";

   private static final String SUBMIT = "submit";

   WebDriver debuger_app_instance = new FirefoxDriver();

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

   }

   @After
   public void tearDown() throws IOException, InterruptedException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      debuger_app_instance.quit();
      Thread.sleep(1000);
   }

   @Test
   public void changeVariableTest() throws Exception
   {
      String currentWin = driver.getWindowHandle();

      //run debug app 2 check link and get link adress
      runDebugApp(PROJECT);

      //step 2 check link and get link location
      IDE.OUTPUT.clickOnOutputTab();
      IDE.OUTPUT.waitLinkWithParticalText("http://");
      String lnk = IDE.OUTPUT.getUrlTextText("http://");
      IDE.OUTPUT.clickOnAppLinkWithParticalText("http://");
      IDE.DEBUGER.waitOpenedSomeWin();

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

      //step send on demo app, return from demo app to IDE window and set breakpoint
      // check setting breackpoint is correctly
      typeAndSendOnDemeApp(lnk);
      IDE.DEBUGER.waitInVariableTabContainerWithSpecidiedValue("selenium");
      IDE.DEBUGER.clickOnValueInVariableTabContainer("selenium");
      isDebugerButtonsAllBtnActive();
      IDE.DEBUGER.clickOnChangeValueBtn();
      isDebugerButtonsAllBtnActive();
      IDE.DEBUGER.waitChangeVariableWindow();
      IDE.DEBUGER.clearChangVariableTexField();
      IDE.DEBUGER.typeToChangeVariableField("\"webdriver\"");
      IDE.DEBUGER.clickChangeVariableBtnOnVariableWindow();
      IDE.DEBUGER.waitChangeVariableWindowClose();
      IDE.DEBUGER.waitInVariableTabContainerWithSpecidiedValue("webdriver");
      IDE.DEBUGER.clickResumeBtnClick();
      new WebDriverWait(debuger_app_instance, 80).until(ExpectedConditions.visibilityOfElementLocated(By
         .xpath("//span[text()='" + "Hello, webdriver!" + "']")));
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

   /**
    * wait field and button in demo application
    */
   private void waitMainElementsDemoApp()
   {
      new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            WebElement submitForm = driver.findElement(By.name(USER));
            WebElement okBtn = driver.findElement(By.name(SUBMIT));
            return submitForm.isDisplayed() && okBtn.isDisplayed();
         }
      });
   }

   /**
    * wait field and button in demo application
    */
   private void waitTextOnDemoPage(final String demoTxt)
   {
      new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return driver.findElement(By.xpath(("//span[text()='" + demoTxt + "']"))).isDisplayed();
         }
      });
   }

}
