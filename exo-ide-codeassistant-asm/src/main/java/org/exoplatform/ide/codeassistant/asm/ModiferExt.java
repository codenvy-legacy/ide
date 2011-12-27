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
package org.exoplatform.ide.codeassistant.asm;

import java.lang.reflect.Modifier;

/**
 *
 */
public class ModiferExt extends Modifier
{
   /**
    * There are constants for <b>modifier</b> flag.<br>
    * There are no (yet) <b>public</b> constants for this flags in {@list
    * Modifier}, because <b>they have different meanings for fields and
    * methods</b>. More details, see in {@link Modifier} source;
    * 
    */

   public static final int ANNOTATION = 0x00002000;

   public static final int ENUM = 0x00004000;

   /**
    * Return <tt>true</tt> if the integer argument includes the
    * <tt>annotation</tt> modifier, <tt>false</tt> otherwise.
    * 
    * @param mod
    *           a set of modifiers
    * @return <tt>true</tt> if <code>mod</code> includes the <tt>annotation</tt>
    *         modifier; <tt>false</tt> otherwise.
    */
   public static boolean isAnnotation(int mod)
   {
      return (mod & ANNOTATION) != 0;
   }

   /**
    * Return <tt>true</tt> if the integer argument includes the <tt>enum</tt>
    * modifier, <tt>false</tt> otherwise.
    * 
    * @param mod
    *           a set of modifiers
    * @return <tt>true</tt> if <code>mod</code> includes the <tt>enum</tt>
    *         modifier; <tt>false</tt> otherwise.
    */
   public static boolean isEnum(int mod)
   {
      return (mod & ENUM) != 0;
   }

}
