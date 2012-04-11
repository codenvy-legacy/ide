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
 * Value of JdiVariable.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JdiValue
{
   /**
    * Get value in String representation.
    *
    * @return value in String representation
    * @throws DebuggerException
    *    if an error occurs
    */
   String getAsString() throws DebuggerException;

   /**
    * Get nested variables.
    *
    * @return nested variables. This method always returns empty array for primitive type since primitive type has not
    *         any fields. If value represents array this method returns array members
    * @throws DebuggerException
    *    if an error occurs
    */
   JdiVariable[] getVariables() throws DebuggerException;

   /**
    * Get nested variable by name.
    *
    * @param name
    *    name of variable. Typically it is name of field. If this value represents array then name should be in form:
    *    <i>[i]</i>, where <i>i</i> is index of element
    * @return nested variable with specified name or <code>null</code> if there is no such variable
    * @throws DebuggerException
    *    if an error occurs
    * @see org.exoplatform.ide.extension.java.jdi.server.JdiVariable#getName()
    */
   JdiVariable getVariableByName(String name) throws DebuggerException;
}
