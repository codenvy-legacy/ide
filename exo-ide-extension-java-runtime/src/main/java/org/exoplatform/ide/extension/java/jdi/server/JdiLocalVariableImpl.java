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
package org.exoplatform.ide.extension.java.jdi.server;

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Value;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JdiLocalVariableImpl implements JdiLocalVariable
{
   private final LocalVariable variable;
   private final StackFrame stackFrame;

   public JdiLocalVariableImpl(StackFrame stackFrame, LocalVariable variable)
   {
      this.stackFrame = stackFrame;
      this.variable = variable;
   }

   @Override
   public String getName()
   {
      return variable.name();
   }

   @Override
   public boolean isArray() throws DebuggerException
   {
      return JdiType.isArray(variable.signature());
   }

   @Override
   public boolean isPrimitive() throws DebuggerException
   {
      return JdiType.isPrimitive(variable.signature());
   }

   @Override
   public JdiValue getValue()
   {
      Value value = stackFrame.getValue(variable);
      if (value == null)
      {
         return new JdiNullValue();
      }
      return new JdiValueImpl(value);
   }

   @Override
   public String getTypeName()
   {
      return variable.typeName();
   }
}
