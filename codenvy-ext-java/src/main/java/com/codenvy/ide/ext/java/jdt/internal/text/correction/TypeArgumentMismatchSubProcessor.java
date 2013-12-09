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
import com.codenvy.ide.ext.java.jdt.core.dom.ASTNode;
import com.codenvy.ide.ext.java.jdt.core.dom.ParameterizedType;
import com.codenvy.ide.ext.java.jdt.core.dom.SimpleName;
import com.codenvy.ide.ext.java.jdt.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.ASTNodes;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.proposals.ASTRewriteCorrectionProposal;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;

import java.util.Collection;

public class TypeArgumentMismatchSubProcessor {

    //	public static void getTypeParameterMismatchProposals(IInvocationContext context, IProblemLocation problem, Collection proposals) {
    //	CompilationUnit astRoot= context.getASTRoot();
    //	ASTNode selectedNode= problem.getCoveredNode(astRoot);
    //	if (!(selectedNode instanceof SimpleName)) {
    //	return;
    //	}

    //	ASTNode normalizedNode= ASTNodes.getNormalizedNode(selectedNode);
    //	if (!(normalizedNode instanceof ParameterizedType)) {
    //	return;
    //	}
    //	// waiting for result of https://bugs.eclipse.org/bugs/show_bug.cgi?id=81544

    //	}

    public static void removeMismatchedArguments(InvocationContext context, IProblemLocation problem,
                                                 Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveredNode(context.getASTRoot());
        if (!(selectedNode instanceof SimpleName)) {
            return;
        }

        ASTNode normalizedNode = ASTNodes.getNormalizedNode(selectedNode);
        if (normalizedNode instanceof ParameterizedType) {
            ASTRewrite rewrite = ASTRewrite.create(normalizedNode.getAST());
            ParameterizedType pt = (ParameterizedType)normalizedNode;
            ASTNode mt = rewrite.createMoveTarget(pt.getType());
            rewrite.replace(pt, mt, null);
            String label = CorrectionMessages.INSTANCE.TypeArgumentMismatchSubProcessor_removeTypeArguments();
            Images image = Images.correction_change;
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            proposals.add(proposal);
        }
    }

    public static void getInferDiamondArgumentsProposal(InvocationContext context, IProblemLocation problem,
                                                        Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveredNode(context.getASTRoot());
        if (!(selectedNode instanceof SimpleName)) {
            return;
        }

        QuickAssistProcessorImpl.getInferDiamondArgumentsProposal(context, selectedNode, null, proposals);
    }

}
