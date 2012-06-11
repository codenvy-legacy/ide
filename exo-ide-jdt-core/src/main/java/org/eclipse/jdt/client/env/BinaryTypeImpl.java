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
import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryField;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryNestedType;
import org.eclipse.jdt.client.internal.compiler.env.IBinaryType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 17, 2012 2:41:08 PM evgen $
 */
public class BinaryTypeImpl implements IBinaryType
{

   private JSONObject jsObj;

   /** @param jsObj */
   public BinaryTypeImpl(JSONObject jsObj)
   {
      super();
      this.jsObj = jsObj;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericType#getModifiers() */
   @Override
   public int getModifiers()
   {
      return (int)jsObj.get("modifiers").isNumber().doubleValue();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IGenericType#isBinaryType() */
   @Override
   public boolean isBinaryType()
   {
      return true;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IDependent#getFileName() */
   @Override
   public char[] getFileName()
   {
      return getSourceName();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getAnnotations() */
   @Override
   public IBinaryAnnotation[] getAnnotations()
   {
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getEnclosingMethod() */
   @Override
   public char[] getEnclosingMethod()
   {
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getEnclosingTypeName() */
   @Override
   public char[] getEnclosingTypeName()
   {

      char[] name = getName();
      if (CharOperation.contains('$', name))
      {
         return CharOperation.subarray(name, 0, CharOperation.indexOf('$', name));
      }
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getFields() */
   @Override
   public IBinaryField[] getFields()
   {
      JSONArray array = jsObj.get("fields").isArray();
      if (array.size() == 0)
         return null;

      IBinaryField[] fields = new IBinaryField[array.size()];

      for (int i = 0; i < array.size(); i++)
      {
         fields[i] = new BinaryFieldImpl(array.get(i).isObject());
      }

      return fields;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getGenericSignature() */
   @Override
   public char[] getGenericSignature()
   {
      if (jsObj.containsKey("signature") && jsObj.get("signature").isNull() == null)
      {
         String stringValue = jsObj.get("signature").isString().stringValue();
         if (!stringValue.isEmpty())
            return stringValue.toCharArray();
      }
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getInterfaceNames() */
   @Override
   public char[][] getInterfaceNames()
   {
      JSONArray array = jsObj.get("interfaces").isArray();
      if (array.size() == 0)
         return null;
      char res[][] = new char[array.size()][];
      for (int i = 0; i < array.size(); i++)
      {
         res[i] = array.get(i).isString().stringValue().replaceAll("\\.", "/").toCharArray();
      }

      return res;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getMemberTypes() */
   @Override
   public IBinaryNestedType[] getMemberTypes()
   {
      if(jsObj.get("nestedTypes").isArray() != null)
      {
         JSONArray array = jsObj.get("nestedTypes").isArray();
         IBinaryNestedType[] nested = new IBinaryNestedType[array.size()];
         char[] parentType = getName();
         for(int i = 0; i< array.size(); i++)
         {
            nested[i] = new BinaryNestedTypeImpl(parentType, array.get(i).isObject());
         }
         return nested;
      }
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getMethods() */
   @Override
   public IBinaryMethod[] getMethods()
   {
      JSONArray array = jsObj.get("methods").isArray();
      if (array.size() == 0)
         return null;

      // remove methods not declared in this class
      List<IBinaryMethod> methods = new ArrayList<IBinaryMethod>();
      String fqn = jsObj.get("name").isString().stringValue();
      for (int i = 0; i < array.size(); i++)
      {
         JSONObject object = array.get(i).isObject();
         if (object.get("declaringClass").isString().stringValue().equals(fqn))
            methods.add(new BinaryMethodImpl(object));
      }

      return methods.toArray(new IBinaryMethod[methods.size()]);
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getMissingTypeNames() */
   @Override
   public char[][][] getMissingTypeNames()
   {
      return null;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getName() */
   @Override
   public char[] getName()
   {
      return jsObj.get("name").isString().stringValue().replaceAll("\\.", "/").toCharArray();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getSourceName() */
   @Override
   public char[] getSourceName()
   {
      String name = jsObj.get("name").isString().stringValue();
      if (name.contains("$"))
         return Signature.getSimpleName(name.substring(name.lastIndexOf("$") + 1)).toCharArray();

      return Signature.getSimpleName(name).toCharArray();

   }

   /**
    * Answer the resolved name of the type in the source file format: <code>java.lang.String</code>
    * 
    * @return
    */
   public char[] getFqn()
   {
      return jsObj.get("name").isString().stringValue().toCharArray();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getSuperclassName() */
   @Override
   public char[] getSuperclassName()
   {
      String value = jsObj.get("superClass").isString().stringValue();
      if (value.isEmpty())
         return null;
      return value.replaceAll("\\.", "/").toCharArray();
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#getTagBits() */
   @Override
   public long getTagBits()
   {
      return 0;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#isAnonymous() */
   @Override
   public boolean isAnonymous()
   {
      return false;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#isLocal() */
   @Override
   public boolean isLocal()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#isMember() */
   @Override
   public boolean isMember()
   {
      return CharOperation.contains('$', getName());
   }

   /** @see org.eclipse.jdt.client.internal.compiler.env.IBinaryType#sourceFileName() */
   @Override
   public char[] sourceFileName()
   {
      return null;
   }

   /**
    * JSON representation of this object
    * 
    * @return JSON string
    */
   public String toJsonString()
   {
      return jsObj.toString();
   }

}
