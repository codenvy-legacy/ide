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
import static org.fest.assertions.Assertions.*;
import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.ASTVisitor;
import org.eclipse.jdt.client.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.SimpleName;
import org.eclipse.jdt.client.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 *
 */
public class ASTParserTest
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
   public void parseUnit() throws Exception
   {
      assertThat(unit.types()).isNotEmpty().hasSize(1);
   }

   @Test
   public void pareseClass() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      assertThat(td.getName().getFullyQualifiedName()).isEqualTo("CreateJavaClassPresenter");
   }

   @Test
   public void parseInnerType() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      assertThat(td.getTypes()).hasSize(1);
      TypeDeclaration innerType = td.getTypes()[0];
      assertThat(innerType.getName().getFullyQualifiedName()).isEqualTo("Display");
   }
   
   @Test
   public void innerTypeMethods() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      TypeDeclaration innerType = td.getTypes()[0];
      assertThat(innerType.getMethods()).hasSize(19);
   }
   
   @Test
   public void innerTypeFields() throws Exception
   {
      TypeDeclaration td = (TypeDeclaration)unit.types().get(0);
      TypeDeclaration innerType = td.getTypes()[0];
      assertThat(innerType.getFields()).hasSize(1);
   }
   
   

}