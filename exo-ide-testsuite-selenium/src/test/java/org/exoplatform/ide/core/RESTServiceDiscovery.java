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

import org.exoplatform.ide.Utils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: RESTServiceDiscovery.java Jan 4, 2012 3:23:09 PM vereshchaka $
 *
 */
public class RESTServiceDiscovery extends AbstractTestModule
{

   private static final String REST_SERVICE_DISCOVERY_VIEW = "//div[@view-id='ideResrServicesDiscoveryView']";

   private static final String OK_BUTTON_ID = "exoRestServicesDiscoveryOkButton";

   private static final String OPEN_CLOSE_BUTTON_LOCATOR = "//div[@id='%s']/table/tbody/tr/td[1]/img";
   
   private static final String METHOD_PATH_FIELD_NAME = "ideMethodPathField";
   
   private static final String REQUEST_PATH_FIELD_NAME = "ideRequestType";
   
   private static final String PARAMETERS_TABLE_ID = "ideRestServiceDiscoveryParameters";

   @FindBy(xpath = REST_SERVICE_DISCOVERY_VIEW)
   private WebElement restServiceDiscovery;

   @FindBy(id = OK_BUTTON_ID)
   private WebElement okButton;
   
   @FindBy(name = METHOD_PATH_FIELD_NAME)
   private WebElement methodField;
   
   @FindBy(name = REQUEST_PATH_FIELD_NAME)
   private WebElement requestField;
   
   @FindBy(id = PARAMETERS_TABLE_ID)
   private WebElement parametersTable;

   
   public boolean isMethodFieldPresent()
   {
      return methodField != null && methodField.isDisplayed();
   }
   
   public boolean isRequestFieldPresent()
   {
      return requestField != null && requestField.isDisplayed();
   }
   
   public boolean isParametersTablePresent()
   {
      return parametersTable != null && parametersTable.isDisplayed();
   }
   
   public String getTextFromMethodField()
   {
      return IDE().INPUT.getValue(methodField);
   }
   
   public String getTextFromRequestField()
   {
      return IDE().INPUT.getValue(requestField);
   }
   
   public boolean isRequestFieldEnabled()
   {
      return requestField.isEnabled();
   }
   
   /**
    * Wait appearance REST Service Form
   * 
   */
   public void waitOpened()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return restServiceDiscovery != null && restServiceDiscovery.isDisplayed();
         }
      });
   }

   public void selectItem(String path) throws Exception
   {
      WebElement item = driver().findElement(By.id(getItemId(path)));
      new Actions(driver()).moveToElement(item, 1, 1).click().perform();
   }

   public void selectItemById(String id) throws Exception
   {
      WebElement item = driver().findElement(By.id(id));
      new Actions(driver()).moveToElement(item, 1, 1).click().perform();
   }

   /**
    * Click open/close(+/-) button of the pointed item.
    * 
    * @param path item's path
    * @throws Exception
    */
   public void clickOpenCloseButton(String path) throws Exception
   {
      WebElement button = driver().findElement(By.xpath(String.format(OPEN_CLOSE_BUTTON_LOCATOR, getItemId(path))));
      button.click();
   }

   public void clickOpenCloseButtonById(String id) throws Exception
   {
      WebElement button = driver().findElement(By.xpath(String.format(OPEN_CLOSE_BUTTON_LOCATOR, id)));
      button.click();
   }

   /**
    * Generate item id 
    * @param path item's name 
    * @return id of item
    */
   public String getItemId(String path) throws Exception
   {
      String itemId = (path.startsWith("/")) ? path : "/" + path;
      itemId = Utils.md5old(itemId);
      return itemId;
   }

   public void waitOkButtonAppeared()
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            return okButton != null && okButton.isDisplayed();
         }
      });
   }

   public void clickOkButton()
   {
      okButton.click();
   }

   /**
    * 
    * @param path
    * @throws Exception
    */
   public void waitForItem(final String path) throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement item = driver().findElement(By.id(getItemId(path)));
               return item != null && item.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   public void waitForItemById(final String id) throws Exception
   {
      new WebDriverWait(driver(), 5).until(new ExpectedCondition<Boolean>()
      {

         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               WebElement item = driver().findElement(By.id(id));
               return item != null && item.isDisplayed();
            }
            catch (Exception e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Wait disappearance REST Service Form
   *  @throws InterruptedException
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
               input.findElement(By.xpath(REST_SERVICE_DISCOVERY_VIEW));
               return false;
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

}
