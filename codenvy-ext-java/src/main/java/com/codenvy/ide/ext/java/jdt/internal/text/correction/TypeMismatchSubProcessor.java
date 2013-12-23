/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla <bmuskalla@eclipsesource.com> - [quick fix] proposes wrong cast from Object to primitive int - https://bugs
 *     .eclipse.org/bugs/show_bug.cgi?id=100593
 *     Benjamin Muskalla <bmuskalla@eclipsesource.com> - [quick fix] "Add exceptions to..." quickfix does nothing - https://bugs.eclipse
 *     .org/bugs/show_bug.cgi?id=107924
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ArrayInitializer;
import com.codenvy.ide.ext.java.jdt.core.dom.Assignment;
import com.codenvy.ide.ext.java.jdt.core.dom.BodyDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.CastExpression;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.EnhancedForStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.Expression;
import com.codenvy.ide.ext.java.jdt.core.dom.FieldAccess;
import com.codenvy.ide.ext.java.jdt.core.dom.IBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.IMethodBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.IVariableBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.InfixExpression;
import com.codenvy.ide.ext.java.jdt.core.dom.MemberValuePair;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleMemberAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Type;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.CastCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal.ChangeDescription;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ChangeMethodSignatureProposal.RemoveDescription;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.LinkedCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.TypeChangeCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.ui.BindingLabelProvider;
import com.codenvy.ide.ext.java.jdt.internal.ui.JavaElementLabels;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.runtime.CoreException;

import java.util.ArrayList;
import java.util.Collection;

public class TypeMismatchSubProcessor {

    private TypeMismatchSubProcessor() {
    }

