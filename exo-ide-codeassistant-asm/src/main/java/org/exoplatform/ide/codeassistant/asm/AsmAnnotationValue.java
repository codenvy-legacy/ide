/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AsmAnnotationValue implements AnnotationValue
{

   private Object defaultValue;

   /**
    * @param defaultValue
    */
   public AsmAnnotationValue(Object defaultValue)
   {
      super();
      this.defaultValue = defaultValue;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getPrimitiveType()
    */
   @Override
   public String[] getPrimitiveType()
   {
      Class<? extends Object> clazz = defaultValue.getClass();
      if (clazz == Byte.class || //
         clazz == Boolean.class || //
         clazz == Character.class || //
         clazz == Short.class || //
         clazz == Integer.class || //
         clazz == Long.class || //
         clazz == Float.class || //
         clazz == Double.class || //
         clazz == String.class //
      )
      {
         return new String[]{clazz.getSimpleName(), defaultValue.toString()};
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getArrayType()
    */
   @Override
   public String[] getArrayType()
   {
      if (defaultValue instanceof List<?>)
      {
         List<?> list = (List<?>)defaultValue;
         if(list.size() == 0)
            return new String[0];
         String[] arr = new String[list.size() + 1];
         for (int i = 0; i < list.size(); i++)
         {
            arr[i + 1] = list.get(i).toString();
         }
         arr[0] = list.get(0).getClass().getSimpleName();
         return arr;
      }
      else if (defaultValue.getClass().isArray())
      {
         String[] arr = new String[Array.getLength(defaultValue) + 1];
         for (int i = 0; i < Array.getLength(defaultValue); i++)
         {
            arr[i+1] = Array.get(defaultValue, i).toString();
         }
         arr[0] = Array.get(defaultValue, 0).getClass().getSimpleName();
         return arr;
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getClassSignature()
    */
   @Override
   public String getClassSignature()
   {
      if (defaultValue instanceof Type)
      {
         return ((Type)defaultValue).getDescriptor();
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getEnumConstant()
    */
   @Override
   public String[] getEnumConstant()
   {
      if (defaultValue instanceof String[])
      {
         return (String[])defaultValue;
      }
      else
         return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getAnnotation()
    */
   @Override
   public Annotation getAnnotation()
   {
      if(defaultValue instanceof AnnotationNode)
      {
         return new AsmAnnotation((AnnotationNode)defaultValue);
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setPrimitiveType(java.lang.String[])
    */
   @Override
   public void setPrimitiveType(String[] value)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setArrayType(java.lang.String[])
    */
   @Override
   public void setArrayType(String[] value)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setClassSignature()
    */
   @Override
   public void setClassSignature(String value)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setEnumConstant(java.lang.String[])
    */
   @Override
   public void setEnumConstant(String[] value)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setAnnotation(org.exoplatform.ide.codeassistant.jvm.shared.Annotation)
    */
   @Override
   public void setAnnotation(Annotation annotation)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

}
