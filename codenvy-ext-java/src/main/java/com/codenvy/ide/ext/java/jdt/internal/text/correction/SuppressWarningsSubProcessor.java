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

package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.CorrectionEngine;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.Annotation;
import com.codenvy.ide.ext.java.jdt.core.dom.AnnotationTypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.AnnotationTypeMemberDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ArrayInitializer;
import com.codenvy.ide.ext.java.jdt.core.dom.ChildListPropertyDescriptor;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.EnumConstantDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.EnumDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Expression;
import com.codenvy.ide.ext.java.jdt.core.dom.FieldDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ImportDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.MemberValuePair;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.NormalAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleMemberAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.StringLiteral;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationExpression;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class SuppressWarningsSubProcessor {

    private static final String ADD_SUPPRESSWARNINGS_ID = "org.eclipse.jdt.ui.correction.addSuppressWarnings"; //$NON-NLS-1$

    public static final boolean hasSuppressWarningsProposal(int problemId) {
        //		if (CorrectionEngine.getWarningToken(problemId) != null && JavaModelUtil.is50OrHigher(javaProject)) {
        //			String optionId= JavaCore.getOptionForConfigurableSeverity(problemId);
        //			if (optionId != null) {
        //				String optionValue= javaProject.getOption(optionId, true);
        //				return JavaCore.WARNING.equals(optionValue);
        //			}
        //		}
        return true;
    }

    public static void addSuppressWarningsProposals(InvocationContext context, IProblemLocation problem,
                                                    Collection<ICommandAccess> proposals) {
        //      if (problem.isError()
        //         && !JavaCore.ENABLED.equals(context.getCompilationUnit().getJavaProject()
        //            .getOption(JavaCore.COMPILER_PB_SUPPRESS_OPTIONAL_ERRORS, true)))
        //      {
        //         return;
        //      }
        if (JavaCore.DISABLED.equals(JavaCore.getOption(JavaCore.COMPILER_PB_SUPPRESS_WARNINGS))) {
            return;
        }

        String warningToken = CorrectionEngine.getWarningToken(problem.getProblemId());
        if (warningToken == null) {
            return;
        }
        for (Iterator<ICommandAccess> iter = proposals.iterator(); iter.hasNext(); ) {
            Object element = iter.next();
            if (element instanceof SuppressWarningsProposal
                && warningToken.equals(((SuppressWarningsProposal)element).getWarningToken())) {
                return; // only one at a time
            }
        }

        ASTNode node = problem.getCoveringNode(context.getASTRoot());
        if (node == null) {
            return;
        }

        ASTNode target = node;
        int relevance = -2;
        do {
            relevance =
                    addSuppressWarningsProposalIfPossible(context.getDocument(), target, warningToken, relevance, proposals);
            if (relevance == 0)
                return;
            target = target.getParent();
        }
        while (target != null);

        ASTNode importStatement = ASTNodes.getParent(node, ImportDeclaration.IMPORT_DECLARATION);
        if (importStatement != null && !context.getASTRoot().types().isEmpty()) {
            target = (ASTNode)context.getASTRoot().types().get(0);
            if (target != null) {
                addSuppressWarningsProposalIfPossible(context.getDocument(), target, warningToken, -2, proposals);
            }
        }
    }

    private static String getFirstFragmentName(List<VariableDeclarationFragment> fragments) {
        if (fragments.size() > 0) {
            return fragments.get(0).getName().getIdentifier();
        }
        return new String();
    }

    private static class SuppressWarningsProposal extends ASTRewriteCorrectionProposal {

        private final String fWarningToken;

        private final ASTNode fNode;

        private final ChildListPropertyDescriptor fProperty;

        public SuppressWarningsProposal(String warningToken, String label, ASTNode node,
                                        ChildListPropertyDescriptor property, int relevance, Document document) {
            super(label, null, relevance, document, Images.javadoc);
            fWarningToken = warningToken;
            fNode = node;
            fProperty = property;
            setCommandId(ADD_SUPPRESSWARNINGS_ID);
        }

        /** @return Returns the warningToken. */
        public String getWarningToken() {
            return fWarningToken;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal#getRewrite()
         */
        @Override
        protected ASTRewrite getRewrite() throws CoreException {
            AST ast = fNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            StringLiteral newStringLiteral = ast.newStringLiteral();
            newStringLiteral.setLiteralValue(fWarningToken);

            Annotation existing = findExistingAnnotation((List<? extends ASTNode>)fNode.getStructuralProperty(fProperty));
            if (existing == null) {
                ListRewrite listRewrite = rewrite.getListRewrite(fNode, fProperty);

                SingleMemberAnnotation newAnnot = ast.newSingleMemberAnnotation();
                String importString =
                        createImportRewrite((CompilationUnit)fNode.getRoot()).addImport("java.lang.SuppressWarnings"); //$NON-NLS-1$
                newAnnot.setTypeName(ast.newName(importString));

                newAnnot.setValue(newStringLiteral);

                listRewrite.insertFirst(newAnnot, null);
            } else if (existing instanceof SingleMemberAnnotation) {
                SingleMemberAnnotation annotation = (SingleMemberAnnotation)existing;
                Expression value = annotation.getValue();
                if (!addSuppressArgument(rewrite, value, newStringLiteral)) {
                    rewrite.set(existing, SingleMemberAnnotation.VALUE_PROPERTY, newStringLiteral, null);
                }
            } else if (existing instanceof NormalAnnotation) {
                NormalAnnotation annotation = (NormalAnnotation)existing;
                Expression value = findValue(annotation.values());
                if (!addSuppressArgument(rewrite, value, newStringLiteral)) {
                    ListRewrite listRewrite = rewrite.getListRewrite(annotation, NormalAnnotation.VALUES_PROPERTY);
                    MemberValuePair pair = ast.newMemberValuePair();
                    pair.setName(ast.newSimpleName("value")); //$NON-NLS-1$
                    pair.setValue(newStringLiteral);
                    listRewrite.insertFirst(pair, null);
                }
            }
            return rewrite;
        }

        private static boolean addSuppressArgument(ASTRewrite rewrite, Expression value, StringLiteral newStringLiteral) {
            if (value instanceof ArrayInitializer) {
                ListRewrite listRewrite = rewrite.getListRewrite(value, ArrayInitializer.EXPRESSIONS_PROPERTY);
                listRewrite.insertLast(newStringLiteral, null);
            } else if (value instanceof StringLiteral) {
                ArrayInitializer newArr = rewrite.getAST().newArrayInitializer();
                newArr.expressions().add(rewrite.createMoveTarget(value));
                newArr.expressions().add(newStringLiteral);
                rewrite.replace(value, newArr, null);
            } else {
                return false;
            }
            return true;
        }

        private static Expression findValue(List<MemberValuePair> keyValues) {
            for (int i = 0, len = keyValues.size(); i < len; i++) {
                MemberValuePair curr = keyValues.get(i);
                if ("value".equals(curr.getName().getIdentifier())) { //$NON-NLS-1$
                    return curr.getValue();
                }
            }
            return null;
        }

        private static Annotation findExistingAnnotation(List<? extends ASTNode> modifiers) {
            for (int i = 0, len = modifiers.size(); i < len; i++) {
                Object curr = modifiers.get(i);
                if (curr instanceof NormalAnnotation || curr instanceof SingleMemberAnnotation) {
                    Annotation annotation = (Annotation)curr;
                    ITypeBinding typeBinding = annotation.resolveTypeBinding();
                    if (typeBinding != null) {
                        if ("java.lang.SuppressWarnings".equals(typeBinding.getQualifiedName())) { //$NON-NLS-1$
                            return annotation;
                        }
                    } else {
                        String fullyQualifiedName = annotation.getTypeName().getFullyQualifiedName();
                        if ("SuppressWarnings".equals(fullyQualifiedName) ||
                            "java.lang.SuppressWarnings".equals(fullyQualifiedName)) { //$NON-NLS-1$ //$NON-NLS-2$
                            return annotation;
                        }
                    }
                }
            }
            return null;
        }
    }

    /**
     * Adds a SuppressWarnings proposal if possible and returns whether parent nodes should be processed or not (and with what relevance).
     *
     * @param cu
     *         the compilation unit
     * @param node
     *         the node on which to add a SuppressWarning token
     * @param warningToken
     *         the warning token to add
     * @param relevance
     *         the proposal's relevance
     * @param proposals
     *         collector to which the proposal should be added
     * @return <code>0</code> if no further proposals should be added to parent nodes, or the relevance of the next proposal
     * @since 3.6
     */
    private static int addSuppressWarningsProposalIfPossible(Document document, ASTNode node, String warningToken,
                                                             int relevance, Collection<ICommandAccess> proposals) {

        ChildListPropertyDescriptor property;
        String name;
        boolean isLocalVariable = false;
        switch (node.getNodeType()) {
            case ASTNode.SINGLE_VARIABLE_DECLARATION:
                property = SingleVariableDeclaration.MODIFIERS2_PROPERTY;
                name = ((SingleVariableDeclaration)node).getName().getIdentifier();
                isLocalVariable = true;
                break;
            case ASTNode.VARIABLE_DECLARATION_STATEMENT:
                property = VariableDeclarationStatement.MODIFIERS2_PROPERTY;
                name = getFirstFragmentName(((VariableDeclarationStatement)node).fragments());
                isLocalVariable = true;
                break;
            case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
                property = VariableDeclarationExpression.MODIFIERS2_PROPERTY;
                name = getFirstFragmentName(((VariableDeclarationExpression)node).fragments());
                isLocalVariable = true;
                break;
            case ASTNode.TYPE_DECLARATION:
                property = TypeDeclaration.MODIFIERS2_PROPERTY;
                name = ((TypeDeclaration)node).getName().getIdentifier();
                break;
            case ASTNode.ANNOTATION_TYPE_DECLARATION:
                property = AnnotationTypeDeclaration.MODIFIERS2_PROPERTY;
                name = ((AnnotationTypeDeclaration)node).getName().getIdentifier();
                break;
            case ASTNode.ENUM_DECLARATION:
                property = EnumDeclaration.MODIFIERS2_PROPERTY;
                name = ((EnumDeclaration)node).getName().getIdentifier();
                break;
            case ASTNode.FIELD_DECLARATION:
                property = FieldDeclaration.MODIFIERS2_PROPERTY;
                name = getFirstFragmentName(((FieldDeclaration)node).fragments());
                break;
            // case ASTNode.INITIALIZER: not used, because Initializer cannot have annotations
            case ASTNode.METHOD_DECLARATION:
                property = MethodDeclaration.MODIFIERS2_PROPERTY;
                name = ((MethodDeclaration)node).getName().getIdentifier() + "()"; //$NON-NLS-1$
                break;
            case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
                property = AnnotationTypeMemberDeclaration.MODIFIERS2_PROPERTY;
                name = ((AnnotationTypeMemberDeclaration)node).getName().getIdentifier() + "()"; //$NON-NLS-1$
                break;
            case ASTNode.ENUM_CONSTANT_DECLARATION:
                property = EnumConstantDeclaration.MODIFIERS2_PROPERTY;
                name = ((EnumConstantDeclaration)node).getName().getIdentifier();
                break;
            default:
                return relevance;
        }

        String label =
                CorrectionMessages.INSTANCE.SuppressWarningsSubProcessor_suppress_warnings_label(warningToken, name);
        ASTRewriteCorrectionProposal proposal =
                new SuppressWarningsProposal(warningToken, label, node, property, relevance, document);

        proposals.add(proposal);
        return isLocalVariable ? relevance - 1 : 0;
    }

    /**
     * Adds a proposal to correct the name of the SuppressWarning annotation
     *
     * @param context
     *         the context
     * @param problem
     *         the problem
     * @param proposals
     *         the resulting proposals
     */
    public static void addUnknownSuppressWarningProposals(InvocationContext context, IProblemLocation problem,
                                                          Collection<ICommandAccess> proposals) {

        ASTNode coveringNode = context.getCoveringNode();
        if (!(coveringNode instanceof StringLiteral))
            return;

        AST ast = coveringNode.getAST();
        StringLiteral literal = (StringLiteral)coveringNode;

        String literalValue = literal.getLiteralValue();
        String[] allWarningTokens = CorrectionEngine.getAllWarningTokens();
        for (int i = 0; i < allWarningTokens.length; i++) {
            String curr = allWarningTokens[i];
            if (NameMatcher.isSimilarName(literalValue, curr)) {
                StringLiteral newLiteral = ast.newStringLiteral();
                newLiteral.setLiteralValue(curr);
                ASTRewrite rewrite = ASTRewrite.create(ast);
                rewrite.replace(literal, newLiteral, null);
                String label = CorrectionMessages.INSTANCE.SuppressWarningsSubProcessor_fix_suppress_token_label(curr);
                Images image = Images.correction_change;
                ASTRewriteCorrectionProposal proposal =
                        new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
                proposals.add(proposal);
            }
        }
        addRemoveUnusedSuppressWarningProposals(context, problem, proposals);
    }

    public static void addRemoveUnusedSuppressWarningProposals(InvocationContext context, IProblemLocation problem,
                                                               Collection<ICommandAccess> proposals) {
        ASTNode coveringNode = problem.getCoveringNode(context.getASTRoot());
        if (!(coveringNode instanceof StringLiteral))
            return;

        StringLiteral literal = (StringLiteral)coveringNode;

        if (coveringNode.getParent() instanceof MemberValuePair) {
            coveringNode = coveringNode.getParent();
        }

        ASTNode parent = coveringNode.getParent();

        ASTRewrite rewrite = ASTRewrite.create(coveringNode.getAST());
        if (parent instanceof SingleMemberAnnotation) {
            rewrite.remove(parent, null);
        } else if (parent instanceof NormalAnnotation) {
            NormalAnnotation annot = (NormalAnnotation)parent;
            if (annot.values().size() == 1) {
                rewrite.remove(annot, null);
            } else {
                rewrite.remove(coveringNode, null);
            }
        } else if (parent instanceof ArrayInitializer) {
            rewrite.remove(coveringNode, null);
        } else {
            return;
        }
        String label =
                CorrectionMessages.INSTANCE.SuppressWarningsSubProcessor_remove_annotation_label(literal.getLiteralValue());
        Images image = Images.delete_obj;
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
        proposals.add(proposal);
    }

}
