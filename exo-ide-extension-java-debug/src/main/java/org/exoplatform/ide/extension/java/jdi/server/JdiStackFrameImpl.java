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

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Field;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JdiStackFrameImpl implements JdiStackFrame
{
   private final StackFrame stackFrame;
   private JdiField[] fields;
   private JdiLocalVariable[] localVariables;

   public JdiStackFrameImpl(StackFrame stackFrame)
   {
      this.stackFrame = stackFrame;
   }

   @Override
   public JdiField[] getFields() throws DebuggerException
   {
      if (fields == null)
      {
         try
         {
            ObjectReference object = stackFrame.thisObject();
            if (object == null)
            {
               ReferenceType type = stackFrame.location().declaringType();
               List<Field> fs = stackFrame.location().declaringType().allFields();
               fields = new JdiField[fs.size()];
               int i = 0;
               for (Field f : fs)
               {
                  fields[i++] = new JdiFieldImpl(f, type);
               }
            }
            else
            {
               List<Field> fs = object.referenceType().allFields();
               fields = new JdiField[fs.size()];
               int i = 0;
               for (Field f : fs)
               {
                  fields[i++] = new JdiFieldImpl(f, object);
               }
            }

            Arrays.sort(fields);
         }
         catch (InvalidStackFrameException e)
         {
            throw new DebuggerException(e.getMessage(), e);
         }
      }
      return fields;
   }

   @Override
   public JdiField getFieldByName(String name) throws DebuggerException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Field name may not be null. ");
      }
      for (JdiField f : getFields())
      {
         if (name.equals(f.getName()))
         {
            return f;
         }
      }
      return null;
   }

   @Override
   public JdiLocalVariable[] getLocalVariables() throws DebuggerException
   {
      if (localVariables == null)
      {
         try
         {
            List<LocalVariable> targetVariables = stackFrame.visibleVariables();
            localVariables = new JdiLocalVariable[targetVariables.size()];
            int i = 0;
            for (LocalVariable var : targetVariables)
            {
               localVariables[i++] = new JdiLocalVariableImpl(stackFrame, var);
            }
         }
         catch (AbsentInformationException e)
         {
            throw new DebuggerException(e.getMessage(), e);
         }
         catch (InvalidStackFrameException e)
         {
            throw new DebuggerException(e.getMessage(), e);
         }
         catch (NativeMethodException e)
         {
            throw new DebuggerException(e.getMessage(), e);
         }
      }
      return localVariables;
   }

   @Override
   public JdiLocalVariable getLocalVariableByName(String name) throws DebuggerException
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Field name may not be null. ");
      }
      for (JdiLocalVariable var : getLocalVariables())
      {
         if (name.equals(var.getName()))
         {
            return var;
         }
      }
      return null;
   }
}
