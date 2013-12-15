/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.java.client.core.outline;

import com.codenvy.ide.ext.java.client.BaseTest;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineModelTest extends BaseTest {
//
//    @Mock
//    private OutlineModel model;
//
//    @Mock
//    private com.codenvy.ide.ext.java.worker.JavaParserWorker worker;
//
//    protected static INameEnvironment env = new FileSystem(
//            new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8");
//
//    private WorkerOutlineModelUpdater updater;
//
//    protected CompilationUnit getASTRoot(char[] cu, String name) {
//        ASTParser astParser = ASTParser.newParser(AST.JLS4);
//        astParser.setSource(cu);
//        astParser.setResolveBindings(true);
//        astParser.setStatementsRecovery(true);
//        astParser.setBindingsRecovery(true);
//        astParser.setNameEnvironment(env);
//        astParser.setUnitName(name);
//        return (CompilationUnit)astParser.createAST();
//    }
//
//    @Before
//    public void setUp() {
//        updater = new WorkerOutlineModelUpdater(worker);
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void packageDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder("package exo.ide.test;\n");
//        b.append("public class Test{\n");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        assertThat(childrens).isNotNull();
//        CodeBlock im = childrens.get(0);
//        assertThat(im.getType()).isEqualTo(BlockTypes.PACKAGE.getType());
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void importsDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder("package exo.ide.test;\n");
//        b.append("import java.util.List;\n");
//        b.append("import java.util.Map;\n");
//        b.append("public class Test{\n");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        CodeBlock im = childrens.get(1);
//        assertThat(im.getType()).isEqualTo(BlockTypes.IMPORTS.getType());
//        assertThat(im.getChildren().size()).isEqualTo(2);
//        WorkerCodeBlock javaType = (WorkerCodeBlock)im.getChildren().get(0);
//        assertThat(javaType.getType()).isEqualTo(BlockTypes.IMPORT.getType());
//        assertThat(javaType.getName()).isEqualTo("java.util.List");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void classDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("public class Test{\n");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        assertThat(javaType.getType()).isEqualTo(BlockTypes.CLASS.getType());
//        assertThat(javaType.getModifiers()).isEqualTo(1);
//        assertThat(javaType.getName()).isEqualTo("Test");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void fildDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("public class Test{\n");
//        b.append("public String str;");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        WorkerCodeBlock field = (WorkerCodeBlock)javaType.getChildren().get(0);
//        assertThat(field.getName()).isEqualTo("str");
//        assertThat(field.getJavaType()).isEqualTo("String");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void multiplefildDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("public class Test{\n");
//        b.append("public String str,str2;");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        WorkerCodeBlock field1 = (WorkerCodeBlock)javaType.getChildren().get(0);
//        WorkerCodeBlock field2 = (WorkerCodeBlock)javaType.getChildren().get(1);
//        assertThat(field1.getName()).isEqualTo("str");
//        assertThat(field2.getName()).isEqualTo("str2");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void methodDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("public class Test{\n");
//        b.append("public void met(){\n");
//        b.append("return;};\n");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        WorkerCodeBlock method = (WorkerCodeBlock)javaType.getChildren().get(0);
//        assertThat(method.getName()).isEqualTo("met()");
//        assertThat(method.getJavaType()).isEqualTo("void");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void interfaceDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("public interface Test{\n");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        assertThat(javaType.getType()).isEqualTo(BlockTypes.INTERFACE.getType());
//        assertThat(javaType.getModifiers()).isEqualTo(1);
//        assertThat(javaType.getName()).isEqualTo("Test");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void enumDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("protected enum Test{\n");
//        b.append("ONE, TWO;");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        assertThat(javaType.getType()).isEqualTo(BlockTypes.ENUM.getType());
//        assertThat(javaType.getModifiers()).isEqualTo(Modifier.PROTECTED);
//        assertThat(javaType.getName()).isEqualTo("Test");
//    }
//
//    @Test
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    public void annotationDeclaration() throws Exception {
//        StringBuilder b = new StringBuilder();
//        b.append("public @interface Test{\n");
//        b.append("\n}\n");
//        CompilationUnit astRoot = getASTRoot(b.toString().toCharArray(), "Test");
//        ArgumentCaptor<Array> codeCaptor = ArgumentCaptor.<Array>forClass(Array.class);
//        updater.onCompilationUnitChanged(astRoot, message.id());
//        verify(model).setRootChildren(codeCaptor.capture());
//        ;
//        Array<CodeBlock> childrens = codeCaptor.getValue();
//        WorkerCodeBlock javaType = (WorkerCodeBlock)childrens.get(0);
//        assertThat(javaType.getType()).isEqualTo(BlockTypes.ANNOTATION.getType());
//        assertThat(javaType.getModifiers()).isEqualTo(1);
//        assertThat(javaType.getName()).isEqualTo("Test");
//    }

}
