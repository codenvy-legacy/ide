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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * This visitor is used for collecting child nodes of the parent AST node. First, <code>visit</code> method must be called for
 * necessary AST node. Child nodes are available with the use of {@link GetChildrenVisitor.#getNodes()}.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 6, 2012 6:07:12 PM anya $
 */
public class GetChildrenVisitor extends ASTVisitor {
    /** Child nodes. */
    private List<Object> nodes = new ArrayList<Object>();

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.CompilationUnit) */
    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(CompilationUnit compilationUnit) {
        nodes.clear();
        if (compilationUnit.getPackage() != null) {
            nodes.add(compilationUnit.getPackage());
        }

        if (!compilationUnit.imports().isEmpty()) {
            nodes.add(new ImportGroupNode("import declarations", compilationUnit.imports()));
        }

        if (!compilationUnit.types().isEmpty()) {
            nodes.add((ASTNode)compilationUnit.types().get(0));
        }
        return true;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.TypeDeclaration) */
    @Override
    public boolean visit(TypeDeclaration typeDeclaration) {
        nodes.clear();
        nodes.addAll(retrieveFields(typeDeclaration.getFields()));
        nodes.addAll(Arrays.asList(typeDeclaration.getMethods()));
        nodes.addAll(Arrays.asList(typeDeclaration.getTypes()));
        return true;
    }

    /**
     * The incoming list of fields may contain following field declaration: <code>
     * private int x, y;
     * </code> This method returns it as separate fields.
     * <p/>
     * TODO rework this method
     *
     * @param fields
     * @return {@link List}
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<FieldDeclaration> retrieveFields(FieldDeclaration[] fields) {
        List<FieldDeclaration> separatedFields = new ArrayList<FieldDeclaration>();
        for (FieldDeclaration field : fields) {
            List fragments = new ArrayList(field.fragments());
            Iterator<VariableDeclarationFragment> iterator = fragments.iterator();
            while (iterator.hasNext()) {
                VariableDeclarationFragment declarationFragment = iterator.next();
                FieldDeclaration newField = (FieldDeclaration)field.clone0(field.getAST());
                newField.fragments().clear();
                newField.fragments().addAll(ASTNode.copySubtrees(newField.getAST(), Arrays.asList(declarationFragment)));
                separatedFields.add(newField);
            }
        }
        return separatedFields;
    }

    /** @see org.eclipse.jdt.client.core.dom.ASTVisitor#visit(org.eclipse.jdt.client.core.dom.EnumDeclaration) */
    @SuppressWarnings("unchecked")
    @Override
    public boolean visit(EnumDeclaration enumDeclaration) {
        nodes.clear();
        nodes.addAll(enumDeclaration.enumConstants());
        nodes.addAll(enumDeclaration.bodyDeclarations());
        return true;
    }

    /** @return {@link List} the list of child nodes */
    public List<Object> getNodes() {
        return nodes;
    }
}
