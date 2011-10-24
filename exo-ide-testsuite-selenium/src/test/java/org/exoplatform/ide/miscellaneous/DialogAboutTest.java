package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.fail;

import org.exoplatform.ide.IDE;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.awt.event.KeyEvent;

import com.thoughtworks.selenium.Selenium;

import junit.framework.TestCase;

@RunWith(BlockJUnit4ClassRunner.class)
public class DialogAboutTest extends TestCase
{

   private static ChromeDriverService service;

   private static WebDriver driver;
   
   static Selenium selenium;
   
   private static final String COMPANY = "2009-2011 eXo Platform SAS (c)";

   private static final String PRODUCT_NAME = "eXo IDE";


   @BeforeClass
   public static void start()
   {
      System.setProperty("webdriver.chrome.driver", "/home/vetal/eXo/chromedriver");
      driver = new FirefoxDriver(); 
               //new ChromeDriver();
//      driver.get("http://www.google.com");
      selenium = new WebDriverBackedSelenium(driver, "http://localhost:8080/site/index.html");
      selenium.open("http://localhost:8080/site/index.html");
      selenium.waitForPageToLoad("20000");
   }
   
   Selenium selenium() {
      return selenium;
   }

   @Test
   public void testGoogleSearch() throws Exception
   {
      waitForElementPresent("//input[@name='j_username']");
      selenium.type("//input[@name='j_username']", "exo");
      selenium.type("//input[@name='j_password']", "exo");
      selenium.click("//input[@value='Log In']");
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      
      
      Thread.sleep(TestConstants.SLEEP);
      IDE ide = new IDE(selenium, "http://localhost:8080/IDE/rest/private/jcr/repository/dev-monit"); 
      ide.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.ABOUT);
//    //      Thread.sleep(TestConstants.SLEEP);
//    //check About form
     assertTrue(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]"));
    assertTrue(selenium().isElementPresent("ideAboutViewOkButton"));
    assertTrue(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]//td//img[@class=\"gwt-Image\"]"));
    assertTrue(selenium().isTextPresent(PRODUCT_NAME));
    assertTrue(selenium().isTextPresent(COMPANY));
    assertTrue(selenium().isTextPresent("Revision:"));
    assertTrue(selenium().isTextPresent("Build Time:"));
//
//    //click Ok button
    selenium().click("ideAboutViewOkButton");
    waitForElementNotPresent("ideAboutViewOkButton");
//
//    //check About form dissapeared
    assertFalse(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]"));
    assertFalse(selenium().isElementPresent("ideAboutFormOkButton"));
    assertFalse(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]//td//img[@class=\"gwt-Image\"]"));
    assertFalse(selenium().isTextPresent("About"));
  //  assertFalse(selenium().isTextPresent(PRODUCT_NAME));
    //      assertFalse(selenium().isTextPresent("Version: 1.0-Beta03-SNAPSHOT"));
    assertFalse(selenium().isTextPresent(COMPANY));
    assertFalse(selenium().isTextPresent("Revision:"));
    assertFalse(selenium().isTextPresent("Build Time:"));

//      selenium.type("q", "webdriver");
////               (By.name("q"));
//      Thread.sleep(1000);
////      selenium.click("btnG");
//
//      selenium.keyPress("q", "\\13");
//      selenium.keyUp("q", "\\13");                                                                                                                                                                 
//      Thread.sleep(10000);
//
//      assertEquals("webdriver - Пошук Google", driver.getTitle());
   }

   public void waitForElementPresent(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();

      while (true)
      {
         if (selenium.isElementPresent(locator))
         {
            break;
         }

         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("timeout for element " + locator);
         }

         Thread.sleep(1);
      }
   }
   
   
   public void waitForElementNotPresent(String locator) throws Exception
   {
      for (int second = 0;; second++)
      {
         if (second >= 60)
            fail("timeout");

         try
         {
            if (!selenium().isElementPresent("locator"))
               break;
         }

         catch (Exception e)
         {
            fail("timeout for element " + locator);
         }

         Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      }
   }
   
   @After
   public void quitDriver()
   {
      driver.quit();
   }
}

///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.miscellaneous;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.exoplatform.ide.BaseTest;
//import org.exoplatform.ide.MenuCommands;
//import org.exoplatform.ide.TestConstants;
//import org.junit.Test;
//
///**
// * Created by The eXo Platform SAS.
// * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
// * @version $Id:
// *
// */
//public class DialogAboutTest extends BaseTest
//{
//
//  
//   @Test
//   public void testDialogAbout() throws Exception
//   {
//      //TODO: you can change information in About window
//      //as you see fit
//      Thread.sleep(TestConstants.SLEEP);
//      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.ABOUT);
//      //      Thread.sleep(TestConstants.SLEEP);
//      //check About form
//      assertTrue(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]"));
//      assertTrue(selenium().isElementPresent("ideAboutViewOkButton"));
//      assertTrue(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]//td//img[@class=\"gwt-Image\"]"));
//      assertTrue(selenium().isTextPresent(PRODUCT_NAME));
//      assertTrue(selenium().isTextPresent(COMPANY));
//      assertTrue(selenium().isTextPresent("Revision:"));
//      assertTrue(selenium().isTextPresent("Build Time:"));
//
//      //click Ok button
//      selenium().click("ideAboutViewOkButton");
//      waitForElementNotPresent("ideAboutViewOkButton");
//
//      //check About form dissapeared
//      assertFalse(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]"));
//      assertFalse(selenium().isElementPresent("ideAboutFormOkButton"));
//      assertFalse(selenium().isElementPresent("//div[@view-id=\"ideAboutView\"]//td//img[@class=\"gwt-Image\"]"));
//      assertFalse(selenium().isTextPresent("About"));
//    //  assertFalse(selenium().isTextPresent(PRODUCT_NAME));
//      //      assertFalse(selenium().isTextPresent("Version: 1.0-Beta03-SNAPSHOT"));
//      assertFalse(selenium().isTextPresent(COMPANY));
//      assertFalse(selenium().isTextPresent("Revision:"));
//      assertFalse(selenium().isTextPresent("Build Time:"));
//   }
//}
