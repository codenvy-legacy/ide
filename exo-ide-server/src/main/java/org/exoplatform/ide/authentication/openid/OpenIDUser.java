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
package org.exoplatform.ide.authentication.openid;

import org.openid4java.discovery.Identifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OpenIDUser
{
   private final Map<String, String> attributes;
   private final Map<String, String> unmodifiableAttributes;
   private final Identifier identifier;

   public OpenIDUser(Identifier identifier)
   {
      if (identifier == null)
      {
         throw new IllegalArgumentException("Identifier may not be null. ");
      }
      this.identifier = identifier;
      this.attributes = new HashMap<String, String>();
      this.unmodifiableAttributes = Collections.unmodifiableMap(attributes);
   }

   /**
    * Get OpenID user identifier.
    *
    * @return OpenID user identifier
    */
   public Identifier getIdentifier()
   {
      return identifier;
   }

   /**
    * Ge attribute of OpenID user.
    *
    * @param name
    *    name of attribute
    * @return value of attribute or <code>null</code>
    * @see #setAttribute(String, String)
    */
   public String getAttribute(String name)
   {
      return attributes.get(name);
   }

   /**
    * Set attribute of OpenID user, such as email, user name, language, etc.
    *
    * @param name
    *    name of attribute
    * @param value
    *    value of attribute
    */
   public void setAttribute(String name, String value)
   {
      this.attributes.put(name, value);
   }

   /**
    * Get all user attributes.
    *
    * @return unmodifiable map of user's attributes
    */
   public Map<String, String> getAllAttributes()
   {
      return unmodifiableAttributes;
   }
}
