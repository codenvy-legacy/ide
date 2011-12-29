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

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readObjectList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTFList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeObjectList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTFList;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
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
      this.methods = Collections.emptyList();
      this.fields = Collections.emptyList();
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
    * @return the fields
    */
   public List<FieldInfo> getFields()
   {
      return fields;
   }

   /**
    * @return the interfaces
    */
   public List<String> getInterfaces()
   {
      return interfaces;
   }

   /**
    * @return the methods
    */
   public List<MethodInfo> getMethods()
   {
      return methods;
   }

   /**
    * @return the superClass
    */
   public String getSuperClass()
   {
      return superClass;
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
   {
      super.readExternal(in);
      superClass = readStringUTF(in);
      interfaces = readStringUTFList(in);
      fields = readObjectList(FieldInfo.class, in);
      methods = readObjectList(MethodInfo.class, in);
   }

   /**
    * @param fields
    *           the fields to set
    */
   public void setFields(List<FieldInfo> fields)
   {
      if (fields == null)
      {
         this.fields = Collections.emptyList();
      }
      else
      {
         this.fields = fields;
      }
   }

   /**
    * @param interfaces
    *           the interfaces to set
    */
   public void setInterfaces(List<String> interfaces)
   {
      if (interfaces == null)
      {
         this.interfaces = Collections.emptyList();
      }
      else
      {
         this.interfaces = interfaces;
      }
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
    * @param superClass
    *           the superClass to set
    */
   public void setSuperClass(String superClass)
   {
      this.superClass = superClass;
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException
   {
      super.writeExternal(out);
      writeStringUTF(superClass, out);
      writeStringUTFList(interfaces, out);
      writeObjectList(FieldInfo.class, fields, out);
      writeObjectList(MethodInfo.class, methods, out);
   }

}
