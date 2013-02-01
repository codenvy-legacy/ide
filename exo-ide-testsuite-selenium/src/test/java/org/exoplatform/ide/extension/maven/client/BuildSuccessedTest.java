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
package org.exoplatform.ide.extension.maven.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildSuccessedTest.java Feb 27, 2012 6:36:37 PM azatsarynnyy $
 *
 */
public class BuildSuccessedTest extends BaseTest
{
   private static final String PROJECT = BuildSuccessedTest.class.getSimpleName();

   private static final String DEPENDENCY =
      "\n<dependency>\n  <groupId>cloud-ide</groupId>\n  <artifactId>spring-demo</artifactId>\n"
         + "  <version>1.0</version>\n  <type>war</type>\n</dependency>";

   private static String CURRENT_WINDOW = null;

   private WebDriverWait wait;

   protected static Map<String, Link> project;

   @BeforeClass
   public static void before()
   {
      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/extension/maven/TestSpringProjectWithPOM.zip");
         //need for full complete import zip folder in DavFs
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void after()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         //need for full deleting zip folder in DavFs
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testBuild() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();

      // Open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.waitPackageExplorerOpened();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.LOADER.waitClosed();
      // Building of project is started
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_PROJECT);
      IDE.BUILD.waitOpened();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      String builderMessage = IDE.BUILD.getOutputMessage();
      assertTrue(builderMessage.startsWith(Build.Messages.BUILDING_PROJECT));

      // Wait until building is finished.
      IDE.STATUSBAR.waitDiasspearBuildStatus();
      IDE.LOADER.waitClosed();
      // check clear output button
      IDE.BUILD.clickClearButton();

      String emptyMessage = IDE.BUILD.getOutputMessage();
      assertEquals("", emptyMessage);

      // Close Build project view because Output view is not visible
      driver.findElement(By.xpath("//div[@class='gwt-TabLayoutPanelTabs']//div[@tab-title='Build project']")).click();

      // Get success message
      IDE.OUTPUT.waitForMessageShow(1, 15);
      String buildSuccessMessage = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(buildSuccessMessage.endsWith(Build.Messages.BUILD_SUCCESS));

      IDE.OUTPUT.clickClearButton();

   }

   @Test
   public void testBuildAndPublish() throws Exception
   {
      CURRENT_WINDOW = driver.getWindowHandle();
      wait = new WebDriverWait(driver, 15);

      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.BUILD_AND_PUBLISH_PROJECT);
      IDE.BUILD.waitOpened();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.OUTPUT.waitForMessageShow(1, 60);

      IDE.BUILD.selectBuilderOutputTab();
      IDE.BUILD.waitBuilderMessage("Building project " + PROJECT + "\nFinished building project " + PROJECT
         + ".\nResult: Successful");

      // Close Build project view because Output view is not visible
      driver.findElement(By.xpath("//div[@class='tabTitleCloseButton' and @tab-title='Build project']")).click();

      //cheking output tab
      IDE.OUTPUT.waitForMessageShow(1, 60);
      String buildAndPublishSuccessMessage = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(buildAndPublishSuccessMessage.endsWith(Build.Messages.BUILD_SUCCESS));
      String urlToArtifact = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(urlToArtifact.contains(Build.Messages.URL_TO_ARTIFACT));
      String dependency_mess = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(dependency_mess.contains(Build.Messages.DEPENDENCY_MESS));
      assertTrue(dependency_mess.contains(DEPENDENCY));

      IDE.OUTPUT.waitForMessageShow(3, 60);
      IDE.OUTPUT.clickOnAppLinkWithParticalText("http://");

      // switching to application window
      switchToRepositoryWindow(CURRENT_WINDOW);

      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tbody//a[text()='spring-demo-1.0.war']")));
   }

   /**
    * @param currentWin
    */
   private void switchToRepositoryWindow(String currentWin)
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
