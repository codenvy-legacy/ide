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

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

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
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getFields()
    */
   @Override
   public List<FieldInfo> getFields()
   {
      return fields;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getInterfaces()
    */
   @Override
   public List<String> getInterfaces()
   {
      return interfaces;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getMethods()
    */
   @Override
   public List<MethodInfo> getMethods()
   {
      return methods;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getSuperClass()
    */
   @Override
   public String getSuperClass()
   {
      return superClass;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setFields(java.util.List)
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
         this.fields = Collections.unmodifiableList(fields);
      }
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setInterfaces(java.util.List)
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
         this.interfaces = Collections.unmodifiableList(interfaces);
      }
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setMethods(java.util.List)
    */
   @Override
   public void setMethods(List<MethodInfo> methods)
   {
      this.methods = Collections.unmodifiableList(methods);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setSuperClass(java.lang.String)
    */
   @Override
   public void setSuperClass(String superClass)
   {
      this.superClass = superClass;
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
