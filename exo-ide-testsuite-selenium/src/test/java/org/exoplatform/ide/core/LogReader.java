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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: LogReader.java Jan 3, 2012 10:58:37 AM vereshchaka $
 */
public class LogReader extends AbstractTestModule
{
   
   private static final String LOG_READER_VIEW = "//div[@view-id='ideExtensionLogReaderView']";
   
   private static final String LOG_READER_CONTENT = LOG_READER_VIEW + "//pre";
   
   private static final String PREV_BUTTON_LOCATOR = LOG_READER_VIEW + "//div[@title='Previous Log']";
   
   private static final String NEXT_BUTTON_LOCATOR = LOG_READER_VIEW + "//div[@title='Next Log']";
   
   private static final String ENABLED_ATTR = "enabled";
   
   @FindBy(xpath = LOG_READER_VIEW)
   private WebElement view;
   
   @FindBy(xpath = LOG_READER_CONTENT)
   private WebElement content;
   
   @FindBy(xpath = PREV_BUTTON_LOCATOR)
   private WebElement prevButton;
   
   @FindBy(xpath = NEXT_BUTTON_LOCATOR)
   private WebElement nextButton;
   
   public void waitOpened()
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
         {
            @Override
            public Boolean apply(WebDriver input)
            {
               return view != null && view.isDisplayed();
            }
         });
   }
   
   public boolean isPreviousButtonEnabled()
   {
      return isButtonEnabled(prevButton);
   }
   
   public boolean isNextButtonEnabled()
   {
      return isButtonEnabled(nextButton);
   }
   
   public void clickPrevButton()
   {
      prevButton.click();
   }
   
   public void clickNextButton()
   {
      nextButton.click();
   }
   
   public String getLogContent()
   {
      return content.getText();
   }
   
   private boolean isButtonEnabled(WebElement button)
   {
      return Boolean.parseBoolean(button.getAttribute(ENABLED_ATTR));
   }

}