    public static void addTypeMismatchProposals(InvocationContext context, IProblemLocation problem,
                                                Collection<ICommandAccess> proposals) throws CoreException {
        String[] args = problem.getProblemArguments();
        if (args.length != 2) {
            return;
        }

        CompilationUnit astRoot = context.getASTRoot();
        AST ast = astRoot.getAST();

        ASTNode selectedNode = problem.getCoveredNode(astRoot);
        if (!(selectedNode instanceof Expression)) {
            return;
        }
        Expression nodeToCast = (Expression)selectedNode;
        Name receiverNode = null;
        ITypeBinding castTypeBinding = null;

        int parentNodeType = selectedNode.getParent().getNodeType();
        if (parentNodeType == ASTNode.ASSIGNMENT) {
            Assignment assign = (Assignment)selectedNode.getParent();
            Expression leftHandSide = assign.getLeftHandSide();
            if (selectedNode.equals(leftHandSide)) {
                nodeToCast = assign.getRightHandSide();
            }
            castTypeBinding = assign.getLeftHandSide().resolveTypeBinding();
            if (leftHandSide instanceof Name) {
                receiverNode = (Name)leftHandSide;
            } else if (leftHandSide instanceof FieldAccess) {
                receiverNode = ((FieldAccess)leftHandSide).getName();
            }
        } else if (parentNodeType == ASTNode.VARIABLE_DECLARATION_FRAGMENT) {
            VariableDeclarationFragment frag = (VariableDeclarationFragment)selectedNode.getParent();
            if (selectedNode.equals(frag.getName()) || selectedNode.equals(frag.getInitializer())) {
                nodeToCast = frag.getInitializer();
                castTypeBinding = ASTNodes.getType(frag).resolveBinding();
                receiverNode = frag.getName();
            }
        } else if (parentNodeType == ASTNode.MEMBER_VALUE_PAIR) {
            receiverNode = ((MemberValuePair)selectedNode.getParent()).getName();
            castTypeBinding = ASTResolving.guessBindingForReference(nodeToCast);
        } else if (parentNodeType == ASTNode.SINGLE_MEMBER_ANNOTATION) {
            receiverNode = ((SingleMemberAnnotation)selectedNode.getParent()).getTypeName(); // use the type name
            castTypeBinding = ASTResolving.guessBindingForReference(nodeToCast);
        } else {
            // try to find the binding corresponding to 'castTypeName'
            castTypeBinding = ASTResolving.guessBindingForReference(nodeToCast);
        }
        if (castTypeBinding == null) {
            return;
        }

        ITypeBinding currBinding = nodeToCast.resolveTypeBinding();

        if (!(nodeToCast instanceof ArrayInitializer)) {
            ITypeBinding castFixType = null;
            if (currBinding == null || castTypeBinding.isCastCompatible(currBinding)
                || nodeToCast instanceof CastExpression) {
                castFixType = castTypeBinding;
            } else if (true /*JavaModelUtil.is50OrHigher(cu.getJavaProject())*/) {
                ITypeBinding boxUnboxedTypeBinding = boxUnboxPrimitives(castTypeBinding, currBinding, ast);
                if (boxUnboxedTypeBinding != castTypeBinding && boxUnboxedTypeBinding.isCastCompatible(currBinding)) {
                    castFixType = boxUnboxedTypeBinding;
                }
            }
            if (castFixType != null) {
                proposals.add(createCastProposal(context, castFixType, nodeToCast, 7));
            }
        }

        boolean nullOrVoid = currBinding == null || "void".equals(currBinding.getName()); //$NON-NLS-1$

        // change method return statement to actual type
        if (!nullOrVoid && parentNodeType == ASTNode.RETURN_STATEMENT) {
            BodyDeclaration decl = ASTResolving.findParentBodyDeclaration(selectedNode);
            if (decl instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration)decl;

                currBinding = Bindings.normalizeTypeBinding(currBinding);
                if (currBinding == null) {
                    currBinding = ast.resolveWellKnownType("java.lang.Object"); //$NON-NLS-1$
                }
                if (currBinding.isWildcardType()) {
                    currBinding = ASTResolving.normalizeWildcardType(currBinding, true, ast);
                }

                ASTRewrite rewrite = ASTRewrite.create(ast);

                String label =
                        CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_changereturntype_description(currBinding.getName());
                Images image = Images.correction_change;
                LinkedCorrectionProposal proposal =
                        new LinkedCorrectionProposal(label, rewrite, 6, context.getDocument(), image);

                ImportRewrite imports = proposal.createImportRewrite(astRoot);
                ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(decl, imports);

                Type newReturnType = imports.addImport(currBinding, ast, importRewriteContext);
                rewrite.replace(methodDeclaration.getReturnType2(), newReturnType, null);

                String returnKey = "return"; //$NON-NLS-1$
                //            proposal.addLinkedPosition(rewrite.track(newReturnType), true, returnKey);
                ITypeBinding[] typeSuggestions = ASTResolving.getRelaxingTypes(ast, currBinding);
                //            for (int i = 0; i < typeSuggestions.length; i++)
                //            {
                //               proposal.addLinkedPositionProposal(returnKey, typeSuggestions[i]);
                //            }
                proposals.add(proposal);
            }
        }

        if (!nullOrVoid && receiverNode != null) {
            currBinding = Bindings.normalizeTypeBinding(currBinding);
            if (currBinding == null) {
                currBinding = ast.resolveWellKnownType("java.lang.Object"); //$NON-NLS-1$
            }
            if (currBinding.isWildcardType()) {
                currBinding = ASTResolving.normalizeWildcardType(currBinding, true, ast);
            }
            addChangeSenderTypeProposals(context, receiverNode, currBinding, true, 6, proposals);
        }

        addChangeSenderTypeProposals(context, nodeToCast, castTypeBinding, false, 5, proposals);

