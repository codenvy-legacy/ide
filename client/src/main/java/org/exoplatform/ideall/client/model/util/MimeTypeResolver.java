/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MimeTypeResolver
{

   private static final String DEFAULT_MIMETYPE = "application/octet-stream";

   private static HashMap<String, List<String>> mimeTypes;

   private static native JavaScriptObject getMimeTypesConfig() /*-{
      return $wnd.mimeTypes;
   }-*/;

   private static void loadMimeTypes()
   {
      mimeTypes = new HashMap<String, List<String>>();
      try
      {
         JSONObject json = new JSONObject(getMimeTypesConfig());

         Iterator<String> iterator = json.keySet().iterator();
         while (iterator.hasNext())
         {
            String key = iterator.next();

            JSONValue value = json.get(key);
            if (value.isArray() != null)
            {
               JSONArray array = value.isArray();
               List<String> types = new ArrayList<String>();
               for (int i = 0; i < array.size(); i++)
               {
                  String mimeType = array.get(i).isString().stringValue();
                  types.add(mimeType);
               }
               mimeTypes.put(key, types);

            }
            else if (value.isString() != null)
            {
               String mimeType = value.isString().stringValue();
               List<String> types = new ArrayList<String>();
               types.add(mimeType);
               mimeTypes.put(key, types);
            }
         }
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
   }

   public static List<String> getMimeTypes(String fileExtension)
   {
      if (mimeTypes == null)
      {
         loadMimeTypes();
      }

      List<String> types = mimeTypes.get(fileExtension);
      if (types == null)
      {
         types = new ArrayList<String>();
         types.add(DEFAULT_MIMETYPE);
      }

      return types;
   }

}
