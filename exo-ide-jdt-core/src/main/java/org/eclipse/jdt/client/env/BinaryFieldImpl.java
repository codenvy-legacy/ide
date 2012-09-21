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

import com.google.gwt.json.client.JSONObject;

import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.client.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.client.internal.compiler.impl.ByteConstant;
import org.eclipse.jdt.client.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;
import org.eclipse.jdt.client.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.client.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.client.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.client.internal.compiler.impl.LongConstant;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 4:33:03 PM evgen $
 */
public class BinaryFieldImpl implements IBinaryField
{

   private JSONObject field;

   /** @param field */
   public BinaryFieldImpl(JSONObject field)
   {
      this.field = field;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericField#getModifiers() */
   @Override
   public int getModifiers()
   {
      return (int)field.get("modifiers").isNumber().doubleValue();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryField#getAnnotations() */
   @Override
   public IBinaryAnnotation[] getAnnotations()
   {
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryField#getTagBits() */
   @Override
   public long getTagBits()
   {
      return 0;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryField#getConstant() */
   @Override
   public Constant getConstant()
   {
      if (field.containsKey("value") && field.get("value").isString() != null)
      {
         try
         {
            String defaultValue = field.get("value").isString().stringValue();
            if (defaultValue.isEmpty())
               return null;
            char[] elementType = Signature.getElementType(getTypeName());
            if (elementType.length > 1)
               return null;
            switch (elementType[0])
            {
               case 'I' :
                  return IntConstant.fromValue(Integer.parseInt(defaultValue));
               case 'Z' :
                  return BooleanConstant.fromValue(Boolean.parseBoolean(defaultValue));
               case 'C' :
                  return CharConstant.fromValue(defaultValue.charAt(0));
               case 'D' :
                  return DoubleConstant.fromValue(Double.parseDouble(defaultValue));
               case 'B' :
                  return ByteConstant.fromValue(Byte.parseByte(defaultValue));
               case 'F' :
                  return FloatConstant.fromValue(Float.parseFloat(defaultValue));
               case 'J' :
                  return LongConstant.fromValue(Long.parseLong(defaultValue));
            }
         }
         catch (Throwable e)
         {
            //ignore
         }
      }
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryField#getGenericSignature() */
   @Override
   public char[] getGenericSignature()
   {
      if (field.containsKey("signature") && field.get("signature").isNull() == null)
      {
         String stringValue = field.get("signature").isString().stringValue();
         if (!stringValue.isEmpty())
            return stringValue.toCharArray();
      }
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryField#getName() */
   @Override
   public char[] getName()
   {
      return field.get("name").isString().stringValue().toCharArray();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryField#getTypeName() */
   @Override
   public char[] getTypeName()
   {
      return field.get("descriptor").isString().stringValue().toCharArray();
   }

}
