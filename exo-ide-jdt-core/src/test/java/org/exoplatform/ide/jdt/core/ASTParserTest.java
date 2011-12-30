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
package org.exoplatform.ide.jdt.core;

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 *
 */
public class ASTParserTest
{

   @Test
   public void pareseClass() throws Exception
   {
      char[] javaFile =
         IOUtils.toCharArray(Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("CreateJavaClassPresenter.java"));
      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(javaFile);
      ASTNode createAST = parser.createAST(null);
      createAST.accept(new methodVisitor());

      //      CompilationUnit unit = (CompilationUnit)createAST;
      //      unit.getTypeRoot();
   }

   private static class CalssVisitor extends ASTVisitor
   {
      /**
       * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.TypeDeclaration)
       */
      @Override
      public boolean visit(TypeDeclaration node)
      {
         System.out.println(node.isMemberTypeDeclaration());
//         if (node.isMemberTypeDeclaration())
//         {
            node.accept(new methodVisitor());
//         }
         return super.visit(node);
      }

   }

   private static class methodVisitor extends ASTVisitor
   {
      /**
       * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
       */
      @Override
      public boolean visit(MethodDeclaration node)
      {
         
         System.out.println(node.getModifiers() + " type: " + node.getReturnType2() + " " + node.getName());
         
//         for(Object n : node.parameters())
//         {
//            SingleVariableDeclaration param = (SingleVariableDeclaration)n;
//            System.out.print(param.getType() + " " + param.getName());
//         }
//         System.out.println();
//         System.out.println(")");
         return false;
      }
      
      
   }
}