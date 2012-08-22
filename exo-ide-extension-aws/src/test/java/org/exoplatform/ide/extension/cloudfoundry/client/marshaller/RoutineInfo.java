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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import java.util.List;



/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RoutineInfo extends Member implements IRoutineInfo
{
   /**
    * Array FQN of exceptions throws by method
    */
   private List<String> genericExceptionTypes;

   /**
    * Full Qualified Class Name of parameter <code>(java.lang.Object)</code>
    * (not use for now)
    */
   private String genericParameterTypes;

   /**
    * Short Class name of Parameter <code>(Object)</code>
    */
   private String parameterTypes;

   /**
    * Method declaration like:
    * <code>public boolean java.lang.String.equals(java.lang.Object)</code>
    */
   private String generic;

   /**
    * Full Qualified Class Name where method declared Example: method equals()
    * declared in java.lang.String
    */
   private String declaringClass;
   

   public RoutineInfo()
   {
   }

   public RoutineInfo(Integer modifiers, String name, List<String> genericExceptionTypes, String genericParameterTypes,
      String parameterTypes, String generic, String declaringClass)
   {
      super(modifiers, name);
      this.genericExceptionTypes = genericExceptionTypes;
      this.genericParameterTypes = genericParameterTypes;
      this.parameterTypes = parameterTypes;
      this.generic = generic;
      this.declaringClass = declaringClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getGenericParameterTypes()
   {
      return genericParameterTypes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setGenericParameterTypes(String genericParameterTypes)
   {
      this.genericParameterTypes = genericParameterTypes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setGeneric(String generic)
   {
      this.generic = generic;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getGeneric()
   {
      return generic;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDeclaringClass()
   {
      return declaringClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<String> getGenericExceptionTypes()
   {
      return genericExceptionTypes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setGenericExceptionTypes(List<String> genericExceptionTypes)
   {
      this.genericExceptionTypes = genericExceptionTypes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getParameterTypes()
   {
      return parameterTypes;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setParameterTypes(String parameterTypes)
   {
      this.parameterTypes = parameterTypes;
   }

}
