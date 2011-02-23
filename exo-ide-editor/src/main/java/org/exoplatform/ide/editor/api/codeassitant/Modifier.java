/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public enum Modifier {
   STATIC(0x00000008), FINAL(0x00000010), PRIVATE(0x00000002), PUBLIC(0x00000001), PROTECTED(0x00000004), ABSTRACT(
      0x00000400), STRICTFP(0x00000800), SYNCHRONIZED(0x00000020), THREADSAFE(0), TRANSIENT(0x00000080), VOLATILE(
      0x00000040);
   private final int mod;

   Modifier(int i)
   {
      this.mod = i;
   }

   public int value()
   {
      return mod;
   }
}