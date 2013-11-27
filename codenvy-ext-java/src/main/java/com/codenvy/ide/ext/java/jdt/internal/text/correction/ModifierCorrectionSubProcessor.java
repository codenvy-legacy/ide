/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla <bmuskalla@innoopract.com> - [quick fix] 'Remove invalid modifiers' does not appear for enums and annotations -
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=110589
 *     Benjamin Muskalla <b.muskalla@gmx.net> - [quick fix] Quick fix for missing synchronized modifier - https://bugs.eclipse
 *     .org/bugs/show_bug.cgi?id=245250
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.*;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier.ModifierKeyword;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodeFactory;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.CompilationUnitRewriteOperationsFix.CompilationUnitRewriteOperation;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.UnimplementedCodeFix;
import com.codenvy.ide.ext.java.jdt.internal.corext.fix.UnimplementedCodeFix.MakeTypeAbstractOperation;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.JdtFlags;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.FixCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.LinkedCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ModifierChangeCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;

import java.util.Collection;
import java.util.List;

/**
 */
public class ModifierCorrectionSubProcessor {

    public static final int TO_STATIC = 1;

    public static final int TO_VISIBLE = 2;

    public static final int TO_NON_PRIVATE = 3;

    public static final int TO_NON_STATIC = 4;

    public static final int TO_NON_FINAL = 5;

    public static void addNonAccessibleReferenceProposal(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals, int kind, int relevance)
            throws CoreException {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode == null) {
            return;
        }

