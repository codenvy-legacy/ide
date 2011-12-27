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

package org.exoplatform.ide.codeassistant.jvm;

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTF;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Represent information about class field. Can be transform to JSON. Example of
 * JSON: <code>
 * {
 * "declaringClass": "java.lang.String",
 * "name": "CASE_INSENSITIVE_ORDER",
 * "modifiers": 25,
 * "type": "Comparator"
 * }
 * </code>
 * 
 * 
 */
public class FieldInfo extends Member
{
   /**
    * Short Class Name <code>Comparator</code>
    */
   private String type;

   /**
    * Full Qualified Class Name where field declared
    */
   private String declaringClass;

   public FieldInfo()
   {

   }

   public FieldInfo(String name, int modifiers, String type, String declaringClass)
   {
      super(name, modifiers);
      this.type = type;
      this.declaringClass = declaringClass;
   }

   public String getDeclaringClass()
   {
      return declaringClass;
   }

   public String getType()
   {
      return type;
   }

   /**
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);

      type = readStringUTF(in);
      declaringClass = readStringUTF(in);
   }

   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#toString()
    */
   @Override
   public String toString()
   {
      return modifierToString() + " " + type + " " + declaringClass + "." + getName();
   }

   /**
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);

      writeStringUTF(type, out);
      writeStringUTF(declaringClass, out);
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
      FieldInfo other = (FieldInfo)obj;
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
      return true;
   }

}
