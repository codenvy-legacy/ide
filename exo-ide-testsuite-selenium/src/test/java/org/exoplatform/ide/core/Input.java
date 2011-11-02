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

import org.openqa.selenium.WebElement;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Nov 2, 2011 12:18:42 PM evgen $
 *
 */
public class Input extends AbstractTestModule
{
   /**
    * Type text to element, optional clear it. 
    * @param element WebElement MUST point or input or textarea Html element
    * @param text to type
    * @param isClear is clear element before typing
    */
   public void typeToElement(WebElement element, String text, boolean isClear)
   {
      if (isClear)
      {
         element.clear();
      }
      element.sendKeys(text);
   }

   /**
    *  Type text to element
    * @param element WebElement, MUST point or input or textarea Html element
    * @param text Text to type
    */
   public void typeToElement(WebElement element, String text)
   {
      typeToElement(element, text, false);
   }
}
