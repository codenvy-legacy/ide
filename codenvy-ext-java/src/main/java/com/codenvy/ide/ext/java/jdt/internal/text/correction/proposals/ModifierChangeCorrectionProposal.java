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
package com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.AbstractTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Block;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.FieldDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.IBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationExpression;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.VariableDeclarationRewrite;
import com.codenvy.ide.text.Document;


public class ModifierChangeCorrectionProposal extends LinkedCorrectionProposal {

    private IBinding fBinding;

    private ASTNode fNode;

    private int fIncludedModifiers;

    private int fExcludedModifiers;

    public ModifierChangeCorrectionProposal(String label, IBinding binding, ASTNode node, int includedModifiers,
                                            int excludedModifiers, int relevance, Document document, Images image) {
        super(label, null, relevance, document, image);
        fBinding = binding;
        fNode = node;
        fIncludedModifiers = includedModifiers;
        fExcludedModifiers = excludedModifiers;
    }

    @Override
    protected ASTRewrite getRewrite() {
        CompilationUnit astRoot = ASTResolving.findParentCompilationUnit(fNode);
        ASTNode boundNode = astRoot.findDeclaringNode(fBinding);
        ASTNode declNode = null;

        if (boundNode != null) {
            declNode = boundNode; // is same CU
        } else {
            //setSelectionDescription(selectionDescription);
            CompilationUnit newRoot = ASTResolving.createQuickFixAST(document);
            declNode = newRoot.findDeclaringNode(fBinding.getKey());
        }
        if (declNode != null) {
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            if (declNode.getNodeType() == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
                VariableDeclarationFragment fragment = (VariableDeclarationFragment)declNode;
                ASTNode parent = declNode.getParent();
                if (parent instanceof FieldDeclaration) {
                    FieldDeclaration fieldDecl = (FieldDeclaration)parent;
                    if (fieldDecl.fragments().size() > 1 && (fieldDecl.getParent() instanceof AbstractTypeDeclaration)) { // split
                        VariableDeclarationRewrite.rewriteModifiers(fieldDecl, new VariableDeclarationFragment[]{fragment},
                                                                    fIncludedModifiers, fExcludedModifiers, rewrite, null);
                        return rewrite;
                    }
                } else if (parent instanceof VariableDeclarationStatement) {
                    VariableDeclarationStatement varDecl = (VariableDeclarationStatement)parent;
                    if (varDecl.fragments().size() > 1 && (varDecl.getParent() instanceof Block)) { // split
                        VariableDeclarationRewrite.rewriteModifiers(varDecl, new VariableDeclarationFragment[]{fragment},
                                                                    fIncludedModifiers, fExcludedModifiers, rewrite, null);
                        return rewrite;
                    }
                } else if (parent instanceof VariableDeclarationExpression) {
                    // can't separate
                }
                declNode = parent;
            }
            //         ModifierRewrite listRewrite = ModifierRewrite.create(rewrite, declNode);
            //         PositionInformation trackedDeclNode = listRewrite.setModifiers(fIncludedModifiers, fExcludedModifiers, null);

            //			LinkedProposalPositionGroup positionGroup= new LinkedProposalPositionGroup("group"); //$NON-NLS-1$
            //			positionGroup.addPosition(trackedDeclNode);
            //			getLinkedProposalModel().addPositionGroup(positionGroup);

            //			if (boundNode != null) {
            //				// only set end position if in same CU
            //				setEndPosition(rewrite.track(fNode));
            //			}
            return rewrite;
        }
        return null;
    }
}
