/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.thoughtworks.selenium.Selenium;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id:   ${date} ${time}
 *
 */
@RunWith(RCRunner.class)
public abstract class BaseTest
{
   public static final ResourceBundle IDE_SETTINGS = ResourceBundle.getBundle("conf/ide-selenium");

   public static final String SELENIUM_HOST = IDE_SETTINGS.getString("selenium.host");

   public static final String SELENIUM_PORT = IDE_SETTINGS.getString("selenium.port");

   public static final String GIT_PATH = IDE_SETTINGS.getString("git.location");

   /**
    * Default workspace.
    */
   public static final String WS_NAME = IDE_SETTINGS.getString("ide.ws.name");

   /**
    * Second workspace. Needed in some tests.
    */
   protected static final String WS_NAME_2 = IDE_SETTINGS.getString("ide.ws.name2");

   public static String IDE_HOST = IDE_SETTINGS.getString("ide.host");

   public static final int IDE_PORT = Integer.valueOf(IDE_SETTINGS.getString("ide.port"));

   public static String BASE_URL = "https://" + IDE_HOST + ((IDE_PORT == 80) ? ("") : (":" + IDE_PORT)) + "/";

   public static final String USER_NAME = IDE_SETTINGS.getString("ide.user.root.name");

   public static final String USER_PASSWORD = IDE_SETTINGS.getString("ide.user.root.password");

   public static final String NOT_ROOT_USER_NAME = IDE_SETTINGS.getString("ide.user.dev.name");

   public static final String NOT_ROOT_USER_PASSWORD = IDE_SETTINGS.getString("ide.user.dev.password");

   protected static String APPLICATION_URL = BASE_URL + IDE_SETTINGS.getString("ide.app.url");

   protected static String LOGIN_URL = "https://" + IDE_HOST + ((IDE_PORT == 80) ? ("") : (":" + IDE_PORT)) + "/";

   protected static String STANDALONE_LOGIN_URL = BASE_URL + IDE_SETTINGS.getString("ide.login.url");;

   public static final String REST_CONTEXT = IDE_SETTINGS.getString("ide.rest.context");

   public static final String REPO_NAME = IDE_SETTINGS.getString("ide.repository.name");

   public static final String WEBDAV_CONTEXT = IDE_SETTINGS.getString("ide.webdav.context");

   public static String REST_CONTEXT_IDE = IDE_SETTINGS.getString("ide.rest.contenxt.ide");

   public static String ENTRY_POINT_URL_IDE = BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME
      + "/";

   public static String WS_URL_IDE = ENTRY_POINT_URL_IDE + WS_NAME + "/";

   public static String ENTRY_POINT_URL = BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/";

   //for restore default values in IDE
   public final static String PRODUCTION_SERVICE_PREFIX = "production/ide-home/users/" + USER_NAME
      + "/settings/userSettings";

   public static Selenium selenium;

   /**
    * Default workspace URL.
    */
   public static final String WS_URL = ENTRY_POINT_URL + WS_NAME + "/" /*+ USER_NAME + "/"*/;

   protected static final String REGISTER_IN_PORTAL = BASE_URL + "portal/private";

   protected static final EnumBrowserCommand BROWSER_COMMAND = EnumBrowserCommand.valueOf(IDE_SETTINGS
      .getString("selenium.browser.commad"));

   protected static final String LINE_NUMBERS_COOKIE = "eXo-IDE-" + USER_NAME + "-line-numbers_bool";

   public static Selenium selenium()
   {
      return selenium;
   }

   /**
    * URL of default workspace in IDE.
    */
   protected static final String WORKSPACE_URL = ENTRY_POINT_URL + WS_NAME + "/";

   private static int maxRunTestsOnOneSession = 5;

   private static int testsCounter = 0;

   private static boolean beforeClass = false;

   public static String REST_WORKSPACE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/";

   public static String IDE_WORKSPACE_URL = BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/";

   protected static WebDriver driver;

   public static IDE IDE;

