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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.JsonWriterImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.StringReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class JsonHelper
{
   /** Use StringBuilder instead of StringBuffer as it done in {@link java.io.StringWriter}. */
   private static class FastStrWriter extends Writer
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

   private static class Json extends JsonValue
   {
      JsonValue delegate;

      Json(JsonValue value)
      {
         delegate = value;
      }

      @Override
      public boolean isObject()
      {
         return delegate.isObject();
      }

      @Override
      public boolean isArray()
      {
         return delegate.isArray();
      }

      @Override
      public boolean isNumeric()
      {
         return delegate.isNumeric();
      }

      @Override
      public boolean isLong()
      {
         return delegate.isLong();
      }

      @Override
      public boolean isDouble()
      {
         return delegate.isDouble();
      }

      @Override
      public boolean isString()
      {
         return delegate.isString();
      }

      @Override
      public boolean isBoolean()
      {
         return delegate.isBoolean();
      }

      @Override
      public boolean isNull()
      {
         return delegate.isNull();
      }

      @Override
      public void addElement(JsonValue child)
      {
         delegate.addElement(child);
      }

      @Override
      public void addElement(String key, JsonValue child)
      {
         delegate.addElement(key, child);
      }

      @Override
      public Iterator<JsonValue> getElements()
      {
         return delegate.getElements();
      }

      @Override
      public Iterator<String> getKeys()
      {
         return delegate.getKeys();
      }

      @Override
      public JsonValue getElement(String key)
      {
         return delegate.getElement(key);
      }

      @Override
      public int size()
      {
         return delegate.size();
      }

      @Override
      public String getStringValue()
      {
         return toString();
      }

      @Override
      public boolean getBooleanValue()
      {
         return delegate.getBooleanValue();
      }

      @Override
      public Number getNumberValue()
      {
         return delegate.getNumberValue();
      }

      @Override
      public byte getByteValue()
      {
         return delegate.getByteValue();
      }

      @Override
      public short getShortValue()
      {
         return delegate.getShortValue();
      }

      @Override
      public int getIntValue()
      {
         return delegate.getIntValue();
      }

      @Override
      public long getLongValue()
      {
         return delegate.getLongValue();
      }

      @Override
      public float getFloatValue()
      {
         return delegate.getFloatValue();
      }

      @Override
      public double getDoubleValue()
      {
         return delegate.getDoubleValue();
      }

      @Override
      public String toString()
      {
         return delegate.toString();
      }

      @Override
      public void writeTo(JsonWriter writer) throws JsonException
      {
         delegate.writeTo(writer);
      }
   }

   @SuppressWarnings("unchecked")
   static <O> String toJson(O instance)
   {
      try
      {
         JsonValue json;
         if (instance.getClass().isArray())
            json = new JsonGeneratorImpl().createJsonArray(instance);
         else if (instance instanceof Collection)
            json = new JsonGeneratorImpl().createJsonArray((Collection<?>)instance);
         else if (instance instanceof Map)
            json = new JsonGeneratorImpl().createJsonObjectFromMap((Map<String, ?>)instance);
         else
            json = new JsonGeneratorImpl().createJsonObject(instance);

         Writer w = new FastStrWriter();
         json.writeTo(new JsonWriterImpl(w));
         return w.toString();
      }
      catch (JsonException jsone)
      {
         // Must not happen since serialize well known object.
         throw new RuntimeException(jsone.getMessage(), jsone);
      }
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   static <O> O fromJson(String json, Class<O> klass, Type type) throws ParsingResponseException
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

   static SystemService[] parseSystemServices(String json) throws ParsingResponseException
   {
      try
      {
         JsonValue jservices = parseJson(json);
         List<SystemService> result = new ArrayList<SystemService>(3);
         for (Iterator<String> types = jservices.getKeys(); types.hasNext();)
         {
            String type = types.next();
            for (Iterator<String> vendors = jservices.getElement(type).getKeys(); vendors.hasNext();)
            {
               String vendor = vendors.next();
               for (Iterator<String> versions = jservices.getElement(type).getElement(vendor).getKeys(); versions
                  .hasNext();)
               {
                  String version = versions.next();
                  result.add(ObjectBuilder.createObject(SystemService.class, jservices.getElement(type).getElement(vendor)
                     .getElement(version)));
               }
            }
         }
         return result.toArray(new SystemService[result.size()]);
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }

   private static JsonValue parseJson(String json) throws ParsingResponseException
   {
      try
      {
         JsonHandler jsonHandler = new JsonDefaultHandler();
         new JsonParserImpl().parse(new StringReader(json), jsonHandler);
         JsonValue jsonValue = jsonHandler.getJsonObject();
         return new Json(jsonValue);
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }
}
