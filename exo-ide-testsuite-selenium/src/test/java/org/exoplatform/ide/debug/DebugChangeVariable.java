package org.exoplatform.ide.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.extension.maven.client.BuildSuccessedTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.fest.assertions.AssertExtension;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Map;

public class DebugChangeVariable extends BaseTest
{

   private static final String PROJECT = DebugChangeVariable.class.getSimpleName();

   protected static Map<String, Link> project;

   private static final String USER = "user";

   private static final String SUBMIT = "submit";

   private static final String DEBUG_TAB = "//td[@class='tabTitleText' and text()='Debug']";

   private static final String USERNAME_LABEL = "//div[@class='gwt-TabLayoutPanelContent']//span[text()='userName']";

   private static final String USERNAME_LOCATOR =
      "//div[@class='gwt-TabLayoutPanelContent']//span[text()=' : null']";

   @Before
   public void before()
   {

      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/debug/change-variable-proj.zip");
         Thread.sleep(2000);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown() throws IOException, InterruptedException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      Thread.sleep(1000);
   }

   @Test
   public void changeVariableTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();

      // Open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEBUG_APPLICATION);
      IDE.BUILD.waitOpened();
      String builderMessage = IDE.BUILD.getOutputMessage();
      assertTrue(builderMessage.startsWith(Build.Messages.BUILDING_PROJECT));
      IDE.DEBUGER.waitOpened();
      openGreetingController();
      IDE.DEBUGER.setBrackPoint(20);

      IDE.OUTPUT.clickOnOutputTab();

      String currentWin = driver.getWindowHandle();
//      System.out.println("***********CURRENT!!!************" + currentWin);
      driver.findElement(By.partialLinkText("exoplatform.com")).click();
      Thread.sleep(2000);
//      for (String handle : driver.getWindowHandles())
//      {
//         if (currentWin.equals(handle))
//         {
//            System.out.println("***********NONE-SWITCHED************" + handle);
//         }
//         else
//         {
//            System.out.println("***********I SWITCHED TO THIS************" + handle);
//            driver.switchTo().window(handle);
//            break;
//         }
//      }
//      waitTestApp();
//      driver.findElement(By.name(USER)).sendKeys("max");
//      driver.findElement(By.name(SUBMIT)).click();
//      Thread.sleep(1000);
//    //  new Actions(driver).sendKeys(Keys.CONTROL + "w").build().perform();
//      Thread.sleep(1000);

      driver.switchTo().window(currentWin);
      waitDebugTab();
      driver.findElement(By.xpath(DEBUG_TAB)).click();
      IDE.DEBUGER.waitOpened();
      Thread.sleep(5000);

      WebElement usrName = driver.findElement(By.xpath(USERNAME_LOCATOR));
      usrName.click();
      IDE.DEBUGER.changeVarBtnClick();
      IDE.DEBUGER.waitShangeWarField();
      IDE.DEBUGER.typeToChangeVariableField("userName=\"eXo\"");
      IDE.DEBUGER.confirmChangeBtnClick();
      Thread.sleep(2000);
      IDE.DEBUGER.resumeBtnClick();
      Thread.sleep(2000);
//
//      IDE.OUTPUT.clickOnOutputTab();
//      driver.findElement(By.partialLinkText("exoplatform.com")).click();

      switchOnDemoAppWin(currentWin);
      waitTestApp();
      assertEquals("Hello, eXo!", driver.findElement(By.xpath("//div[2]/span")).getText());
   }

   private void switchOnDemoAppWin(String currentWin)
   {
      for (String handle : driver.getWindowHandles())
      {
         if (currentWin.equals(handle))
         {
            System.out.println("**NONE-SWITCHED***" + handle);
         }
         else
         {
           
            driver.switchTo().window(handle);
            break;
         }
      }
   }

   private void openGreetingController() throws Exception
   {
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void waitTestApp()
   {
      new WebDriverWait(driver, 20).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement submit = driver.findElement(By.name(SUBMIT));
               return submit != null && submit.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

   public void waitDebugTab()
   {
      new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement debug = driver.findElement(By.xpath(DEBUG_TAB));
               return debug != null && debug.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });
   }

}
