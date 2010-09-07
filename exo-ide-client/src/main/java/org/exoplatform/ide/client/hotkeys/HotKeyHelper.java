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
package org.exoplatform.ide.client.hotkeys;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class HotKeyHelper
{
   static final Map<String, String> KEY_CODES = new HashMap<String, String>();
   
   static 
   {
      /*
       * Keys, that doesn't present in table
       * Symbolic       Value(dec)  Mouse or keyboard equivalents
       * constant name
       * ---------------------------------
       * VK_LBUTTON     01          Left mouse button
       * VK_RBUTTON     02          Right mouse button
       * VK_CANCEL      03          Control-break processing
       * VK_MBUTTON     04          Middle mouse button (three-button mouse)
       * VK_XBUTTON1    05          Windows 2000/XP/2003/Vista/2008/7: X1 mouse button
       * VK_XBUTTON2    06          Windows 2000/XP/2003/Vista/2008/7: X2 mouse button
       * -              07          Undefined
       * -              10-11       Reserved
       * VK_CLEAR       12          CLEAR key
       * -              14-15       Undefined
       * -              58-64       Undefined
       * -              136-143       Undefined
       * -              124-135     F13-F14
       * -              151-153,156   Undefined
       * -              158-159       Undefined       
       */
      KEY_CODES.put("8", "Backspace");
      KEY_CODES.put("9", "Tab");
      KEY_CODES.put("13", "Enter");
      KEY_CODES.put("16", "Shift");
      KEY_CODES.put("17", "Ctrl");
      KEY_CODES.put("18", "Alt");
      KEY_CODES.put("19", "Pause");
      KEY_CODES.put("20", "CapsLock");
      KEY_CODES.put("27", "Esc");
      KEY_CODES.put("32", "Space"); 
      KEY_CODES.put("33", "PageUp");
      KEY_CODES.put("34", "PageDown");
      KEY_CODES.put("35", "End");
      KEY_CODES.put("36", "Home");
      
      KEY_CODES.put("37", "Left");
      KEY_CODES.put("38", "Up");
      KEY_CODES.put("39", "Right");
      KEY_CODES.put("40", "Down");
      KEY_CODES.put("41", "Select");
      KEY_CODES.put("42", "Print");
      KEY_CODES.put("43", "Execute");
      KEY_CODES.put("44", "Print screen"); //PRINT SCREEN key for Windows 3.0 and later
      KEY_CODES.put("45", "Insert");
      KEY_CODES.put("46", "Delete");
      KEY_CODES.put("47", "Help");
      
      KEY_CODES.put("48", "0");
      KEY_CODES.put("49", "1");
      KEY_CODES.put("50", "2");
      KEY_CODES.put("51", "3");
      KEY_CODES.put("52", "4");
      KEY_CODES.put("53", "5");
      KEY_CODES.put("54", "6");
      KEY_CODES.put("55", "7");
      KEY_CODES.put("56", "8");
      KEY_CODES.put("57", "9");
      
      
      KEY_CODES.put("65", "A");
      KEY_CODES.put("66", "B");
      KEY_CODES.put("67", "C");
      KEY_CODES.put("68", "D");
      KEY_CODES.put("69", "E");
      KEY_CODES.put("70", "F");
      KEY_CODES.put("71", "G");
      KEY_CODES.put("72", "H");
      KEY_CODES.put("73", "I");
      KEY_CODES.put("74", "J");
      KEY_CODES.put("75", "K");
      KEY_CODES.put("76", "L");
      KEY_CODES.put("77", "M");
      KEY_CODES.put("78", "N");
      KEY_CODES.put("79", "O");
      KEY_CODES.put("80", "P");
      KEY_CODES.put("81", "Q");
      KEY_CODES.put("82", "R");
      KEY_CODES.put("83", "S");
      KEY_CODES.put("84", "T");
      KEY_CODES.put("85", "U");
      KEY_CODES.put("86", "V");
      KEY_CODES.put("87", "W");
      KEY_CODES.put("88", "X");
      KEY_CODES.put("89", "Y");
      KEY_CODES.put("90", "Z");
      KEY_CODES.put("91", "LeftWin");
      KEY_CODES.put("92", "RightWin");
      KEY_CODES.put("93", "Menu");
      KEY_CODES.put("96", "Numpad 0");
      KEY_CODES.put("97", "Numpad 1");
      KEY_CODES.put("98", "Numpad 2");
      KEY_CODES.put("99", "Numpad 3");
      KEY_CODES.put("100", "Numpad 4");
      KEY_CODES.put("101", "Numpad 5");
      KEY_CODES.put("102", "Numpad 6");
      KEY_CODES.put("103", "Numpad 7");
      KEY_CODES.put("104", "Numpad 8");
      KEY_CODES.put("105", "Numpad 9");
      KEY_CODES.put("106", "Numpad *");
      KEY_CODES.put("107", "Numpad +");
      KEY_CODES.put("109", "Numpad -");
      KEY_CODES.put("110", "Numpad .");
      KEY_CODES.put("111", "Numpad /");
      KEY_CODES.put("112", "F1");
      KEY_CODES.put("113", "F2");
      KEY_CODES.put("114", "F3");
      KEY_CODES.put("115", "F4");
      KEY_CODES.put("116", "F5");
      KEY_CODES.put("117", "F6");
      KEY_CODES.put("118", "F7");
      KEY_CODES.put("119", "F8");
      KEY_CODES.put("120", "F9");
      KEY_CODES.put("121", "F10");
      KEY_CODES.put("122", "F11");
      KEY_CODES.put("123", "F12");
      
      KEY_CODES.put("144", "NumLock");
      KEY_CODES.put("145", "ScrollLock");
      
      KEY_CODES.put("154", "PrintScreen");
      KEY_CODES.put("157", "Meta");
      
      KEY_CODES.put("186", ";");
      KEY_CODES.put("187", "=");
      KEY_CODES.put("188", ",");
      KEY_CODES.put("189", "-");
      KEY_CODES.put("190", ".");
      KEY_CODES.put("191", "/");
      KEY_CODES.put("192", "~");
      KEY_CODES.put("219", "[");
      KEY_CODES.put("220", "\\");
      KEY_CODES.put("221", "]");
      KEY_CODES.put("222", "'");
   }
   
   /**
    * For example, converts from "Ctrl+65" to "Ctrl+A"
    * 
    * @param codeCombination - combination of hotkey, wich consists of Ctrl or Alt
    * and of code of key
    * @return {@link String}
    */
   public static String convertToStringCombination(String codeCombination)
   {
      if (codeCombination == null || codeCombination.length() < 1)
         return "";
      
      if (!codeCombination.contains("+") || codeCombination.endsWith("+"))
         return codeCombination;
      
      String controlKey = codeCombination.substring(0, codeCombination.indexOf("+"));
      String keyCode = codeCombination.substring(codeCombination.indexOf("+") + 1, codeCombination.length());
      
      String charKey = (KEY_CODES.get(keyCode) != null) ? KEY_CODES.get(keyCode) : null;
      
      if (charKey == null)
         throw new IllegalArgumentException("Can't find " + codeCombination + " code in keycodes map");
      
      return controlKey + "+" + charKey;
   }
   
   /**
    * Return string for key code.
    * 
    * @param keyCode
    * @return {@link String}
    */
   public static String getKeyName(String keyCode)
   {
      return KEY_CODES.get(keyCode);
   }
   
   /**
    * Find in keycodes map value of keyString and return the key code.
    * 
    * @param keyName
    * @return {@link String}
    */
   public static String getKeyCode(String keyName)
   {
      Iterator<Entry<String, String>> it = KEY_CODES.entrySet().iterator();
      while (it.hasNext())
      {
         Entry<String, String> entry = it.next();
         if (entry.getValue().equals(keyName))
         {
            return entry.getKey();
         }
      }
      return null;
   }
   
   /**
    * For example, converts from "Ctrl+A" to "Ctrl+65".
    * 
    * @param stringCombination - combination of hotkey, than consists of Ctrl or Alt
    * and of key name
    * @return {@link String}
    */
   public static String convertToCodeCombination(String stringCombination)
   {
      String controlKey = stringCombination.substring(0, stringCombination.indexOf("+"));
      String keyString = stringCombination.substring(stringCombination.indexOf("+") + 1, stringCombination.length());
      String keyInt = getKeyCode(keyString);
      return controlKey + "+" + keyInt;
   }
}
