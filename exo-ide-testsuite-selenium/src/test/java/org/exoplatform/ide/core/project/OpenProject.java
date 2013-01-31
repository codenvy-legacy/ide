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
package org.exoplatform.ide.core.project;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 8, 2011 12:50:04 PM evgen $
 *
 */
public class OpenProject extends AbstractTestModule
{
   private static final String VIEW_LOCATOR = "//div[@view-id='ideShowProjectsView']";

   private static final String LIST_ID = "ideProjectsListGrid";

   private static final String PROJECT_LOCATOR = "//table[@id='ideProjectsListGrid']//tbody//td/div[text()='%s']";

   @FindBy(how = How.XPATH, using = VIEW_LOCATOR)
   private WebElement view;

   @FindBy(id = LIST_ID)
   private WebElement tree;

   @FindBy(id = "ideShowProjectsOpenButton")
   private WebElement openButton;

   @FindBy(id = "ideShowProjectsCancelButton")
   private WebElement cancelButton;

   /**
    * Wait view is opened.
    * 
    * @throws InterruptedException
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 30).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(VIEW_LOCATOR)));
   }

   public void openProject(String name) throws Exception
   {
      waitProject(name);
      IDE().MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.OPEN_PROJECT);
      waitOpened();
      IDE().LOADER.waitClosed();
      selectProjectName(name);
      clickOpenButton();
      waitClosed();
      IDE().LOADER.waitClosed();
   }

   public void waitProject(final String name)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver driver)
         {
            try
            {
               IDE().MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.OPEN_PROJECT);
               waitOpened();
               IDE().LOADER.waitClosed();
               WebElement prj = driver().findElement(By.xpath(String.format(PROJECT_LOCATOR, name)));
               return prj.isDisplayed() && prj != null;
            }
            catch (Exception e)
            {
               return false;
            }
            finally
            {
               cancelButton.click();
            }
         }
      });
   }

   public void openOtherProjectIfCurentProjectNotClosed(String name) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.OPEN_PROJECT);
      waitOpened();
      IDE().LOADER.waitClosed();
      selectProjectName(name);
      clickOpenButton();
      IDE().ASK_DIALOG.waitOpened();
      IDE().ASK_DIALOG.clickYes();
      waitClosed();
   }

   /**
    * 
    */
   public void waitClosed()
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return view != null;
            }
            catch (Exception e)
            {
               return true;
            }
         }
      });

   }

   /**
    * 
    */
   public void clickOpenButton()
   {
      openButton.click();
   }

   /**
    * @param name
    */
   public void selectProjectName(String name)
   {
      tree.findElement(By.xpath("tbody//td/div[text()='" + name + "']")).click();
   }
}
