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
package org.exoplatform.ide.vfs.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PropertyFilter
{
   /** Property filter for all properties. */
   public static final String ALL = "*";

   /** Property filter for skip all properties. */
   public static final String NONE = "none";

   /** Property filter for all properties. */
   public static final PropertyFilter ALL_FILTER;

   /** Property filter for skip all properties. */
   public static final PropertyFilter NONE_FILTER;

   static
   {
      ALL_FILTER = new PropertyFilter();
      ALL_FILTER.retrievalAllProperties = true;
      NONE_FILTER = new PropertyFilter();
      NONE_FILTER.propertyNames = Collections.emptySet();
   }

   /**
    * Construct new Property Filter.
    * 
    * @param filterString the string that contains either '*' or comma-separated list of properties names. An arbitrary number of
    *           space allowed before and after each comma. If filterString is 'none' it minds all properties should be rejected by
    *           filter.
    * @throws InvalidArgumentException if <code>filterString</code> is invalid
    */
   public static PropertyFilter valueOf(String filterString) throws InvalidArgumentException
   {
      if (filterString == null || filterString.length() == 0 || ALL.equals(filterString = filterString.trim()))
         return ALL_FILTER;
      else if (filterString.equalsIgnoreCase(NONE))
         return NONE_FILTER;
      return new PropertyFilter(filterString);
   }

   /** Characters that split. */
   private static final Pattern SPLITTER = Pattern.compile("\\s*,\\s*");

   /** Characters that not allowed in property name. */
   private static final String ILLEGAL_CHARACTERS = ",\"'\\.()";

   /** Property names. */
   private Set<String> propertyNames;

   /** Is all properties requested. */
   private boolean retrievalAllProperties = false;

   /**
    * Construct new Property Filter.
    * 
    * @param filterString the string that contains either '*' or comma-separated list of properties names. An arbitrary number of
    *           space allowed before and after each comma.
    * @throws InvalidArgumentException if <code>filterString</code> is invalid
    */
   private PropertyFilter(String filterString) throws InvalidArgumentException
   {
      this.propertyNames = new HashSet<String>();
      for (String token : SPLITTER.split(filterString))
      {
         if (token.length() > 0 && !token.equals(ALL))
         {
            for (char ch : token.toCharArray())
            {
               if (Character.isWhitespace(ch) || ILLEGAL_CHARACTERS.indexOf(ch) != -1)
                  throw new InvalidArgumentException("Invalid filter '" + filterString
                     + "' contains illegal characters.");
            }
            this.propertyNames.add(token);
         }
         else
         {
            throw new InvalidArgumentException("Invalid filter '" + filterString
               + "'. Filter must contains either '*' OR comma-separated list of properties.");
         }
      }
   }

   private PropertyFilter()
   {
   }

   public boolean accept(String name)
   {
      return retrievalAllProperties || propertyNames.contains(name);
   }
}
