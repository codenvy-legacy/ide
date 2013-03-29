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
package com.codenvy.ide.java.client.core.outline;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Mockito.*;

import com.codenvy.ide.api.outline.CodeBlock;
import com.codenvy.ide.api.outline.OutlineModel;
import com.codenvy.ide.java.client.BaseTest;
import com.codenvy.ide.java.client.core.dom.AST;
import com.codenvy.ide.java.client.core.dom.ASTParser;
import com.codenvy.ide.java.client.core.dom.CompilationUnit;
import com.codenvy.ide.java.client.editor.AstProvider;
import com.codenvy.ide.java.client.editor.outline.BlockTypes;
import com.codenvy.ide.java.client.editor.outline.JavaCodeBlock;
import com.codenvy.ide.java.client.editor.outline.OutlineModelUpdater;
import com.codenvy.ide.java.client.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.java.emul.FileSystem;
import com.codenvy.ide.json.JsonArray;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.lang.reflect.Modifier;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OutlineModelTest extends BaseTest
{

   @Mock
   private OutlineModel model;

   @Mock
   private AstProvider astProvider;

   protected static INameEnvironment env = new FileSystem(
      new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8");

   private OutlineModelUpdater updater;

   protected CompilationUnit getASTRoot(char[] cu, String name)
   {
      ASTParser astParser = ASTParser.newParser(AST.JLS4);
      astParser.setSource(cu);
      astParser.setResolveBindings(true);
      astParser.setStatementsRecovery(true);
      astParser.setBindingsRecovery(true);
      astParser.setNameEnvironment(env);
      astParser.setUnitName(name);
      return (CompilationUnit)astParser.createAST();
   }

   @Before
   public void setUp()
   {
      updater = new OutlineModelUpdater(model, astProvider);
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void packageDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder("package exo.ide.test;\n");
      b.append("public class Test{\n");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      assertThat(childrens).isNotNull();
      CodeBlock im = childrens.get(0);
      assertThat(im.getType()).isEqualTo(BlockTypes.PACKAGE.getType());
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void importsDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder("package exo.ide.test;\n");
      b.append("import java.util.List;\n");
      b.append("import java.util.Map;\n");
      b.append("public class Test{\n");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      CodeBlock im = childrens.get(1);
      assertThat(im.getType()).isEqualTo(BlockTypes.IMPORTS.getType());
      assertThat(im.getChildren().size()).isEqualTo(2);
      JavaCodeBlock javaType = (JavaCodeBlock)im.getChildren().get(0);
      assertThat(javaType.getType()).isEqualTo(BlockTypes.IMPORT.getType());
      assertThat(javaType.getName()).isEqualTo("java.util.List");
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void classDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("public class Test{\n");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      assertThat(javaType.getType()).isEqualTo(BlockTypes.CLASS.getType());
      assertThat(javaType.getModifiers()).isEqualTo(1);
      assertThat(javaType.getName()).isEqualTo("Test");
   }
   
   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void fildDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("public class Test{\n");
      b.append("public String str;");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      JavaCodeBlock field =  (JavaCodeBlock)javaType.getChildren().get(0);
      assertThat(field.getName()).isEqualTo("str");
      assertThat(field.getJavaType()).isEqualTo("String");
   }
   
   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void multiplefildDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("public class Test{\n");
      b.append("public String str,str2;");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      JavaCodeBlock field1 =  (JavaCodeBlock)javaType.getChildren().get(0);
      JavaCodeBlock field2 =  (JavaCodeBlock)javaType.getChildren().get(1);
      assertThat(field1.getName()).isEqualTo("str");
      assertThat(field2.getName()).isEqualTo("str2");
   }
   
   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void methodDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("public class Test{\n");
      b.append("public void met(){\n");
      b.append("return;};\n");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      JavaCodeBlock method =  (JavaCodeBlock)javaType.getChildren().get(0);
      assertThat(method.getName()).isEqualTo("met()");
      assertThat(method.getJavaType()).isEqualTo("void");
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void interfaceDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("public interface Test{\n");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      assertThat(javaType.getType()).isEqualTo(BlockTypes.INTERFACE.getType());
      assertThat(javaType.getModifiers()).isEqualTo(1);
      assertThat(javaType.getName()).isEqualTo("Test");
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void enumDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("protected enum Test{\n");
      b.append("ONE, TWO;");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      assertThat(javaType.getType()).isEqualTo(BlockTypes.ENUM.getType());
      assertThat(javaType.getModifiers()).isEqualTo(Modifier.PROTECTED);
      assertThat(javaType.getName()).isEqualTo("Test");
   }

   @Test
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void annotationDeclaration() throws Exception
   {
      StringBuilder b = new StringBuilder();
      b.append("public @interface Test{\n");
      b.append("\n}\n");
      CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
      ArgumentCaptor<JsonArray> codeCaptor = ArgumentCaptor.<JsonArray>forClass(JsonArray.class);
      updater.onCompilationUnitChanged(astRoot);
      verify(model).setRootChildren(codeCaptor.capture());;
      JsonArray<CodeBlock> childrens = codeCaptor.getValue();
      JavaCodeBlock javaType = (JavaCodeBlock)childrens.get(0);
      assertThat(javaType.getType()).isEqualTo(BlockTypes.ANNOTATION.getType());
      assertThat(javaType.getModifiers()).isEqualTo(1);
      assertThat(javaType.getName()).isEqualTo("Test");
   }

}
