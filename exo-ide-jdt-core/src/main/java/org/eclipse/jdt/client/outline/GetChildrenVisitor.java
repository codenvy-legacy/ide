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
