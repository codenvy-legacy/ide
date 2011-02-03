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
   public static final PropertyFilter ALL_FILTER;

   static
   {
      ALL_FILTER = new PropertyFilter();
      ALL_FILTER.retrievalAllProperties = true;
   }

   /** Characters that split. */
   private static final Pattern SPLITTER = Pattern.compile("\\s*,\\s*");

   /** Characters that not allowed in property name. */
   private static final String ILLEGAL_CHARACTERS = ",\"'\\.()";

   /** Property filter for all properties. */
   private static final String ALL = "*";

   /** Property names. */
   private Set<String> propertyNames;

   /** Is all properties requested. */
   private boolean retrievalAllProperties = false;

   /**
    * Construct new Property Filter.
    * 
    * @param filterString the string that contains either '*' or comma-separated
    *           list of properties names. An arbitrary number of space allowed
    *           before and after each comma.
    * @throws InvalidArgumentException if <code>filterString</code> is invalid
    */
   public PropertyFilter(String filterString) throws InvalidArgumentException
   {
      if (filterString == null || filterString.length() == 0 || ALL.equals(filterString = filterString.trim()))
      {
         this.retrievalAllProperties = true;
      }
      else
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
   }

   protected PropertyFilter()
   {
   }

   public boolean accept(String name)
   {
      return retrievalAllProperties || propertyNames.contains(name);
   }
}
