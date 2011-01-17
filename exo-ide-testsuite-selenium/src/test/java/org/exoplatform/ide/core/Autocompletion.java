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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 17, 2011 2:27:36 PM vereshchaka $
 *
 */
public class Autocompletion
{
   public interface Locators
   {
      /**
       * XPath autocompletion panel locator.
       */
      public static final String PANEL = "//table[@class='exo-autocomplete-panel']";
      
      /**
       * Xpath autocompletion input locator.
       */
      public static final String INPUT = "//input[@class='exo-autocomplete-edit']";
   }
   
   private static final Selenium selenium;
   
   static
   {
      selenium = BaseTest.selenium;
   }
   
   /**
    * Type text to input field of autocompletion form.
    * 
    * @param text - text to type
    * @throws Exception
    */
   public static void typeToInput(String text) throws Exception
   {
      selenium.typeKeys(Autocompletion.Locators.INPUT, text);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Check, that element <code>elementTitle</code> is present in autocomplete panel.
    *  
    * @param elementTitle - the title of element
    */
   public static void checkElementPresent(String elementTitle)
   {
      assertTrue(selenium.isElementPresent(Locators.PANEL + "//div[text()='" + elementTitle + "']"));
   }

}
