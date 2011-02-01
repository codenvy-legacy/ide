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

/**
 * Output property. It will be serialized to JSON format. Example :
 * 
 * <pre>
 * OutputProperty property = new OutputProperty(&quot;name&quot;, new String[]{&quot;value_1&quot;, &quot;value_2&quot;});
 * </pre>
 * 
 * will be serialized to:
 * 
 * <pre>
 * {"name":"name", "value":["value_1", "value_2"]}
 * </pre>
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class OutputProperty
{
   /** Name of property. */
   private String name;

   /** Value of property. */
   private Object[] value;

   /**
    * Multiple property.
    * 
    * @param name the name of property
    * @param value the multiple value
    */
   public OutputProperty(String name, Object[] value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Property with single value.
    * 
    * @param name the name of property
    * @param value the value of property
    */
   public OutputProperty(String name, Object value)
   {
      this.name = name;
      if (value != null)
         this.value = new Object[]{value};
   }

   /**
    * Property without value.
    * 
    * @param name the name of property
    */
   public OutputProperty(String name)
   {
      this.name = name;
   }

   /**
    * @return name of property
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return value of property
    */
   public Object[] getValue()
   {
      return value;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return "[name: " + name + " values: " + java.util.Arrays.toString(value) + "]";
   }
}
