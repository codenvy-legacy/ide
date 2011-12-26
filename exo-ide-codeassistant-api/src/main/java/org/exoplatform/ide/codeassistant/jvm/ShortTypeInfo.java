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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Short information about class or interface. Contain fqn, short name,
 * modifiers Example : { "name": "String", "qualifiedName": "java.lang.String",
 * "modifiers": 0, "type": "CLASS" }
 * 
 * 
 * Created by The eXo Platform SAS.
 * 
 */
public class ShortTypeInfo extends Member
{
   /**
    * Full Qualified Class Name
    */
   private String qualifiedName;

   /**
    * Means this is CLASS, INTERFACE or ANNOTATION
    */
   private String type;

   public ShortTypeInfo()
   {
   }

   public ShortTypeInfo(int modifiers, String name, String qualifiedName, String type)
   {
      super(modifiers, name);
      this.qualifiedName = qualifiedName;
      this.type = type;
   }

   public void setQualifiedName(String qualifiedName)
   {
      this.qualifiedName = qualifiedName;
   }

   public String getQualifiedName()
   {
      return qualifiedName;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);

      out.writeObject(qualifiedName);
      out.writeObject(type);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);

      qualifiedName = (String)in.readObject();
      type = (String)in.readObject();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#toString()
    */
   @Override
   public String toString()
   {
      return modifierToString() + " " + type + " " + qualifiedName;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (qualifiedName == null ? 0 : qualifiedName.hashCode());
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
      ShortTypeInfo other = (ShortTypeInfo)obj;
      if (qualifiedName == null)
      {
         if (other.qualifiedName != null)
         {
            return false;
         }
      }
      else if (!qualifiedName.equals(other.qualifiedName))
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
