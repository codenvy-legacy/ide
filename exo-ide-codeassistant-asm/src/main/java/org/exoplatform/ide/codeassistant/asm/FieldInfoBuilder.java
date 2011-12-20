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

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;

/**
 * This class used for building FieldInfo objects
 */
public class FieldInfoBuilder extends MemberInfoBuilder
{

   protected final String desc;

   protected final String declaredClass;

   public FieldInfoBuilder(int access, String name, String desc, String declaredClass)
   {
      super(access, name);
      this.desc = desc.replace('/', '.');
      this.declaredClass = declaredClass;
   }

   public FieldInfo buildFieldInfo()
   {
      return new FieldInfo(transformTypeFormat(desc), access, name, declaredClass);
   }

}
