/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.utils;

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 *
 */
public class TextUtil extends AbstractTextUtil
{

   /**
    * @param selenium
    */
   public TextUtil(Selenium selenium)
   {
      super(selenium);
   }

   /**
    * @see org.exoplatform.ide.utils.AbstractTextUtil#typeText(java.lang.String, java.lang.String)
    */
   @Override
   public void typeTextToInput(String locator, String text) throws Exception
   {
      typeText(locator, text);
   }

   /**
    * @see org.exoplatform.ide.utils.AbstractTextUtil#typeTextToEditor(java.lang.String)
    */
   @Override
   public void typeTextToEditor(String locator, String text) throws Exception
   {
      typeText(locator, text);
   }

   /**
    * @param locator
    * @param text
    * @throws Exception
    */
   private void typeText(String locator, String text) throws Exception
   {
      for (int i = 0; i < text.length(); i++)
      {
         char symbol = text.charAt(i);
         if (symbol == 'y')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
         }
         else if (symbol == '\n')
         {
            Thread.sleep(300);
            selenium.keyDown(locator, "\\13");
            selenium.keyUp(locator, "\\13");
            Thread.sleep(300);
         }
         else if (symbol == '.')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
         }
         else
         {
            selenium.typeKeys(locator, String.valueOf(symbol));
         }
         
         //Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
}
