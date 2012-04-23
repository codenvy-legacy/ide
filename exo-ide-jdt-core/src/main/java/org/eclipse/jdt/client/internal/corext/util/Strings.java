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
package org.eclipse.jdt.client.internal.corext.util;

import org.eclipse.jdt.client.core.compiler.CharOperation;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class Strings
{

   /**
    * Returns <code>true</code> if the given string only consists of
    * white spaces according to Java. If the string is empty, <code>true
    * </code> is returned.
    *
    * @param s the string to test
    * @return <code>true</code> if the string only consists of white
    *    spaces; otherwise <code>false</code> is returned
    *
    * @see java.lang.Character#isWhitespace(char)
    */
   public static boolean containsOnlyWhitespaces(String s)
   {
      int size = s.length();
      for (int i = 0; i < size; i++)
      {
         if (!CharOperation.isWhitespace(s.charAt(i)))
            return false;
      }
      return true;
   }

}
