/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
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
import com.codenvy.ide.ext.java.jdt.core.IJavaElement;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.SignatureUtil;
import com.codenvy.ide.ext.java.worker.WorkerTypeInfoStorage;

/** Proposal info that computes the javadoc lazily when it is queried. */
public final class TypeProposalInfo extends MemberProposalInfo {

    /**
     * Creates a new proposal info.
     *
     * @param project
     *         the java project to reference when resolving types
     * @param proposal
     *         the proposal to generate information for
     */
    public TypeProposalInfo(CompletionProposal proposal, String projectId, String docContext, String vfsId) {
        super(proposal, projectId, docContext, vfsId);
    }

    /** @see com.codenvy.ide.ext.java.jdt.codeassistant.MemberProposalInfo#getURL() */
    @Override
    protected String getURL() {
        return docContext + Signature.toString(new String(fProposal.getSignature())) + "&projectid=" + projectId
               + "&vfsid=" + vfsId + "&isclass=true";

    }

    /** @see com.codenvy.ide.ext.java.jdt.codeassistant.MemberProposalInfo#getJavaElement() */
    @Override
    public IJavaElement getJavaElement() {
        String fqn = String.valueOf(SignatureUtil.stripSignatureToFQN(String.valueOf(fProposal.getSignature())));
        return WorkerTypeInfoStorage.get().getTypeByFqn(fqn);
    }

}
