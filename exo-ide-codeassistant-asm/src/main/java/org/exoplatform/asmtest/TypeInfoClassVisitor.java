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
package org.exoplatform.asmtest;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class TypeInfoClassVisitor implements ClassVisitor
{

   private TypeInfoBuilder builder;

   public TypeInfoBuilder getBuilder()
   {
      return builder;
   }

   @Override
   public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
   {
      this.builder = new TypeInfoBuilder(access, name, superName, interfaces);
   }

   @Override
   public void visitSource(String source, String debug)
   {
   }

   @Override
   public void visitOuterClass(String owner, String name, String desc)
   {
   }

   @Override
   public AnnotationVisitor visitAnnotation(String desc, boolean visible)
   {
      return null;
   }

   @Override
   public void visitAttribute(Attribute attr)
   {
   }

   @Override
   public void visitInnerClass(String name, String outerName, String innerName, int access)
   {
   }

   @Override
   public FieldVisitor visitField(int access, String name, String desc, String signature, Object value)
   {
      builder.addField(access, name, desc);
      return null;
   }

   @Override
   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
   {
      if (name.equals("<init>"))
      {
         builder.addConstructor(access, exceptions, desc);
      }
      else if (!name.equals("<clinit>"))
      {
         builder.addMethod(access, name, exceptions, desc);
      }
      return null;
   }

   @Override
   public void visitEnd()
   {
   }

}
