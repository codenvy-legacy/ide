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
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JdiType
{
   /**
    * <table>
    * <tr><th>Type Signature</th><th>Java Type</th></tr>
    * <tr>Z<td></td><td>boolean</td></tr>
    * <tr>B<td></td><td>byte</td></tr>
    * <tr>C<td></td><td>char</td></tr>
    * <tr>S<td></td><td>short</td></tr>
    * <tr>I<td></td><td>int</td></tr>
    * <tr>J<td></td><td>long</td></tr>
    * <tr>F<td></td><td>float</td></tr>
    * <tr>D<td></td><td>double</td></tr>
    * </table>
    *
    * @param signature
    *    variable signature
    * @return <code>true</code> if primitive and <code>false</code> otherwise
    */
   public static boolean isPrimitive(String signature)
   {
      char t = signature.charAt(0);
      return t == 'Z' || t == 'B' || t == 'C' || t == 'S' || t == 'I' || t == 'J' || t == 'F' || t == 'D';
   }

   /**
    * @param signature
    *    variable signature
    * @return <code>true</code> if array and <code>false</code> otherwise
    */
   public static boolean isArray(String signature)
   {
      return signature.charAt(0) == '[';
   }

   private JdiType()
   {
   }
}
