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
import com.codenvy.ide.ext.java.jdt.JavaPreferencesSettings;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.Block;
import com.codenvy.ide.ext.java.jdt.core.dom.BodyDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ChildListPropertyDescriptor;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.IExtendedModifier;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.IVariableBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.Javadoc;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;
import com.codenvy.ide.ext.java.jdt.core.dom.PrimitiveType;
import com.codenvy.ide.ext.java.jdt.core.dom.ReturnStatement;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleVariableDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Type;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeParameter;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.CodeGenerationSettings;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.StubUtility;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodeFactory;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.Bindings;
import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMethodCorrectionProposal extends LinkedCorrectionProposal {

    private ASTNode fNode;

    private ITypeBinding fSenderBinding;

    public AbstractMethodCorrectionProposal(String label, ASTNode invocationNode, ITypeBinding binding, int relevance,
                                            Document document, Images image) {
        super(label, null, relevance, document, image);

        Assert.isTrue(binding != null && Bindings.isDeclarationBinding(binding));

        fNode = invocationNode;
        fSenderBinding = binding;
    }

    protected ASTNode getInvocationNode() {
        return fNode;
    }

    /** @return The binding of the type declaration (generic type) */
    protected ITypeBinding getSenderBinding() {
        return fSenderBinding;
    }

    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        CompilationUnit astRoot = ASTResolving.findParentCompilationUnit(fNode);
        ASTNode typeDecl = astRoot.findDeclaringNode(fSenderBinding);
        ASTNode newTypeDecl = null;
        boolean isInDifferentCU;
        if (typeDecl != null) {
            isInDifferentCU = false;
            newTypeDecl = typeDecl;
        } else {
            isInDifferentCU = true;
            astRoot = ASTResolving.createQuickFixAST(document);
            newTypeDecl = astRoot.findDeclaringNode(fSenderBinding.getKey());
        }
        createImportRewrite(astRoot);

        if (newTypeDecl != null) {
            ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());

            MethodDeclaration newStub = getStub(rewrite, newTypeDecl);

            ChildListPropertyDescriptor property = ASTNodes.getBodyDeclarationsProperty(newTypeDecl);
            List<BodyDeclaration> members = (List<BodyDeclaration>)newTypeDecl.getStructuralProperty(property);

            int insertIndex;
            if (isConstructor()) {
                insertIndex = findConstructorInsertIndex(members);
            } else if (!isInDifferentCU) {
                insertIndex = findMethodInsertIndex(members, fNode.getStartPosition());
            } else {
                insertIndex = members.size();
            }
            ListRewrite listRewriter = rewrite.getListRewrite(newTypeDecl, property);
            listRewriter.insertAt(newStub, insertIndex, null);

            return rewrite;
        }
        return null;
    }

    private MethodDeclaration getStub(ASTRewrite rewrite, ASTNode targetTypeDecl) throws CoreException {
        AST ast = targetTypeDecl.getAST();
        MethodDeclaration decl = ast.newMethodDeclaration();

        SimpleName newNameNode = getNewName(rewrite);

        decl.setConstructor(isConstructor());

        addNewModifiers(rewrite, targetTypeDecl, decl.modifiers());

        ArrayList<String> takenNames = new ArrayList<String>();
        addNewTypeParameters(rewrite, takenNames, decl.typeParameters());

        decl.setName(newNameNode);

        IVariableBinding[] declaredFields = fSenderBinding.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) { // avoid to take parameter names that are equal to field names
            takenNames.add(declaredFields[i].getName());
        }

        String bodyStatement = ""; //$NON-NLS-1$
        if (!isConstructor()) {
            Type returnType = getNewMethodType(rewrite);
            decl.setReturnType2(returnType);

            boolean isVoid =
                    returnType instanceof PrimitiveType
                    && PrimitiveType.VOID.equals(((PrimitiveType)returnType).getPrimitiveTypeCode());
            if (!fSenderBinding.isInterface() && !isVoid) {
                ReturnStatement returnStatement = ast.newReturnStatement();
                returnStatement.setExpression(ASTNodeFactory.newDefaultExpression(ast, returnType, 0));
                bodyStatement =
                        ASTNodes.asFormattedString(returnStatement, 0, String.valueOf('\n'), WorkerMessageHandler.get().getOptions());
            }
        }

        addNewParameters(rewrite, takenNames, decl.parameters());
        addNewExceptions(rewrite, decl.thrownExceptions());

        Block body = null;
        if (!fSenderBinding.isInterface()) {
            body = ast.newBlock();
            String placeHolder =
                    StubUtility.getMethodBodyContent(isConstructor(), fSenderBinding.getName(), newNameNode.getIdentifier(),
                                                     bodyStatement, String.valueOf('\n'));

            if (placeHolder != null) {
                ReturnStatement todoNode =
                        (ReturnStatement)rewrite.createStringPlaceholder(placeHolder, ASTNode.RETURN_STATEMENT);
                body.statements().add(todoNode);
            }
        }
        decl.setBody(body);

        CodeGenerationSettings settings = JavaPreferencesSettings.getCodeGenerationSettings();
        if (settings.createComments && !fSenderBinding.isAnonymous()) {
            String string = getMethodComment(fSenderBinding.getName(), decl, null, String.valueOf('\n'));

            if (string != null) {
                Javadoc javadoc = (Javadoc)rewrite.createStringPlaceholder(string, ASTNode.JAVADOC);
                decl.setJavadoc(javadoc);
            }
        }
        return decl;
    }

    private int findMethodInsertIndex(List<BodyDeclaration> decls, int currPos) {
        int nDecls = decls.size();
        for (int i = 0; i < nDecls; i++) {
            BodyDeclaration curr = decls.get(i);
            if (curr instanceof MethodDeclaration && currPos < curr.getStartPosition() + curr.getLength()) {
                return i + 1;
            }
        }
        return nDecls;
    }

    private int findConstructorInsertIndex(List<BodyDeclaration> decls) {
        int nDecls = decls.size();
        int lastMethod = 0;
        for (int i = nDecls - 1; i >= 0; i--) {
            BodyDeclaration curr = decls.get(i);
            if (curr instanceof MethodDeclaration) {
                if (((MethodDeclaration)curr).isConstructor()) {
                    return i + 1;
                }
                lastMethod = i;
            }
        }
        return lastMethod;
    }

    protected abstract boolean isConstructor();

    protected abstract void addNewModifiers(ASTRewrite rewrite, ASTNode targetTypeDecl,
                                            List<IExtendedModifier> exceptions);

    protected abstract void addNewTypeParameters(ASTRewrite rewrite, List<String> takenNames, List<TypeParameter> params)
            throws CoreException;

    protected abstract void addNewParameters(ASTRewrite rewrite, List<String> takenNames,
                                             List<SingleVariableDeclaration> params) throws CoreException;

    protected abstract void addNewExceptions(ASTRewrite rewrite, List<Name> exceptions) throws CoreException;

    protected abstract SimpleName getNewName(ASTRewrite rewrite);

    protected abstract Type getNewMethodType(ASTRewrite rewrite) throws CoreException;

}
