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

import org.eclipse.jdt.client.core.IJavaElement;
import org.eclipse.jdt.client.core.IType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:28:31 AM Mar 30, 2012 evgen $
 * 
 */
public class TypeImpl implements IType
{

   private JSONObject jsObj;
   
   /**
    * @param jsObj
    */
   public TypeImpl(JSONObject jsObj)
   {
      this.jsObj = jsObj;
   }

   /**
    * @see org.eclipse.jdt.client.core.IJavaElement#getElementName()
    */
   @Override
   public String getElementName()
   {
      return jsObj.get("name").isString().stringValue();
   }

   /**
    * @see org.eclipse.jdt.client.core.IJavaElement#getElementType()
    */
   @Override
   public int getElementType()
   {
      return IJavaElement.TYPE;
   }

   /**
    * @see org.eclipse.jdt.client.core.IType#getFlags()
    */
   @Override
   public int getFlags()
   {
      return (int)jsObj.get("modifiers").isNumber().doubleValue();
   }

   /**
    * @see org.eclipse.jdt.client.core.IType#getFullyQualifiedName()
    */
   @Override
   public String getFullyQualifiedName()
   {
      return jsObj.get("name").isString().stringValue();
   }

}
