/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla - [quick fix] Create Method in void context should 'box' void. - https://bugs.eclipse.org/bugs/show_bug
 *     .cgi?id=107985
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.core.NamingConventions;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.AnonymousClassDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ClassInstanceCreation;
import com.codenvy.ide.ext.java.jdt.core.dom.Expression;
import com.codenvy.ide.ext.java.jdt.core.dom.ExpressionStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.IBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.IExtendedModifier;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodInvocation;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;
import com.codenvy.ide.ext.java.jdt.core.dom.ParameterizedType;
import com.codenvy.ide.ext.java.jdt.core.dom.PrimitiveType;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.SuperMethodInvocation;
import com.codenvy.ide.ext.java.jdt.core.dom.Type;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeParameter;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;

import java.util.List;

public class NewMethodCorrectionProposal extends AbstractMethodCorrectionProposal {

    private static final String KEY_NAME = "name"; //$NON-NLS-1$

    private static final String KEY_TYPE = "type"; //$NON-NLS-1$

    private List<Expression> fArguments;

    //	invocationNode is MethodInvocation, ConstructorInvocation, SuperConstructorInvocation, ClassInstanceCreation, SuperMethodInvocation
    public NewMethodCorrectionProposal(String label, ASTNode invocationNode, List<Expression> arguments,
                                       ITypeBinding binding, int relevance, Document document, Images image) {
        super(label, invocationNode, binding, relevance, document, image);
        fArguments = arguments;
    }

