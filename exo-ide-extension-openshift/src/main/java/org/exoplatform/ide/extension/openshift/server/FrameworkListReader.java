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
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FrameworkListReader implements ExpressResponseReader<Set<String>>
{
   @Override
   public Set<String> readObject(InputStream in) throws ParsingResponseException
   {
      try
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(in);
         JsonValue resultJson = jsonParser.getJsonObject().getElement("data");
         String resultSrc = resultJson.getStringValue();
         jsonParser.parse(new StringReader(resultSrc));
         JsonValue frameworksJson = jsonParser.getJsonObject().getElement("carts");
         Set<String> frameworks = new HashSet<String>();
         Iterator<JsonValue> iterator = frameworksJson.getElements();
         while (iterator.hasNext())
         {
            frameworks.add(iterator.next().getStringValue());
         }
         return frameworks;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }
}
