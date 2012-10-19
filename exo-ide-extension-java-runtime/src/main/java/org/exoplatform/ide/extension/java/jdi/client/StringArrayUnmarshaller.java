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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: StringArrayUnmarshaller.java Oct 19, 2012 9:24:42 AM azatsarynnyy $
 *
 */
public class StringArrayUnmarshaller
{
   private String payload;

   public StringArrayUnmarshaller(String payload)
   {
      this.payload = payload;
   }

   public String[] unmarshal()
   {
      JSONArray jsonArray = JSONParser.parseStrict(payload).isArray();
      if (jsonArray == null)
      {
         return new String[0];
      }

      String[] apps = new String[jsonArray.size()];
      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONString appName = jsonArray.get(i).isString();
         apps[i] = appName.stringValue();
      }

      return apps;
   }

}
