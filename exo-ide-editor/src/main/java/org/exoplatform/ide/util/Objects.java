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
package org.exoplatform.ide.util;


/**
 * Helper functions that can operate on any {@code Object}.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class Objects
{
   /**
    * Determines whether two possibly-null objects are equal. Returns:
    *
    * <ul>
    * <li>{@code true} if {@code a} and {@code b} are both null.
    * <li>{@code true} if {@code a} and {@code b} are both non-null and they are
    *     equal according to {@link Object#equals(Object)}.
    * <li>{@code false} in all other situations.
    * </ul>
    *
    * <p>This assumes that any non-null objects passed to this function conform
    * to the {@code equals()} contract.
    */
   public static boolean equal( Object a, Object b) {
     return a == b || (a != null && a.equals(b));
   }
}
