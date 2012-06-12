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

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;

public class FieldInfoBean extends MemberBean implements FieldInfo
{
   /**
    * Short Class Name <code>Comparator</code>
    */
   private String type;

   /**
    * Full Qualified Class Name where field declared
    */
   private String declaringClass;

   /**
    * The field's descriptor 
    */
   private String descriptor;

   /**
    * The field's signature. May be <tt>null</tt>.
    */
   private String signature;

   private String value;

   public FieldInfoBean()
   {

   }

   public FieldInfoBean(String name, int modifiers, String type, String declaringClass, String descriptor,
      String signature, String value)
   {
      super(name, modifiers);
      this.type = type;
      this.declaringClass = declaringClass;
      this.descriptor = descriptor;
      this.signature = signature;
      this.value = value;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo#getDeclaringClass()
    */
   @Override
   public String getDeclaringClass()
   {
      return declaringClass;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo#getType()
    */
   @Override
   public String getType()
   {
      return type;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo#setDeclaringClass(java.lang.String)
    */
   @Override
   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo#setType(java.lang.String)
    */
   @Override
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getSignature()
   {
      return signature;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescriptor()
   {
      return descriptor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setSignature(String signature)
   {
      this.signature = signature;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDescriptor(String descriptor)
   {
      this.descriptor = descriptor;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MemberBean#toString()
    */
   @Override
   public String toString()
   {
      return modifierToString() + ' ' + type + ' ' + declaringClass + '.' + getName();
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (declaringClass == null ? 0 : declaringClass.hashCode());
      result = prime * result + (type == null ? 0 : type.hashCode());
      result = prime * result + (signature == null ? 0 : signature.hashCode());
      result = prime * result + (descriptor == null ? 0 : descriptor.hashCode());
      result = prime * result + (value == null ? 0 : value.hashCode());
      return result;
   }

   /**
    * @return the value
    */
   public String getValue()
   {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(String value)
   {
      this.value = value;
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
      FieldInfoBean other = (FieldInfoBean)obj;
      if (declaringClass == null)
      {
         if (other.declaringClass != null)
         {
            return false;
         }
      }
      else if (!declaringClass.equals(other.declaringClass))
      {
         return false;
      }
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

      if (signature == null)
      {
         if (other.signature != null)
         {
            return false;
         }
      }
      else if (!signature.equals(other.signature))
      {
         return false;
      }

      if (descriptor == null)
      {
         if (other.descriptor != null)
         {
            return false;
         }
      }
      else if (!descriptor.equals(other.descriptor))
      {
         return false;
      }
      return true;
   }

}
