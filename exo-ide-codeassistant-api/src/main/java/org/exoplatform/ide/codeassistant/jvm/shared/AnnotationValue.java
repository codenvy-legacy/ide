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
package org.exoplatform.ide.codeassistant.jvm.shared;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface AnnotationValue
{

   /**
    * Must return two element array.
    * First element name of primitive type:
    * <ul>
    * <li> char
    * <li> byte
    * <li> boolean
    * <li> int
    * <li>float
    * ...
    * </ul>
    * Second element is value of primitive type
    * @return
    */
   String[] getPrimitiveType();

   /**
    * Must return array with of values, where first value is Array type
    * @return
    */
   String[] getArrayType();

   String getClassSignature();

   /**
    * Must return two element array, where first element is FQN of enum,
    * second element is constant name;
    * @return
    */
   String[] getEnumConstant();

   Annotation getAnnotation();

   void setPrimitiveType(String[] value);

   void setArrayType(String[] value);

   void setClassSignature(String value);

   void setEnumConstant(String[] value);

   void setAnnotation(Annotation annotation);
}
