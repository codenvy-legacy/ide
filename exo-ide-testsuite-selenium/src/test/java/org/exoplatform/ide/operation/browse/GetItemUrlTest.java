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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.gwt.editor.client.Editor.Ignore;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class GetItemUrlTest extends BaseTest
{

   private final String content1 = "<p> Hello!!! </p>";

   private final String searchPhrase = "Hello";

   private final String file1Name = "gadget.txt";

   private final String folderName = "myFolder";

   private static final String PROJECT = GetItemUrlTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
         fail("Can't create test project");
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Ignore
   @Test
   public void testGetFilePrivateUrl() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.txt");

      assertEquals("", IDE.EDITOR.getTextFromCodeEditor(0));
      IDE.EDITOR.typeTextIntoEditor(0, content1);
      IDE.EDITOR.saveAs(1, file1Name);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + file1Name);
      IDE.EDITOR.closeFile(file1Name);
      IDE.EDITOR.waitTabNotPresent(file1Name);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.FOLDER.createFolder(folderName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folderName);

      // Test project item:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL));

      // Test folder:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + folderName);
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL));

      // Test file:
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + file1Name);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL));

      String privateUrl = IDE.GET_URL.getPrivateURL();

      driver.navigate().to(privateUrl);

      new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement hello = driver.findElement(By.xpath("//body[contains(.,'<p> Hello!!! </p>')]"));
               return hello != null && hello.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

      driver.navigate().back();
      IDE.PROJECT.EXPLORER.waitOpened();
   }

  // @Test
   public void testGetFileUrl() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + file1Name);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.View.VIEW, MenuCommands.View.GET_URL));

      String publicUrl = IDE.GET_URL.getPublicURL();

      driver.navigate().to(publicUrl);

      new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement hello = driver.findElement(By.xpath("//body[contains(.,'<p> Hello!!! </p>')]"));
               return hello != null && hello.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

      driver.navigate().back();
      IDE.PROJECT.EXPLORER.waitOpened();
   }

   /**
    * @throws Exception
    */
  // @Test
   public void testGetFilePrivateUrlWithSearch() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.SEARCH.performSearch("/" + PROJECT, searchPhrase, "");
      IDE.SEARCH_RESULT.waitOpened();
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + file1Name);
      IDE.SEARCH_RESULT.selectItem(PROJECT + "/" + file1Name);

      // Test private URL
      String privateUrl = IDE.GET_URL.getPrivateURL();

      driver.navigate().to(privateUrl);

      new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement hello = driver.findElement(By.xpath("//body[contains(.,'<p> Hello!!! </p>')]"));
               return hello != null && hello.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

      driver.navigate().back();
      IDE.PROJECT.EXPLORER.waitOpened();
   }

   /**
    * @throws Exception
    */
  // @Test
   public void testGetFilePublicUrlWithSearch() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.SEARCH.performSearch("/" + PROJECT, searchPhrase, "");
      IDE.SEARCH_RESULT.waitOpened();
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + file1Name);
      IDE.SEARCH_RESULT.selectItem(PROJECT + "/" + file1Name);

      // Test public URL
      String publicUrl = IDE.GET_URL.getPublicURL();

      driver.navigate().to(publicUrl);

      new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               WebElement hello = driver.findElement(By.xpath("//body[contains(.,'<p> Hello!!! </p>')]"));
               return hello != null && hello.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return false;
            }
         }
      });

      driver.navigate().back();
      IDE.PROJECT.EXPLORER.waitOpened();
   }
}
