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
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
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

   public TypeInfo(Integer modifiers, String name, MethodInfo[] methods, MethodInfo[] declaredMethods,
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

      out.writeInt(interfaces.length);
      for (String methodInfo : interfaces)
      {
         out.writeObject(methodInfo);
      }

      out.writeInt(constructors.length);
      for (RoutineInfo methodInfo : constructors)
      {
         out.writeObject(methodInfo);
      }

      out.writeInt(declaredConstructors.length);
      for (RoutineInfo methodInfo : declaredConstructors)
      {
         out.writeObject(methodInfo);
      }

      out.writeInt(fields.length);
      for (FieldInfo methodInfo : fields)
      {
         out.writeObject(methodInfo);
      }

      out.writeInt(declaredFields.length);
      for (FieldInfo methodInfo : declaredFields)
      {
         out.writeObject(methodInfo);
      }

      out.writeInt(methods.length);
      for (MethodInfo methodInfo : methods)
      {
         out.writeObject(methodInfo);
      }

      out.writeInt(declaredMethods.length);
      for (MethodInfo methodInfo : declaredMethods)
      {
         out.writeObject(methodInfo);
      }

   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      superClass = (String)in.readObject();

      int interfacesCount = in.readInt();
      interfaces = new String[interfacesCount];
      for (int i = 0; i < interfacesCount; i++)
      {
         interfaces[i] = (String)in.readObject();
      }

      int constructorsCount = in.readInt();
      constructors = new RoutineInfo[constructorsCount];
      for (int i = 0; i < constructorsCount; i++)
      {
         constructors[i] = (RoutineInfo)in.readObject();
      }

      int declaredConstructorsCount = in.readInt();
      declaredConstructors = new RoutineInfo[declaredConstructorsCount];
      for (int i = 0; i < declaredConstructorsCount; i++)
      {
         declaredConstructors[i] = (RoutineInfo)in.readObject();
      }

      int fieldsCount = in.readInt();
      fields = new FieldInfo[fieldsCount];
      for (int i = 0; i < fieldsCount; i++)
      {
         fields[i] = (FieldInfo)in.readObject();
      }

      int declaredFieldsCount = in.readInt();
      declaredFields = new FieldInfo[declaredFieldsCount];
      for (int i = 0; i < declaredFieldsCount; i++)
      {
         declaredFields[i] = (FieldInfo)in.readObject();
      }

      int methodsCount = in.readInt();
      methods = new MethodInfo[methodsCount];
      for (int i = 0; i < methodsCount; i++)
      {
         methods[i] = (MethodInfo)in.readObject();
      }

      int declaredMethodsCount = in.readInt();
      declaredMethods = new MethodInfo[declaredMethodsCount];
      for (int i = 0; i < declaredMethodsCount; i++)
      {
         declaredMethods[i] = (MethodInfo)in.readObject();
      }
   }

}
