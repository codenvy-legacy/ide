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

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTFArray;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTFArray;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

/**
 * Created by The eXo Platform SAS.
 * 
 */
public class RoutineInfo extends Member
{
   /**
    * Array FQN of exceptions throws by method
    */
   private String[] genericExceptionTypes;

   /**
    * Full Qualified Class Name of parameter <code>(java.lang.Object)</code>
    * (not use for now)
    */
   private String genericParameterTypes;

   /**
    * Short Class name of Parameter <code>(Object)</code>
    */
   private String parameterTypes;

   /**
    * Method declaration like:
    * <code>public boolean java.lang.String.equals(java.lang.Object)</code>
    */
   private String generic;

   /**
    * Full Qualified Class Name where method declared Example: method equals()
    * declared in java.lang.String
    */
   private String declaringClass;

   public RoutineInfo()
   {
   }

   public RoutineInfo(int modifiers, String name, String[] genericExceptionTypes, String genericParameterTypes,
      String parameterTypes, String generic, String declaringClass)
   {
      super(modifiers, name);
      this.genericExceptionTypes = genericExceptionTypes;
      this.genericParameterTypes = genericParameterTypes;
      this.parameterTypes = parameterTypes;
      this.generic = generic;
      this.declaringClass = declaringClass;
   }

   public String getGenericParameterTypes()
   {
      return genericParameterTypes;
   }

   public void setGenericParameterTypes(String genericParameterTypes)
   {
      this.genericParameterTypes = genericParameterTypes;
   }

   public void setGeneric(String generic)
   {
      this.generic = generic;
   }

   public String getGeneric()
   {
      return generic;
   }

   public String getDeclaringClass()
   {
      return declaringClass;
   }

   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   public String[] getGenericExceptionTypes()
   {
      return genericExceptionTypes;
   }

   public void setGenericExceptionTypes(String[] genericExceptionTypes)
   {
      this.genericExceptionTypes = genericExceptionTypes;
   }

   public String getParameterTypes()
   {
      return parameterTypes;
   }

   public void setParameterTypes(String parameterTypes)
   {
      this.parameterTypes = parameterTypes;
   }

   /**
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);

      out.writeObject(genericParameterTypes);
      out.writeObject(parameterTypes);
      out.writeObject(generic);
      out.writeObject(declaringClass);
      writeStringUTFArray(genericExceptionTypes, out);
   }

   /**
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);

      genericParameterTypes = (String)in.readObject();
      parameterTypes = (String)in.readObject();
      generic = (String)in.readObject();
      declaringClass = (String)in.readObject();
      genericExceptionTypes = readStringUTFArray(in);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#toString()
    */
   @Override
   public String toString()
   {

      return generic;
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
      result = prime * result + (generic == null ? 0 : generic.hashCode());
      result = prime * result + Arrays.hashCode(genericExceptionTypes);
      result = prime * result + (genericParameterTypes == null ? 0 : genericParameterTypes.hashCode());
      result = prime * result + (parameterTypes == null ? 0 : parameterTypes.hashCode());
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
      RoutineInfo other = (RoutineInfo)obj;
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
      if (generic == null)
      {
         if (other.generic != null)
         {
            return false;
         }
      }
      else if (!generic.equals(other.generic))
      {
         return false;
      }
      if (!Arrays.equals(genericExceptionTypes, other.genericExceptionTypes))
      {
         return false;
      }
      if (genericParameterTypes == null)
      {
         if (other.genericParameterTypes != null)
         {
            return false;
         }
      }
      else if (!genericParameterTypes.equals(other.genericParameterTypes))
      {
         return false;
      }
      if (parameterTypes == null)
      {
         if (other.parameterTypes != null)
         {
            return false;
         }
      }
      else if (!parameterTypes.equals(other.parameterTypes))
      {
         return false;
      }
      return true;
   }

}
