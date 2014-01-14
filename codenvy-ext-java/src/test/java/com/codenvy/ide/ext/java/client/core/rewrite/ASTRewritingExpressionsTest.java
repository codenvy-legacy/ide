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
package com.codenvy.ide.ext.java.client.core.rewrite;

import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.Block;
import com.codenvy.ide.ext.java.jdt.core.dom.ClassInstanceCreation;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.ExpressionStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodInvocation;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleType;
import com.codenvy.ide.ext.java.jdt.core.dom.SuperConstructorInvocation;
import com.codenvy.ide.ext.java.jdt.core.dom.SuperMethodInvocation;
import com.codenvy.ide.ext.java.jdt.core.dom.Type;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ASTRewritingExpressionsTest extends ASTRewritingTest {

    @Test
    public void testClassInstanceCreation2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E<A> {\n");
        buf.append("    public void foo() {\n");
        buf.append("        new Inner();\n");
        buf.append("        new <A>Inner();\n");
        buf.append("        new<A>Inner();\n");
        buf.append("        new <A, A>Inner();\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu);
        ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

        AST ast = astRoot.getAST();

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        MethodDeclaration methodDecl = findMethodDeclaration(type, "foo");
        Block block = methodDecl.getBody();
        List statements = block.statements();
        assertTrue("Number of statements not 3", statements.size() == 4);
        { // add type argument
            ExpressionStatement stmt = (ExpressionStatement)statements.get(0);
            ClassInstanceCreation creation = (ClassInstanceCreation)stmt.getExpression();

            Type newTypeArg = ast.newSimpleType(ast.newSimpleName("A"));
            ListRewrite listRewrite = rewrite.getListRewrite(creation, ClassInstanceCreation.TYPE_ARGUMENTS_PROPERTY);
            listRewrite.insertFirst(newTypeArg, null);

        }
        { // remove type argument
            ExpressionStatement stmt = (ExpressionStatement)statements.get(1);
            ClassInstanceCreation creation = (ClassInstanceCreation)stmt.getExpression();

            List typeArgs = creation.typeArguments();
            rewrite.remove((ASTNode)typeArgs.get(0), null);
        }
        { // remove type argument
            ExpressionStatement stmt = (ExpressionStatement)statements.get(2);
            ClassInstanceCreation creation = (ClassInstanceCreation)stmt.getExpression();

            List typeArgs = creation.typeArguments();
            rewrite.remove((ASTNode)typeArgs.get(0), null);
        }
        { // add type argument to existing
            ExpressionStatement stmt = (ExpressionStatement)statements.get(3);
            ClassInstanceCreation creation = (ClassInstanceCreation)stmt.getExpression();

            Type newTypeArg = ast.newSimpleType(ast.newSimpleName("String"));

            ListRewrite listRewrite = rewrite.getListRewrite(creation, ClassInstanceCreation.TYPE_ARGUMENTS_PROPERTY);
            listRewrite.insertLast(newTypeArg, null);
        }

        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E<A> {\n");
        buf.append("    public void foo() {\n");
        buf.append("        new <A> Inner();\n");
        buf.append("        new Inner();\n");
        buf.append("        new Inner();\n");
        buf.append("        new <A, A, String>Inner();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testMethodInvocation2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.foo(3);\n");
        buf.append("        this.<String>foo(3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        MethodDeclaration methodDecl = findMethodDeclaration(type, "foo");
        Block block = methodDecl.getBody();
        List statements = block.statements();
        assertTrue("Number of statements not 2", statements.size() == 2);
        { // add type arguments
            ExpressionStatement stmt = (ExpressionStatement)statements.get(0);
            MethodInvocation invocation = (MethodInvocation)stmt.getExpression();
            SimpleType newType = ast.newSimpleType(ast.newSimpleName("String"));
            ListRewrite listRewriter = rewrite.getListRewrite(invocation, MethodInvocation.TYPE_ARGUMENTS_PROPERTY);
            listRewriter.insertFirst(newType, null);
        }
        { // remove type arguments
            ExpressionStatement stmt = (ExpressionStatement)statements.get(1);
            MethodInvocation invocation = (MethodInvocation)stmt.getExpression();
            rewrite.remove((ASTNode)invocation.typeArguments().get(0), null);
        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.<String>foo(3);\n");
        buf.append("        this.foo(3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testSuperConstructorInvocation2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("        x.super();\n");
        buf.append("    }\n");
        buf.append("    public E(int i) {\n");
        buf.append("        x.<String>super(i);\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        assertTrue("Number of methods not 2", type.bodyDeclarations().size() == 2);
        { // add type arguments
            MethodDeclaration methodDecl = (MethodDeclaration)type.bodyDeclarations().get(0);
            SuperConstructorInvocation invocation = (SuperConstructorInvocation)methodDecl.getBody().statements().get(0);
            SimpleType newType = ast.newSimpleType(ast.newSimpleName("String"));
            ListRewrite listRewriter =
                    rewrite.getListRewrite(invocation, SuperConstructorInvocation.TYPE_ARGUMENTS_PROPERTY);
            listRewriter.insertFirst(newType, null);
        }
        { // remove type arguments
            MethodDeclaration methodDecl = (MethodDeclaration)type.bodyDeclarations().get(1);
            SuperConstructorInvocation invocation = (SuperConstructorInvocation)methodDecl.getBody().statements().get(0);

            rewrite.remove((ASTNode)invocation.typeArguments().get(0), null);

        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("        x.<String>super();\n");
        buf.append("    }\n");
        buf.append("    public E(int i) {\n");
        buf.append("        x.super(i);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testSuperConstructorInvocation4() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("        x.super();\n");
        buf.append("    }\n");
        buf.append("    public E(int i) {\n");
        buf.append("        x.<String>super(i);\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        assertTrue("Number of methods not 2", type.bodyDeclarations().size() == 2);
        { // add type arguments
            MethodDeclaration methodDecl = (MethodDeclaration)type.bodyDeclarations().get(0);
            SuperConstructorInvocation invocation = (SuperConstructorInvocation)methodDecl.getBody().statements().get(0);
            rewrite.remove(invocation.getExpression(), null);
            SimpleType newType = ast.newSimpleType(ast.newSimpleName("String"));
            ListRewrite listRewriter =
                    rewrite.getListRewrite(invocation, SuperConstructorInvocation.TYPE_ARGUMENTS_PROPERTY);
            listRewriter.insertFirst(newType, null);
        }
        { // remove type arguments
            MethodDeclaration methodDecl = (MethodDeclaration)type.bodyDeclarations().get(1);
            SuperConstructorInvocation invocation = (SuperConstructorInvocation)methodDecl.getBody().statements().get(0);

            rewrite.remove(invocation.getExpression(), null);
            rewrite.remove((ASTNode)invocation.typeArguments().get(0), null);

        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public E() {\n");
        buf.append("        <String>super();\n");
        buf.append("    }\n");
        buf.append("    public E(int i) {\n");
        buf.append("        super(i);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    @Test
    public void testSuperMethodInvocation2() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        X.super.foo(3);\n");
        buf.append("        X.super.<String>foo(3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        MethodDeclaration methodDecl = findMethodDeclaration(type, "foo");
        Block block = methodDecl.getBody();
        List statements = block.statements();
        assertTrue("Number of statements not 2", statements.size() == 2);
        { // add type arguments
            ExpressionStatement stmt = (ExpressionStatement)statements.get(0);
            SuperMethodInvocation invocation = (SuperMethodInvocation)stmt.getExpression();
            SimpleType newType = ast.newSimpleType(ast.newSimpleName("String"));
            ListRewrite listRewriter = rewrite.getListRewrite(invocation, SuperMethodInvocation.TYPE_ARGUMENTS_PROPERTY);
            listRewriter.insertFirst(newType, null);
        }
        { // remove type arguments
            ExpressionStatement stmt = (ExpressionStatement)statements.get(1);
            SuperMethodInvocation invocation = (SuperMethodInvocation)stmt.getExpression();
            rewrite.remove((ASTNode)invocation.typeArguments().get(0), null);
        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        X.super.<String>foo(3);\n");
        buf.append("        X.super.foo(3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

}
