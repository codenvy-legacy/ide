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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.sorround;

import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTVisitor;
import com.codenvy.ide.ext.java.jdt.core.dom.BodyDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.IVariableBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclaration;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Selection;

import java.util.ArrayList;
import java.util.List;


public class LocalDeclarationAnalyzer extends ASTVisitor {

    private Selection fSelection;

    private List<VariableDeclaration> fAffectedLocals;

    public static VariableDeclaration[] perform(BodyDeclaration parent, Selection selection) {
        LocalDeclarationAnalyzer analyzer = new LocalDeclarationAnalyzer(selection);
        parent.accept(analyzer);
        return analyzer.fAffectedLocals.toArray(new VariableDeclaration[analyzer.fAffectedLocals.size()]);
    }

    private LocalDeclarationAnalyzer(Selection selection) {
        fSelection = selection;
        fAffectedLocals = new ArrayList<VariableDeclaration>(1);
    }

    @Override
    public boolean visit(SimpleName node) {
        IVariableBinding binding = null;
        if (node.isDeclaration() || !considerNode(node) || (binding = ASTNodes.getLocalVariableBinding(node)) == null)
            return false;
        handleReferenceToLocal(node, binding);
        return true;
    }

    private boolean considerNode(ASTNode node) {
        return fSelection.getVisitSelectionMode(node) == Selection.AFTER;
    }

    private void handleReferenceToLocal(SimpleName node, IVariableBinding binding) {
        VariableDeclaration declaration = ASTNodes.findVariableDeclaration(binding, node);
        if (declaration != null && fSelection.covers(declaration))
            addLocalDeclaration(declaration);
    }

    private void addLocalDeclaration(VariableDeclaration declaration) {
        if (!fAffectedLocals.contains(declaration))
            fAffectedLocals.add(declaration);
    }
}
