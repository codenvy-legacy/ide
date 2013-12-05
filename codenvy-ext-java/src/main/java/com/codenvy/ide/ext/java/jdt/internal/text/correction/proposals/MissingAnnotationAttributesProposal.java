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
import com.codenvy.ide.ext.java.jdt.core.dom.Annotation;
import com.codenvy.ide.ext.java.jdt.core.dom.ArrayInitializer;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.Expression;
import com.codenvy.ide.ext.java.jdt.core.dom.IMethodBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.MarkerAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.MemberValuePair;
import com.codenvy.ide.ext.java.jdt.core.dom.Name;
import com.codenvy.ide.ext.java.jdt.core.dom.NormalAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.SingleMemberAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.CorrectionMessages;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MissingAnnotationAttributesProposal extends LinkedCorrectionProposal {

    private Annotation fAnnotation;

    public MissingAnnotationAttributesProposal(Annotation annotation, int relevance, Document document) {
        super(CorrectionMessages.INSTANCE.MissingAnnotationAttributesProposal_add_missing_attributes_label(), null,
              relevance, document, null);
        setImage(Images.correction_change);

        fAnnotation = annotation;
        Assert.isNotNull(fAnnotation.resolveTypeBinding());
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal#getRewrite()
     */
    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        AST ast = fAnnotation.getAST();

        ASTRewrite rewrite = ASTRewrite.create(ast);
        createImportRewrite((CompilationUnit)fAnnotation.getRoot());

        ListRewrite listRewrite;
        if (fAnnotation instanceof NormalAnnotation) {
            listRewrite = rewrite.getListRewrite(fAnnotation, NormalAnnotation.VALUES_PROPERTY);
        } else {
            NormalAnnotation newAnnotation = ast.newNormalAnnotation();
            newAnnotation.setTypeName((Name)rewrite.createMoveTarget(fAnnotation.getTypeName()));
            rewrite.replace(fAnnotation, newAnnotation, null);

            listRewrite = rewrite.getListRewrite(newAnnotation, NormalAnnotation.VALUES_PROPERTY);
        }
        addMissingAtributes(fAnnotation.resolveTypeBinding(), listRewrite);

        return rewrite;
    }

    private void addMissingAtributes(ITypeBinding binding, ListRewrite listRewriter) {
        Set<String> implementedAttribs = new HashSet<String>();
        if (fAnnotation instanceof NormalAnnotation) {
            List<MemberValuePair> list = ((NormalAnnotation)fAnnotation).values();
            for (int i = 0; i < list.size(); i++) {
                MemberValuePair curr = list.get(i);
                implementedAttribs.add(curr.getName().getIdentifier());
            }
        } else if (fAnnotation instanceof SingleMemberAnnotation) {
            implementedAttribs.add("value"); //$NON-NLS-1$
        }
        ASTRewrite rewriter = listRewriter.getASTRewrite();
        AST ast = rewriter.getAST();
        ImportRewriteContext context = null;
        ASTNode bodyDeclaration = ASTResolving.findParentBodyDeclaration(listRewriter.getParent());
        if (bodyDeclaration != null) {
            context = new ContextSensitiveImportRewriteContext(bodyDeclaration, getImportRewrite());
        }

        IMethodBinding[] declaredMethods = binding.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            IMethodBinding curr = declaredMethods[i];
            if (!implementedAttribs.contains(curr.getName()) && curr.getDefaultValue() == null) {
                MemberValuePair pair = ast.newMemberValuePair();
                pair.setName(ast.newSimpleName(curr.getName()));
                pair.setValue(newDefaultExpression(ast, curr.getReturnType(), context));
                listRewriter.insertLast(pair, null);

                //            addLinkedPosition(rewriter.track(pair.getName()), false, "val_name_" + i); //$NON-NLS-1$
                //            addLinkedPosition(rewriter.track(pair.getValue()), false, "val_type_" + i); //$NON-NLS-1$
            }
        }
    }

    private Expression newDefaultExpression(AST ast, ITypeBinding type, ImportRewriteContext context) {
        if (type.isPrimitive()) {
            String name = type.getName();
            if ("boolean".equals(name)) { //$NON-NLS-1$
                return ast.newBooleanLiteral(false);
            } else {
                return ast.newNumberLiteral("0"); //$NON-NLS-1$
            }
        }
        if (type == ast.resolveWellKnownType("java.lang.String")) { //$NON-NLS-1$
            return ast.newStringLiteral();
        }
        if (type.isArray()) {
            ArrayInitializer initializer = ast.newArrayInitializer();
            initializer.expressions().add(newDefaultExpression(ast, type.getElementType(), context));
            return initializer;
        }
        if (type.isAnnotation()) {
            MarkerAnnotation annotation = ast.newMarkerAnnotation();
            annotation.setTypeName(ast.newName(getImportRewrite().addImport(type, context)));
            return annotation;
        }
        return ast.newNullLiteral();
    }
}
