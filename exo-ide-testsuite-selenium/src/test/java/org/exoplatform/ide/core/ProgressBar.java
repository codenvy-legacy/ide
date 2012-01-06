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

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProgressBar.java Jan 5, 2012 5:41:09 PM vereshchaka $
 */
public class ProgressBar extends AbstractTestModule
{
   private static final String PROGRESS_BAR_CONTROL = "//div[@control-id='__request-notification-control']";

   private static final String PROGRESS_BAR_VIEW = "//div[@view-id='ideRequestNotificationView']";
   
   @FindBy(xpath = PROGRESS_BAR_CONTROL)
   private WebElement control;
   
   @FindBy(xpath = PROGRESS_BAR_VIEW)
   private WebElement view;
   
   public void waitProgressBarView()
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
         {
            @Override
            public Boolean apply(WebDriver input)
            {
               try
               {
                  return view != null && view.isDisplayed();
               }
               catch (NoSuchElementException e)
               {
                  return false;
               }
            }
         });
   }
   
   public void clickProgressBarControl()
   {
      control.click();
   }
   
   public void waitProgressBarControl()
   {
      new WebDriverWait(driver(), 2).until(new ExpectedCondition<Boolean>()
         {
            @Override
            public Boolean apply(WebDriver input)
            {
               try
               {
                  return control != null && control.isDisplayed();
               }
               catch (NoSuchElementException e)
               {
                  return false;
               }
            }
         });
   }
   
   public String getViewText()
   {
      return view.getText();
   }

}
