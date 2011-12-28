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
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTFList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTFList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 * Represent information about class method. Can be transform to JSON. <code>
 *  {
 *     "generic": "public boolean java.lang.String.equals(java.lang.Object)",
 *     "genericExceptionTypes": [],
 *     "declaringClass": "java.lang.String",
 *     "name": "equals",
 *     "genericParameterTypes": "(java.lang.Object)",
 *     "modifiers": 1,
 *     "returnType": "boolean",
 *     "parameterTypes": "(Object)",
 *     "genericReturnType": "boolean"
 *   }
 * </code>
 * 
 */
public class MethodInfo extends Member
{
   /**
    * Array FQN of exceptions throws by method
    */
   private List<String> genericExceptionTypes;

   /**
    * FQN's of parameters
    */
   private List<String> parameterTypes;

   /**
    * Full Qualified Class Name where method declared Example: method equals()
    * declared in java.lang.String
    */
   private String declaringClass;

   /**
    * Full Qualified Class Name that method return <code>java.lang.String</code>
    */
   private String genericReturnType;

   /**
    * true if method is a class constructor
    */
   private boolean isConstructor;

   public MethodInfo()
   {
   }

   public MethodInfo(String name,//
      int modifiers,//
      List<String> genericExceptionTypes,//
      List<String> parameterTypes,//
      boolean isConstructor, //
      String genericReturnType,//
      String declaringClass)
   {
      super(name, modifiers);
      this.genericExceptionTypes = genericExceptionTypes;
      this.parameterTypes = parameterTypes;
      this.isConstructor = isConstructor;
      this.genericReturnType = genericReturnType;
      this.declaringClass = declaringClass;

   }

   public String getDeclaringClass()
   {
      return declaringClass;
   }

   public List<String> getGenericExceptionTypes()
   {
      return genericExceptionTypes;
   }

   /**
    * @return the parameterTypes
    */
   public List<String> getParameterTypes()
   {
      return parameterTypes;
   }

   public String getGenericReturnType()
   {
      return genericReturnType;
   }

   public void setGenericReturnType(String genericReturnType)
   {
      this.genericReturnType = genericReturnType;
   }

   /**
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);

      declaringClass = readStringUTF(in);
      genericExceptionTypes = readStringUTFList(in);
      parameterTypes = readStringUTFList(in);
      genericReturnType = readStringUTF(in);
      isConstructor = in.readBoolean();
   }

   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   public void setGenericExceptionTypes(List<String> genericExceptionTypes)
   {
      this.genericExceptionTypes = genericExceptionTypes;
   }

   /**
    * @param parameterTypes
    *           the parameterTypes to set
    */
   public void setParameterTypes(List<String> parameterTypes)
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

      writeStringUTF(declaringClass, out);
      writeStringUTFList(genericExceptionTypes, out);
      writeStringUTFList(parameterTypes, out);
      writeStringUTF(genericReturnType, out);
      out.writeBoolean(isConstructor);
   }

   /**
    * @return the isConstructor
    */
   public boolean isConstructor()
   {
      return isConstructor;
   }

   /**
    * @param isConstructor
    *           the isConstructor to set
    */
   public void setConstructor(boolean isConstructor)
   {
      this.isConstructor = isConstructor;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder buildString = new StringBuilder();

      buildString.append(modifierToString());
      buildString.append(" ");
      if (!isConstructor)
      {
         if (genericReturnType == null || genericReturnType.length() < 1)
         {
            buildString.append("void ");
         }
         else
         {
            buildString.append(genericReturnType);
            buildString.append(" ");
         }
      }
      if (declaringClass != null && declaringClass.length() > 0 && !isConstructor)
      {
         buildString.append(declaringClass);
         buildString.append(".");
      }
      buildString.append(getName());
      buildString.append("(");

      if (parameterTypes != null)
      {
         for (int i = 0; i < parameterTypes.size(); i++)
         {
            if (i > 0)
            {
               buildString.append(",");
            }
            buildString.append(parameterTypes.get(i));

         }
      }
      buildString.append(")");

      if (genericExceptionTypes != null && genericExceptionTypes.size() > 0)
      {
         buildString.append(" throws ");
         for (int i = 0; i < genericExceptionTypes.size(); i++)
         {
            if (i > 0)
            {
               buildString.append(",");
            }
            buildString.append(genericExceptionTypes.get(i));

         }
      }
      return buildString.toString();
   }
}
