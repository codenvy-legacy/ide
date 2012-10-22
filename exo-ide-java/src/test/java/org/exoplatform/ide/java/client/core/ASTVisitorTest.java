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
package org.exoplatform.ide.java.client.core;

import org.exoplatform.ide.java.client.core.dom.ASTVisitor;
import org.exoplatform.ide.java.client.core.dom.MethodDeclaration;
import org.exoplatform.ide.java.client.core.dom.TypeDeclaration;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 4, 2012 2:50:05 PM evgen $
 * 
 */
public class ASTVisitorTest extends ParserBaseTest
{
   @Test
   public void testTypeDeclarationVisitor() throws Exception
   {
      TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
      unit.accept(visitor);
      Assert.assertEquals(2, visitor.typeCount);

   }
   @Test
   public void testMethodDeclarationVisitor() throws Exception
   {
      MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
      TypeDeclaration type = (TypeDeclaration)unit.types().get(0);
      type.getTypes()[0].accept(visitor);
      Assert.assertEquals(19, visitor.methodCount);
   }

   private static class MethodDeclarationVisitor extends ASTVisitor
   {
      private int methodCount;

      /**
       * @see org.exoplatform.ide.java.client.core.dom.ASTVisitor#visit(org.exoplatform.ide.java.client.core.dom.MethodDeclaration)
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
       * @see org.exoplatform.ide.java.client.core.dom.ASTVisitor#visit(org.exoplatform.ide.java.client.core.dom.TypeDeclaration)
       */
      @Override
      public boolean visit(TypeDeclaration node)
      {
         typeCount++;
         return super.visit(node);
      }
   }
}