        if (castTypeBinding == ast.resolveWellKnownType("boolean") && currBinding != null && !currBinding.isPrimitive() &&
            !Bindings.isVoidType(currBinding)) { //$NON-NLS-1$
            String label = CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_insertnullcheck_description();
            Images image = Images.correction_change;
            ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

            InfixExpression expression = ast.newInfixExpression();
            expression.setLeftOperand((Expression)rewrite.createMoveTarget(nodeToCast));
            expression.setRightOperand(ast.newNullLiteral());
            expression.setOperator(InfixExpression.Operator.NOT_EQUALS);
            rewrite.replace(nodeToCast, expression, null);

            proposals.add(new ASTRewriteCorrectionProposal(label, rewrite, 2, context.getDocument(), image));
        }

    }

    public static ITypeBinding boxUnboxPrimitives(ITypeBinding castType, ITypeBinding toCast, AST ast) {
      /*
       * e.g:
       * 	void m(toCast var) {
       * 		castType i= var;
       * 	}
       */
        if (castType.isPrimitive() && !toCast.isPrimitive()) {
            return Bindings.getBoxedTypeBinding(castType, ast);
        } else if (!castType.isPrimitive() && toCast.isPrimitive()) {
            return Bindings.getUnboxedTypeBinding(castType, ast);
        } else {
            return castType;
        }
    }

    public static void addChangeSenderTypeProposals(InvocationContext context, Expression nodeToCast,
                                                    ITypeBinding castTypeBinding, boolean isAssignedNode, int relevance,
                                                    Collection<ICommandAccess> proposals) {
        IBinding callerBinding = Bindings.resolveExpressionBinding(nodeToCast, false);

        CompilationUnit astRoot = context.getASTRoot();

        boolean isThisCu = false;
        ITypeBinding declaringType = null;
        IBinding callerBindingDecl = callerBinding;
        if (callerBinding instanceof IVariableBinding) {
            IVariableBinding variableBinding = (IVariableBinding)callerBinding;

            if (variableBinding.isEnumConstant()) {
                return;
            }
            if (!variableBinding.isField()) {
                isThisCu = true;
            } else {
                callerBindingDecl = variableBinding.getVariableDeclaration();
                ITypeBinding declaringClass = variableBinding.getDeclaringClass();
                if (declaringClass == null) {
                    return; // array length
                }
                declaringType = declaringClass.getTypeDeclaration();
            }
        } else if (callerBinding instanceof IMethodBinding) {
            IMethodBinding methodBinding = (IMethodBinding)callerBinding;
            if (!methodBinding.isConstructor()) {
                declaringType = methodBinding.getDeclaringClass().getTypeDeclaration();
                callerBindingDecl = methodBinding.getMethodDeclaration();
            }
        } else if (callerBinding instanceof ITypeBinding
                   && nodeToCast.getLocationInParent() == SingleMemberAnnotation.TYPE_NAME_PROPERTY) {
            declaringType = (ITypeBinding)callerBinding;
            callerBindingDecl = Bindings.findMethodInType(declaringType, "value", (String[])null); //$NON-NLS-1$
            if (callerBindingDecl == null) {
                return;
            }
        }

        if (declaringType != null && declaringType.isFromSource()) {
            //TODO load class file
            //         targetCu = ASTResolving.findCompilationUnitForBinding(cu, astRoot, declaringType);
            isThisCu = true;
        }
        if (isThisCu && ASTResolving.isUseableTypeInContext(castTypeBinding, callerBindingDecl, false)) {
            proposals.add(new TypeChangeCorrectionProposal(callerBindingDecl, astRoot, castTypeBinding, isAssignedNode,
                                                           relevance, context.getDocument()));
        }

        // add interface to resulting type
        if (!isAssignedNode) {
            ITypeBinding nodeType = nodeToCast.resolveTypeBinding();
            if (castTypeBinding.isInterface() && nodeType != null && nodeType.isClass() && !nodeType.isAnonymous()
                && nodeType.isFromSource()) {
                //TODO load class file
                //            ITypeBinding typeDecl = nodeType.getTypeDeclaration();
                //            //            ICompilationUnit nodeCu = ASTResolving.findCompilationUnitForBinding(cu, astRoot, typeDecl);
                //            if (ASTResolving.isUseableTypeInContext(castTypeBinding, typeDecl, true))
                //            {
                //               proposals.add(new ImplementInterfaceProposal(typeDecl, astRoot, castTypeBinding, relevance - 1, context
                //                  .getDocument()));
                //            }
            }
        }
    }

    public static ASTRewriteCorrectionProposal createCastProposal(InvocationContext context,
                                                                  ITypeBinding castTypeBinding, Expression nodeToCast, int relevance) {

        String label;
        String castType = BindingLabelProvider.getBindingLabel(castTypeBinding, JavaElementLabels.ALL_DEFAULT);
        if (nodeToCast.getNodeType() == ASTNode.CAST_EXPRESSION) {
            label = CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_changecast_description(castType);
        } else {
            label = CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_addcast_description(castType);
        }
        return new CastCorrectionProposal(label, nodeToCast, castTypeBinding, relevance, context.getDocument());
    }

    public static void addIncompatibleReturnTypeProposals(InvocationContext context, IProblemLocation problem,
                                                          Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null) {
            return;
        }
        MethodDeclaration decl = ASTResolving.findParentMethodDeclaration(selectedNode);
        if (decl == null) {
            return;
        }
        IMethodBinding methodDeclBinding = decl.resolveBinding();
        if (methodDeclBinding == null) {
            return;
        }

        ITypeBinding returnType = methodDeclBinding.getReturnType();
        IMethodBinding overridden = Bindings.findOverriddenMethod(methodDeclBinding, false);
        if (overridden == null || overridden.getReturnType() == returnType) {
            return;
        }

        IMethodBinding methodDecl = methodDeclBinding.getMethodDeclaration();
        ITypeBinding overriddenReturnType = overridden.getReturnType();
        //      if (!JavaModelUtil.is50OrHigher(context.getCompilationUnit().getJavaProject()))
        //      {
        //         overriddenReturnType = overriddenReturnType.getErasure();
        //      }
        proposals.add(new TypeChangeCorrectionProposal(methodDecl, astRoot, overriddenReturnType, false, 8, context
                .getDocument()));

        IMethodBinding overriddenDecl = overridden.getMethodDeclaration();
        ITypeBinding overridenDeclType = overriddenDecl.getDeclaringClass();

        if (overridenDeclType.isFromSource()) {
            //TODO load class
            //         //         targetCu = ASTResolving.findCompilationUnitForBinding(cu, astRoot, overridenDeclType);
            //         if (ASTResolving.isUseableTypeInContext(returnType, overriddenDecl, false))
            //         {
            //            TypeChangeCorrectionProposal proposal =
            //               new TypeChangeCorrectionProposal(overriddenDecl, astRoot, returnType, false, 7, context.getDocument());
            //            if (overridenDeclType.isInterface())
            //            {
            //               proposal.setDisplayName(CorrectionMessages.INSTANCE
            //                  .TypeMismatchSubProcessor_changereturnofimplemented_description(overriddenDecl.getName()));
            //            }
            //            else
            //            {
            //               proposal.setDisplayName(CorrectionMessages.INSTANCE
            //                  .TypeMismatchSubProcessor_changereturnofoverridden_description(overriddenDecl.getName()));
            //            }
            //            proposals.add(proposal);
            //         }
        }
    }

    public static void addIncompatibleThrowsProposals(InvocationContext context, IProblemLocation problem,
                                                      Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (!(selectedNode instanceof MethodDeclaration)) {
            return;
        }
        MethodDeclaration decl = (MethodDeclaration)selectedNode;
        IMethodBinding methodDeclBinding = decl.resolveBinding();
        if (methodDeclBinding == null) {
            return;
        }

        IMethodBinding overridden = Bindings.findOverriddenMethod(methodDeclBinding, false);
        if (overridden == null) {
            return;
        }

        ITypeBinding[] methodExceptions = methodDeclBinding.getExceptionTypes();
        ITypeBinding[] definedExceptions = overridden.getExceptionTypes();

        ArrayList<ITypeBinding> undeclaredExceptions = new ArrayList<ITypeBinding>();
        {
            ChangeDescription[] changes = new ChangeDescription[methodExceptions.length];

            for (int i = 0; i < methodExceptions.length; i++) {
                if (!isDeclaredException(methodExceptions[i], definedExceptions)) {
                    changes[i] = new RemoveDescription();
                    undeclaredExceptions.add(methodExceptions[i]);
                }
            }
            String label =
                    CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_removeexceptions_description(methodDeclBinding
                                                                                                              .getName());
            Images image = Images.remove_correction;
            proposals.add(new ChangeMethodSignatureProposal(label, astRoot, methodDeclBinding, null, changes, 8, context
                    .getDocument(), image));
        }

        //TODO load class file
        //ITypeBinding declaringType = overridden.getDeclaringClass();
        //            if (declaringType.isFromSource())
        //            {
        //               targetCu = ASTResolving.findCompilationUnitForBinding(cu, astRoot, declaringType);
        //            }
        //            if (targetCu != null)
        //            {
        //      ChangeDescription[] changes = new ChangeDescription[definedExceptions.length + undeclaredExceptions.size()];
        //
        //      for (int i = 0; i < undeclaredExceptions.size(); i++)
        //      {
        //         changes[i + definedExceptions.length] = new InsertDescription(undeclaredExceptions.get(i), ""); //$NON-NLS-1$
        //      }
        //      IMethodBinding overriddenDecl = overridden.getMethodDeclaration();
        //      String label =
        //         CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_addexceptions_description(declaringType.getName(),
        //            overridden.getName());
        //      Image image = new Image(JavaClientBundle.INSTANCE.add_obj());
        //      proposals.add(new ChangeMethodSignatureProposal(label, astRoot, overriddenDecl, null, changes, 7, context
        //         .getDocument(), image));
        //            }
    }

    private static boolean isDeclaredException(ITypeBinding curr, ITypeBinding[] declared) {
        for (int i = 0; i < declared.length; i++) {
            if (Bindings.isSuperType(declared[i], curr)) {
                return true;
            }
        }
        return false;
    }

    public static void addTypeMismatchInForEachProposals(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();
        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null || selectedNode.getLocationInParent() != EnhancedForStatement.EXPRESSION_PROPERTY) {
            return;
        }
        EnhancedForStatement forStatement = (EnhancedForStatement)selectedNode.getParent();

        ITypeBinding expressionBinding = forStatement.getExpression().resolveTypeBinding();
        if (expressionBinding == null) {
            return;
        }

        ITypeBinding expectedBinding;
        if (expressionBinding.isArray()) {
            expectedBinding = expressionBinding.getComponentType();
        } else {
            IMethodBinding iteratorMethod = Bindings.findMethodInHierarchy(expressionBinding, "iterator", new String[0]); //$NON-NLS-1$
            if (iteratorMethod == null) {
                return;
            }
            ITypeBinding[] typeArguments = iteratorMethod.getReturnType().getTypeArguments();
            if (typeArguments.length != 1) {
                return;
            }
            expectedBinding = typeArguments[0];
        }
        AST ast = astRoot.getAST();
        expectedBinding = Bindings.normalizeForDeclarationUse(expectedBinding, ast);

        SingleVariableDeclaration parameter = forStatement.getParameter();

        String label =
                CorrectionMessages.INSTANCE.TypeMismatchSubProcessor_incompatible_for_each_type_description(parameter
                                                                                                                    .getName()
                                                                                                                    .getIdentifier(),
                                                                                                            BindingLabelProvider
                                                                                                                    .getBindingLabel(
                                                                                                                            expectedBinding,
                                                                                                                            BindingLabelProvider.DEFAULT_TEXTFLAGS));
        Images image = Images.correction_change;
        ASTRewrite rewrite = ASTRewrite.create(ast);
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);

        ImportRewrite importRewrite = proposal.createImportRewrite(astRoot);
        ImportRewriteContext importRewriteContext =
                new ContextSensitiveImportRewriteContext(ASTResolving.findParentBodyDeclaration(selectedNode), importRewrite);
        Type newType = importRewrite.addImport(expectedBinding, ast, importRewriteContext);
        rewrite.replace(parameter.getType(), newType, null);

        proposals.add(proposal);
    }

}
