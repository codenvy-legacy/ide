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
package org.exoplatform.ide.vfs.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class Property<V>
{
   protected String name;

   protected List<V> value;

   public Property(String name, List<V> value)
   {
      this.name = name;
      this.value = value;
   }

   /**
    * Create single-valued Property. Using this constructor is equivalent to:
    * 
    * <pre>
    * V val = ...;
    * List&lt;V&gt; l = new ArrayList&lt;V&gt;(1);
    * l.add(val);
    * new Property(&quot;MyName&quot;, l) {
    * };
    * </pre>
    * 
    * @param name the name of property
    * @param value the value. If <code>value == null</code> it means no value
    */
   public Property(String name, V value)
   {
      this.name = name;
      if (value != null)
      {
         this.value = new ArrayList<V>(1);
         this.value.add(value);
      }
   }

   public Property()
   {
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   // NOTE getter and setter for value must be overridden.
   // Need this for correct work of JSON tool on server side. Implementation of such
   // methods here make impossible to determine type via reflection.
   public abstract List<V> getValue();

   public abstract void setValue(List<V> value);

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "Property [name=" + name + ", value=" + value + "]";
   }
}
