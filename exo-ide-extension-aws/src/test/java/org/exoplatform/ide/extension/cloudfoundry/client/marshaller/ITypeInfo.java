/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface ITypeInfo extends IShortType
{

   /**
    * @return the methods
    */
   List<IMethodInfo> getMethods();

   /**
    * @param methods
    *           the methods to set
    */
   void setMethods(List<IMethodInfo> methods);

   /**
    * @return the declaredMethods
    */
   List<IMethodInfo> getDeclaredMethods();

   /**
    * @param declaredMethods
    *           the declaredMethods to set
    */
   void setDeclaredMethods(List<IMethodInfo> declaredMethods);

   /**
    * @return the constructors
    */
   List<IRoutineInfo> getConstructors();

   /**
    * @param constructors
    *           the constructors to set
    */
   void setConstructors(List<IRoutineInfo> constructors);

   /**
    * @return the declaredConstructors
    */
   List<IRoutineInfo> getDeclaredConstructors();

   /**
    * @param declaredConstructors
    *           the declaredConstructors to set
    */
   void setDeclaredConstructors(List<IRoutineInfo> declaredConstructors);

   /**
    * @return the fields
    */
   List<IFieldInfo> getFields();

   /**
    * @param fields
    *           the fields to set
    */
   void setFields(List<IFieldInfo> fields);

   /**
    * @return the declaredFields
    */
   List<IFieldInfo> getDeclaredFields();

   /**
    * @param declaredFields
    *           the declaredFields to set
    */
   void setDeclaredFields(List<IFieldInfo> declaredFields);

   /**
    * @return the superClass
    */
   String getSuperClass();

   /**
    * @param superClass
    *           the superClass to set
    */
   void setSuperClass(String superClass);

   /**
    * @return the interfaces
    */
   List<String> getInterfaces();

   /**
    * @param interfaces
    *           the interfaces to set
    */
   void setInterfaces(List<String> interfaces);

}