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
public abstract class AbstractTextUtil
{
   private static AbstractTextUtil instance;

   protected static Selenium selenium;

   public static AbstractTextUtil getInstance()
   {
      return instance;
   }

   protected AbstractTextUtil(Selenium selenium)
   {
      instance = this;
      this.selenium = selenium;
   }

   /**
    * @param locator
    * @param text
    * @param refresh
    * @throws Exception
    */
   public void typeToInput(String locator, String text, boolean clear) throws Exception
   {
      selenium.focus(locator);
      selenium.click(locator);
      
      if (clear)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
         selenium.type(locator, "");
      }

      typeTextToInput(locator, text);
   }

   public void typeTextToEditor(String locator, String text, boolean clear) throws Exception
   {
      if (clear)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);
      }
      typeTextToEditor(locator, text);
   }

   abstract public void typeTextToEditor(String locator, String text) throws Exception;

   abstract public void typeTextToInput(String locator, String text) throws Exception;
}
