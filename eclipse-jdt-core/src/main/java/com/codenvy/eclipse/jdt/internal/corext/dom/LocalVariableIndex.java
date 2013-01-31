/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.dom;

import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.jdt.core.dom.ASTNode;
import com.codenvy.eclipse.jdt.core.dom.ASTVisitor;
import com.codenvy.eclipse.jdt.core.dom.BodyDeclaration;
import com.codenvy.eclipse.jdt.core.dom.FieldDeclaration;
import com.codenvy.eclipse.jdt.core.dom.IVariableBinding;
import com.codenvy.eclipse.jdt.core.dom.Initializer;
import com.codenvy.eclipse.jdt.core.dom.MethodDeclaration;
import com.codenvy.eclipse.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.eclipse.jdt.core.dom.VariableDeclarationFragment;



public class LocalVariableIndex extends ASTVisitor
{

   private int fTopIndex;

   /**
    * Computes the maximum number of local variable declarations in the
    * given body declaration.
    *
    * @param declaration the body declaration. Must either be a method
    *                    declaration, or an initializer, or a field declaration.
    * @return the maximum number of local variables
    */
   public static int perform(BodyDeclaration declaration)
   {
      Assert.isTrue(declaration != null);
      switch (declaration.getNodeType())
      {
         case ASTNode.METHOD_DECLARATION:
         case ASTNode.FIELD_DECLARATION:
         case ASTNode.INITIALIZER:
            return internalPerform(declaration);
         default:
            throw new IllegalArgumentException(declaration.toString());
      }
   }

   private static int internalPerform(BodyDeclaration methodOrInitializer)
   {
      // we have to find the outermost method/initializer/field declaration since a local or anonymous
      // type can reference final variables from the outer scope.
      BodyDeclaration target = methodOrInitializer;
      ASTNode parent = target.getParent();
      while (parent != null)
      {
         if (parent instanceof MethodDeclaration || parent instanceof Initializer || parent instanceof FieldDeclaration)
         {
            target = (BodyDeclaration)parent;
         }
         parent = parent.getParent();
      }

      return doPerform(target);
   }

   private static int doPerform(BodyDeclaration node)
   {
      LocalVariableIndex counter = new LocalVariableIndex();
      node.accept(counter);
      return counter.fTopIndex;
   }

   @Override
   public boolean visit(SingleVariableDeclaration node)
   {
      handleVariableBinding(node.resolveBinding());
      return true;
   }

   @Override
   public boolean visit(VariableDeclarationFragment node)
   {
      handleVariableBinding(node.resolveBinding());
      return true;
   }

   private void handleVariableBinding(IVariableBinding binding)
   {
      if (binding == null)
      {
         return;
      }
      fTopIndex = Math.max(fTopIndex, binding.getVariableId());
   }
}
