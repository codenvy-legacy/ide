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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.client.internal.compiler.env.NameEnvironmentAnswer;
import org.exoplatform.ide.editor.java.client.codeassistant.services.JavaCodeAssistantService;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 12, 2012 3:23:29 PM evgen $
 * 
 */
public abstract class ParserBaseTestGwt extends BaseTestGwt
{

   protected CompilationUnit unit;

   protected char[] javaFiles;

   protected interface Resources extends ClientBundle
   {
      @Source("CreateJavaClassPresenter.txt")
      TextResource resource();
   }

   /**
    * 
    */
   public ParserBaseTestGwt()
   {
      super();
   }

   public void gwtSetUp()
   {
      Resources rs = GWT.create(Resources.class);
      javaFiles = rs.resource().getText().toCharArray();

      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      parser.setUnitName("/P/org/test/CreateJavaClassPresenter.java");
      parser.setSource(javaFiles);
      parser.setNameEnvironment(new MockNameEnv());
      ASTNode ast = parser.createAST(null);
      unit = (CompilationUnit)ast;

      new JavaCodeAssistantService(null, null);
   }

   private class MockNameEnv implements INameEnvironment
   {

      /**
       * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[][])
       */
      @Override
      public NameEnvironmentAnswer findType(char[][] compoundTypeName)
      {
         // TODO Auto-generated method stub
         return null;
      }

      /**
       * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[], char[][])
       */
      @Override
      public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName)
      {
         // TODO Auto-generated method stub
         return null;
      }

      /**
       * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#isPackage(char[][], char[])
       */
      @Override
      public boolean isPackage(char[][] parentPackageName, char[] packageName)
      {
         // TODO Auto-generated method stub
         return false;
      }

      /**
       * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#cleanup()
       */
      @Override
      public void cleanup()
      {
         // TODO Auto-generated method stub

      }

   }

}