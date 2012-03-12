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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ValueImpl implements Value
{
   private String name;
   private String value;
   private String type;

   public ValueImpl(String name, String value, String type)
   {
      this.name = name;
      this.value = value;
      this.type = type;
   }

   public ValueImpl()
   {
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String getValue()
   {
      return value;
   }

   @Override
   public void setValue(String value)
   {
      this.value = value;
   }

   @Override
   public String getType()
   {
      return type;
   }

   @Override
   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public String toString()
   {
      return "ValueImpl{" +
         "name='" + name + '\'' +
         ", value='" + value + '\'' +
         ", type='" + type + '\'' +
         '}';
   }
}