   @Before
   public void start() throws Exception
   {
      if (beforeClass)
         return;

      beforeClass = true;

      //Choose browser Web driver:
      switch (BROWSER_COMMAND)
      {
         case GOOGLE_CHROME :

            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability("chrome.switches", Arrays.asList("--start-maximized"));
            driver = new ChromeDriver(capabilities);

            // driver = new ChromeDriver(options);

            break;
         case IE_EXPLORE_PROXY :
            driver = new InternetExplorerDriver();
            break;
         default :
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
      }

      selenium = new WebDriverBackedSelenium(driver, APPLICATION_URL);

      IDE = new IDE(selenium(), ENTRY_POINT_URL + WS_NAME + "/", driver);
      try
      {

         if (IDE_SETTINGS.getString("selenium.browser.commad").equals("CHROME"))
         {
            driver.manage().window().maximize();
         }
         driver.get(APPLICATION_URL);
         waitIdeLoginPage();
         if (isRunIdeAsStandalone())
         {
            IDE.LOGIN.waitStandaloneLogin();
            IDE.LOGIN.standaloneLogin(USER_NAME, USER_PASSWORD);
         }
         else if (isRunIdeAsTenant())
         {
            IDE.LOGIN.waitTenantAllLoginPage();
            IDE.LOGIN.tenantLogin(USER_NAME, USER_PASSWORD);
         }
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void stopSelenium()
   {
      beforeClass = false;
   }

   /**
    * Read file content.
    * 
    * @param file to read
    * @return String file content
    */
   protected String getFileContent(String filePath)
   {
      File file = new File(filePath);
      StringBuilder content = new StringBuilder();

      try
      {
         BufferedReader input = new BufferedReader(new FileReader(file));
         try
         {
            String line = null;

            while ((line = input.readLine()) != null)
            {
               content.append(line);
               content.append('\n');
            }
         }
         finally
         {
            input.close();
         }
      }
      catch (IOException e)
      {
         assertTrue(false);
      }

      return content.toString();
   }

   /**
    * wait load login page
    * @throws Exception
    */
   public void waitIdeLoginPage() throws Exception
   {

      if (IDE_SETTINGS.getString("ide.port").equals("8080"))
      {
         IDE.LOGIN.waitStandaloneLoginPage();
      }
      else
      {
         IDE.LOGIN.waitTenantLoginPage();
      }

   }

   @AfterClass
   public static void killFireFox()
   {

      if (IDE.POPUP.isAlertPresent())
      {
         IDE.POPUP.acceptAlert();
      }
      driver.quit();

      if (IDE.POPUP.isAlertPresent())
      {
         IDE.POPUP.acceptAlert();
      }

   }

   protected boolean isRunIdeAsTenant()
   {
      return !isRunIdeAsStandalone() && !isRunIdeAsShell();
   }

   protected boolean isRunIdeAsStandalone()
   {
      return IDE_HOST.contains("localhost");
   }

   protected boolean isRunIdeAsShell()
   {
      return APPLICATION_URL.contains("IDE/Shell");
   }

   @AfterFailure
   public void captureScreenShotOnFailure(Throwable failure) throws IOException
   {
      // Get test method name
      String testMethodName = null;
      for (StackTraceElement stackTrace : failure.getStackTrace())
      {
         if (stackTrace.getClassName().equals(this.getClass().getName()))
         {
            testMethodName = stackTrace.getMethodName();
            break;
         }
      }

      byte[] sc = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
      File parent = new File("target/screenshots");
      parent.mkdirs();
      File file = new File(parent, this.getClass().getName() + "." + testMethodName + ".png");
      try
      {
         file.createNewFile();
      }
      catch (IOException e)
      {
         throw new IOException("I/O Error: Can't create screenshot file :" + file.toString());
      }
      FileOutputStream outputStream = new FileOutputStream(file);
      try
      {
         outputStream.write(sc);
      }
      // Closing opened file
      finally
      {
         try
         {
            //need to check for null
            if (outputStream != null)
            {
               outputStream.close();
            }
         }
         catch (Exception e)
         {
            throw new IOException("I/O Error: Can't write screenshot to file :" + file.toString());
         }
      }
   }

}