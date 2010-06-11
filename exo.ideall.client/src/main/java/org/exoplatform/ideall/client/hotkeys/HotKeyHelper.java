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
package org.exoplatform.ideall.client.hotkeys;

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
   static final Map<String, String> keycodes = new HashMap<String, String>();
   
   static 
   {
      keycodes.put("8", "Backspace");
      keycodes.put("9", "Tab");
      keycodes.put("13", "Enter");
      keycodes.put("16", "Shift");
      keycodes.put("19", "Pause");
      keycodes.put("20", "CapsLock");
      keycodes.put("27", "Esc");
      keycodes.put("32", "Space"); 
      keycodes.put("33", "PageUp");
      keycodes.put("34", "PageDown");
      keycodes.put("35", "End");
      keycodes.put("36", "Home");
      
      keycodes.put("37", "Left");
      keycodes.put("38", "Up");
      keycodes.put("39", "Right");
      keycodes.put("40", "Down");
      keycodes.put("45", "Insert");
      keycodes.put("46", "Delete");
      
      keycodes.put("48", "0");
      keycodes.put("49", "1");
      keycodes.put("50", "2");
      keycodes.put("51", "3");
      keycodes.put("52", "4");
      keycodes.put("53", "5");
      keycodes.put("54", "6");
      keycodes.put("55", "7");
      keycodes.put("56", "8");
      keycodes.put("57", "9");
      
      
      keycodes.put("65", "A");
      keycodes.put("66", "B");
      keycodes.put("67", "C");
      keycodes.put("68", "D");
      keycodes.put("69", "E");
      keycodes.put("70", "F");
      keycodes.put("71", "G");
      keycodes.put("72", "H");
      keycodes.put("73", "I");
      keycodes.put("74", "J");
      keycodes.put("75", "K");
      keycodes.put("76", "L");
      keycodes.put("77", "M");
      keycodes.put("78", "N");
      keycodes.put("79", "O");
      keycodes.put("80", "P");
      keycodes.put("81", "Q");
      keycodes.put("82", "R");
      keycodes.put("83", "S");
      keycodes.put("84", "T");
      keycodes.put("85", "U");
      keycodes.put("86", "V");
      keycodes.put("87", "W");
      keycodes.put("88", "X");
      keycodes.put("89", "Y");
      keycodes.put("90", "Z");
      keycodes.put("91", "LeftWin");
      keycodes.put("92", "RightWin");
      keycodes.put("93", "Menu");
      keycodes.put("96", "Numpad 0");
      keycodes.put("97", "Numpad 1");
      keycodes.put("98", "Numpad 2");
      keycodes.put("99", "Numpad 3");
      keycodes.put("100", "Numpad 4");
      keycodes.put("101", "Numpad 5");
      keycodes.put("102", "Numpad 6");
      keycodes.put("103", "Numpad 7");
      keycodes.put("104", "Numpad 8");
      keycodes.put("105", "Numpad 9");
      keycodes.put("106", "Numpad *");
      keycodes.put("107", "Numpad +");
      keycodes.put("109", "Numpad -");
      keycodes.put("110", "Numpad .");
      keycodes.put("111", "Numpad /");
      keycodes.put("112", "F1");
      keycodes.put("113", "F2");
      keycodes.put("114", "F3");
      keycodes.put("115", "F4");
      keycodes.put("116", "F5");
      keycodes.put("117", "F6");
      keycodes.put("118", "F7");
      keycodes.put("119", "F8");
      keycodes.put("120", "F9");
      keycodes.put("121", "F10");
      keycodes.put("122", "F11");
      keycodes.put("123", "F12");
      
      keycodes.put("144", "NumLock");
      keycodes.put("145", "ScrollLock");
      
      keycodes.put("154", "PrintScreen");
      keycodes.put("157", "Meta");
      
      keycodes.put("186", ";");
      keycodes.put("187", "=");
      keycodes.put("188", ",");
      keycodes.put("189", "-");
      keycodes.put("190", ".");
      keycodes.put("191", "/");
      keycodes.put("192", "~");
      keycodes.put("219", "[");
      keycodes.put("220", "\\");
      keycodes.put("221", "]");
      keycodes.put("222", "'");
   }
   
   /**
    * For example, converts from "Ctrl+65" to "Ctrl+A"
    * 
    * @param hotKey
    * @return {@String}
    */
   public static String convertCodeHotKeyToStringHotKey(String hotKey)
   {
      if (hotKey == null || hotKey.length() < 1)
         return "";
      
      if (! hotKey.contains("+") || hotKey.endsWith("+"))
         return hotKey;
      
      String controlKey = hotKey.substring(0, hotKey.indexOf("+"));
      String keyCode = hotKey.substring(hotKey.indexOf("+") + 1, hotKey.length());
      
      String charKey = (keycodes.get(keyCode) != null) ? keycodes.get(keyCode) : null;
      
      if (charKey == null)
         throw new IllegalArgumentException("Can't find " + hotKey + " code in keycodes map");
      
      return controlKey + "+" + charKey;
   }
   
   /**
    * Return string for key code.
    * 
    * @param keyCode
    * @return {@String}
    */
   public static String convertKeyCodeToKeySymbol(String keyCode)
   {
      return keycodes.get(keyCode);
   }
   
   /**
    * Find in keycodes map value of keyString and return the key code.
    * 
    * @param keyString
    * @return
    */
   public static String convertStringSymbolToKeyCode(String keyString)
   {
      Iterator<Entry<String, String>> it = keycodes.entrySet().iterator();
      while (it.hasNext())
      {
         Entry<String, String> entry = it.next();
         if (entry.getValue().equals(keyString))
         {
            return entry.getKey();
         }
      }
      System.out.println("No such value in keycodes map");
      return null;
   }
   
   /**
    * For example, converts from "Ctrl+A" to "Ctrl+65".
    * 
    * @param hotKey
    * @return {@String}
    */
   public static String convertStringHotKeyToCodeHotKey(String hotKey)
   {
      String controlKey = hotKey.substring(0, hotKey.indexOf("+"));
      String keyString = hotKey.substring(hotKey.indexOf("+") + 1, hotKey.length());
      String keyInt = convertStringSymbolToKeyCode(keyString);
      return controlKey + "+" + keyInt;
   }
}
