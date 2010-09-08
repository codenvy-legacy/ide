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

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 *
 */
public class WebKitUtil extends AbstractTextUtil
{

   /**
    * @param selenium
    */
   public WebKitUtil(Selenium selenium)
   {
      super(selenium);
   }

   /**
    * @see org.exoplatform.ide.utils.AbstractTextUtil#typeTextToEditor(java.lang.String)
    */
   @Override
   public void typeTextToEditor(String locator, String text) throws Exception
   {
    //  selenium.typeKeys(locator, text);
      for (int i = 0; i < text.length(); i++)
      {
         char symbol = text.charAt(i);
         if (Character.isLetterOrDigit(symbol))
         {
            KeyStroke key = KeyStroke.getKeyStroke("pressed " + Character.toUpperCase(symbol));
            if (null != key)
            {
               // should only have to worry about case with standard characters
               if (Character.isUpperCase(symbol))
               {
                  selenium.keyDownNative("" + KeyEvent.VK_SHIFT);
               }
               selenium.keyPressNative("" + key.getKeyCode());
               if (Character.isUpperCase(symbol))
               {
                  selenium.keyUpNative("" + KeyEvent.VK_SHIFT);
               }
            }
         }
         
         //Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * @see org.exoplatform.ide.utils.AbstractTextUtil#typeTextToInput(java.lang.String, java.lang.String)
    */
   @Override
   public void typeTextToInput(String locator, String text) throws Exception
   {
      selenium.type(locator, text);
   }

   /*private int getKeyCode(char symbol)
   {
      if (symbol == ' ')
      {
         return KeyEvent.VK_SPACE;
      }
      else if (symbol == '.')
      {
         return KeyEvent.VK_PERIOD;
      }
      else if (symbol == '!')
      {
         return KeyEvent.VK_EXCLAMATION_MARK;
      }
      else if (symbol == '{')
      {
         return KeyEvent.VK_BRACELEFT;
      }
      else if (symbol == '}')
      {
         return KeyEvent.VK_BRACERIGHT;
      }
      else if (symbol == '(')
      {
         return KeyEvent.VK_LEFT_PARENTHESIS;
      }
      else if (symbol == ')')
      {
         return KeyEvent.VK_RIGHT_PARENTHESIS;
      }
      else if (symbol == '[')
      {
         return KeyEvent.VK_OPEN_BRACKET;
      }
      else if (symbol == ']')
      {
         return KeyEvent.VK_CLOSE_BRACKET;
      }

      else if (symbol == '@')
      {
         return KeyEvent.VK_AT;
      }
       else if (symbol == '%')
       {
          return KeyEvent.VK_;
       }
      else if (symbol == '<')
      {
         return KeyEvent.VK_LESS;
      }
      else if (symbol == '>')
      {
         return KeyEvent.VK_GREATER;
      }
      return 0;
   }*/
}
