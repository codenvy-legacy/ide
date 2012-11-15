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

import org.exoplatform.ide.client.framework.websocket.messages.RESTfulResponseMessage;
import org.exoplatform.ide.client.framework.websocket.messages.Unmarshallable;

import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: StringListUnmarshaller.java Oct 19, 2012 9:24:42 AM azatsarynnyy $
 *
 */
public class StringListUnmarshaller implements Unmarshallable<List<String>>
{
   private List<String> appList;

   public StringListUnmarshaller(List<String> list)
   {
      this.appList = list;
   }

   /**
    * @see org.exoplatform.ide.client.framework.websocket.messages.Unmarshallable#unmarshal(org.exoplatform.ide.client.framework.websocket.messages.RESTfulResponseMessage)
    */
   public void unmarshal(RESTfulResponseMessage response)
   {
      JSONArray jsonArray = JSONParser.parseStrict(response.getBody()).isArray();
      if (jsonArray == null)
      {
         return;
      }

      for (int i = 0; i < jsonArray.size(); i++)
      {
         JSONString appName = jsonArray.get(i).isString();
         appList.add(appName.stringValue());
      }
   }

   @Override
   public List<String> getPayload()
   {
      return appList;
   }

}
