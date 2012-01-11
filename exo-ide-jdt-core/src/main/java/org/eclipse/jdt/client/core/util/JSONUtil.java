/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client.core.util;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 10, 2012 5:15:22 PM evgen $
 *
 */
public class JSONUtil
{
   /**
    * @param json
    * @return
    */
   public static long[] parseJsonAsLongArray(String json)
   {
      JSONValue value = JSONParser.parseLenient(json);
      if (value.isArray() == null)
         throw new IllegalArgumentException("'json' parameter must represent a JSON array");
      return jsonArrayToLongArray(value.isArray());
   }

   /**
    * @param array
    * @return
    */
   public static long[] jsonArrayToLongArray(JSONArray array)
   {
      long result[] = new long[array.size()];
      for (int i = 0; i < array.size(); i++)
      {
         result[i] = (long)array.get(i).isNumber().doubleValue();
      }
      return result;
   }
}
