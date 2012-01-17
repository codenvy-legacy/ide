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
package org.eclipse.jdt.client.core;

import org.eclipse.jdt.client.core.dom.ASTVisitor;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 4, 2012 2:50:05 PM evgen $
 * 
 */
public class ASTVisitorTestGwt extends ParserBaseTestGwt
{

   public void testTypeDeclarationVisitor() throws Exception
   {
      TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
      unit.accept(visitor);
      assertEquals(2, visitor.typeCount);

   }

   public void testMethodDeclarationVisitor() throws Exception
   {
      MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
      TypeDeclaration type = (TypeDeclaration)unit.types().get(0);
      type.getTypes()[0].accept(visitor);
      assertEquals(19, visitor.methodCount);
   }

   private static class MethodDeclarationVisitor extends ASTVisitor
   {
      private int methodCount;

      /**
       * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.MethodDeclaration)
       */
      @Override
      public boolean visit(MethodDeclaration node)
      {
         methodCount++;
         return super.visit(node);
      }
   }

   private static class TypeDeclarationVisitor extends ASTVisitor
   {
      private int typeCount;

      /**
       * @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration)
       */
      @Override
      public boolean visit(TypeDeclaration node)
      {
         typeCount++;
         return super.visit(node);
      }
   }
}