    private int evaluateModifiers(ASTNode targetTypeDecl) {
        if (getSenderBinding().isAnnotation()) {
            return 0;
        }
        if (getSenderBinding().isInterface()) {
            // for interface and annotation members copy the modifiers from an existing field
            MethodDeclaration[] methodDecls = ((TypeDeclaration)targetTypeDecl).getMethods();
            if (methodDecls.length > 0) {
                return methodDecls[0].getModifiers();
            }
            return 0;
        }
        ASTNode invocationNode = getInvocationNode();
        if (invocationNode instanceof MethodInvocation) {
            int modifiers = 0;
            Expression expression = ((MethodInvocation)invocationNode).getExpression();
            if (expression != null) {
                if (expression instanceof Name && ((Name)expression).resolveBinding().getKind() == IBinding.TYPE) {
                    modifiers |= Modifier.STATIC;
                }
            } else if (ASTResolving.isInStaticContext(invocationNode)) {
                modifiers |= Modifier.STATIC;
            }
            ASTNode node = ASTResolving.findParentType(invocationNode);
            if (targetTypeDecl.equals(node)) {
                modifiers |= Modifier.PRIVATE;
            } else if (node instanceof AnonymousClassDeclaration && ASTNodes.isParent(node, targetTypeDecl)) {
                modifiers |= Modifier.PROTECTED;
                if (ASTResolving.isInStaticContext(node) && expression == null) {
                    modifiers |= Modifier.STATIC;
                }
            } else {
                modifiers |= Modifier.PUBLIC;
            }
            return modifiers;
        }
        return Modifier.PUBLIC;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#addNewModifiers(org.eclipse.jdt.core
     * .dom.rewrite.ASTRewrite, org.eclipse.jdt.core.dom.ASTNode, java.util.List)
     */
    @Override
    protected void addNewModifiers(ASTRewrite rewrite, ASTNode targetTypeDecl, List<IExtendedModifier> modifiers) {
        modifiers.addAll(rewrite.getAST().newModifiers(evaluateModifiers(targetTypeDecl)));
        //      ModifierCorrectionSubProcessor.installLinkedVisibilityProposals(rewrite, modifiers,
        //         getSenderBinding().isInterface());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#isConstructor()
     */
    @Override
    protected boolean isConstructor() {
        ASTNode node = getInvocationNode();

        return node.getNodeType() != ASTNode.METHOD_INVOCATION && node.getNodeType() != ASTNode.SUPER_METHOD_INVOCATION;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#getNewName(org.eclipse.jdt.core.dom
     * .rewrite.ASTRewrite)
     */
    @Override
    protected SimpleName getNewName(ASTRewrite rewrite) {
        ASTNode invocationNode = getInvocationNode();
        String name;
        if (invocationNode instanceof MethodInvocation) {
            name = ((MethodInvocation)invocationNode).getName().getIdentifier();
        } else if (invocationNode instanceof SuperMethodInvocation) {
            name = ((SuperMethodInvocation)invocationNode).getName().getIdentifier();
        } else {
            name = getSenderBinding().getName(); // name of the class
        }
        AST ast = rewrite.getAST();
        SimpleName newNameNode = ast.newSimpleName(name);
        //      addLinkedPosition(rewrite.track(newNameNode), false, KEY_NAME);

        ASTNode invocationName = getInvocationNameNode();
        //      if (invocationName != null && invocationName.getAST() == ast)
        //      { // in the same CU
        //         addLinkedPosition(rewrite.track(invocationName), true, KEY_NAME);
        //      }
        return newNameNode;
    }

    private ASTNode getInvocationNameNode() {
        ASTNode node = getInvocationNode();
        if (node instanceof MethodInvocation) {
            return ((MethodInvocation)node).getName();
        } else if (node instanceof SuperMethodInvocation) {
            return ((SuperMethodInvocation)node).getName();
        } else if (node instanceof ClassInstanceCreation) {
            Type type = ((ClassInstanceCreation)node).getType();
            while (type instanceof ParameterizedType) {
                type = ((ParameterizedType)type).getType();
            }
            return type;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#getNewMethodType(org.eclipse.jdt.core
     * .dom.rewrite.ASTRewrite)
     */
    @Override
    protected Type getNewMethodType(ASTRewrite rewrite) throws CoreException {
        ASTNode node = getInvocationNode();
        AST ast = rewrite.getAST();

        Type newTypeNode = null;
        ITypeBinding[] otherProposals = null;

        ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(node, getImportRewrite());
        if (node.getParent() instanceof MethodInvocation) {
            MethodInvocation parent = (MethodInvocation)node.getParent();
            if (parent.getExpression() == node) {
                ITypeBinding[] bindings =
                        ASTResolving.getQualifierGuess(node.getRoot(), parent.getName().getIdentifier(), parent.arguments(),
                                                       getSenderBinding());
                if (bindings.length > 0) {
                    newTypeNode = getImportRewrite().addImport(bindings[0], ast, importRewriteContext);
                    otherProposals = bindings;
                }
            }
        }
        if (newTypeNode == null) {
            ITypeBinding binding = ASTResolving.guessBindingForReference(node);
            if (binding != null && binding.isWildcardType()) {
                binding = ASTResolving.normalizeWildcardType(binding, false, ast);
            }
            if (binding != null) {
                newTypeNode = getImportRewrite().addImport(binding, ast, importRewriteContext);
            } else {
                ASTNode parent = node.getParent();
                if (parent instanceof ExpressionStatement) {
                    newTypeNode = ast.newPrimitiveType(PrimitiveType.VOID);
                } else {
                    newTypeNode = ASTResolving.guessTypeForReference(ast, node);
                    if (newTypeNode == null) {
                        newTypeNode = ast.newSimpleType(ast.newSimpleName("Object")); //$NON-NLS-1$
                    }
                }
            }
        }

        //      addLinkedPosition(rewrite.track(newTypeNode), false, KEY_TYPE);
        //      if (otherProposals != null)
        //      {
        //         for (int i = 0; i < otherProposals.length; i++)
        //         {
        //            addLinkedPositionProposal(KEY_TYPE, otherProposals[i]);
        //         }
        //      }

        return newTypeNode;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#addNewParameters(org.eclipse.jdt.core
     * .dom.rewrite.ASTRewrite, java.util.List, java.util.List)
     */
    @Override
    protected void addNewParameters(ASTRewrite rewrite, List<String> takenNames, List<SingleVariableDeclaration> params)
            throws CoreException {
        AST ast = rewrite.getAST();

        List<Expression> arguments = fArguments;
        ImportRewriteContext context =
                new ContextSensitiveImportRewriteContext(ASTResolving.findParentBodyDeclaration(getInvocationNode()),
                                                         getImportRewrite());

        for (int i = 0; i < arguments.size(); i++) {
            Expression elem = arguments.get(i);
            SingleVariableDeclaration param = ast.newSingleVariableDeclaration();

            // argument type
            String argTypeKey = "arg_type_" + i; //$NON-NLS-1$
            Type type = evaluateParameterType(ast, elem, argTypeKey, context);
            param.setType(type);

            // argument name
            String argNameKey = "arg_name_" + i; //$NON-NLS-1$
            String name = evaluateParameterName(takenNames, elem, type, argNameKey);
            param.setName(ast.newSimpleName(name));

            params.add(param);
            //
            //         addLinkedPosition(rewrite.track(param.getType()), false, argTypeKey);
            //         addLinkedPosition(rewrite.track(param.getName()), false, argNameKey);
        }
    }

    private Type evaluateParameterType(AST ast, Expression elem, String key, ImportRewriteContext context) {
        ITypeBinding binding = Bindings.normalizeTypeBinding(elem.resolveTypeBinding());
        if (binding != null && binding.isWildcardType()) {
            binding = ASTResolving.normalizeWildcardType(binding, true, ast);
        }
        if (binding != null) {
            //         ITypeBinding[] typeProposals = ASTResolving.getRelaxingTypes(ast, binding);
            //         for (int i = 0; i < typeProposals.length; i++)
            //         {
            //            addLinkedPositionProposal(key, typeProposals[i]);
            //         }
            return getImportRewrite().addImport(binding, ast, context);
        }
        return ast.newSimpleType(ast.newSimpleName("Object")); //$NON-NLS-1$
    }

    private String evaluateParameterName(List<String> takenNames, Expression argNode, Type type, String key) {
        //      IJavaProject project = getCompilationUnit().getJavaProject();
        String[] names =
                StubUtility.getVariableNameSuggestions(NamingConventions.VK_PARAMETER, type, argNode, takenNames);
        //      for (int i = 0; i < names.length; i++)
        //      {
        //         addLinkedPositionProposal(key, names[i], null);
        //      }
        String favourite = names[0];
        takenNames.add(favourite);
        return favourite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#addNewExceptions(org.eclipse.jdt.core
     * .dom.rewrite.ASTRewrite, java.util.List)
     */
    @Override
    protected void addNewExceptions(ASTRewrite rewrite, List<Name> exceptions) throws CoreException {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.proposals.AbstractMethodCorrectionProposal#addNewTypeParameters(org.eclipse.jdt.core.dom.rewrite.ASTRewrite, java.util.List, java.util.List)
     */
    @Override
    protected void addNewTypeParameters(ASTRewrite rewrite, List<String> takenNames, List<TypeParameter> params)
            throws CoreException {
    }
}
