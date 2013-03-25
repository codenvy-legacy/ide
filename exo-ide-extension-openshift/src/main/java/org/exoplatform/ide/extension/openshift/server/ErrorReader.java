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
import org.exoplatform.ide.commons.ParsingResponseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ErrorReader implements ExpressResponseReader<ExpressException>
{
   private final int httpResponseCode;
   private final String httpContentType;

   public ErrorReader(int httpResponseCode, String httpContentType)
   {
      this.httpContentType = httpContentType;
      this.httpResponseCode = httpResponseCode;
   }

   @Override
   public ExpressException readObject(InputStream in) throws IOException, ParsingResponseException
   {
      int exitCode = -1;
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int point;
      while ((point = in.read(buf)) != -1)
      {
         bout.write(buf, 0, point);
      }
      String msg = new String(bout.toByteArray());
      if (httpContentType.startsWith("application/json")) // May have '; charset=utf-8'
      {
         try
         {
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(new StringReader(msg));
            JsonValue resultJson = jsonParser.getJsonObject().getElement("result");
            if (resultJson != null)
            {
               msg = resultJson.getStringValue();
            }
            JsonValue exitCodeJson = jsonParser.getJsonObject().getElement("exit_code");
            if (exitCodeJson != null)
            {
               exitCode = exitCodeJson.getIntValue();
            }
            return new ExpressException(httpResponseCode, exitCode, msg, "text/plain");
         }
         catch (JsonException ignored)
         {
            // Cannot parse JSON send as is.
         }
      }
      return new ExpressException(httpResponseCode, exitCode, msg, httpContentType);
   }
}
