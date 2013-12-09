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
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.Signature;

/** Proposal info that computes the javadoc lazily when it is queried. */
public final class MethodProposalInfo extends MemberProposalInfo {

    /**
     * Creates a new proposal info.
     *
     * @param project
     *         the java project to reference when resolving types
     * @param proposal
     *         the proposal to generate information for
     */
    public MethodProposalInfo(CompletionProposal proposal, String projectId, String docContext, String vfsId) {
        super(proposal, projectId, docContext, vfsId);
    }

    /** @see com.codenvy.ide.ext.java.jdt.codeassistant.MemberProposalInfo#getURL() */
    @Override
    protected String getURL() {
        char[][] parameterTypes = Signature.getParameterTypes(fProposal.getSignature());
        char[][] params = new char[parameterTypes.length][];

        for (int i = 0; i < parameterTypes.length; i++) {
            params[i] = Signature.getTypeErasure(parameterTypes[i]);
        }
        char[] returnType = Signature.getReturnType(fProposal.getSignature());
        String method = new String(Signature.createMethodSignature(params, Signature.getTypeErasure(returnType)));
        String methodSignature =
                Signature.toString(new String(fProposal.getDeclarationSignature())) + "%23" + new String(fProposal.getName())
                + "%40" + method.replaceAll("\\.", "/");
        return docContext + methodSignature + "&projectid=" + projectId + "&vfsid=" + vfsId + "&isclass=false";
    }

}
