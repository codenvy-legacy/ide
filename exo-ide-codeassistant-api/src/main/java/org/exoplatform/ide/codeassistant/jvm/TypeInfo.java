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

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readObjectArray;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTFArray;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeObjectArray;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTFArray;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

/**
 * Created by The eXo Platform SAS.
 * 
 */
public class TypeInfo extends ShortTypeInfo
{
   private MethodInfo[] methods;

   private MethodInfo[] declaredMethods;

   private RoutineInfo[] constructors;

   private RoutineInfo[] declaredConstructors;

   private FieldInfo[] fields;

   private FieldInfo[] declaredFields;

   private String superClass;

   private String[] interfaces;

   public TypeInfo()
   {
   }

   public TypeInfo(int modifiers, String name, MethodInfo[] methods, MethodInfo[] declaredMethods,
      RoutineInfo[] constructors, RoutineInfo[] declaredConstructors, FieldInfo[] fields, FieldInfo[] declaredFields,
      String superClass, String[] interfaces, String qualifiedName, String type)
   {
      super(modifiers, name, qualifiedName, type);
      this.methods = methods;
      this.declaredMethods = declaredMethods;
      this.constructors = constructors;
      this.declaredConstructors = declaredConstructors;
      this.fields = fields;
      this.declaredFields = declaredFields;
      this.superClass = superClass;
      this.interfaces = interfaces;
   }

   /**
    * @return the methods
    */
   public MethodInfo[] getMethods()
   {
      return methods;
   }

   /**
    * @param methods
    *           the methods to set
    */
   public void setMethods(MethodInfo[] methods)
   {
      this.methods = methods;
   }

   /**
    * @return the declaredMethods
    */
   public MethodInfo[] getDeclaredMethods()
   {
      return declaredMethods;
   }

   /**
    * @param declaredMethods
    *           the declaredMethods to set
    */
   public void setDeclaredMethods(MethodInfo[] declaredMethods)
   {
      this.declaredMethods = declaredMethods;
   }

   /**
    * @return the constructors
    */
   public RoutineInfo[] getConstructors()
   {
      return constructors;
   }

   /**
    * @param constructors
    *           the constructors to set
    */
   public void setConstructors(RoutineInfo[] constructors)
   {
      this.constructors = constructors;
   }

   /**
    * @return the declaredConstructors
    */
   public RoutineInfo[] getDeclaredConstructors()
   {
      return declaredConstructors;
   }

   /**
    * @param declaredConstructors
    *           the declaredConstructors to set
    */
   public void setDeclaredConstructors(RoutineInfo[] declaredConstructors)
   {
      this.declaredConstructors = declaredConstructors;
   }

   /**
    * @return the fields
    */
   public FieldInfo[] getFields()
   {
      return fields;
   }

   /**
    * @param fields
    *           the fields to set
    */
   public void setFields(FieldInfo[] fields)
   {
      this.fields = fields;
   }

   /**
    * @return the declaredFields
    */
   public FieldInfo[] getDeclaredFields()
   {
      return declaredFields;
   }

   /**
    * @param declaredFields
    *           the declaredFields to set
    */
   public void setDeclaredFields(FieldInfo[] declaredFields)
   {
      this.declaredFields = declaredFields;
   }

   /**
    * @return the superClass
    */
   public String getSuperClass()
   {
      return superClass;
   }

   /**
    * @param superClass
    *           the superClass to set
    */
   public void setSuperClass(String superClass)
   {
      this.superClass = superClass;
   }

   /**
    * @return the interfaces
    */
   public String[] getInterfaces()
   {
      return interfaces;
   }

   /**
    * @param interfaces
    *           the interfaces to set
    */
   public void setInterfaces(String[] interfaces)
   {
      this.interfaces = interfaces;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      out.writeObject(superClass);
      writeStringUTFArray(interfaces, out);
      writeObjectArray(RoutineInfo.class, constructors, out);
      writeObjectArray(RoutineInfo.class, declaredConstructors, out);
      writeObjectArray(FieldInfo.class, fields, out);
      writeObjectArray(FieldInfo.class, declaredFields, out);
      writeObjectArray(MethodInfo.class, methods, out);
      writeObjectArray(MethodInfo.class, declaredMethods, out);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      superClass = (String)in.readObject();
      interfaces = readStringUTFArray(in);
      constructors = readObjectArray(RoutineInfo.class, in);
      declaredConstructors = readObjectArray(RoutineInfo.class, in);
      fields = readObjectArray(FieldInfo.class, in);
      declaredFields = readObjectArray(FieldInfo.class, in);
      methods = readObjectArray(MethodInfo.class, in);
      declaredMethods = readObjectArray(MethodInfo.class, in);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder(super.toString());
      if (superClass != null && !superClass.isEmpty())
      {
         sb.append(" extends ");
         sb.append(superClass);
      }
      if (interfaces != null && interfaces.length > 0)
      {
         sb.append(" implements ");
         for (int i = 0; i < interfaces.length; i++)
         {
            sb.append(interfaces[i]);
            if (i + 1 < interfaces.length)
            {
               sb.append(", ");
            }
         }
      }

      return sb.toString();
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + Arrays.hashCode(constructors);
      result = prime * result + Arrays.hashCode(declaredConstructors);
      result = prime * result + Arrays.hashCode(declaredFields);
      result = prime * result + Arrays.hashCode(declaredMethods);
      result = prime * result + Arrays.hashCode(fields);
      result = prime * result + Arrays.hashCode(interfaces);
      result = prime * result + Arrays.hashCode(methods);
      result = prime * result + (superClass == null ? 0 : superClass.hashCode());
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
      TypeInfo other = (TypeInfo)obj;
      if (!Arrays.equals(constructors, other.constructors))
      {
         return false;
      }
      if (!Arrays.equals(declaredConstructors, other.declaredConstructors))
      {
         return false;
      }
      if (!Arrays.equals(declaredFields, other.declaredFields))
      {
         return false;
      }
      if (!Arrays.equals(declaredMethods, other.declaredMethods))
      {
         return false;
      }
      if (!Arrays.equals(fields, other.fields))
      {
         return false;
      }
      if (!Arrays.equals(interfaces, other.interfaces))
      {
         return false;
      }
      if (!Arrays.equals(methods, other.methods))
      {
         return false;
      }
      if (superClass == null)
      {
         if (other.superClass != null)
         {
            return false;
         }
      }
      else if (!superClass.equals(other.superClass))
      {
         return false;
      }
      return true;
   }

}
