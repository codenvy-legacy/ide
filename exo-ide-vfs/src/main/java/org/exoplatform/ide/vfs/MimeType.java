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
package org.exoplatform.ide.vfs;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class MimeType
{
   private static final String WILDCARD = "*";

   /** Type. */
   private final String type;

   /** Sub-type. */
   private final String subType;

   /** Parameters. */
   private final Map<String, String> parameters;

   public MimeType()
   {
      this(null, null);
   }

   /**
    * Create instance of MimeType.
    * 
    * @param type the name of type
    * @param subType the name of sub-type
    */
   public MimeType(String type, String subType)
   {
      this.type =
         type == null || type.length() == 0 ? WILDCARD : ((type = type.trim()).length() == 0 ? WILDCARD : type
            .toLowerCase());
      this.subType =
         subType == null || subType.length() == 0 ? WILDCARD : ((subType = subType.trim()).length() == 0 ? WILDCARD
            : subType.toLowerCase());
      this.parameters = new HashMap<String, String>();
   }

   public MimeType(String type, String subType, Map<String, String> parameters)
   {
      this.type =
         type == null || type.length() == 0 ? WILDCARD : ((type = type.trim()).length() == 0 ? WILDCARD : type
            .toLowerCase());
      this.subType =
         subType == null || subType.length() == 0 ? WILDCARD : ((subType = subType.trim()).length() == 0 ? WILDCARD
            : subType.toLowerCase());
      if (parameters == null)
      {
         this.parameters = new HashMap<String, String>();
      }
      else
      {
         Map<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
               return o1.compareToIgnoreCase(o2);
            }
         });
         for (Map.Entry<String, String> e : parameters.entrySet())
            map.put(e.getKey().toLowerCase(), e.getValue());
         this.parameters = Collections.unmodifiableMap(map);
      }
   }

   /**
    * @return get type
    */
   public String getSubType()
   {
      return subType;
   }

   /**
    * @return get sub-type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @return mime type parameters
    */
   public Map<String, String> getParameters()
   {
      return parameters;
   }

   /**
    * Check is one mime-type compatible to other. Function is not commutative.
    * E.g. image/* compatible with image/png, image/jpeg, but image/png is not
    * compatible with image/*.
    * 
    * @param other MimeType to be checked for compatible with this.
    * @return TRUE if MimeTypes compatible FALSE otherwise
    */
   public boolean match(MimeType other)
   {
      if (other == null)
         return false;
      return type.equals(WILDCARD)
         || (type.equalsIgnoreCase(other.type) && (subType.equals(WILDCARD) || subType.equalsIgnoreCase(other.subType)));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + type.hashCode();
      hash = hash * 31 + subType.hashCode();
      hash = hash * 31 + parameters.hashCode();
      return hash;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null )
         return false;
      if ((obj == null) || (obj.getClass() != this.getClass()))
         return false;
      MimeType otherMimeType = (MimeType)obj;
      return type.equalsIgnoreCase(otherMimeType.type) && subType.equalsIgnoreCase(otherMimeType.subType)
         && parameters.equals(otherMimeType.parameters);
   }
}
