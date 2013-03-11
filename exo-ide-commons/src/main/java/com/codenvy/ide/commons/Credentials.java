/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.commons;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: Credentials.java Mar 1, 2013 vetal $
 *
 */
public class Credentials
{
   private Map<String, String> attributes;

   public Credentials()
   {
   }

   /**
    * @param target
    * @param attributes
    */
   public Credentials(Map<String, String> attributes)
   {
      this.attributes = attributes;
   }

   /**
    * @return the attributes
    */
   public Map<String, String> getAttributes()
   {
      return attributes;
   }

   /**
    * @param attributes the attributes to set
    */
   public void setAttributes(Map<String, String> attributes)
   {
      this.attributes = attributes;
   }

   /**
    * @param key
    * @param value
    */
   public void addAttribute(String key, String value)
   {
      if (attributes == null)
         attributes = new HashMap<String, String>();
      attributes.put(key, value);
   }

   /**
    * Return string value associated with given key if exist 
    * and {@code null} otherwise 
    * 
    * @param key
    * @return
    */
   public String getAttribute(String key)
   {
      if (attributes == null)
         return null;
      return attributes.get(key);
   }

   public void writeTo(Writer out) throws IOException
   {
      StringBuilder body = new StringBuilder();
      body.append('{');
      int i = 0;
      for (Map.Entry<String, String> e : attributes.entrySet())
      {
         if (i > 0)
         {
            body.append(',');
         }
         body.append('"');
         body.append(e.getKey());
         body.append('"');
         body.append(':');
         body.append('"');
         body.append(e.getValue());
         body.append('"');
         i++;
      }
      body.append('}');
      out.write(body.toString());
   }

   public static Credentials readFrom(Reader in) throws IOException
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
      Credentials credentials = new Credentials();
      while (targets.hasNext())
      {
         String cur = targets.next();
         credentials.addAttribute(cur, jsonValue.getElement(cur).getStringValue());
      }
      return credentials;
   }

}
