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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class CloudfoundryCredentials
{
   private final Map<String, String> tokens = new HashMap<String, String>();

   CloudfoundryCredentials()
   {
   }

   Collection<String> getTargets()
   {
      return Collections.unmodifiableSet(tokens.keySet());
   }

   String getToken(String target)
   {
      return tokens.get(target);
   }

   void addToken(String target, String token)
   {
      tokens.put(target, token);
   }

   boolean removeToken(String target)
   {
      return tokens.remove(target) != null;
   }

   void writeTo(Writer out) throws IOException
   {
      StringBuilder body = new StringBuilder();
      body.append('{'); //
      for (Map.Entry<String, String> e : tokens.entrySet())
      {
         body.append('"') //
            .append(e.getKey()) //
            .append('"') //
            .append(':') //
            .append('"') //
            .append(e.getValue()) //
            .append('"');
      }
      body.append('}');
      out.write(body.toString());
   }

   static CloudfoundryCredentials readFrom(Reader in) throws IOException
   {
      JsonParser jsonParser = new JsonParser();
      try
      {
         jsonParser.parse(in);
      }
      catch (JsonException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      JsonValue jsonValue = jsonParser.getJsonObject();
      Iterator<String> targets = jsonValue.getKeys();
      CloudfoundryCredentials credentials = new CloudfoundryCredentials();
      while (targets.hasNext())
      {
         String cur = targets.next();
         credentials.addToken(cur, jsonValue.getElement(cur).getStringValue());
      }
      return credentials;
   }
}
