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
package org.eclipse.jdt.client.outline;

import org.eclipse.jdt.client.core.dom.*;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
final class TypeChildrenVisitor extends ASTVisitor {
    /**
     *
     */
    private final List<ASTNode> list;

    /**
     *
     */
    private final TypeDeclaration t;

    /**
     * @param list
     * @param t
     */
    TypeChildrenVisitor(List<ASTNode> list, TypeDeclaration t) {
        this.list = list;
        this.t = t;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration) */
    @Override
    public boolean visit(EnumDeclaration node) {
        list.add(node);
        return false;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.FieldDeclaration) */
    @Override
    public boolean visit(FieldDeclaration node) {
        // Get field's name and type:

        Iterator iterator = node.fragments().iterator();
        while (iterator.hasNext()) {
            list.add(((VariableDeclarationFragment)iterator.next()));

        }
        return false;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.MethodDeclaration) */
    @Override
    public boolean visit(MethodDeclaration node) {
        list.add(node);
        return false;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration) */
    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.equals(t))
            return true;
        list.add(node);
        return false;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.AnnotationTypeDeclaration) */
    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        list.add(node);
        return false;
    }
}