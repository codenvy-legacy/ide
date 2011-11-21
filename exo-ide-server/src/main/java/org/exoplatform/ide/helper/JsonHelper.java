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
package org.exoplatform.ide.helper;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.JsonWriter;
import org.everrest.core.impl.provider.json.ObjectBuilder;

import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonHelper
{
   /** Use StringBuilder instead of StringBuffer as it done in {@link java.io.StringWriter}. */
   public static class FastStrWriter extends Writer
   {
      private final StringBuilder buf;

      public FastStrWriter()
      {
         buf = new StringBuilder();
      }

      @Override
      public void write(int c)
      {
         buf.append((char)c);
      }

      @Override
      public void write(char[] cbuf)
      {
         buf.append(cbuf);
      }

      @Override
      public void write(char[] cbuf, int off, int len)
      {
         buf.append(cbuf, off, len);
      }

      @Override
      public void write(String str)
      {
         buf.append(str);
      }

      @Override
      public void write(String str, int off, int len)
      {
         buf.append(str, off, len);
      }

      @Override
      public String toString()
      {
         return buf.toString();
      }

      @Override
      public void flush()
      {
      }

      @Override
      public void close()
      {
      }
   }

   @SuppressWarnings("unchecked")
   public static <O> String toJson(O instance)
   {
      try
      {
         JsonValue json;
         if (instance.getClass().isArray())
            json = JsonGenerator.createJsonArray(instance);
         else if (instance instanceof Collection)
            json = JsonGenerator.createJsonArray((Collection<?>)instance);
         else if (instance instanceof Map)
            json = JsonGenerator.createJsonObjectFromMap((Map<String, ?>)instance);
         else
            json = JsonGenerator.createJsonObject(instance);

         Writer w = new FastStrWriter();
         json.writeTo(new JsonWriter(w));
         return w.toString();
      }
      catch (JsonException jsone)
      {
         // Must not happen since serialize well known object.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public static <O> O fromJson(String json, Class<O> klass, Type type) throws ParsingResponseException
   {
      try
      {
         JsonValue jsonValue = parseJson(json);

         O instance;
         if (klass.isArray())
         {
            instance = (O)ObjectBuilder.createArray(klass, jsonValue);
         }
         else if (Collection.class.isAssignableFrom(klass))
         {
            Class k = klass;
            instance = (O)ObjectBuilder.createCollection(k, type, jsonValue);
         }
         else if (Map.class.isAssignableFrom(klass))
         {
            Class k = klass;
            instance = (O)ObjectBuilder.createObject(k, type, jsonValue);
         }
         else
         {
            instance = ObjectBuilder.createObject(klass, jsonValue);
         }
         return instance;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }

   public static JsonValue parseJson(String json) throws ParsingResponseException
   {
      try
      {
        JsonParser parser = new JsonParser();
         parser.parse(new StringReader(json));
         JsonValue jsonValue = parser.getJsonObject();
         return jsonValue;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }
}
