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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import java.util.List;



/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TypeInfo extends ShortTypeInfo implements ITypeInfo
{
   private List<IMethodInfo> methods;

   private List<IMethodInfo> declaredMethods;

   private List<IRoutineInfo> constructors;

   private List<IRoutineInfo> declaredConstructors;

   private List<IFieldInfo> fields;

   private List<IFieldInfo> declaredFields;

   private String superClass;

   private List<String> interfaces;

   public TypeInfo()
   {
   }

   public TypeInfo(Integer modifiers, String name, List<IMethodInfo> methods, List<IMethodInfo> declaredMethods,
      List<IRoutineInfo> constructors, List<IRoutineInfo> declaredConstructors, List<IFieldInfo> fields, List<IFieldInfo> declaredFields,
      String superClass, List<String> interfaces, String qualifiedName, String type)
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
    * {@inheritDoc}
    */
   @Override
   public List<IMethodInfo> getMethods()
   {
      return methods;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setMethods(List<IMethodInfo> methods)
   {
      this.methods = methods;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<IMethodInfo> getDeclaredMethods()
   {
      return declaredMethods;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDeclaredMethods(List<IMethodInfo> declaredMethods)
   {
      this.declaredMethods = declaredMethods;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<IRoutineInfo> getConstructors()
   {
      return constructors;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setConstructors(List<IRoutineInfo> constructors)
   {
      this.constructors = constructors;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<IRoutineInfo> getDeclaredConstructors()
   {
      return declaredConstructors;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDeclaredConstructors(List<IRoutineInfo> declaredConstructors)
   {
      this.declaredConstructors = declaredConstructors;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<IFieldInfo> getFields()
   {
      return fields;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setFields(List<IFieldInfo> fields)
   {
      this.fields = fields;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<IFieldInfo> getDeclaredFields()
   {
      return declaredFields;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDeclaredFields(List<IFieldInfo> declaredFields)
   {
      this.declaredFields = declaredFields;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getSuperClass()
   {
      return superClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setSuperClass(String superClass)
   {
      this.superClass = superClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<String> getInterfaces()
   {
      return interfaces;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setInterfaces(List<String> interfaces)
   {
      this.interfaces = interfaces;
   }

   

   
}
