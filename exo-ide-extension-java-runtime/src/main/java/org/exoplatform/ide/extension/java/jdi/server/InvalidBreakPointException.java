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
 * Thrown by {@link Debugger} when request to add new breakpoint is invalid. Typically it means the location (class
 * name or line number) is invalid.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see Debugger#addBreakPoint(org.exoplatform.ide.extension.java.jdi.shared.BreakPoint)
 */
@SuppressWarnings("serial")
public final class InvalidBreakPointException extends DebuggerException {
    public InvalidBreakPointException(String message) {
        super(message);
    }

    public InvalidBreakPointException(String message, Throwable cause) {
        super(message, cause);
    }
}
