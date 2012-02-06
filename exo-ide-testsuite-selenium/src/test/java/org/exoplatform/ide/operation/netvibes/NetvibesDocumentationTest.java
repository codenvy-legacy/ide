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
package org.exoplatform.ide.operation.netvibes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.file.CreatingAndSavingAsNewFileTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

/**
 * Test for netvibes documentation frame.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetVibesDocumentationTest Jan 24, 2011 2:25:35 PM evgen $
 *
 */
public class NetvibesDocumentationTest extends BaseTest
{

   private static final String PROJECT = NetvibesDocumentationTest.class.getSimpleName();
   /**
    *  Locator for documentation iframe
    */
   private static final String IDE_DOCUMENTATION_FRAME = "//iframe[@id='gwt-debug-ideDocumentationFrame']";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private static String FILE_NAME = "netvibes.html";
   
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);

      }
      catch (Exception e)
      {
         fail("Cant create project ");
      }
   }

   @Test
   public void testNetvibesDocumentation() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      IDE.EDITOR.waitTabPresent(1);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.View.SHOW_DOCUMENTATION, true);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_DOCUMENTATION);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      IDE.EDITOR.waitTabPresent(2);
      waitForCloseDocTab();
      assertTrue(driver.findElements(By.xpath(IDE_DOCUMENTATION_FRAME)).isEmpty());
      IDE.EDITOR.selectTab(1);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      assertEquals(1,driver.findElements(By.xpath(IDE_DOCUMENTATION_FRAME)).size());

      
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS);
      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.setValue(FILE_NAME);
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitClosed();
      
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      IDE.EDITOR.selectTab(2);
      waitForCloseDocTab();
      assertFalse(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

      refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      waitForElementPresent(IDE_DOCUMENTATION_FRAME);
      
      assertTrue(selenium().isElementPresent(IDE_DOCUMENTATION_FRAME));

   }

   private void waitForCloseDocTab()
   {
      new WebDriverWait(driver, 5).until(new ExpectedCondition<Boolean>()
         {

            @Override
            public Boolean apply(WebDriver input)
            {
               try
               {
                  input.findElement(By.xpath(IDE_DOCUMENTATION_FRAME));
                  return false;
               }
               catch (NoSuchElementException e)
               {
                  return true;
               }
            }
         });
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

}
