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
package org.exoplatform.ideall.client.util;

import java.util.HashMap;

import com.google.gwt.user.client.Window;


/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class SimpleParser
{
   private static String FUNCTION = "function";

   public static HashMap<String, Integer> parse(String text)
   {
      HashMap<String, Integer> results = new HashMap<String, Integer>();

      String[] lines = text.split("\n");
      Window.alert("Lines count : " + lines.length);
      if (lines.length > 0)
      {
         for (int i = 0; i < lines.length; i++)
         {
            String line = lines[i].toLowerCase();
            int index = line.indexOf(FUNCTION);
            if (index >= 0)
            {
               String tempStr = line.substring(index + FUNCTION.length()).trim();
               int index2 = tempStr.indexOf("(");
               String name = (index2 >= 0) ? tempStr.substring(0, index2) : "";
               if (name.length() > 0)
               {
                  Window.alert(name.trim() + " at  line " + (i + 1));
                  results.put(name.trim(), (i + 1));
               }
            }
         }
      }
      return results;
   }
}

