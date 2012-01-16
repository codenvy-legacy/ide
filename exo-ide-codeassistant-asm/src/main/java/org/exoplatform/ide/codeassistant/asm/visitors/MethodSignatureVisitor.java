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
package org.exoplatform.ide.codeassistant.asm.visitors;

import org.objectweb.asm.signature.SignatureVisitor;

import java.util.List;

enum Stage {

   FORMAL, PARAMETERS, RETURN

}

/**
 * This visitor parses return type and parameters of method's signature. Formal
 * type parameters are skipped.
 */
public class MethodSignatureVisitor implements SignatureVisitor
{

   private ParameterSignatureVisitor returnType;

   private ParameterSignatureVisitor parameters;

   private Stage stage;

   public MethodSignatureVisitor()
   {
      this.returnType = new ParameterSignatureVisitor();
      this.parameters = new ParameterSignatureVisitor();
   }

   @Override
   public void visitFormalTypeParameter(String name)
   {
      stage = Stage.FORMAL;
   }

   @Override
   public SignatureVisitor visitClassBound()
   {
      return this;
   }

   @Override
   public SignatureVisitor visitInterfaceBound()
   {
      return this;
   }

   @Override
   public SignatureVisitor visitSuperclass()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitInterface()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitParameterType()
   {
      stage = Stage.PARAMETERS;
      return parameters;
   }

   @Override
   public SignatureVisitor visitReturnType()
   {
      stage = Stage.RETURN;
      return returnType;
   }

   @Override
   public SignatureVisitor visitExceptionType()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public void visitBaseType(char descriptor)
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public void visitTypeVariable(String name)
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitArrayType()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public void visitClassType(String name)
   {
      if (stage.equals(Stage.FORMAL))
      {
         // skip
      }
      else
      {
         throw new UnsupportedOperationException("Event not supported by parameter visitor");
      }
   }

   @Override
   public void visitInnerClassType(String name)
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public void visitTypeArgument()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitTypeArgument(char wildcard)
   {
      if (stage.equals(Stage.FORMAL))
      {
         // skip
         return this;
      }
      else
      {
         throw new UnsupportedOperationException("Event not supported by parameter visitor");
      }
   }

   @Override
   public void visitEnd()
   {
   }

   public List<String> getParameters()
   {
      return parameters.getParameters();
   }

   public String getReturnType()
   {
      if (returnType.getParameters().isEmpty())
      {
         return null;
      }
      else
      {
         return returnType.getParameters().get(0);
      }
   }

}
