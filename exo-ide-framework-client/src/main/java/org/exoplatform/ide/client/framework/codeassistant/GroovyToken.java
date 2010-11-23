/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.codeassistant;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 5:22:15 PM evgen $
 *
 */
public class GroovyToken
{
   private Map<GroovyTokenProperties, String> properies = new LinkedHashMap<GroovyTokenProperties, String>();

   private String name;
   
   private GroovyTokenType type;
   
   /**
    * @param name of token
    * @param type of token
    */
   public GroovyToken(String name, GroovyTokenType type)
   {
      super();
      this.name = name;
      this.type = type;
   }
   
   /**
    * @param name of token
    * @param type of token
    * @param properties {@link Map} of token properties
    */
   public GroovyToken(String name, GroovyTokenType type, Map<GroovyTokenProperties, String> properties)
   {
      this.name = name;
      this.type = type;
      
      this.properies.putAll(properties);
   }

   /**
    * Get property value
    * @param name
    * @return property value or <code>null</code> if property not exist
    */
   public String getProperty(GroovyTokenProperties name)
   {
      if (properies.containsKey(name))
      {
         for (GroovyTokenProperties key : properies.keySet())
         {
            if (key.equals(name))
            {
               return properies.get(key);
            }
         }
      }
      return null;
   }
   
   /**
    * Set property
    * @param name
    * @param value
    */
   public void setProperty(GroovyTokenProperties name, String value)
   {
      properies.put(name, value);
   }

   /**
    * @return the name of token
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the type of token
    */
   public GroovyTokenType getType()
   {
      return type;
   }
   
   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      String obj = name + " " + type.toString();
      for(GroovyTokenProperties p : properies.keySet())
      {
         obj += " " + p.toString() + " " + properies.get(p);
      }
      return obj;
   }
   
}
