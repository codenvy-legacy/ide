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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Property<T>
{
   protected final String name;

   protected final String displayName;

   protected List<T> values;

   /**
    * @param name
    * @param displayName
    * @param values
    */
   public Property(String name, String displayName, List<T> values)
   {
      this.name = name;
      this.displayName = displayName;
      if (values != null)
         this.values = new ArrayList<T>(values);
   }

   public Property(String name, String displayName, T value)
   {
      this.name = name;
      this.displayName = displayName;
      if (value != null)
      {
         this.values = new ArrayList<T>(1);
         this.values.add(value);
      }
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the displayName
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * @return the values
    */
   public List<T> getValues()
   {
      if (values == null)
         values = new ArrayList<T>();
      return values;
   }

}
