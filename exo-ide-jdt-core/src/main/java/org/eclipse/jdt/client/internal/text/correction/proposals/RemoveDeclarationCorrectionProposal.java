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
package org.eclipse.jdt.client.internal.text.correction.proposals;

import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.core.dom.*;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.client.internal.corext.dom.LinkedNodeFinder;
import org.eclipse.jdt.client.internal.text.correction.CorrectionMessages;
import org.eclipse.jdt.client.internal.text.correction.JavadocTagsSubProcessor;
import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.ArrayList;
import java.util.List;

public class RemoveDeclarationCorrectionProposal extends ASTRewriteCorrectionProposal {

    private static class SideEffectFinder extends ASTVisitor {

        private ArrayList<Expression> fSideEffectNodes;

        public SideEffectFinder(ArrayList<Expression> res) {
            fSideEffectNodes = res;
        }

        @Override
        public boolean visit(Assignment node) {
            fSideEffectNodes.add(node);
            return false;
        }

        @Override
        public boolean visit(PostfixExpression node) {
            fSideEffectNodes.add(node);
            return false;
        }

        @Override
        public boolean visit(PrefixExpression node) {
            Object operator = node.getOperator();
            if (operator == PrefixExpression.Operator.INCREMENT || operator == PrefixExpression.Operator.DECREMENT) {
                fSideEffectNodes.add(node);
            }
            return false;
        }

        @Override
        public boolean visit(MethodInvocation node) {
            fSideEffectNodes.add(node);
            return false;
        }

        @Override
        public boolean visit(ClassInstanceCreation node) {
            fSideEffectNodes.add(node);
            return false;
        }

        @Override
        public boolean visit(SuperMethodInvocation node) {
            fSideEffectNodes.add(node);
            return false;
        }
    }

    private SimpleName fName;

    private final CompilationUnit completeRoot;

    public RemoveDeclarationCorrectionProposal(CompilationUnit cUnit, SimpleName name, int relevance, IDocument document) {
        super("", null, relevance, document, new Image(JdtClientBundle.INSTANCE.delete_obj())); //$NON-NLS-1$
        this.completeRoot = cUnit;
        fName = name;
    }

    @Override
    public String getName() {
        IBinding binding = fName.resolveBinding();
        String name = fName.getIdentifier();
        switch (binding.getKind()) {
            case IBinding.TYPE:
                return CorrectionMessages.INSTANCE.RemoveDeclarationCorrectionProposal_removeunusedtype_description(name);
            case IBinding.METHOD:
                if (((IMethodBinding)binding).isConstructor()) {
                    return CorrectionMessages.INSTANCE
                                             .RemoveDeclarationCorrectionProposal_removeunusedconstructor_description(name);
                } else {
                    return CorrectionMessages.INSTANCE
                                             .RemoveDeclarationCorrectionProposal_removeunusedmethod_description(name);
                }
            case IBinding.VARIABLE:
                if (((IVariableBinding)binding).isField()) {
                    return CorrectionMessages.INSTANCE
                                             .RemoveDeclarationCorrectionProposal_removeunusedfield_description(name);
                } else {
                    return CorrectionMessages.INSTANCE.RemoveDeclarationCorrectionProposal_removeunusedvar_description(name);
                }
            default:
                return super.getDisplayString();
        }
    }

