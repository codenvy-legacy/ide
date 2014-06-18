/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.jdt.core.dom.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 4, 2012 2:50:05 PM evgen $
 */
public class ASTVisitorTest extends ParserBaseTest {
    @Test
    public void testTypeDeclarationVisitor() throws Exception {
        TypeDeclarationVisitor visitor = new TypeDeclarationVisitor();
        unit.accept(visitor);
        Assert.assertEquals(2, visitor.typeCount);

    }

    @Test
    public void testMethodDeclarationVisitor() throws Exception {
        MethodDeclarationVisitor visitor = new MethodDeclarationVisitor();
        TypeDeclaration type = (TypeDeclaration)unit.types().get(0);
        type.getTypes()[0].accept(visitor);
        Assert.assertEquals(19, visitor.methodCount);
    }

    private static class MethodDeclarationVisitor extends ASTVisitor {
        private int methodCount;

        /** @see com.codenvy.ide.ext.java.jdt.core.dom.ASTVisitor#visit(com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration) */
        @Override
        public boolean visit(MethodDeclaration node) {
            methodCount++;
            return super.visit(node);
        }
    }

    private static class TypeDeclarationVisitor extends ASTVisitor {
        private int typeCount;

        /** @see com.codenvy.ide.ext.java.jdt.core.dom.ASTVisitor#visit(com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration) */
        @Override
        public boolean visit(TypeDeclaration node) {
            typeCount++;
            return super.visit(node);
        }
    }
}
