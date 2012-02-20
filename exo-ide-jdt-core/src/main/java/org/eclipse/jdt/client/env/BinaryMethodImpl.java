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
package org.eclipse.jdt.client.env;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 4:52:34 PM evgen $
 */
public class BinaryMethodImpl implements IBinaryMethod
{

   private JSONObject method;

   /** @param method */
   public BinaryMethodImpl(JSONObject method)
   {
      super();
      this.method = method;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericMethod#getModifiers() */
   @Override
   public int getModifiers()
   {
      return (int)method.get("modifiers").isNumber().doubleValue();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericMethod#isConstructor() */
   @Override
   public boolean isConstructor()
   {
      return method.get("constructor").isBoolean().booleanValue();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericMethod#getArgumentNames() */
   @Override
   public char[][] getArgumentNames()
   {
      JSONArray array = method.get("parameterNames").isArray();
      if (array.size() == 0)
         return null;

      char res[][] = new char[array.size()][];
      for (int i = 0; i < array.size(); i++)
      {
         res[i] = array.get(i).isString().stringValue().toCharArray();
      }

      return res;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getAnnotations() */
   @Override
   public IBinaryAnnotation[] getAnnotations()
   {
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getDefaultValue() */
   @Override
   public Object getDefaultValue()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getExceptionTypeNames() */
   @Override
   public char[][] getExceptionTypeNames()
   {
      JSONArray array = method.get("exceptionTypes").isArray();
      if (array.size() == 0)
         return null;

      char res[][] = new char[array.size()][];
      for (int i = 0; i < array.size(); i++)
      {
         res[i] = array.get(i).isString().stringValue().replaceAll("\\.", "/").toCharArray();
      }

      return res;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getGenericSignature() */
   @Override
   public char[] getGenericSignature()
   {
      if (method.containsKey("signature"))
      {
         String stringValue = method.get("signature").isString().stringValue();
         if (!stringValue.isEmpty())
            return stringValue.toCharArray();
      }
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getMethodDescriptor() */
   @Override
   public char[] getMethodDescriptor()
   {
      return method.get("descriptor").isString().stringValue().toCharArray();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getParameterAnnotations(int) */
   @Override
   public IBinaryAnnotation[] getParameterAnnotations(int index)
   {
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getSelector() */
   @Override
   public char[] getSelector()
   {
      if (isConstructor())
         return ConstantPool.Init;

      return Signature.getSimpleName(method.get("name").isString().stringValue().toCharArray());
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#getTagBits() */
   @Override
   public long getTagBits()
   {
      return 0;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod#isClinit() */
   @Override
   public boolean isClinit()
   {
      // TODO Auto-generated method stub
      return false;
   }

}
