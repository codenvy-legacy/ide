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
import org.eclipse.jdt.client.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.client.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ASTResolving;
import org.eclipse.jdt.client.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.jdt.client.internal.corext.dom.Bindings;
import org.eclipse.jdt.client.internal.text.correction.CorrectionMessages;
import org.eclipse.jdt.client.internal.text.correction.JavadocTagsSubProcessor;
import org.eclipse.jdt.client.runtime.CoreException;
import org.exoplatform.ide.editor.shared.runtime.Assert;
import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.List;
import java.util.Set;

public class AddTypeParameterProposal extends LinkedCorrectionProposal {

    private IBinding fBinding;

    private CompilationUnit fAstRoot;

    private final String fTypeParamName;

    private final ITypeBinding[] fBounds;

    public AddTypeParameterProposal(IBinding binding, CompilationUnit astRoot, String name, ITypeBinding[] bounds,
                                    int relevance, IDocument document) {
        super("", null, relevance, document, new Image(JdtClientBundle.INSTANCE.field_public())); //$NON-NLS-1$

        Assert.isTrue(binding != null && Bindings.isDeclarationBinding(binding));
        Assert.isTrue(binding instanceof IMethodBinding || binding instanceof ITypeBinding);

        fBinding = binding;
        fAstRoot = astRoot;
        fTypeParamName = name;
        fBounds = bounds;

        if (binding instanceof IMethodBinding) {

            setDisplayName(CorrectionMessages.INSTANCE.AddTypeParameterProposal_method_label(fTypeParamName,
                                                                                             ASTResolving.getMethodSignature(
                                                                                                     (IMethodBinding)binding)));
        } else {
            setDisplayName(CorrectionMessages.INSTANCE.AddTypeParameterProposal_type_label(fTypeParamName,
                                                                                           ASTResolving.getTypeSignature(
                                                                                                   (ITypeBinding)binding)));
        }
    }

    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        ASTNode boundNode = fAstRoot.findDeclaringNode(fBinding);
        ASTNode declNode = null;

        if (boundNode != null) {
            declNode = boundNode; // is same CU
            createImportRewrite(fAstRoot);
        } else {
            CompilationUnit newRoot = ASTResolving.createQuickFixAST(document, null);
            declNode = newRoot.findDeclaringNode(fBinding.getKey());
            createImportRewrite(newRoot);
        }
        AST ast = declNode.getAST();
        TypeParameter newTypeParam = ast.newTypeParameter();
        newTypeParam.setName(ast.newSimpleName(fTypeParamName));
        if (fBounds != null && fBounds.length > 0) {
            List<Type> typeBounds = newTypeParam.typeBounds();
            ImportRewriteContext importRewriteContext =
                    new ContextSensitiveImportRewriteContext(declNode, getImportRewrite());
            for (int i = 0; i < fBounds.length; i++) {
                Type newBound = getImportRewrite().addImport(fBounds[i], ast, importRewriteContext);
                typeBounds.add(newBound);
            }
        }
        ASTRewrite rewrite = ASTRewrite.create(ast);
        ListRewrite listRewrite;
        Javadoc javadoc;
        List<TypeParameter> otherTypeParams;
        if (declNode instanceof TypeDeclaration) {
            TypeDeclaration declaration = (TypeDeclaration)declNode;
            listRewrite = rewrite.getListRewrite(declaration, TypeDeclaration.TYPE_PARAMETERS_PROPERTY);
            otherTypeParams = declaration.typeParameters();
            javadoc = declaration.getJavadoc();
        } else {
            MethodDeclaration declaration = (MethodDeclaration)declNode;
            listRewrite = rewrite.getListRewrite(declNode, MethodDeclaration.TYPE_PARAMETERS_PROPERTY);
            otherTypeParams = declaration.typeParameters();
            javadoc = declaration.getJavadoc();
        }
        listRewrite.insertLast(newTypeParam, null);

        if (javadoc != null && otherTypeParams != null) {
            ListRewrite tagsRewriter = rewrite.getListRewrite(javadoc, Javadoc.TAGS_PROPERTY);
            Set<String> previousNames = JavadocTagsSubProcessor.getPreviousTypeParamNames(otherTypeParams, null);

            String name = '<' + fTypeParamName + '>';
            TagElement newTag = ast.newTagElement();
            newTag.setTagName(TagElement.TAG_PARAM);
            TextElement text = ast.newTextElement();
            text.setText(name);
            newTag.fragments().add(text);

            JavadocTagsSubProcessor.insertTag(tagsRewriter, newTag, previousNames);
        }
        return rewrite;
    }

}