        IBinding binding = null;
        switch (selectedNode.getNodeType()) {
            case ASTNode.SIMPLE_NAME:
                binding = ((SimpleName)selectedNode).resolveBinding();
                break;
            case ASTNode.QUALIFIED_NAME:
                binding = ((QualifiedName)selectedNode).resolveBinding();
                break;
            case ASTNode.SIMPLE_TYPE:
                binding = ((SimpleType)selectedNode).resolveBinding();
                break;
            case ASTNode.METHOD_INVOCATION:
                binding = ((MethodInvocation)selectedNode).getName().resolveBinding();
                break;
            case ASTNode.SUPER_METHOD_INVOCATION:
                binding = ((SuperMethodInvocation)selectedNode).getName().resolveBinding();
                break;
            case ASTNode.FIELD_ACCESS:
                binding = ((FieldAccess)selectedNode).getName().resolveBinding();
                break;
            case ASTNode.SUPER_FIELD_ACCESS:
                binding = ((SuperFieldAccess)selectedNode).getName().resolveBinding();
                break;
            case ASTNode.CLASS_INSTANCE_CREATION:
                binding = ((ClassInstanceCreation)selectedNode).resolveConstructorBinding();
                break;
            case ASTNode.SUPER_CONSTRUCTOR_INVOCATION:
                binding = ((SuperConstructorInvocation)selectedNode).resolveConstructorBinding();
                break;
            default:
                return;
        }
        ITypeBinding typeBinding = null;
        String name;
        IBinding bindingDecl;
        boolean isLocalVar = false;
        if (binding instanceof IVariableBinding && problem.getProblemId() == IProblem.NotVisibleType) {
            binding = ((IVariableBinding)binding).getType();
        }
        if (binding instanceof IMethodBinding) {
            IMethodBinding methodDecl = (IMethodBinding)binding;
            if (methodDecl.isDefaultConstructor()) {
                UnresolvedElementsSubProcessor.getConstructorProposals(context, problem, proposals);
                return;
            }
            bindingDecl = methodDecl.getMethodDeclaration();
            typeBinding = methodDecl.getDeclaringClass();
            name = methodDecl.getName() + "()"; //$NON-NLS-1$
        } else if (binding instanceof IVariableBinding) {
            IVariableBinding varDecl = (IVariableBinding)binding;
            typeBinding = varDecl.getDeclaringClass();
            name = binding.getName();
            isLocalVar = !varDecl.isField();
            bindingDecl = varDecl.getVariableDeclaration();
        } else if (binding instanceof ITypeBinding) {
            typeBinding = (ITypeBinding)binding;
            bindingDecl = typeBinding.getTypeDeclaration();
            name = binding.getName();
        } else {
            return;
        }
        if (typeBinding != null && typeBinding.isFromSource() || isLocalVar) {
            int includedModifiers = 0;
            int excludedModifiers = 0;
            String label;
            switch (kind) {
                case TO_VISIBLE:
                    excludedModifiers = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
                    includedModifiers = getNeededVisibility(selectedNode, typeBinding);
                    label =
                            CorrectionMessages.INSTANCE.INSTANCE.ModifierCorrectionSubProcessor_changevisibility_description(
                                    name, getVisibilityString(includedModifiers));
                    break;
                case TO_STATIC:
                    label =
                            CorrectionMessages.INSTANCE.INSTANCE
                                                       .ModifierCorrectionSubProcessor_changemodifiertostatic_description(name);
                    includedModifiers = Modifier.STATIC;
                    break;
                case TO_NON_STATIC:
                    label =

                            CorrectionMessages.INSTANCE.INSTANCE
                                                       .ModifierCorrectionSubProcessor_changemodifiertononstatic_description(name);
                    excludedModifiers = Modifier.STATIC;
                    break;
                case TO_NON_PRIVATE:
                    int visibility;
                    if (context.getASTRoot().getPackage().getName().getFullyQualifiedName()
                               .equals(typeBinding.getPackage().getName()))
                    //               if (cu.getParent().getElementName().equals())
                    {
                        visibility = Modifier.NONE;
                        excludedModifiers = Modifier.PRIVATE;
                    } else {
                        visibility = Modifier.PUBLIC;
                        includedModifiers = Modifier.PUBLIC;
                        excludedModifiers = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
                    }
                    label =
                            CorrectionMessages.INSTANCE.INSTANCE.ModifierCorrectionSubProcessor_changevisibility_description(
                                    name, getVisibilityString(visibility));
                    break;
                case TO_NON_FINAL:
                    label =

                            CorrectionMessages.INSTANCE.INSTANCE
                                                       .ModifierCorrectionSubProcessor_changemodifiertononfinal_description(name);
                    excludedModifiers = Modifier.FINAL;
                    break;
                default:
                    throw new IllegalArgumentException("not supported"); //$NON-NLS-1$
            }
            //TODO load class
            //         ICompilationUnit targetCU =
            //            isLocalVar ? cu : ASTResolving.findCompilationUnitForBinding(cu, context.getASTRoot(),
            //               typeBinding.getTypeDeclaration());
            //         if (targetCU != null)
            //         {
            //         Image image = new Image(JavaClientBundle.INSTANCE.correction_change());
            //         proposals.add(new ModifierChangeCorrectionProposal(label, bindingDecl, selectedNode, includedModifiers,
            //            excludedModifiers, relevance, context.getDocument(), image));
            //         }
        }
        if (kind == TO_VISIBLE && bindingDecl.getKind() == IBinding.VARIABLE) {
            UnresolvedElementsSubProcessor
                    .getVariableProposals(context, problem, (IVariableBinding)bindingDecl, proposals);
        }
    }

    public static void addChangeOverriddenModifierProposal(InvocationContext context, IProblemLocation problem,
                                                           Collection<ICommandAccess> proposals, int kind) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof MethodDeclaration)) {
            return;
        }

        IMethodBinding method = ((MethodDeclaration)selectedNode).resolveBinding();
        ITypeBinding curr = method.getDeclaringClass();

        if (kind == TO_VISIBLE && problem.getProblemId() != IProblem.OverridingNonVisibleMethod) {
            IMethodBinding defining = Bindings.findOverriddenMethod(method, false);
            if (defining != null) {
                int excludedModifiers = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
                int includedModifiers = JdtFlags.getVisibilityCode(defining);
                String label =
                        CorrectionMessages.INSTANCE
                                          .ModifierCorrectionSubProcessor_changemethodvisibility_description(
                                                  getVisibilityString(includedModifiers));
                Images image = Images.correction_change;
                proposals.add(new ModifierChangeCorrectionProposal(label, method, selectedNode, includedModifiers,
                                                                   excludedModifiers, 8, context.getDocument(), image));
            }
        }

        IMethodBinding overriddenInClass = null;
        while (overriddenInClass == null && curr.getSuperclass() != null) {
            curr = curr.getSuperclass();
            overriddenInClass = Bindings.findOverriddenMethodInType(curr, method);
        }
        if (overriddenInClass != null) {
            //TODO
            //IMethodBinding overriddenDecl = overriddenInClass.getMethodDeclaration();
            //         ICompilationUnit targetCU =
            //            ASTResolving.findCompilationUnitForBinding(cu, context.getASTRoot(), overriddenDecl.getDeclaringClass());
            //         if (targetCU != null)
            //         {
            //            String methodLabel = curr.getName() + '.' + overriddenInClass.getName();
            //            String label;
            //            int excludedModifiers;
            //            int includedModifiers;
            //            switch (kind)
            //            {
            //               case TO_VISIBLE :
            //                  excludedModifiers = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
            //                  includedModifiers = JdtFlags.getVisibilityCode(method);
            //                  label =
            //                     CorrectionMessages.INSTANCE.INSTANCE
            //                        .ModifierCorrectionSubProcessor_changeoverriddenvisibility_description(methodLabel,
            //                           getVisibilityString(includedModifiers));
            //                  break;
            //               case TO_NON_FINAL :
            //                  label =
            //                     CorrectionMessages.INSTANCE.INSTANCE
            //                        .ModifierCorrectionSubProcessor_changemethodtononfinal_description(methodLabel);
            //                  excludedModifiers = Modifier.FINAL;
            //                  includedModifiers = 0;
            //                  break;
            //               case TO_NON_STATIC :
            //                  label =
            //                     CorrectionMessages.INSTANCE.INSTANCE
            //                        .ModifierCorrectionSubProcessor_changemethodtononstatic_description(methodLabel);
            //                  excludedModifiers = Modifier.STATIC;
            //                  includedModifiers = 0;
            //                  break;
            //               default :
            //                  Assert.isTrue(false, "not supported"); //$NON-NLS-1$
            //                  return;
            //            }
            //            Image image = new Image(JavaClientBundle.INSTANCE.correction_change());
            //            proposals.add(new ModifierChangeCorrectionProposal(label, overriddenDecl, selectedNode, includedModifiers,
            //               excludedModifiers, 7, context.getDocument(), image));
            //         }
        }
    }

    public static void addNonFinalLocalProposal(InvocationContext context, IProblemLocation problem,
                                                Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof SimpleName)) {
            return;
        }

        IBinding binding = ((SimpleName)selectedNode).resolveBinding();
        if (binding instanceof IVariableBinding) {
            binding = ((IVariableBinding)binding).getVariableDeclaration();
            Images image = Images.correction_change;
            String label =
                    CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_changemodifiertofinal_description(binding
                                                                                                                         .getName());
            proposals.add(new ModifierChangeCorrectionProposal(label, binding, selectedNode, Modifier.FINAL, 0, 5, context
                    .getDocument(), image));
        }
    }

    public static void addRemoveInvalidModifiersProposal(InvocationContext context, IProblemLocation problem,
                                                         Collection<ICommandAccess> proposals, int relevance) {

        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (selectedNode instanceof MethodDeclaration) {
            selectedNode = ((MethodDeclaration)selectedNode).getName();
        }

        if (!(selectedNode instanceof SimpleName)) {
            return;
        }

        IBinding binding = ((SimpleName)selectedNode).resolveBinding();
        if (binding != null) {
            String methodName = binding.getName();
            String label = null;
            int problemId = problem.getProblemId();

            int excludedModifiers = 0;
            int includedModifiers = 0;

            switch (problemId) {
                case IProblem.CannotHideAnInstanceMethodWithAStaticMethod:
                case IProblem.UnexpectedStaticModifierForMethod:
                    excludedModifiers = Modifier.STATIC;
                    label =

                            CorrectionMessages.INSTANCE
                                              .ModifierCorrectionSubProcessor_changemethodtononstatic_description(methodName);
                    break;
                case IProblem.UnexpectedStaticModifierForField:
                    excludedModifiers = Modifier.STATIC;
                    label =

                            CorrectionMessages.INSTANCE
                                              .ModifierCorrectionSubProcessor_changefieldmodifiertononstatic_description(methodName);
                    break;
                case IProblem.IllegalModifierCombinationFinalVolatileForField:
                    excludedModifiers = Modifier.VOLATILE;
                    label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_removevolatile_description();
                    break;
                case IProblem.IllegalModifierForInterfaceMethod:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.ABSTRACT);
                    break;
                case IProblem.IllegalModifierForInterface:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.STRICTFP);
                    break;
                case IProblem.IllegalModifierForClass:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.ABSTRACT | Modifier.FINAL | Modifier.STRICTFP);
                    break;
                case IProblem.IllegalModifierForInterfaceField:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
                    break;
                case IProblem.IllegalModifierForMemberInterface:
                case IProblem.IllegalVisibilityModifierForInterfaceMemberType:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.STATIC | Modifier.STRICTFP);
                    break;
                case IProblem.IllegalModifierForMemberClass:
                    excludedModifiers =
                            ~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.ABSTRACT
                              | Modifier.FINAL | Modifier.STRICTFP);
                    break;
                case IProblem.IllegalModifierForLocalClass:
                    excludedModifiers = ~(Modifier.ABSTRACT | Modifier.FINAL | Modifier.STRICTFP);
                    break;
                case IProblem.IllegalModifierForArgument:
                    excludedModifiers = ~Modifier.FINAL;
                    break;
                case IProblem.IllegalModifierForField:
                    excludedModifiers =
                            ~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL
                              | Modifier.VOLATILE | Modifier.TRANSIENT);
                    break;
                case IProblem.IllegalModifierForMethod:
                    excludedModifiers =
                            ~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.ABSTRACT
                              | Modifier.FINAL | Modifier.NATIVE | Modifier.STRICTFP | Modifier.SYNCHRONIZED);
                    break;
                case IProblem.IllegalModifierForConstructor:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);
                    break;
                case IProblem.IllegalModifierForVariable:
                    excludedModifiers = ~Modifier.FINAL;
                    break;
                case IProblem.IllegalModifierForEnum:
                    excludedModifiers = ~(Modifier.PUBLIC | Modifier.STRICTFP);
                    break;
                case IProblem.IllegalModifierForEnumConstant:
                    excludedModifiers = ~Modifier.NONE;
                    break;
                case IProblem.IllegalModifierForEnumConstructor:
                    excludedModifiers = ~Modifier.PRIVATE;
                    break;
                case IProblem.IllegalModifierForMemberEnum:
                    excludedModifiers =
                            ~(Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED | Modifier.STATIC | Modifier.STRICTFP);
                    break;
                default:
                    Assert.isTrue(false, "not supported"); //$NON-NLS-1$
                    return;
            }

            if (label == null)
                label =
                        CorrectionMessages.INSTANCE
                                          .ModifierCorrectionSubProcessor_removeinvalidmodifiers_description(methodName);

            Images image = Images.correction_change;
            proposals.add(new ModifierChangeCorrectionProposal(label, binding, selectedNode, includedModifiers,
                                                               excludedModifiers, relevance, context.getDocument(), image));

            if (problemId == IProblem.IllegalModifierCombinationFinalVolatileForField) {
                proposals.add(new ModifierChangeCorrectionProposal(CorrectionMessages.INSTANCE
                                                                                     .ModifierCorrectionSubProcessor_removefinal_description(),
                                                                   binding, selectedNode, 0, Modifier.FINAL,
                                                                   relevance + 1, context.getDocument(), image));
            }

            if (problemId == IProblem.UnexpectedStaticModifierForField && binding instanceof IVariableBinding) {
                ITypeBinding declClass = ((IVariableBinding)binding).getDeclaringClass();
                if (declClass.isMember()) {
                    proposals.add(new ModifierChangeCorrectionProposal(CorrectionMessages.INSTANCE
                                                                                         .ModifierCorrectionSubProcessor_changemodifiertostaticfinal_description(),
                                                                       binding, selectedNode,
                                                                       Modifier.FINAL, Modifier.VOLATILE, relevance + 1,
                                                                       context.getDocument(), image));
                    ASTNode parentType = context.getASTRoot().findDeclaringNode(declClass);
                    if (parentType != null) {
                        proposals.add(new ModifierChangeCorrectionProposal(CorrectionMessages.INSTANCE
                                                                                             .ModifierCorrectionSubProcessor_addstatictoparenttype_description(),
                                                                           declClass, parentType,
                                                                           Modifier.STATIC, 0, relevance - 1, context.getDocument(),
                                                                           image));
                    }
                }
            }
            if (problemId == IProblem.UnexpectedStaticModifierForMethod && binding instanceof IMethodBinding) {
                ITypeBinding declClass = ((IMethodBinding)binding).getDeclaringClass();
                if (declClass.isMember()) {
                    ASTNode parentType = context.getASTRoot().findDeclaringNode(declClass);
                    if (parentType != null) {
                        proposals.add(new ModifierChangeCorrectionProposal(CorrectionMessages.INSTANCE
                                                                                             .ModifierCorrectionSubProcessor_addstatictoparenttype_description(),
                                                                           declClass, parentType,
                                                                           Modifier.STATIC, 0, relevance - 1, context.getDocument(),
                                                                           image));
                    }
                }
            }
        }
    }

    private static String getVisibilityString(int code) {
        if (Modifier.isPublic(code)) {
            return "public"; //$NON-NLS-1$
        } else if (Modifier.isProtected(code)) {
            return "protected"; //$NON-NLS-1$
        } else if (Modifier.isPrivate(code)) {
            return "private"; //$NON-NLS-1$
        }
        return CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_default();
    }

    private static int getNeededVisibility(ASTNode currNode, ITypeBinding targetType) {
        ITypeBinding currNodeBinding = Bindings.getBindingOfParentType(currNode);
        if (currNodeBinding == null) { // import
            return Modifier.PUBLIC;
        }

        if (Bindings.isSuperType(targetType, currNodeBinding)) {
            return Modifier.PROTECTED;
        }

        if (currNodeBinding.getPackage().getKey().equals(targetType.getPackage().getKey())) {
            return 0;
        }
        return Modifier.PUBLIC;
    }

    public static void addAbstractMethodProposals(InvocationContext context, IProblemLocation problem,
                                                  Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();

        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null) {
            return;
        }
        MethodDeclaration decl;
        if (selectedNode instanceof SimpleName) {
            decl = (MethodDeclaration)selectedNode.getParent();
        } else if (selectedNode instanceof MethodDeclaration) {
            decl = (MethodDeclaration)selectedNode;
        } else {
            return;
        }

        ASTNode parentType = ASTResolving.findParentType(decl);
        TypeDeclaration parentTypeDecl = null;
        boolean parentIsAbstractClass = false;
        if (parentType instanceof TypeDeclaration) {
            parentTypeDecl = (TypeDeclaration)parentType;
            parentIsAbstractClass = !parentTypeDecl.isInterface() && Modifier.isAbstract(parentTypeDecl.getModifiers());
        }
        boolean hasNoBody = decl.getBody() == null;

        int id = problem.getProblemId();
        if (id == IProblem.AbstractMethodInAbstractClass || id == IProblem.EnumAbstractMethodMustBeImplemented
            || id == IProblem.AbstractMethodInEnum || parentIsAbstractClass) {
            AST ast = astRoot.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            Modifier modifierNode = ASTNodes.findModifierNode(Modifier.ABSTRACT, decl.modifiers());
            if (modifierNode != null) {
                rewrite.remove(modifierNode, null);
            }

            if (hasNoBody) {
                Block newBody = ast.newBlock();
                rewrite.set(decl, MethodDeclaration.BODY_PROPERTY, newBody, null);

                Type returnType = decl.getReturnType2();
                if (returnType != null) {
                    Expression expr = ASTNodeFactory.newDefaultExpression(ast, returnType, decl.getExtraDimensions());
                    if (expr != null) {
                        ReturnStatement returnStatement = ast.newReturnStatement();
                        returnStatement.setExpression(expr);
                        newBody.statements().add(returnStatement);
                    }
                }
            }

            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_removeabstract_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            proposals.add(proposal);
        }

        if (!hasNoBody && id == IProblem.BodyForAbstractMethod) {
            ASTRewrite rewrite = ASTRewrite.create(decl.getAST());
            rewrite.remove(decl.getBody(), null);

            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_removebody_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal2 =
                    new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
            proposals.add(proposal2);
        }

        if (id == IProblem.AbstractMethodInAbstractClass && parentTypeDecl != null) {
            addMakeTypeAbstractProposal(context, parentTypeDecl, proposals);
        }

    }

    private static void addMakeTypeAbstractProposal(InvocationContext context, TypeDeclaration parentTypeDecl,
                                                    Collection<ICommandAccess> proposals) {
        MakeTypeAbstractOperation operation = new UnimplementedCodeFix.MakeTypeAbstractOperation(parentTypeDecl);

        String label =
                CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_addabstract_description(parentTypeDecl.getName()
                                                                                                                 .getIdentifier());
        UnimplementedCodeFix fix =
                new UnimplementedCodeFix(label, context.getASTRoot(), new CompilationUnitRewriteOperation[]{operation},
                                         context.getDocument());

        Images image = Images.correction_change;
        FixCorrectionProposal proposal = new FixCorrectionProposal(fix, null, 5, image, context);
        proposals.add(proposal);
    }

    public static void addAbstractTypeProposals(InvocationContext context, IProblemLocation problem,
                                                Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();

        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null) {
            return;
        }

        TypeDeclaration parentTypeDecl = null;
        if (selectedNode instanceof SimpleName) {
            ASTNode parent = selectedNode.getParent();
            if (parent != null) {
                parentTypeDecl = (TypeDeclaration)parent;
            }
        } else if (selectedNode instanceof TypeDeclaration) {
            parentTypeDecl = (TypeDeclaration)selectedNode;
        }

        if (parentTypeDecl == null) {
            return;
        }

        addMakeTypeAbstractProposal(context, parentTypeDecl, proposals);
    }

    public static void addNativeMethodProposals(InvocationContext context, IProblemLocation problem,
                                                Collection<ICommandAccess> proposals) {
        CompilationUnit astRoot = context.getASTRoot();

        ASTNode selectedNode = problem.getCoveringNode(astRoot);
        if (selectedNode == null) {
            return;
        }
        MethodDeclaration decl;
        if (selectedNode instanceof SimpleName) {
            decl = (MethodDeclaration)selectedNode.getParent();
        } else if (selectedNode instanceof MethodDeclaration) {
            decl = (MethodDeclaration)selectedNode;
        } else {
            return;
        }

        {
            AST ast = astRoot.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            Modifier modifierNode = ASTNodes.findModifierNode(Modifier.NATIVE, decl.modifiers());
            if (modifierNode != null) {
                rewrite.remove(modifierNode, null);
            }

            Block newBody = ast.newBlock();
            rewrite.set(decl, MethodDeclaration.BODY_PROPERTY, newBody, null);

            Type returnType = decl.getReturnType2();
            if (returnType != null) {
                Expression expr = ASTNodeFactory.newDefaultExpression(ast, returnType, decl.getExtraDimensions());
                if (expr != null) {
                    ReturnStatement returnStatement = ast.newReturnStatement();
                    returnStatement.setExpression(expr);
                    newBody.statements().add(returnStatement);
                }
            }

            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_removenative_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            proposals.add(proposal);
        }

        if (decl.getBody() != null) {
            ASTRewrite rewrite = ASTRewrite.create(decl.getAST());
            rewrite.remove(decl.getBody(), null);

            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_removebody_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal2 =
                    new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
            proposals.add(proposal2);
        }

    }

    public static void addMethodRequiresBodyProposals(InvocationContext context, IProblemLocation problem,
                                                      Collection<ICommandAccess> proposals) {
        AST ast = context.getASTRoot().getAST();

        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof MethodDeclaration)) {
            return;
        }
        MethodDeclaration decl = (MethodDeclaration)selectedNode;
        Modifier modifierNode = ASTNodes.findModifierNode(Modifier.ABSTRACT, decl.modifiers());
        {
            ASTRewrite rewrite = ASTRewrite.create(ast);

            if (modifierNode != null) {
                rewrite.remove(modifierNode, null);
            }

            Block body = ast.newBlock();
            rewrite.set(decl, MethodDeclaration.BODY_PROPERTY, body, null);

            if (!decl.isConstructor()) {
                Type returnType = decl.getReturnType2();
                if (returnType != null) {
                    Expression expression = ASTNodeFactory.newDefaultExpression(ast, returnType, decl.getExtraDimensions());
                    if (expression != null) {
                        ReturnStatement returnStatement = ast.newReturnStatement();
                        returnStatement.setExpression(expression);
                        body.statements().add(returnStatement);
                    }
                }
            }

            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_addmissingbody_description();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 9, context.getDocument(), image);

            proposals.add(proposal);
        }

        if (modifierNode == null) {
            ASTRewrite rewrite = ASTRewrite.create(ast);

            Modifier newModifier = ast.newModifier(Modifier.ModifierKeyword.ABSTRACT_KEYWORD);
            rewrite.getListRewrite(decl, MethodDeclaration.MODIFIERS2_PROPERTY).insertLast(newModifier, null);

            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_setmethodabstract_description();
            Images image = Images.correction_change;
            LinkedCorrectionProposal proposal =
                    new LinkedCorrectionProposal(label, rewrite, 8, context.getDocument(), image);
            //         proposal.addLinkedPosition(rewrite.track(newModifier), true, "modifier"); //$NON-NLS-1$

            proposals.add(proposal);
        }

    }

    public static void addNeedToEmulateProposal(InvocationContext context, IProblemLocation problem,
                                                Collection<ModifierChangeCorrectionProposal> proposals) {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof SimpleName)) {
            return;
        }

        IBinding binding = ((SimpleName)selectedNode).resolveBinding();
        if (binding instanceof IVariableBinding) {
            binding = ((IVariableBinding)binding).getVariableDeclaration();
            Images image = Images.correction_change;
            String label =

                    CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_changemodifiertofinal_description(binding
                                                                                                                         .getName());
            proposals.add(new ModifierChangeCorrectionProposal(label, binding, selectedNode, Modifier.FINAL, 0, 5, context
                    .getDocument(), image));
        }
    }

    public static void addOverrideAnnotationProposal(InvocationContext context, IProblemLocation problem,
                                                     Collection<ICommandAccess> proposals) {
        System.out.println("ModifierCorrectionSubProcessor.addOverrideAnnotationProposal()");
        //TODO
        //      IProposableFix fix = Java50Fix.createAddOverrideAnnotationFix(context.getASTRoot(), problem);
        //      if (fix != null)
        //      {
        //         Image image = new Image(JavaClientBundle.INSTANCE.correction_change());
        //         Map<String, String> options = new HashMap<String, String>();
        //         options.put(CleanUpConstants.ADD_MISSING_ANNOTATIONS, CleanUpOptions.TRUE);
        //         options.put(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE, CleanUpOptions.TRUE);
        //         options.put(CleanUpConstants.ADD_MISSING_ANNOTATIONS_OVERRIDE_FOR_INTERFACE_METHOD_IMPLEMENTATION,
        //            CleanUpOptions.TRUE);
        //         FixCorrectionProposal proposal =
        //            new FixCorrectionProposal(fix, new Java50CleanUp(options), 15, image, context);
        //         proposals.add(proposal);
        //      }
    }

    public static void addDeprecatedAnnotationProposal(InvocationContext context, IProblemLocation problem,
                                                       Collection<ICommandAccess> proposals) {
        //TODO
        //      IProposableFix fix = Java50Fix.createAddDeprectatedAnnotation(context.getASTRoot(), problem);
        //      if (fix != null)
        //      {
        //         Image image = new Image(JavaClientBundle.INSTANCE.correction_change());
        //         Map<String, String> options = new HashMap<String, String>();
        //         options.put(CleanUpConstants.ADD_MISSING_ANNOTATIONS, CleanUpOptions.TRUE);
        //         options.put(CleanUpConstants.ADD_MISSING_ANNOTATIONS_DEPRECATED, CleanUpOptions.TRUE);
        //         FixCorrectionProposal proposal =
        //            new FixCorrectionProposal(fix, new Java50CleanUp(options), 15, image, context);
        //         proposals.add(proposal);
        //      }
    }

    public static void addOverridingDeprecatedMethodProposal(InvocationContext context, IProblemLocation problem,
                                                             Collection<ICommandAccess> proposals) {

        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof MethodDeclaration)) {
            return;
        }
        boolean is50OrHigher = true; //JavaModelUtil.is50OrHigher(cu.getJavaProject());
        MethodDeclaration methodDecl = (MethodDeclaration)selectedNode;
        AST ast = methodDecl.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        if (is50OrHigher) {
            Annotation annot = ast.newMarkerAnnotation();
            annot.setTypeName(ast.newName("Deprecated")); //$NON-NLS-1$
            rewrite.getListRewrite(methodDecl, methodDecl.getModifiersProperty()).insertFirst(annot, null);
        }
        Javadoc javadoc = methodDecl.getJavadoc();
        if (javadoc != null || !is50OrHigher) {
            if (!is50OrHigher) {
                javadoc = ast.newJavadoc();
                rewrite.set(methodDecl, MethodDeclaration.JAVADOC_PROPERTY, javadoc, null);
            }
            TagElement newTag = ast.newTagElement();
            newTag.setTagName(TagElement.TAG_DEPRECATED);
            JavadocTagsSubProcessor.insertTag(rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY), newTag, null);
        }

        String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_overrides_deprecated_description();
        Images image = Images.correction_change;
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 15, context.getDocument(), image);
        proposals.add(proposal);
    }

    public static void removeOverrideAnnotationProposal(InvocationContext context, IProblemLocation problem,
                                                        Collection<ICommandAccess> proposals) throws CoreException {
        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof MethodDeclaration)) {
            return;
        }
        MethodDeclaration methodDecl = (MethodDeclaration)selectedNode;
        Annotation annot = findAnnotation("java.lang.Override", methodDecl.modifiers()); //$NON-NLS-1$
        if (annot != null) {
            ASTRewrite rewrite = ASTRewrite.create(annot.getAST());
            rewrite.remove(annot, null);
            String label = CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_remove_override();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            proposals.add(proposal);

            QuickAssistProcessorImpl.getCreateInSuperClassProposals(context, methodDecl.getName(), proposals);
        }
    }

    public static void addSynchronizedMethodProposal(InvocationContext context, IProblemLocation problem,
                                                     Collection<ICommandAccess> proposals) {
        addAddMethodModifierProposal(context, problem, proposals, Modifier.SYNCHRONIZED,
                                     CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_addsynchronized_description());
    }

    public static void addStaticMethodProposal(InvocationContext context, IProblemLocation problem,
                                               Collection<ICommandAccess> proposals) {
        addAddMethodModifierProposal(context, problem, proposals, Modifier.STATIC,
                                     CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_addstatic_description());
    }

    private static void addAddMethodModifierProposal(InvocationContext context, IProblemLocation problem,
                                                     Collection<ICommandAccess> proposals, int modifier, String label) {

        ASTNode selectedNode = problem.getCoveringNode(context.getASTRoot());
        if (!(selectedNode instanceof MethodDeclaration)) {
            return;
        }

        IBinding binding = ((MethodDeclaration)selectedNode).resolveBinding();
        if (binding instanceof IMethodBinding) {
            binding = ((IMethodBinding)binding).getMethodDeclaration();
            Images image = Images.correction_change;
            proposals.add(new ModifierChangeCorrectionProposal(label, binding, selectedNode, modifier, 0, 5, context
                    .getDocument(), image));
        }
    }

    private static final String KEY_MODIFIER = "modifier"; //$NON-NLS-1$

    //   private static class ModifierLinkedModeProposal extends LinkedProposalPositionGroup.Proposal
    //   {
    //
    //      private final int fModifier;
    //
    //      public ModifierLinkedModeProposal(int modifier, int relevance)
    //      {
    //         super(null, null, relevance);
    //         fModifier = modifier;
    //      }
    //
    //      @Override
    //      public String getAdditionalProposalInfo()
    //      {
    //         return getDisplayString();
    //      }
    //
    //      @Override
    //      public String getDisplayString()
    //      {
    //         if (fModifier == 0)
    //         {
    //            return CorrectionMessages.INSTANCE.ModifierCorrectionSubProcessor_default_visibility_label;
    //         }
    //         else
    //         {
    //            return ModifierKeyword.fromFlagValue(fModifier).toString();
    //         }
    //      }
    //
    //      /* (non-Javadoc)
    //       * @see org.eclipse.jdt.internal.corext.fix.PositionGroup.Proposal#computeEdits(int, org.eclipse.jface.text.link.LinkedPosition, char, int, org.eclipse.jface.text.link.LinkedModeModel)
    //       */
    //      @Override
    //      public TextEdit computeEdits(int offset, LinkedPosition currentPosition, char trigger, int stateMask,
    //         LinkedModeModel model) throws CoreException
    //      {
    //         try
    //         {
    //            IDocument document = currentPosition.getDocument();
    //            MultiTextEdit edit = new MultiTextEdit();
    //            int documentLen = document.getLength();
    //            if (fModifier == 0)
    //            {
    //               int end = currentPosition.offset + currentPosition.length; // current end position
    //               int k = end;
    //               while (k < documentLen && IndentManipulation.isIndentChar(document.getChar(k)))
    //               {
    //                  k++;
    //               }
    //               // first remove space then replace range (remove space can destroy empty position)
    //               edit.addChild(new ReplaceEdit(end, k - end, new String())); // remove extra spaces
    //               edit.addChild(new ReplaceEdit(currentPosition.offset, currentPosition.length, new String()));
    //            }
    //            else
    //            {
    //               // first then replace range the insert space (insert space can destroy empty position)
    //               edit.addChild(new ReplaceEdit(currentPosition.offset, currentPosition.length, ModifierKeyword
    //                  .fromFlagValue(fModifier).toString()));
    //               int end = currentPosition.offset + currentPosition.length; // current end position
    //               if (end < documentLen && !Character.isWhitespace(document.getChar(end)))
    //               {
    //                  edit.addChild(new ReplaceEdit(end, 0, String.valueOf(' '))); // insert extra space
    //               }
    //            }
    //            return edit;
    //         }
    //         catch (BadLocationException e)
    //         {
    //            throw new CoreException(new Status(IStatus.ERROR, JavaUI.ID_PLUGIN, IStatus.ERROR, e.getMessage(), e));
    //         }
    //      }
    //   }

    //   public static void installLinkedVisibilityProposals(LinkedProposalModel linkedProposalModel, ASTRewrite rewrite,
    //      List<IExtendedModifier> modifiers, boolean inInterface)
    //   {
    //      ASTNode modifier = findVisibilityModifier(modifiers);
    //      if (modifier != null)
    //      {
    //         int selected = ((Modifier)modifier).getKeyword().toFlagValue();
    //
    //         LinkedProposalPositionGroup positionGroup = linkedProposalModel.getPositionGroup(KEY_MODIFIER, true);
    //         positionGroup.addPosition(rewrite.track(modifier), false);
    //         positionGroup.addProposal(new ModifierLinkedModeProposal(selected, 10));
    //
    //         // add all others
    //         int[] flagValues =
    //            inInterface ? new int[]{Modifier.PUBLIC, 0} : new int[]{Modifier.PUBLIC, 0, Modifier.PROTECTED,
    //               Modifier.PRIVATE};
    //         for (int i = 0; i < flagValues.length; i++)
    //         {
    //            if (flagValues[i] != selected)
    //            {
    //               positionGroup.addProposal(new ModifierLinkedModeProposal(flagValues[i], 9 - i));
    //            }
    //         }
    //      }
    //   }

    private static Modifier findVisibilityModifier(List<IExtendedModifier> modifiers) {
        for (int i = 0; i < modifiers.size(); i++) {
            IExtendedModifier curr = modifiers.get(i);
            if (curr instanceof Modifier) {
                Modifier modifier = (Modifier)curr;
                ModifierKeyword keyword = modifier.getKeyword();
                if (keyword == ModifierKeyword.PUBLIC_KEYWORD || keyword == ModifierKeyword.PROTECTED_KEYWORD
                    || keyword == ModifierKeyword.PRIVATE_KEYWORD) {
                    return modifier;
                }
            }
        }
        return null;
    }

    private static Annotation findAnnotation(String qualifiedTypeName, List<IExtendedModifier> modifiers) {
        for (int i = 0; i < modifiers.size(); i++) {
            IExtendedModifier curr = modifiers.get(i);
            if (curr instanceof Annotation) {
                Annotation annot = (Annotation)curr;
                ITypeBinding binding = annot.getTypeName().resolveTypeBinding();
                if (binding != null && qualifiedTypeName.equals(binding.getQualifiedName())) {
                    return annot;
                }
            }
        }
        return null;
    }

}
