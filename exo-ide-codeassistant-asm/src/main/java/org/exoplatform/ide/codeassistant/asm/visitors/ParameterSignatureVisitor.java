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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

enum Operation {

   GENERIC, ARRAY, CLASS, BASE

}

/**
 * Visitor parses part of method's signature with type or types. Its visitor may
 * be used for parsing method's return type and parameters from other visitor.
 */
public class ParameterSignatureVisitor implements SignatureVisitor
{

   private StringBuilder currentParameter;

   private final List<String> parameters;

   private Stack<Operation> operationStack;

   public ParameterSignatureVisitor()
   {
      this.parameters = new ArrayList<String>();
      this.currentParameter = new StringBuilder();

      this.operationStack = new Stack<Operation>();
   }

   @Override
   public void visitFormalTypeParameter(String name)
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitClassBound()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitInterfaceBound()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
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
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitReturnType()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public SignatureVisitor visitExceptionType()
   {
      throw new UnsupportedOperationException("Event not supported by parameter visitor");
   }

   @Override
   public void visitBaseType(char descriptor)
   {
      begin(Operation.BASE);
      switch (descriptor)
      {
         case 'V' :
            currentParameter.append("void");
            break;
         case 'B' :
            currentParameter.append("byte");
            break;
         case 'J' :
            currentParameter.append("long");
            break;
         case 'Z' :
            currentParameter.append("boolean");
            break;
         case 'I' :
            currentParameter.append("int");
            break;
         case 'S' :
            currentParameter.append("short");
            break;
         case 'C' :
            currentParameter.append("char");
            break;
         case 'F' :
            currentParameter.append("float");
            break;
         case 'D' :
            currentParameter.append("double");
            break;
      }
      end();
   }

   @Override
   public void visitTypeVariable(String name)
   {
      begin(Operation.BASE);
      currentParameter.append(name);
      end();
   }

   @Override
   public SignatureVisitor visitArrayType()
   {
      begin(Operation.ARRAY);
      return this;
   }

   @Override
   public void visitClassType(String name)
   {
      this.currentParameter.append(name.replace('/', '.'));
      begin(Operation.CLASS);
   }

   @Override
   public void visitInnerClassType(String name)
   {
      currentParameter.append("$");
      currentParameter.append(name);
   }

   @Override
   public void visitTypeArgument()
   {
      currentParameter.append("<?>");
   }

   @Override
   public SignatureVisitor visitTypeArgument(char wildcard)
   {
      if (operationStack.peek().equals(Operation.GENERIC))
      {
         currentParameter.append(", ");
      }
      else
      {
         begin(Operation.GENERIC);
         currentParameter.append("<");
      }

      if (wildcard == SignatureVisitor.EXTENDS)
      {
         currentParameter.append("? extends ");
      }
      else if (wildcard == SignatureVisitor.SUPER)
      {
         currentParameter.append("? super ");
      }
      return this;
   }

   @Override
   public void visitEnd()
   {
      end();
   }

   private void begin(Operation operation)
   {
      operationStack.push(operation);
   }

   private void end()
   {
      if (operationStack.peek().equals(Operation.GENERIC))
      {
         currentParameter.append(">");
         operationStack.pop();
      }
      operationStack.pop();
      while (!operationStack.isEmpty() && operationStack.peek().equals(Operation.ARRAY))
      {
         operationStack.pop();
         currentParameter.append("[]");
      }
      if (operationStack.isEmpty())
      {
         this.parameters.add(currentParameter.toString());
         currentParameter = new StringBuilder();
      }
   }

   public List<String> getParameters()
   {
      return parameters;
   }

}
