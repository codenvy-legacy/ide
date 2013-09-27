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
package org.eclipse.jdt.client.core;

import org.eclipse.jdt.client.core.dom.ASTVisitor;
import org.eclipse.jdt.client.core.dom.MethodDeclaration;
import org.eclipse.jdt.client.core.dom.TypeDeclaration;
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

        /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.MethodDeclaration) */
        @Override
        public boolean visit(MethodDeclaration node) {
            methodCount++;
            return super.visit(node);
        }
    }

    private static class TypeDeclarationVisitor extends ASTVisitor {
        private int typeCount;

        /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration) */
        @Override
        public boolean visit(TypeDeclaration node) {
            typeCount++;
            return super.visit(node);
        }
    }
}
