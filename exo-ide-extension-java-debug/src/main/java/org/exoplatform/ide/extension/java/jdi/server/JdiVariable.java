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
 * Variable at debuggee JVM.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see JdiField
 * @see JdiLocalVariable
 * @see JdiArrayElement
 */
public interface JdiVariable
{
   /**
    * Name of variable. If this variable is element of array then name is: <i>[i]</i>, where <i>i</i> - index of element
    *
    * @return name of variable
    * @throws DebuggerException
    *    if an error occurs
    */
   String getName() throws DebuggerException;

   /**
    * Check is this variable is array.
    *
    * @return <code>true</code> if variable is array and <code>false</code> otherwise
    * @throws DebuggerException
    *    if an error occurs
    */
   boolean isArray() throws DebuggerException;

   /**
    * Check is this variable is primitive.
    *
    * @return <code>true</code> if variable is primitive and <code>false</code> otherwise
    * @throws DebuggerException
    *    if an error occurs
    */
   boolean isPrimitive() throws DebuggerException;

   /**
    * Get value of variable.
    *
    * @return value
    * @throws DebuggerException
    *    if an error occurs
    */
   JdiValue getValue() throws DebuggerException;

   /**
    * Name of variable type.
    *
    * @return type name
    * @throws DebuggerException
    *    if an error occurs
    */
   String getTypeName() throws DebuggerException;
}
