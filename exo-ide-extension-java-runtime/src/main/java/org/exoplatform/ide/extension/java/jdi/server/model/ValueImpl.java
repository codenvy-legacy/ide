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

import org.exoplatform.ide.extension.java.jdi.shared.Value;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ValueImpl implements Value
{
   private List<Variable> variables;
   private String value;

   public ValueImpl(List<Variable> variables, String value)
   {
      this.variables = variables;
      this.value = value;
   }

   public ValueImpl()
   {
   }

   @Override
   public List<Variable> getVariables()
   {
      if (variables == null)
      {
         variables = new ArrayList<Variable>();
      }
      return variables;
   }

   @Override
   public void setVariables(List<Variable> variables)
   {
      this.variables = variables;
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
   public String toString()
   {
      return "ValueImpl{" +
         "variables=" + variables +
         ", value='" + value + '\'' +
         '}';
   }
}
