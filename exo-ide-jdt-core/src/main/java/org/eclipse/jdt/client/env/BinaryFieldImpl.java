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

import org.eclipse.jdt.client.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.client.internal.compiler.impl.Constant;

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
