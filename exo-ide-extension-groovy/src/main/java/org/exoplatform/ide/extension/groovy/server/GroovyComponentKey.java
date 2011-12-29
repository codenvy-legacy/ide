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
package org.exoplatform.ide.extension.groovy.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Key for Groovy source components. Such key used to register components in picocontainer.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GroovyComponentKey
{
   public static GroovyComponentKey make(String vfsID, String itemIdentifier)
   {
      return new GroovyComponentKey(vfsID, itemIdentifier);
   }

   private final String vfsID;

   private final String itemIdentifier;

   private Map<String, Object> attributes;

   public GroovyComponentKey(String vfsID, String itemIdentifier)
   {
      if (vfsID == null)
      {
         throw new NullPointerException("Virtual file system ID may not be null. ");
      }
      if (itemIdentifier == null)
      {
         throw new NullPointerException("Path or ID of virtual file system item may not be null. ");
      }

      this.vfsID = vfsID;
      this.itemIdentifier = itemIdentifier;
   }

   /**
    * Set optional attributes for this key. If value of attribute is <code>null</code> then the attribute is removed.
    *
    * @param name the name of attribute
    * @param value the value of attribute
    */
   public void setAttribute(String name, Object value)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("name");
      }
      if (value == null && attributes == null)
      {
         return;
      }
      else if (attributes == null)
      {
         attributes = new HashMap<String, Object>();
      }

      if (value == null)
      {
         attributes.remove(name);
      }
      else
      {
         attributes.put(name, value);
      }
   }

   /**
    * Get attribute by name.
    *
    * @param name the name of attribute
    * @return the value of attribute or <code>null</code>
    */
   public Object getAttribute(String name)
   {
      if (attributes == null)
      {
         return null;
      }
      return attributes.get(name);
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }
      GroovyComponentKey other = (GroovyComponentKey)o;
      return vfsID.equals(other.vfsID) && itemIdentifier.equals(other.itemIdentifier);
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 31 * hash + vfsID.hashCode();
      hash = 31 * hash + itemIdentifier.hashCode();
      return hash;
   }

   @Override
   public String toString()
   {
      return "GroovyComponentKey{" //
         + "vfsID='" + vfsID + "', "  //
         + "itemIdentifier='" + itemIdentifier + "', " //
         + "attributes=" + attributes + //
         "}";
   }
}
