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
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.*;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 */
public class TypeInfo extends ShortTypeInfo
{
   private List<MethodInfo> methods;

   private List<FieldInfo> fields;

   private String superClass;

   private List<String> interfaces;

   public TypeInfo()
   {
   }

   public TypeInfo(String name, int modifiers, List<MethodInfo> methods, List<FieldInfo> fields, String superClass,
      List<String> interfaces, String type)
   {
      super(name, modifiers, type);
      this.methods = methods;
      this.fields = fields;
      this.superClass = superClass;
      this.interfaces = interfaces;
   }

   /**
    * @return the methods
    */
   public List<MethodInfo> getMethods()
   {
      return methods;
   }

   /**
    * @param methods
    *           the methods to set
    */
   public void setMethods(List<MethodInfo> methods)
   {
      this.methods = methods;
   }

   /**
    * @return the fields
    */
   public List<FieldInfo> getFields()
   {
      return fields;
   }

   /**
    * @param fields
    *           the fields to set
    */
   public void setFields(List<FieldInfo> fields)
   {
      this.fields = fields;
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
   public List<String> getInterfaces()
   {
      return interfaces;
   }

   /**
    * @param interfaces
    *           the interfaces to set
    */
   public void setInterfaces(List<String> interfaces)
   {
      this.interfaces = interfaces;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      writeStringUTF(superClass, out);
      writeStringUTFArray(interfaces, out);
      writeObjectArray(FieldInfo.class, fields, out);
      writeObjectArray(MethodInfo.class, methods, out);
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      superClass = readStringUTF(in);
      interfaces = readStringUTFArray(in);
      fields = readObjectArray(FieldInfo.class, in);
      methods = readObjectArray(MethodInfo.class, in);
   }

}
