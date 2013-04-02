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
package org.eclipse.jdt.client.internal.text.correction;

import com.google.gwt.user.client.ui.Image;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.codeassistant.api.IInvocationContext;
import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ParameterizedType;
import org.eclipse.jdt.client.core.dom.SimpleName;
import org.eclipse.jdt.client.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.client.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.client.internal.text.correction.proposals.ASTRewriteCorrectionProposal;

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

    public static void removeMismatchedArguments(IInvocationContext context, IProblemLocation problem,
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
            Image image = new Image(JdtClientBundle.INSTANCE.correction_change());
            ASTRewriteCorrectionProposal proposal =
                    new ASTRewriteCorrectionProposal(label, rewrite, 6, context.getDocument(), image);
            proposals.add(proposal);
        }
    }

    public static void getInferDiamondArgumentsProposal(IInvocationContext context, IProblemLocation problem,
                                                        Collection<ICommandAccess> proposals) {
        ASTNode selectedNode = problem.getCoveredNode(context.getASTRoot());
        if (!(selectedNode instanceof SimpleName)) {
            return;
        }

        QuickAssistProcessor.getInferDiamondArgumentsProposal(context, selectedNode, null, proposals);
    }

}
