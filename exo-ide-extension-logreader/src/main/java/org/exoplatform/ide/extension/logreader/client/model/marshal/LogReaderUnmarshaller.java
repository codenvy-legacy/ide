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
package org.exoplatform.ide.extension.logreader.client.model.marshal;

import com.google.gwt.json.client.JSONObject;

import com.google.gwt.json.client.JSONValue;

import com.google.gwt.json.client.JSONParser;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.extension.logreader.client.model.LogEntry;

/**
 * Dummy unmarshaller, need for pass response text to callback
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderUnmarshaller implements Unmarshallable
{

   private LogEntry logEntry;

   /**
    * @param callback
    */
   public LogReaderUnmarshaller(LogEntry logEntry)
   {
      super();
      this.logEntry = logEntry;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONValue parseStrict = JSONParser.parseStrict(response.getText());
         JSONObject object = parseStrict.isObject();
         logEntry.setContent(object.get("content").isString().stringValue());
         logEntry.setToken(object.get("token").isString().stringValue());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new UnmarshallerException("Can't parse log.");
      }
   }

}
