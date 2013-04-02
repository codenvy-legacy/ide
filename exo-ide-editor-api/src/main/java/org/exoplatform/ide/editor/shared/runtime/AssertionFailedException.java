/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.editor.shared.runtime;

/**
 * <code>AssertionFailedException</code> is a runtime exception thrown by some of the methods in <code>Assert</code>.
 * <p>
 * This class can be used without OSGi running.
 * </p>
 * <p>
 * This class is not intended to be instantiated or sub-classed by clients.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @see Assert
 * @since org.eclipse.equinox.common 3.2
 */
public class AssertionFailedException extends RuntimeException {

    /** All serializable objects should have a stable serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param detail
     *         the message
     */
    public AssertionFailedException(String detail) {
        super(detail);
    }
}
