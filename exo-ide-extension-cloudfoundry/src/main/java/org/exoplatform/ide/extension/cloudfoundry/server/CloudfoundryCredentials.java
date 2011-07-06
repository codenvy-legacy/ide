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

import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class CloudfoundryCredentials
{
   private final String token;
   private final String target;

   public CloudfoundryCredentials(String target, String token)
   {
      this.target = target;
      this.token = token;
   }

   public String getTarget()
   {
      return target;
   }

   public String getToken()
   {
      return token;
   }

   public void writeTo(Writer out) throws IOException
   {
      StringBuilder body = new StringBuilder();
      body.append('{') //
         .append('"').append(target).append('"') //
         .append(':') //
         .append('"').append(token).append('"') //
         .append('}');
      out.write(body.toString());
   }

   static CloudfoundryCredentials readFrom(Reader in) throws IOException
   {
      JsonHandler h = new JsonDefaultHandler();
      try
      {
         new JsonParserImpl().parse(in, h);
      }
      catch (JsonException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      JsonValue jsonValue = h.getJsonObject();
      String api = jsonValue.getKeys().next();
      String token = jsonValue.getElement(api).getStringValue();
      return new CloudfoundryCredentials(api, token);
   }
}
