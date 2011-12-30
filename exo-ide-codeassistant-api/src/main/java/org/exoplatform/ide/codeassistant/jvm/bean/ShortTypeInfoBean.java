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
package org.exoplatform.ide.codeassistant.jvm.bean;

import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;

public class ShortTypeInfoBean extends MemberBean implements ShortTypeInfo
{

   /**
    * Means this is CLASS, INTERFACE or ANNOTATION
    */
   private String type;

   public ShortTypeInfoBean()
   {
   }

   public ShortTypeInfoBean(String name, int modifiers, String type)
   {
      super(name, modifiers);
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo#getType()
    */
   @Override
   public String getType()
   {
      return type;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo#setType(java.lang.String)
    */
   @Override
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MemberBean#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder();
      builder.append(modifierToString());
      builder.append(" ");
      builder.append(type);
      builder.append(" ");
      builder.append(getName());

      return builder.toString();
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (type == null ? 0 : type.hashCode());
      return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (!super.equals(obj))
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      ShortTypeInfoBean other = (ShortTypeInfoBean)obj;
      if (type == null)
      {
         if (other.type != null)
         {
            return false;
         }
      }
      else if (!type.equals(other.type))
      {
         return false;
      }
      return true;
   }

}
