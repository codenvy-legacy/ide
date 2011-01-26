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
package org.exoplatform.ide.vfs.shared;

import java.util.Arrays;

/**
 * Input property. It will be restored from JSON source.To simplify JSON
 * structure we accept all values as array of String.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class InputProperty
{
   /** Name of property. */
   protected String name;

   /** Value of property. */
   protected String[] value;

   public InputProperty(String name, String[] value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Create single-valued InputProperty. Using this constructor is equivalent
    * to:
    * 
    * <pre>
    * new InputProperty(&quot;MyName&quot;, new String[]{&quot;SingleValue&quot;});
    * </pre>
    * 
    * @param name the name of property
    * @param value the value. If <code>value == null</code> it means no value
    */
   public InputProperty(String name, String value)
   {
      this.name = name;
      if (value != null)
         this.value = new String[]{value};
   }

   public InputProperty()
   {
   }

   /**
    * @return name of property
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name of property
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return value of property
    */
   public String[] getValue()
   {
      return value;
   }

   /**
    * @param value the value of property
    */
   public void setValue(String[] value)
   {
      this.value = value;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "InputProperty [name=" + name + ", value=" + Arrays.toString(value) + "]";
   }
}
