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
package org.exoplatform.ide.shell;

import org.exoplatform.ide.shell.core.Shell;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;

import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 26, 2011 12:48:04 PM evgen $
 *
 */
public abstract class BaseTest
{
   @Rule
   public static final TestRule rule = new ShellTestWatcher();
   
   public static final ResourceBundle IDE_SETTINGS = ResourceBundle.getBundle("conf/shell-selenium");

   public static final String REST_CONTEXT = IDE_SETTINGS.getString("ide.rest.context");

   public static WebDriver driver;

   protected static final BrowserCommand BROWSER_COMMAND = BrowserCommand.valueOf(IDE_SETTINGS
      .getString("selenium.browser.commad"));
   
   public static final String USER_NAME = IDE_SETTINGS.getString("ide.user.root.name");

   public static final String USER_PASSWORD = IDE_SETTINGS.getString("ide.user.root.password");

   public static String IDE_HOST = IDE_SETTINGS.getString("ide.host");

   public static final int IDE_PORT = Integer.valueOf(IDE_SETTINGS.getString("ide.port"));

   public static String BASE_URL = "http://" +USER_NAME + ":" + USER_PASSWORD +"@" + IDE_HOST + ":" + IDE_PORT + "/";

   protected static String APPLICATION_URL = BASE_URL + IDE_SETTINGS.getString("ide.app.url");

   protected Shell shell;

   @BeforeClass
   public static void startWebDriver()
   {
      switch (BROWSER_COMMAND)
      {
         case GOOGLE_CHROME :
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
            
            driver = new ChromeDriver(capabilities);
            
            break;
         case IE :
            driver = new InternetExplorerDriver();
            break;
         default :
            driver = new FirefoxDriver();
      }

      driver.get(APPLICATION_URL);

   }

   @AfterClass
   public static void stopWebDriver()
   {
      driver.close();
      driver.quit();
   }

   @Before
   public void createCore()
   {
      driver.navigate().refresh();
      shell = PageFactory.initElements(driver, Shell.class);
   }

}
