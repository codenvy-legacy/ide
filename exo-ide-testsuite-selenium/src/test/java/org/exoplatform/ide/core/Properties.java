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
package org.exoplatform.ide.core;

import org.exoplatform.ide.ToolbarCommands;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Properties May 12, 2011 12:15:53 PM evgen $
 *
 */
public class Properties extends AbstractTestModule
{
   private interface Locators
   {
      String PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyname='%1s']";

      String VIEW_LOCATOR = "//div[@view-id='ideFilePropertiesView']";

      String NAME_PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyname='Name']";

      String PATH_PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyname='Path']";

      String MIME_TYPE_PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyname='Mime Type']";

      String CREATED_PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyname='Created']";

      String LAST_MODIFIED_PROPERTY_LOCATOR =
         "//div[@view-id='ideFilePropertiesView']//td[@propertyname='Last modified']";

      String CONTENT_LENGHT_PROPERTY_LOCATOR =
         "//div[@view-id='ideFilePropertiesView']//td[@propertyname='Content lenght']";

      String CLOSE_VIEW_BUTTON_LOCATOR = "//div[@button-name='close-tab' and @tab-title='Properties']";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   /*Properties*/
   @FindBy(xpath = Locators.NAME_PROPERTY_LOCATOR)
   private WebElement nameProperty;

   @FindBy(xpath = Locators.PATH_PROPERTY_LOCATOR)
   private WebElement pathProperty;

   @FindBy(xpath = Locators.MIME_TYPE_PROPERTY_LOCATOR)
   private WebElement mimeTypeProperty;

   @FindBy(xpath = Locators.CREATED_PROPERTY_LOCATOR)
   private WebElement createdProperty;

   @FindBy(xpath = Locators.LAST_MODIFIED_PROPERTY_LOCATOR)
   private WebElement lastModifiedProperty;

   @FindBy(xpath = Locators.CONTENT_LENGHT_PROPERTY_LOCATOR)
   private WebElement contentLenghtProperty;

   @FindBy(xpath = Locators.CLOSE_VIEW_BUTTON_LOCATOR)
   private WebElement closeViewButton;

   /**
    * Get Content Length property value.
    * @return String value
    */
   public String getContentLength()
   {
      return contentLenghtProperty.getText();
   }

   /**
    * Get Content Type property value
    * @return String value
    */
   public String getContentType()
   {
      return mimeTypeProperty.getText();
   }

   /**
    * Get Display Name property value
    * @return String value
    */
   public String getDisplayName()
   {
      return nameProperty.getText();
   }

   /**
    * Get the value of path property.
    * 
    * @return {@link String} path value
    */
   public String getPath()
   {
      return pathProperty.getText();
   }

   /**
    * Get the value of last modified property.
    * 
    * @return {@link String} last modified value
    */
   public String getLastModified()
   {
      return lastModifiedProperty.getText();
   }

   /**
    * Get the value of created property.
    * 
    * @return {@link String} created value
    */
   public String getCreated()
   {
      return createdProperty.getText();
   }

   /**
    * Close Properties View.
    */
   public void closeProperties() throws Exception
   {
      closeViewButton.click();
      waitClosed();
   }

   /**
    * Open properties view.
    * 
    * @throws Exception
    */
   public void openProperties() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.View.SHOW_PROPERTIES);
      waitOpened();
   }

   /**
    * Wait for properties view to be opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return (view != null && view.isDisplayed() && contentLenghtProperty != null
               && contentLenghtProperty.isDisplayed() && nameProperty != null && nameProperty.isDisplayed());
         }
      });
   }

   /**
    * Wait for properties view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 3).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               input.findElement(By.xpath(Locators.VIEW_LOCATOR));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Returns active state of properties view.
    * 
    * @return {@link Boolean} active state of properties view
    */
   public boolean isActive()
   {
      return IDE().PERSPECTIVE.isViewActive(view);
   }
}
