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

import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Class for operations with classpath form (for configuring classpath of project).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Project.java May 12, 2011 12:35:39 PM vereshchaka $
 *
 */
public class Classpath extends AbstractTestModule
{

   private static final String CLASSPATH_VIEW_ID = "//div[@view-id='ideConfigureBuildPathForm']";

   private static final String LIST_GRID_ID = "ideClassPathEntryListGrid";

   private static final String SAVE_BUTTON_ID = "ideConfigureBuildPathFormSaveButton";

   private static final String CANCEL_BUTTON_ID = "ideConfigureBuildPathFormCancelButton";

   private static final String ADD_BUTTON_ID = "ideConfigureBuildPathFormAddButton";

   private static final String REMOVE_BUTTON_ID = "ideConfigureBuildPathFormRemoveButton";

   private static final String CLASSPATH_LIST_LINE = "tbody//td/div/span[contains(text(), '%s')]";

   @FindBy(id = SAVE_BUTTON_ID)
   private WebElement saveButton;

   @FindBy(id = CANCEL_BUTTON_ID)
   private WebElement cancelButton;

   @FindBy(id = ADD_BUTTON_ID)
   private WebElement addButton;

   @FindBy(id = REMOVE_BUTTON_ID)
   private WebElement removeButton;

   @FindBy(id = LIST_GRID_ID)
   private WebElement listGrid;

   @FindBy(xpath = CLASSPATH_VIEW_ID)
   private WebElement classpathForm;

   public boolean isSaveButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(saveButton);
   }

   public boolean isCancelButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(cancelButton);
   }

   public boolean isAddButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(addButton);
   }

   public void clickAddButton()
   {
      addButton.click();
   }

   public void clickCancelButton()
   {
      cancelButton.click();
   }

   public void clickRemoveButton()
   {
      removeButton.click();
   }
   
   public void clickSaveButton()
   {
      saveButton.click();
   }

   public boolean isRemoveButtonEnabled()
   {
      return IDE().BUTTON.isButtonEnabled(removeButton);
   }

   public void waitOpened()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return classpathForm != null && classpathForm.isDisplayed();
         }
      });
   }

   public void waitClosed()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(CLASSPATH_VIEW_ID));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public void waitPathRemoved(final String path)
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return listGrid.findElement(By.xpath(String.format(CLASSPATH_LIST_LINE, path))) == null;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   public void selectPath(String path)
   {
      listGrid.findElement(By.xpath(String.format(CLASSPATH_LIST_LINE, path))).click();
   }

   public boolean isPathPresent(String path)
   {
      try
      {
         return listGrid.findElement(By.xpath(String.format(CLASSPATH_LIST_LINE, path))) != null;
      }
      catch (NoSuchElementException e)
      {
         return false;
      }
   }
}
