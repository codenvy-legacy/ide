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