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
package org.exoplatform.ide.jdt.core;

import static org.fest.assertions.Assertions.*;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 4, 2012 2:50:05 PM evgen $
 *
 */
public class ASTVisitorTest
{
   private CompilationUnit unit;

   @Before
   public void parseFile() throws IOException
   {
      char[] javaFile =
         IOUtils.toCharArray(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("CreateJavaClassPresenter.java"));
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName("CreateJavaClassPresenter");
      parser.setSource(javaFile);

      parser.setEnvironment(null, new String[]{"/my/path"}, new String[]{"UTF-8"}, true);
      ASTNode ast = parser.createAST(null);
      unit = (CompilationUnit)ast;
   }

   @Test
   public void typeDeclarationVisitor() throws Exception
   {
      TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
      unit.accept(visitor);
      assertThat(visitor.typeCount).isEqualTo(2);
   }
   
   @Test
   public void methodDeclarationVisitor() throws Exception
   {
      MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
      TypeDeclaration type = (TypeDeclaration)unit.types().get(0);
      type.getTypes()[0].accept(visitor);
      assertThat(visitor.methodCount).isEqualTo(19);
   }

   private static class MethodDeclarationVisitor extends ASTVisitor
   {
      private int methodCount;

      /**
       * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
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
      * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
      */
      @Override
      public boolean visit(TypeDeclaration node)
      {
         typeCount++;
         return super.visit(node);
      }
   }
}
