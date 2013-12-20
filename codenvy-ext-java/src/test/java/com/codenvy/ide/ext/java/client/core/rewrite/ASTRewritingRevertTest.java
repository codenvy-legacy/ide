/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
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

import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ASTRewritingRevertTest extends ASTRewritingTest {

    @Test
    public void testRemoveInserted() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
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
        {
            // revert inserted node
            PrimitiveType newType = ast.newPrimitiveType(PrimitiveType.INT);

            rewrite.set(methodDecl, MethodDeclaration.RETURN_TYPE2_PROPERTY, newType, null);
            rewrite.remove(newType, null);

        }
        {
            // revert inserted list child
            SingleVariableDeclaration newParam = createNewParam(ast, "x");

            rewrite.getListRewrite(methodDecl, MethodDeclaration.PARAMETERS_PROPERTY).insertFirst(newParam, null);

            rewrite.remove(newParam, null);
        }

        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertEqualString(preview, buf.toString());
    }

    @Test
    public void testReplaceInserted() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public void foo() {\n");
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
        {
            // replace inserted node
            PrimitiveType newType = ast.newPrimitiveType(PrimitiveType.INT);

            rewrite.set(methodDecl, MethodDeclaration.RETURN_TYPE2_PROPERTY, newType, null);

            PrimitiveType betterType = ast.newPrimitiveType(PrimitiveType.BOOLEAN);

            rewrite.replace(newType, betterType, null);

        }
        {
            // replace inserted list child
            SingleVariableDeclaration newParam = createNewParam(ast, "x");

            rewrite.getListRewrite(methodDecl, MethodDeclaration.PARAMETERS_PROPERTY).insertFirst(newParam, null);

            SingleVariableDeclaration betterParam = createNewParam(ast, "y");

            rewrite.replace(newParam, betterParam, null);
        }

        String preview = evaluateRewrite(cu, rewrite);

        buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("public class E {\n");
        buf.append("    public boolean foo(float y) {\n");
        buf.append("    }\n");
        buf.append("}\n");

        assertEqualString(preview, buf.toString());
    }
}
