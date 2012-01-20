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

import org.exoplatform.ide.codeassistant.jvm.shared.Member;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MemberNode;

/**
 * Member based on org.objectweb.asm.tree.MemberNode created during class file
 * parsing.
 * 
 * @see org.objectweb.asm.tree.MemberNode
 */
public class AsmMember implements Member
{
   //for future use
   private final MemberNode memberNode;

   private final String name;

   private final int modifiers;

   public AsmMember(String name, int modifiers, MemberNode memberNode)
   {
      super();
      this.name = name;
      this.modifiers = modifiers;
      this.memberNode = memberNode;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#getModifiers()
    */
   @Override
   public int getModifiers()
   {
      return modifiers;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#setModifiers(int)
    */
   @Override
   public void setModifiers(int modifiers)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.Member#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      throw new UnsupportedOperationException("Set not supported");
   }

   public static String classNameFromType(String type)
   {
      // can be null for Object super class.
      return type == null ? "" : Type.getObjectType(type).getClassName();
   }

}
