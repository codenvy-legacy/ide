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
package org.exoplatform.ide.extension.openshift.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonWriter;

import java.io.StringWriter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonRequestBuilder
{
   private final StringWriter writer;
   private final JsonWriter jsonWriter;

   public JsonRequestBuilder()
   {
      writer = new StringWriter();
      jsonWriter = new JsonWriter(writer);
      try
      {
         jsonWriter.writeStartObject();
      }
      catch (JsonException e)
      {
         throw new RequestBuilderException(e.getMessage(), e);
      }
   }


   public JsonRequestBuilder addProperty(String writer, String value)
   {
      try
      {
         jsonWriter.writeKey(writer);
         jsonWriter.writeString(value);
      }
      catch (JsonException e)
      {
         throw new RequestBuilderException(e.getMessage(), e);
      }
      return this;
   }

   public String build()
   {
      try
      {
         jsonWriter.writeEndObject();
      }
      catch (JsonException e)
      {
         throw new RequestBuilderException(e.getMessage(), e);
      }
      return writer.toString();
   }
}
