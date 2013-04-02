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
 * Instance of class member in debuggee JVM.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JdiField extends JdiVariable {
    /**
     * Check is this field is final.
     *
     * @return <code>true</code> if field is final and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isFinal() throws DebuggerException;

    /**
     * Check is this field is static.
     *
     * @return <code>true</code> if field is static and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isStatic() throws DebuggerException;

    /**
     * Check is this transient is transient.
     *
     * @return <code>true</code> if field is transient and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isTransient() throws DebuggerException;

    /**
     * Check is this field is volatile.
     *
     * @return <code>true</code> if field is volatile and <code>false</code> otherwise
     * @throws DebuggerException
     *         if an error occurs
     */
    boolean isVolatile() throws DebuggerException;
}
