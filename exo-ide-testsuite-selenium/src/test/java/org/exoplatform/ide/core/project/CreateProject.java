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
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Nov 4, 2011 12:34:31 PM anya $
 *
 */
public class CreateProject extends AbstractTestModule
{
   interface Locators
   {
      String VIEW_ID = "org.exoplatform.ide.client.project.create.CreateProjectForm";

      String VIEW_LOCATOR = "//div[@view-id='org.exoplatform.ide.client.project.create.CreateProjectForm']";

      String PROJECT_NAME_ID = "CreateProjectFormProjectName";

      String PROJECT_TYPE_ID = "CreateProjectFormProjectType";

      String CREATE_BUTTON_ID = "CreateProjectFormCreateButton";

      String CANCEL_BUTTON_ID = "CreateProjectFormCancelButton";

   }

   @FindBy(how = How.XPATH, using = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(name = Locators.PROJECT_NAME_ID)
   private WebElement projectNameField;

   @FindBy(name = Locators.PROJECT_TYPE_ID)
   private WebElement projectTypeField;

   @FindBy(id = Locators.CREATE_BUTTON_ID)
   private WebElement createButton;

   @FindBy(id = Locators.CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   /**
    * Wait view is opened.
    * 
    * @throws InterruptedException
    */
   public void waitOpened() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return view != null && view.isDisplayed();
            }
            catch (Exception e)
            {
               e.printStackTrace();
               return false;
            }
         }
      });
   }

   /**
    * Wait view is closed.
    * 
    * @throws InterruptedException
    */
   public void waitClosed() throws InterruptedException
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return false;
            }
            catch (Exception e)
            {
               e.printStackTrace();
               return true;
            }
         }
      });
   }

   /**
    * Click create button.
    */
   public void clickCreateButton()
   {
      createButton.click();
   }

   /**
    * Click cancel button.
    */
   public void clickCancelButton()
   {
      cancelButton.click();
   }

   /**
    * Set project's name (type to input).
    * 
    * @param name project's name
    * @throws InterruptedException 
    */
   public void setProjectName(String name) throws InterruptedException
   {
      IDE().INPUT.typeToElement(projectNameField, name, true);
   }

   /**
    * Create default project with pointed name.
    * 
    * @param name project's name
    * @throws Exception
    */
   public void createProject(String name) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.NEW, MenuCommands.Project.EMPTY_PROJECT);
      waitOpened();
      setProjectName(name);
      clickCreateButton();
      waitClosed();
   }
}