    /*(non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal#getRewrite()
     */
    @Override
    protected ASTRewrite getRewrite() {
        IBinding binding = fName.resolveBinding();
        CompilationUnit root = (CompilationUnit)fName.getRoot();
        ASTRewrite rewrite;
        if (binding.getKind() == IBinding.METHOD) {
            IMethodBinding decl = ((IMethodBinding)binding).getMethodDeclaration();
            ASTNode declaration = root.findDeclaringNode(decl);
            rewrite = ASTRewrite.create(root.getAST());
            rewrite.remove(declaration, null);
        } else if (binding.getKind() == IBinding.TYPE) {
            ITypeBinding decl = ((ITypeBinding)binding).getTypeDeclaration();
            ASTNode declaration = root.findDeclaringNode(decl);
            rewrite = ASTRewrite.create(root.getAST());
            rewrite.remove(declaration, null);
        } else if (binding.getKind() == IBinding.VARIABLE) {

            SimpleName nameNode =
                    (SimpleName)NodeFinder.perform(completeRoot, fName.getStartPosition(), fName.getLength());

            rewrite = ASTRewrite.create(completeRoot.getAST());
            SimpleName[] references = LinkedNodeFinder.findByBinding(completeRoot, nameNode.resolveBinding());
            for (int i = 0; i < references.length; i++) {
                removeVariableReferences(rewrite, references[i]);
            }

            IVariableBinding bindingDecl = ((IVariableBinding)nameNode.resolveBinding()).getVariableDeclaration();
            ASTNode declaringNode = completeRoot.findDeclaringNode(bindingDecl);
            if (declaringNode instanceof SingleVariableDeclaration) {
                removeParamTag(rewrite, (SingleVariableDeclaration)declaringNode);
            }
        } else {
            throw new IllegalArgumentException("Unexpected binding"); //$NON-NLS-1$
        }
        return rewrite;
    }

    private void removeParamTag(ASTRewrite rewrite, SingleVariableDeclaration varDecl) {
        if (varDecl.getParent() instanceof MethodDeclaration) {
            Javadoc javadoc = ((MethodDeclaration)varDecl.getParent()).getJavadoc();
            if (javadoc != null) {
                TagElement tagElement = JavadocTagsSubProcessor.findParamTag(javadoc, varDecl.getName().getIdentifier());
                if (tagElement != null) {
                    rewrite.remove(tagElement, null);
                }
            }
        }
    }

    /**
     * Remove the field or variable declaration including the initializer.
     *
     * @param rewrite
     *         the ast rewrite
     * @param reference
     *         the reference
     */
    private void removeVariableReferences(ASTRewrite rewrite, SimpleName reference) {
        ASTNode parent = reference.getParent();
        while (parent instanceof QualifiedName) {
            parent = parent.getParent();
        }
        if (parent instanceof FieldAccess) {
            parent = parent.getParent();
        }

        int nameParentType = parent.getNodeType();
        if (nameParentType == ASTNode.ASSIGNMENT) {
            Assignment assignment = (Assignment)parent;
            Expression rightHand = assignment.getRightHandSide();

            ASTNode assignParent = assignment.getParent();
            if (assignParent.getNodeType() == ASTNode.EXPRESSION_STATEMENT
                && rightHand.getNodeType() != ASTNode.ASSIGNMENT) {
                removeVariableWithInitializer(rewrite, rightHand, assignParent);
            } else {
                rewrite.replace(assignment, rewrite.createCopyTarget(rightHand), null);
            }
        } else if (nameParentType == ASTNode.SINGLE_VARIABLE_DECLARATION) {
            rewrite.remove(parent, null);
        } else if (nameParentType == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
            VariableDeclarationFragment frag = (VariableDeclarationFragment)parent;
            ASTNode varDecl = frag.getParent();
            List<VariableDeclarationFragment> fragments;
            if (varDecl instanceof VariableDeclarationExpression) {
                fragments = ((VariableDeclarationExpression)varDecl).fragments();
            } else if (varDecl instanceof FieldDeclaration) {
                fragments = ((FieldDeclaration)varDecl).fragments();
            } else {
                fragments = ((VariableDeclarationStatement)varDecl).fragments();
            }
            if (fragments.size() == 1) {
                rewrite.remove(varDecl, null);
            } else {
                rewrite.remove(frag, null); // don't try to preserve
            }
        }
    }

    private void removeVariableWithInitializer(ASTRewrite rewrite, ASTNode initializerNode, ASTNode statementNode) {
        ArrayList<Expression> sideEffectNodes = new ArrayList<Expression>();
        initializerNode.accept(new SideEffectFinder(sideEffectNodes));
        int nSideEffects = sideEffectNodes.size();
        if (nSideEffects == 0) {
            if (ASTNodes.isControlStatementBody(statementNode.getLocationInParent())) {
                rewrite.replace(statementNode, rewrite.getAST().newBlock(), null);
            } else {
                rewrite.remove(statementNode, null);
            }
        } else {
            // do nothing yet
        }
    }

}
