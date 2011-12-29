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
package org.exoplatform.ide.codeassistant.jvm.bean;

import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readObjectList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.readStringUTFList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeObjectList;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTF;
import static org.exoplatform.ide.codeassistant.jvm.serialization.ExternalizationTools.writeStringUTFList;

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class TypeInfoBean extends ShortTypeInfoBean implements TypeInfo
{
   private List<MethodInfo> methods;

   private List<FieldInfo> fields;

   private String superClass;

   private List<String> interfaces;

   public TypeInfoBean()
   {
      this.methods = Collections.emptyList();
      this.fields = Collections.emptyList();
   }

   public TypeInfoBean(String name, int modifiers, List<MethodInfo> methods, List<FieldInfo> fields, String superClass,
      List<String> interfaces, String type)
   {
      super(name, modifiers, type);
      this.superClass = superClass;
      setMethods(methods);
      setFields(fields);
      setInterfaces(interfaces);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getFields()
    */
   @Override
   public List<FieldInfo> getFields()
   {
      return fields;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getInterfaces()
    */
   @Override
   public List<String> getInterfaces()
   {
      return interfaces;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getMethods()
    */
   @Override
   public List<MethodInfo> getMethods()
   {
      return methods;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getSuperClass()
    */
   @Override
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
      fields = readObjectList(FieldInfo.class, FieldInfoBean.class, in);
      methods = readObjectList(MethodInfo.class, MethodInfoBean.class, in);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setFields(java.util.List)
    */
   @Override
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
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setInterfaces(java.util.List)
    */
   @Override
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
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setMethods(java.util.List)
    */
   @Override
   public void setMethods(List<MethodInfo> methods)
   {
      this.methods = methods;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setSuperClass(java.lang.String)
    */
   @Override
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
      writeObjectList(FieldInfoBean.class, fields, out);
      writeObjectList(MethodInfoBean.class, methods, out);
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (fields == null ? 0 : fields.hashCode());
      result = prime * result + (interfaces == null ? 0 : interfaces.hashCode());
      result = prime * result + (methods == null ? 0 : methods.hashCode());
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
      TypeInfoBean other = (TypeInfoBean)obj;
      if (fields == null)
      {
         if (other.fields != null)
         {
            return false;
         }
      }
      else if (!fields.equals(other.fields))
      {
         return false;
      }
      if (interfaces == null)
      {
         if (other.interfaces != null)
         {
            return false;
         }
      }
      else if (!interfaces.equals(other.interfaces))
      {
         return false;
      }
      if (methods == null)
      {
         if (other.methods != null)
         {
            return false;
         }
      }
      else if (!methods.equals(other.methods))
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
