/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.ssh.client.marshaller;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeysUnmarshaller May 18, 2011 4:57:42 PM evgen $
 *
 * Changed made for use this marshaler in JsonpRequest.
 * We use it for cross-domain Ajax calls
 */
public class SshKeysUnmarshaller
{

   public static List<KeyItem> unmarshal(JavaScriptObject response) throws UnmarshallerException
   {
      try
      {
         List<KeyItem> keyItems = new ArrayList<KeyItem>();
         JSONArray array = new JSONArray(response);
         for (int i = 0; i < array.size(); i++)
         {
            JSONObject object = array.get(i).isObject();
            KeyItem keyItem =
               new KeyItem(object.get("host").isString().stringValue(), object.get("publicKeyURL").isString()
                  .stringValue(), object.get("removeKeyURL").isString().stringValue());
            keyItems.add(keyItem);
         }
         return keyItems;
      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse Ssh Keys");
      }
   }

}
