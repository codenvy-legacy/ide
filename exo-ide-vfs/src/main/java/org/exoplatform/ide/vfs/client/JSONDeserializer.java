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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class JSONDeserializer<O>
{
   // ----------- Common deserializers. -------------
   public static final JSONDeserializer<String> STRING_SERIALIZER = new JSONDeserializer<String>() {
      @Override
      public String toObject(JSONValue json)
      {
         if (json == null)
            return null;
         JSONString jsonString = json.isString();
         if (jsonString == null)
            return null;
         return jsonString.stringValue();
      }

      @Override
      protected String[] createArray(int length)
      {
         return new String[length];
      }
   };

   public static final JSONDeserializer<Boolean> BOOLEAN_SERIALIZER = new JSONDeserializer<Boolean>() {
      @Override
      public Boolean toObject(JSONValue json)
      {
         if (json == null)
            return null;
         JSONBoolean jsonBoolean = json.isBoolean();
         if (jsonBoolean == null)
            return null;
         return jsonBoolean.booleanValue();
      }

      @Override
      protected Boolean[] createArray(int length)
      {
         return new Boolean[length];
      }
   };

   public static final JSONDeserializer<Double> NUMBER_SERIALIZER = new JSONDeserializer<Double>() {
      @Override
      public Double toObject(JSONValue json)
      {
         if (json == null)
            return null;
         JSONNumber jsonDouble = json.isNumber();
         if (jsonDouble == null)
            return null;
         return jsonDouble.doubleValue();
      }

      @Override
      protected Double[] createArray(int length)
      {
         return new Double[length];
      }
   };

   public O[] toArray(JSONValue json)
   {
      if (json == null)
         return null;
      JSONArray jsonArray = json.isArray();
      if (jsonArray == null)
         return null;
      int size = jsonArray.size();
      O[] array = createArray(size);
      for (int i = 0; i < size; i++)
         array[i] = toObject(jsonArray.get(i));
      return array;
   }

   protected abstract O[] createArray(int length);

   public List<O> toList(JSONValue json)
   {
      if (json == null)
         return null;
      JSONArray jsonArray = json.isArray();
      if (jsonArray == null)
         return null;
      int size = jsonArray.size();
      List<O> list = createList(size);
      for (int i = 0; i < size; i++)
         list.add(toObject(jsonArray.get(i)));
      return list;
   }

   protected List<O> createList(int length)
   {
      return new ArrayList<O>(length);
   }

   public Set<O> toSet(JSONValue json)
   {
      if (json == null)
         return null;
      JSONArray jsonArray = json.isArray();
      if (jsonArray == null)
         return null;
      int size = jsonArray.size();
      Set<O> set = createSet(size);
      for (int i = 0; i < size; i++)
         set.add(toObject(jsonArray.get(i)));
      return set;
   }

   protected Set<O> createSet(int length)
   {
      return new HashSet<O>(length);
   }

   public Map<String, O> toMap(JSONValue json)
   {
      if (json == null)
         return null;
      JSONObject jsonObject = json.isObject();
      if (jsonObject == null)
         return null;
      Set<String> keySet = jsonObject.keySet();
      Map<String, O> map = createMap(keySet.size());
      for (Iterator<String> i = keySet.iterator(); i.hasNext();)
      {
         String key = i.next();
         map.put(key, toObject(jsonObject.get(key)));
      }
      return map;
   }

   protected Map<String, O> createMap(int length)
   {
      return new HashMap<String, O>(length);
   }

   public abstract O toObject(JSONValue json);
}
