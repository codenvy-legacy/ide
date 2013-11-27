/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core.rewrite;

import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;

import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ASTRewritingWithStatementsRecoveryTest extends ASTRewritingTest {

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=272711
    @Test
    public void testBug272711_01() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.foo#3);\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu, true);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        MethodDeclaration methodDecl = findMethodDeclaration(type, "foo");
        Block block = methodDecl.getBody();
        List statements = block.statements();
        assertTrue("Number of statements not 1", statements.size() == 1);
        { // add type arguments
            ExpressionStatement stmt = (ExpressionStatement)statements.get(0);
            MethodInvocation invocation = (MethodInvocation)stmt.getExpression();
            ASTNode firstArgument = (ASTNode)invocation.arguments().get(0);
            NumberLiteral newNumberLiteral = ast.newNumberLiteral("0");
            ListRewrite listRewriter = rewrite.getListRewrite(invocation, MethodInvocation.ARGUMENTS_PROPERTY);
            listRewriter.replace(firstArgument, newNumberLiteral, null);
        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        this.foo#0);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=272711
    @Test
    public void testBug272711_02() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        throws new UnsupportedOperationException();\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu, true);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        List bodyDeclarations = type.bodyDeclarations();
        assertTrue("Number of body declarations not 1", bodyDeclarations.size() == 1);
        { // add field declaration
            MethodDeclaration methodDeclaration = (MethodDeclaration)bodyDeclarations.get(0);
            VariableDeclarationFragment newFragment = ast.newVariableDeclarationFragment();
            newFragment.setName(ast.newSimpleName("field"));
            FieldDeclaration newFieldDeclaration = ast.newFieldDeclaration(newFragment);
            newFieldDeclaration.setType(ast.newSimpleType(ast.newName("Object")));
            ListRewrite listRewriter = rewrite.getListRewrite(type, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
            listRewriter.insertBefore(newFieldDeclaration, methodDeclaration, null);
        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    Object field;\n");
        buf.append("\n");
        buf.append("    public void foo() {\n");
        buf.append("        throws new UnsupportedOperationException();\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

    //https://bugs.eclipse.org/bugs/show_bug.cgi?id=272711
    @Test
    public void testBug272711_03() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        do {\n");
        buf.append("        } (a);\n");
        buf.append("    }\n");
        buf.append("}\n");
        ICompilationUnit cu =
                new com.codenvy.ide.ext.java.jdt.compiler.batch.CompilationUnit(buf.toString().toCharArray(), "E.java", "");

        CompilationUnit astRoot = createAST3(cu, true);
        AST ast = astRoot.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        assertTrue("Parse errors", (astRoot.getFlags() & ASTNode.MALFORMED) == 0);
        TypeDeclaration type = findTypeDeclaration(astRoot, "E");
        MethodDeclaration methodDecl = findMethodDeclaration(type, "foo");
        Block block = methodDecl.getBody();
        List statements = block.statements();
        assertTrue("Number of statements not 1", statements.size() == 1);
        { // replace the 'a' simple name with another simple name
            DoStatement stmt = (DoStatement)statements.get(0);
            Statement body = stmt.getBody();
            EmptyStatement newEmptyStatement = ast.newEmptyStatement();
            rewrite.replace(body, newEmptyStatement, null);
        }
        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("        do\n");
        buf.append("            ;  (a);\n");
        buf.append("    }\n");
        buf.append("}\n");
        assertEqualString(preview, buf.toString());

    }

}
