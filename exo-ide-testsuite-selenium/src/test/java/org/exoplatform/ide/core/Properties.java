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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Properties May 12, 2011 12:15:53 PM evgen $
 *
 */
public class Properties extends AbstractTestModule
{
   interface Locators
   {
      String PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyname='%1s']";

      String PROPERTIES_FORM_LOCATOR = "//div[@view-id='ideFilePropertiesView']";

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

   @FindBy(how = How.XPATH, using = Locators.PROPERTIES_FORM_LOCATOR)
   private WebElement propertiesView;

   /*Properties*/
   @FindBy(how = How.XPATH, using = Locators.NAME_PROPERTY_LOCATOR)
   private WebElement nameProperty;

   @FindBy(how = How.XPATH, using = Locators.PATH_PROPERTY_LOCATOR)
   private WebElement pathProperty;

   @FindBy(how = How.XPATH, using = Locators.MIME_TYPE_PROPERTY_LOCATOR)
   private WebElement mimeTypeProperty;

   @FindBy(how = How.XPATH, using = Locators.CREATED_PROPERTY_LOCATOR)
   private WebElement createdProperty;

   @FindBy(how = How.XPATH, using = Locators.LAST_MODIFIED_PROPERTY_LOCATOR)
   private WebElement lastModifiedProperty;

   @FindBy(how = How.XPATH, using = Locators.CONTENT_LENGHT_PROPERTY_LOCATOR)
   private WebElement contentLenghtProperty;

   @FindBy(how = How.XPATH, using = Locators.CLOSE_VIEW_BUTTON_LOCATOR)
   private WebElement closeViewButton;

   /**
    * Get Autoload property value
    * @return String value
    */
   @Deprecated
   public String getAutoloadProperty()
   {
      //TODO No such property
      return null;
   }

   /**
    * Get Content Node Type property value 
    * @return String value
    */
   @Deprecated
   public String getContentNodeType()
   {
      //TODO No such property
      return null;
   }

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
    * Get File Node Type property value
    * @return String value
    */
   @Deprecated
   public String getFileNodeType()
   {
      //TODO No such property
      return null;
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
            return (propertiesView != null && propertiesView.isDisplayed() && contentLenghtProperty != null && contentLenghtProperty
               .isDisplayed());
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
            return (propertiesView == null);
         }
      });
   }
}
