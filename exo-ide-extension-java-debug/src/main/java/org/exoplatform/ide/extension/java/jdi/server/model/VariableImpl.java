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
package org.exoplatform.ide.extension.java.jdi.server.model;

import org.exoplatform.ide.extension.java.jdi.shared.Variable;
import org.exoplatform.ide.extension.java.jdi.shared.VariablePath;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class VariableImpl implements Variable
{
   private String name;
   private String value;
   private String type;
   private VariablePath variablePath;
   private boolean primitive;

   public VariableImpl(String name,
                       String value,
                       String type,
                       VariablePath variablePath,
                       boolean primitive)
   {
      this.name = name;
      this.value = value;
      this.type = type;
      this.variablePath = variablePath;
      this.primitive = primitive;
   }

   public VariableImpl()
   {
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String getValue()
   {
      return value;
   }

   @Override
   public void setValue(String value)
   {
      this.value = value;
   }

   @Override
   public String getType()
   {
      return type;
   }

   @Override
   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public VariablePath getVariablePath()
   {
      return variablePath;
   }

   @Override
   public void setVariablePath(VariablePath variablePath)
   {
      this.variablePath = variablePath;
   }

   @Override
   public boolean isPrimitive()
   {
      return primitive;
   }

   @Override
   public void setPrimitive(boolean primitive)
   {
      this.primitive = primitive;
   }

   @Override
   public String toString()
   {
      return "VariableImpl{" +
         "name='" + name + '\'' +
         ", value='" + value + '\'' +
         ", type='" + type + '\'' +
         ", variablePath=" + variablePath +
         ", primitive=" + primitive +
         '}';
   }
}
