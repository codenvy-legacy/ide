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
package org.exoplatform.ide.client.module.groovy.classpath;

import com.google.gwt.json.client.JSONValue;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Util helps to convert JSON format to java and vice versa.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 11, 2011 $
 *
 */
public class GroovyClassPathUtil
{
   public static final String ENTRIES = "entries";

   /**
    * Get the list of {@link GroovyClassPathEntry} elements from classpath file's content.
    * 
    * @param jsonObject object in JSON format
    * @return {@link List} sources of classpath file
    */
   public static List<GroovyClassPathEntry> getClassPathEntries(String jsonObject)
   {
      List<GroovyClassPathEntry> groovyClassPathEntries = new ArrayList<GroovyClassPathEntry>();
      //Try to get object from string
      JavaScriptObject json = build(jsonObject);
      if (json == null)
         return groovyClassPathEntries;
      //Get entries:
      JSONValue entries = new JSONObject(json).get(ENTRIES);
      if (entries == null)
         return groovyClassPathEntries;
      //The "entries" property contains array value:
      JSONArray array = entries.isArray();
      for (int i = 0; i < array.size(); i++)
      {
         //Form java object from JSON:
         GroovyClassPathEntry groovyClassPathEntry = GroovyClassPathEntry.build(array.get(i).isObject().toString());
         if (groovyClassPathEntries != null)
         {
            groovyClassPathEntries.add(groovyClassPathEntry);
         }
      }
      return groovyClassPathEntries;
   }

   /**
    * Form JSON object from list of {@link GroovyClassPathEntry}.
    * 
    * @param groovyClassPathEntries
    * @return {@link String} JSON object
    */
   public static String getClassPathJSON(List<GroovyClassPathEntry> groovyClassPathEntries)
   {
      JSONObject jsonObject = new JSONObject();
      JSONArray array = new JSONArray();
      for (int i = 0; i < groovyClassPathEntries.size(); i++)
      {
         array.set(i, new JSONObject(groovyClassPathEntries.get(i)));
      }
      jsonObject.put(ENTRIES, array);
      return jsonObject.toString();
   }

   /**
    * Build {@link JavaScriptObject} from string.
    * 
    * @param json string that contains object
    * @return {@link JavaScriptObject}
    */
   private static native JavaScriptObject build(String json) /*-{
      try 
      {
         var object = eval('(' + json + ')');
         return object;
      } catch (e)
      {
         return null;
      }
   }-*/;
}
