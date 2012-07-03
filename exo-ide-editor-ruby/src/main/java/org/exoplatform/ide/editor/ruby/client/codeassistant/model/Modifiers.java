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
 *******************************************************************************/
/*
 * (c) 2002, 2005 xored software and others all rights reserved. http://www.xored.com
 */

package org.exoplatform.ide.editor.ruby.client.codeassistant.model;

/**
 * Flags for declaration modifiers
 */
public interface Modifiers
{

   public static final int AccDefault = 0;

   public static final int AccAbstract = (1 << 0);

   /**
    * Constant declaration
    */
   public static final int AccConstant = (1 << 1);

   public static final int AccFinal = (1 << 2);

   public static final int AccInterface = (1 << 3);

   public static final int AccPrivate = (1 << 4);

   public static final int AccProtected = (1 << 5);

   public static final int AccPublic = (1 << 6);

   public static final int AccStatic = (1 << 7);

   public final static int AccReference = (1 << 8);

   public final static int AccConst = (1 << 9);

   /**
    * Module declaration (class and module are both types and can be distinguished by this flag)
    */
   public static final int AccModule = (1 << 10);

   /**
    * Namespace (type it TCL)
    */
   public static final int AccNameSpace = (1 << 11);

   public static final int AccAnnotation = (1 << 12);

   public static final int AccGlobal = (1 << 13);

   public static final int AccUpVar = (1 << 14);

   public static final int AccTestCase = (1 << 15);

   public static final int AccTest = (1 << 16);

   /**
    * @since 2.0
    */
   public static final int AccSynthetic = (1 << 17);

   public static final int USER_MODIFIER = 18;
}