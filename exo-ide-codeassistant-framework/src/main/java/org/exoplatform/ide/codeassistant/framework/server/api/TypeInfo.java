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
package org.exoplatform.ide.codeassistant.framework.server.api;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TypeInfo extends ShortTypeInfo
{
   
   
   private MethodInfo[] methods;   
   
   private MethodInfo[] declaredMethods;;
   
   private RoutineInfo[] constructors;
   
   private RoutineInfo[] declaredConstructors;
   
   private FieldInfo[] fields;
   
   private FieldInfo[] declaredFields;
   
   private String superClass;
   
   private String[] interfaces;
   
   
   public TypeInfo()
   {
   }
   
   
   public TypeInfo(Integer modifiers, String name, MethodInfo[] methods,
      MethodInfo[] declaredMethods, RoutineInfo[] constructors, RoutineInfo[] declaredConstructors,
      FieldInfo[] fields, FieldInfo[] declaredFields, String superClass, String[] interfaces,String qualifiedName, String type)
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
    * @param methods the methods to set
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
    * @param declaredMethods the declaredMethods to set
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
    * @param constructors the constructors to set
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
    * @param declaredConstructors the declaredConstructors to set
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
    * @param fields the fields to set
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
    * @param declaredFields the declaredFields to set
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
    * @param superClass the superClass to set
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
    * @param interfaces the interfaces to set
    */
   public void setInterfaces(String[] interfaces)
   {
      this.interfaces = interfaces;
   }
   
}
