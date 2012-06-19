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

/**
 * State of method invocation.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JdiStackFrame
{
   /**
    * Get all available instance or class members.
    *
    * @return list of fields. Fields should be ordered:
    *         <ul>
    *         <li>static fields should go before non-static fields</li>
    *         <li>fields of the same type should be ordered by name</li>
    *         </ul>
    * @throws DebuggerException
    *    if an error occurs
    */
   JdiField[] getFields() throws DebuggerException;

   /**
    * Get field by name.
    *
    * @return field or <code>null</code> if there is not such field
    * @throws DebuggerException
    *    if an error occurs
    */
   JdiField getFieldByName(String name) throws DebuggerException;

   /**
    * Get all available local variables.
    *
    * @return list of local variables
    * @throws DebuggerException
    *    if an error occurs
    */
   JdiLocalVariable[] getLocalVariables() throws DebuggerException;

   /**
    * Get local variable by name.
    *
    * @return local variable or <code>null</code> if there is not such local variable
    * @throws DebuggerException
    *    if an error occurs
    */
   JdiLocalVariable getLocalVariableByName(String name) throws DebuggerException;
}
