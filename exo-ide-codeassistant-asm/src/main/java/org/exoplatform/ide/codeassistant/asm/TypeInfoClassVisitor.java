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

import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * <p>
 * This class is asm class visitor. It's use {@link TypeInfoBuilder} class for
 * storing class data. When {@link TypeInfoClassVisitor} visit class definition,
 * it's create {@link TypeInfoBuilder} object. When {@link TypeInfoClassVisitor}
 * visit field, method or constructor definition, it's add it to private
 * {@link TypeInfoBuilder}, and {@link TypeInfoBuilder} transform data from asm
 * format to {@link TypeInfo}.
 * </p>
 */
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
      builder.addField(new FieldInfoBuilder(access, name, desc, builder.getQualifiedName()).buildFieldInfo());
      // no need detailed info about field, so return null
      return null;
   }

   @Override
   public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
   {
      if (name.equals("<init>"))
      {
         builder.addConstructor(new ConstructorInfoBuilder(access, exceptions, desc, builder.getQualifiedName())
            .buildConstructorInfo());
      }
      /*
       * "<clinit>" is static class initialization area, so ignore it
       */
      else if (!name.equals("<clinit>"))
      {
         builder.addMethod(new MethodInfoBuilder(access, name, exceptions, desc, builder.getQualifiedName())
            .buildMethodInfo());
      }
      // no need detailed info about method, so return null
      return null;
   }

   @Override
   public void visitEnd()
   {
   }

}
