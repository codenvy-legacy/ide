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
package org.exoplatform.ide.resources.marshal;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.resources.model.AccessControlEntry;
import org.exoplatform.ide.resources.properties.BooleanProperty;
import org.exoplatform.ide.resources.properties.NumberProperty;
import org.exoplatform.ide.resources.properties.Property;
import org.exoplatform.ide.resources.properties.StringProperty;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JSONSerializer.java 75889 2011-11-01 10:42:51Z anya $
 */
public abstract class JSONSerializer<O>
{
   // --------- Common serializers. -------------
   public static final JSONSerializer<String> STRING_SERIALIZER = new JSONSerializer<String>()
   {
      @Override
      public JSONValue fromObject(String object)
      {
         if (object == null)
         {
            return JSONNull.getInstance();
         }
         return new JSONString(object);
      }
   };

   public static final JSONSerializer<Double> NUMBER_SERIALIZER = new JSONSerializer<Double>()
   {
      @Override
      public JSONValue fromObject(Double object)
      {
         if (object == null)
         {
            return JSONNull.getInstance();
         }
         return new JSONNumber(object.doubleValue());
      }
   };

   public static final JSONSerializer<Boolean> BOLEAN_SERIALIZER = new JSONSerializer<Boolean>()
   {
      @Override
      public JSONValue fromObject(Boolean object)
      {
         if (object == null)
         {
            return JSONNull.getInstance();
         }
         return JSONBoolean.getInstance(object.booleanValue());
      }
   };

   // --------- Customized serializers. -------------
   @SuppressWarnings({"unchecked", "rawtypes"})
   public static final JSONSerializer<Property> PROPERTY_SERIALIZER = new JSONSerializer<Property>()
   {
      @Override
      public JSONValue fromObject(Property source)
      {
         if (source == null)
         {
            return JSONNull.getInstance();
         }
         String typename = source.getClass().getName();
         if (typename.equals(BooleanProperty.class.getName()))
         {
            JSONObject target = new JSONObject();
            target.put("name", STRING_SERIALIZER.fromObject(source.getName()));
            target.put("value", BOLEAN_SERIALIZER.fromCollection(source.getValue()));
            return target;
         }
         if (typename.equals(StringProperty.class.getName()))
         {
            JSONObject target = new JSONObject();
            target.put("name", STRING_SERIALIZER.fromObject(source.getName()));
            target.put("value", STRING_SERIALIZER.fromCollection(source.getValue()));
            return target;

         }
         if (typename.equals(NumberProperty.class.getName()))
         {
            JSONObject target = new JSONObject();
            target.put("name", STRING_SERIALIZER.fromObject(source.getName()));
            target.put("value", NUMBER_SERIALIZER.fromCollection(source.getValue()));
            return target;
         }
         throw new JSONException("Not found JSONSerializer for type " + typename);
      }
   };

   public static final JSONSerializer<AccessControlEntry> ACL_SERIALIZER = new JSONSerializer<AccessControlEntry>()
   {
      @Override
      public JSONValue fromObject(AccessControlEntry source)
      {
         if (source == null)
         {
            return JSONNull.getInstance();
         }
         JSONObject target = new JSONObject();
         target.put("principal", STRING_SERIALIZER.fromObject(source.getPrincipal()));
         target.put("permissions", STRING_SERIALIZER.fromCollection(source.getPermissions()));
         return target;
      }
   };

   public JSONValue fromArray(O[] source)
   {
      if (source == null)
      {
         return JSONNull.getInstance();
      }
      JSONArray target = new JSONArray();
      for (int i = 0; i < source.length; i++)
      {
         target.set(i, fromObject(source[i]));
      }
      return target;
   }

   public JSONValue fromCollection(JsonArray<O> source)
   {
      if (source == null)
      {
         return JSONNull.getInstance();
      }
      JSONArray target = new JSONArray();
      for (int i = 0; i < source.size(); i++)
      {
         target.set(i, fromObject(source.get(i)));
      }
      return target;
   }

   public JSONValue fromMap(JsonStringMap<O> source)
   {
      if (source == null)
      {
         return JSONNull.getInstance();
      }
      JSONObject target = new JSONObject();

      JsonArray<String> keys = source.getKeys();
      for (int i = 0; i < keys.size(); i++)
      {
         target.put(keys.get(i), fromObject(source.get(keys.get(i))));
      }
      return target;
   }

   public abstract JSONValue fromObject(O source);
}
