/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
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
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.IExtendedModifier;
import com.codenvy.ide.ext.java.jdt.core.dom.IMethodBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.MarkerAnnotation;
import com.codenvy.ide.ext.java.jdt.core.dom.MethodDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.Modifier;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ListRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.ASTResolving;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.LinkedCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.text.Document;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class VarargsWarningsSubProcessor {

    private static class AddSafeVarargsProposal extends LinkedCorrectionProposal {

        private IMethodBinding fMethodBinding;

        private MethodDeclaration fMethodDeclaration;

        public AddSafeVarargsProposal(Document document, String label, MethodDeclaration methodDeclaration,
                                      IMethodBinding methodBinding, int relevance) {
            super(label, null, relevance, document, Images.javadoc);
            fMethodDeclaration = methodDeclaration;
            fMethodBinding = methodBinding;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jdt.internal.ui.text.correction.ASTRewriteCorrectionProposal#getRewrite()
         */
        @Override
        protected ASTRewrite getRewrite() throws CoreException {
            if (fMethodDeclaration == null) {
                CompilationUnit astRoot = ASTResolving.createQuickFixAST(document);
                fMethodDeclaration = (MethodDeclaration)astRoot.findDeclaringNode(fMethodBinding.getKey());
            }
            AST ast = fMethodDeclaration.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);
            ListRewrite listRewrite = rewrite.getListRewrite(fMethodDeclaration, MethodDeclaration.MODIFIERS2_PROPERTY);

            MarkerAnnotation annotation = ast.newMarkerAnnotation();
            String importString =
                    createImportRewrite((CompilationUnit)fMethodDeclaration.getRoot()).addImport("java.lang.SafeVarargs"); //$NON-NLS-1$
            annotation.setTypeName(ast.newName(importString));
            listRewrite.insertFirst(annotation, null);

            // set up linked mode
            //         addLinkedPosition(rewrite.track(annotation), true, "annotation"); //$NON-NLS-1$

            return rewrite;
        }

    }

    public static void addAddSafeVarargsProposals(InvocationContext context, IProblemLocation problem,
                                                  Collection<ICommandAccess> proposals) {
        ASTNode coveringNode = problem.getCoveringNode(context.getASTRoot());

        MethodDeclaration methodDeclaration = ASTResolving.findParentMethodDeclaration(coveringNode);
        if (methodDeclaration == null)
            return;

        IMethodBinding methodBinding = methodDeclaration.resolveBinding();
        if (methodBinding == null)
            return;

        int modifiers = methodBinding.getModifiers();
        if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && !methodBinding.isConstructor())
            return;

        String label = CorrectionMessages.INSTANCE.VarargsWarningsSubProcessor_add_safevarargs_label();
        AddSafeVarargsProposal proposal =
                new AddSafeVarargsProposal(context.getDocument(), label, methodDeclaration, null, -2);
        proposals.add(proposal);
    }

    public static void addAddSafeVarargsToDeclarationProposals(InvocationContext context, IProblemLocation problem,
                                                               Collection<ICommandAccess> proposals) {
        //TODO
        //      if (!JavaModelUtil.is17OrHigher(context.getCompilationUnit().getJavaProject()))
        //         return;
        //
        //      ASTNode coveringNode = problem.getCoveringNode(context.getASTRoot());
        //      IMethodBinding methodBinding;
        //      if (coveringNode instanceof MethodInvocation)
        //      {
        //         methodBinding = ((MethodInvocation)coveringNode).resolveMethodBinding();
        //      }
        //      else if (coveringNode instanceof ClassInstanceCreation)
        //      {
        //         methodBinding = ((ClassInstanceCreation)coveringNode).resolveConstructorBinding();
        //      }
        //      else
        //      {
        //         return;
        //      }
        //      if (methodBinding == null)
        //         return;
        //
        //      String label =
        //         Messages.format(CorrectionMessages.VarargsWarningsSubProcessor_add_safevarargs_to_method_label,
        //            methodBinding.getName());
        //
        //      ITypeBinding declaringType = methodBinding.getDeclaringClass();
        //      CompilationUnit astRoot = (CompilationUnit)coveringNode.getRoot();
        //      if (declaringType != null && declaringType.isFromSource())
        //      {
        //         try
        //         {
        //            ICompilationUnit targetCu =
        //               ASTResolving.findCompilationUnitForBinding(context.getCompilationUnit(), astRoot, declaringType);
        //            if (targetCu != null)
        //            {
        //               AddSafeVarargsProposal proposal =
        //                  new AddSafeVarargsProposal(label, targetCu, null, methodBinding.getMethodDeclaration(), -2);
        //               proposals.add(proposal);
        //            }
        //         }
        //         catch (JavaModelException e)
        //         {
        //            return;
        //         }
        //      }
    }

    public static void addRemoveSafeVarargsProposals(InvocationContext context, IProblemLocation problem,
                                                     Collection<ICommandAccess> proposals) {
        ASTNode coveringNode = problem.getCoveringNode(context.getASTRoot());
        if (!(coveringNode instanceof MethodDeclaration))
            return;

        MethodDeclaration methodDeclaration = (MethodDeclaration)coveringNode;
        MarkerAnnotation annotation = null;

        List<IExtendedModifier> modifiers = methodDeclaration.modifiers();
        for (Iterator<IExtendedModifier> iterator = modifiers.iterator(); iterator.hasNext(); ) {
            IExtendedModifier node = iterator.next();
            if (node instanceof MarkerAnnotation) {
                annotation = (MarkerAnnotation)node;
                if ("SafeVarargs".equals(annotation.resolveAnnotationBinding().getName())) { //$NON-NLS-1$
                    break;
                }
            }
        }

        if (annotation == null)
            return;

        ASTRewrite rewrite = ASTRewrite.create(coveringNode.getAST());
        rewrite.remove(annotation, null);

        String label = CorrectionMessages.INSTANCE.VarargsWarningsSubProcessor_remove_safevarargs_label();
        Images image = Images.delete_obj;
        ASTRewriteCorrectionProposal proposal =
                new ASTRewriteCorrectionProposal(label, rewrite, 5, context.getDocument(), image);
        proposals.add(proposal);
    }

}
