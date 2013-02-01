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
package org.exoplatform.ide.core;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildSuccessedTest.java Feb 28, 2012 2:16:57 PM azatsarynnyy $
 *
 */
public class Build extends AbstractTestModule
{
   public interface Messages
   {
      String BUILDING_PROJECT = "Building project";

      String BUILD_SUCCESS = "Project successfully built.";

      String BUILD_FAILED = "Building of project failed. See details in Build project view.";

      String BUILD_IN_PROGRESS = "You can not start the build of two or more projects at the same time";

      String URL_TO_ARTIFACT = "You can download your artifact :http://";

      String DEPENDENCY_MESS = "Dependency for your pom:";
   }

   private interface Locators
   {
      String VIEW_ID = "ide.builder.build.view";

      String VIEW_LOCATOR = "//div[@view-id='" + VIEW_ID + "']";

      String OUTPUT_PANEL_ID = "ide.builder.buildOutput";

      String CLEAR_BUTTON_SELECTOR = "div[view-id='" + VIEW_ID + "'] div[title='Clear Output']>img";

      String LINK_ON_BUILT_RESULT = "//pre[@id='ide.builder.buildOutput']//a[text()='here']";

      String BUILDER_OUTPUT_TAB = "//div[@id='operation']//td[@class='tabTitleText' and text()='Build project']";

      String OPERATION_FORM = "//div[@id='operation']/ancestor::div[contains(@style, 'height: 300')]";
   }

   @FindBy(xpath = Locators.VIEW_LOCATOR)
   private WebElement view;

   @FindBy(xpath = Locators.LINK_ON_BUILT_RESULT)
   private WebElement buildResultLink;

   @FindBy(id = Locators.OUTPUT_PANEL_ID)
   private WebElement outputPanel;

   @FindBy(css = Locators.CLEAR_BUTTON_SELECTOR)
   private WebElement clearButton;

   @FindBy(xpath = Locators.BUILDER_OUTPUT_TAB)
   private WebElement builderOutputTab;

   @FindBy(xpath = Locators.OPERATION_FORM)
   private WebElement operationForm;

   /**
    * Wait build project view opened.
    * 
    * @throws Exception
    */
   public void waitOpened() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return view != null && view.isDisplayed();
         }
      });
   }

   /**
    * Wait build output panel project opened.
    * 
    * @throws Exception
    */
   public void waitBuildResultLink() throws Exception
   {
      new WebDriverWait(driver(), 120).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return buildResultLink != null && buildResultLink.isDisplayed();
         }
      });
   }

   /**
    * Wait build output panel project opened.
    * 
    * @throws Exception
    */
   public void waitBuildOutputPanel() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            return outputPanel != null && outputPanel.isDisplayed() && operationForm.isDisplayed()
               && operationForm != null;
         }
      });
   }

   /**
    * Wait build project view closed.
    * 
    * @throws Exception
    */
   public void waitClosed() throws Exception
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            try
            {
               return view == null || !view.isDisplayed();
            }
            catch (NoSuchElementException e)
            {
               return true;
            }
         }
      });
   }

   /**
    * Check is build project view opened.
    */
   public boolean isOpened()
   {
      try
      {
         return view != null && view.isDisplayed();
      }
      catch (Exception e)
      {
         return false;
      }
   }

   /**
    * Get output message.
    * 
    * @return {@link String} text of the message
    */
   public String getOutputMessage()
   {
      return outputPanel.getText();
   }

   /**
    * Click clear output button.
    */
   public void clickClearButton() throws Exception
   {
      clearButton.click();
   }

   /**
    * wait for message in builder output
    */
   public void waitBuilderMessage(final String mess)
   {
      new WebDriverWait(driver(), 30).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver input)
         {
            String message = outputPanel.getText();
            return message.equals(mess);
         }
      });
   }

   /**
    * select builder output tab
    */
   public void selectBuilderOutputTab()
   {
      builderOutputTab.click();
   }
}
