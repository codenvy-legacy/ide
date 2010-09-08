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
package org.exoplatform.ide;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: 2010
 *
 */
public enum EnumBrowserCommand {
   FIREFOX("*firefox"),
   MOCK("*mock"),
   FIREFOX_PROXY("*firefoxproxy"),
   PIFIREFOX("*pifirefox"),
   CHROME("*chrome"),
   IE_EXPLORE_PROXY("*iexploreproxy"),
   IE_EXPLORE("*iexplore"),
   FIREFOX_3("*firefox3"),
   FIREFOX_2("*firefox2"),
   SAFARI_PROXY("*safariproxy"), 
   GOOGLE_CHROME("*googlechrome"),
   SAFARI("*safari"),
   PIEXPLORE("*piiexplore"),
   FIREFOX_CHROME("*firefoxchrome"),
   OPERA("*opera"),
   IEHTA("*iehta"),
   CUSTOM("*custom");
   
   /**
    * Value. 
    */
   private final String value;

   /**
    * @param v value
    */
   EnumBrowserCommand(String v)
   {
      value = v;
   }

   /**
    * @return String
    */
   public String value()
   {
      return value;
   }

   /**
    * @param v value
    * @return EnumBaseObjectTypeIds
    */ 
   public static EnumBrowserCommand fromValue(String v)
   {
      for (EnumBrowserCommand c : EnumBrowserCommand.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }
}  
