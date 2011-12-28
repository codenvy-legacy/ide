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
package org.exoplatform.ide.shell;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 * <br />Command "*iexplore" had been commented because of Selenium doesn't perform selenium.type(), selenium.typeKeys() and selenium.keyPressNative() methods properly in the CodeMirror and TextItems of DynamicForms. Use IE_EXPLORE_PROXY("*iexploreproxy") instead.
 */
public enum BrowserCommand {
   FIREFOX("*firefox"), GOOGLE_CHROME("*googlechrome"), SAFARI("*safari"), IE("*ie");

   /**
    * Value. 
    */
   private final String value;

   /**
    * @param v value
    */
   BrowserCommand(String v)
   {
      value = v;
   }

   /**
    * @return String
    */
   @Override
   public String toString()
   {
      return value;
   }

   /**
    * @param v value
    * @return EnumBaseObjectTypeIds
    */
   public static BrowserCommand fromValue(String v)
   {
      for (BrowserCommand c : BrowserCommand.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }
}
