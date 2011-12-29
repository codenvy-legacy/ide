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
import java.util.Collections;
import java.util.List;

/**
 * Represent information about class method. Can be transform to JSON. <code>
 *  {
 *     "generic": "public boolean java.lang.String.equals(java.lang.Object)",
 *     "exceptionTypes": [],
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
   private List<String> exceptionTypes;

   /**
    * FQN's of parameters
    */
   private List<String> parameterTypes;

   /**
    * Names of parameters
    */
   private List<String> parameterNames;

   /**
    * Full Qualified Class Name where method declared Example: method equals()
    * declared in java.lang.String
    */
   private String declaringClass;

   /**
    * Full Qualified Class Name that method return <code>java.lang.String</code>
    */
   private String returnType;

   /**
    * true if method is a class constructor
    */
   private boolean isConstructor;

   public MethodInfo()
   {
      this.parameterTypes = Collections.emptyList();
      this.parameterNames = Collections.emptyList();
      this.exceptionTypes = Collections.emptyList();
   }

   public MethodInfo(String name,//
      int modifiers,//
      List<String> exceptionTypes,//
      List<String> parameterTypes,//
      List<String> parameterNames,//
      boolean isConstructor, //
      String genericReturnType,//
      String declaringClass)
   {
      super(name, modifiers);
      this.isConstructor = isConstructor;
      this.returnType = genericReturnType;
      this.declaringClass = declaringClass;
      setExceptionTypes(exceptionTypes);
      setParameterNames(parameterNames);
      setParameterTypes(parameterTypes);

   }

   public String getDeclaringClass()
   {
      return declaringClass;
   }

   public List<String> getExceptionTypes()
   {

      return exceptionTypes;
   }

   /**
    * @return the parameterNames
    */
   public List<String> getParameterNames()
   {
      return parameterNames;
   }

   /**
    * @return the parameterTypes
    */
   public List<String> getParameterTypes()
   {
      return parameterTypes;
   }

   public String getReturnType()
   {
      return returnType;
   }

   /**
    * @return the isConstructor
    */
   public boolean isConstructor()
   {
      return isConstructor;
   }

   /**
    * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
    */
   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);

      declaringClass = readStringUTF(in);
      exceptionTypes = readStringUTFList(in);
      parameterTypes = readStringUTFList(in);
      parameterNames = readStringUTFList(in);
      returnType = readStringUTF(in);
      isConstructor = in.readBoolean();
   }

   /**
    * @param isConstructor
    *           the isConstructor to set
    */
   public void setConstructor(boolean isConstructor)
   {
      this.isConstructor = isConstructor;
   }

   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   public void setExceptionTypes(List<String> exceptionTypes)
   {
      if (exceptionTypes == null)
      {
         this.exceptionTypes = Collections.emptyList();
      }
      else
      {
         this.exceptionTypes = exceptionTypes;
      }
   }

   /**
    * @param parameterNames
    *           the parameterNames to set
    */
   public void setParameterNames(List<String> parameterNames)
   {
      if (parameterNames == null)
      {
         this.parameterNames = Collections.emptyList();
      }
      else
      {
         this.parameterNames = parameterNames;
      }

   }

   /**
    * @param parameterTypes
    *           the parameterTypes to set
    */
   public void setParameterTypes(List<String> parameterTypes)
   {

      if (parameterTypes == null)
      {
         this.parameterTypes = Collections.emptyList();
      }
      else
      {
         this.parameterTypes = parameterTypes;
      }
   }

   public void setReturnType(String returnType)
   {
      this.returnType = returnType;
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
         if (returnType == null || returnType.length() < 1)
         {
            buildString.append("void ");
         }
         else
         {
            buildString.append(returnType);
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

      if (exceptionTypes != null && exceptionTypes.size() > 0)
      {
         buildString.append(" throws ");
         for (int i = 0; i < exceptionTypes.size(); i++)
         {
            if (i > 0)
            {
               buildString.append(",");
            }
            buildString.append(exceptionTypes.get(i));

         }
      }
      return buildString.toString();
   }

   /**
    * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
    */
   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);

      writeStringUTF(declaringClass, out);
      writeStringUTFList(exceptionTypes, out);
      writeStringUTFList(parameterTypes, out);
      writeStringUTFList(parameterNames, out);
      writeStringUTF(returnType, out);
      out.writeBoolean(isConstructor);
   }
}
