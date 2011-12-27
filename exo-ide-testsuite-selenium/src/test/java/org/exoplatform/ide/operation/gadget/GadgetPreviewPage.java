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
package org.exoplatform.ide.operation.gadget;

import org.exoplatform.ide.core.AbstractTestModule;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page "Calculator" in gadget preview test.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CalculatorPreview.java Dec 26, 2011 11:45:18 AM vereshchaka $
 */
public class GadgetPreviewPage extends AbstractTestModule
{
   
   private static final String CALCULATOR = "//div[@class='CenterCalculator']";
   
   private static final String DISPLAY = "//div[@class='Display']";
   
   private static final String NUMBER = "//div[@class='Number']";
   
   @FindBy(xpath = CALCULATOR)
   private WebElement calculator;
   
   @FindBy(xpath = DISPLAY)
   private WebElement display;
   
   @FindBy(xpath = NUMBER)
   private WebElement number;
   
   public boolean calculatorPresent()
   {
      try
      {
         return (calculator != null && calculator.isDisplayed());
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
   public boolean displayPresent()
   {
      try
      {
         return (display != null && display.isDisplayed());
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
   public boolean numberPresent()
   {
      try
      {
         return (number != null && number.isDisplayed());
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
}


