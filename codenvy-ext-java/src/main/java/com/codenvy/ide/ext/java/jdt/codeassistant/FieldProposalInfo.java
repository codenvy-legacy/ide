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
import com.codenvy.ide.ext.java.jdt.core.Signature;

/** Proposal info that computes the javadoc lazily when it is queried. */
public final class FieldProposalInfo extends MemberProposalInfo {

    /**
     * Creates a new proposal info.
     *
     * @param project
     *         the java project to reference when resolving types
     * @param proposal
     *         the proposal to generate information for
     */
    public FieldProposalInfo(CompletionProposal proposal, String projectId, String docContext, String vfsId) {
        super(proposal, projectId, docContext, vfsId);
    }

    /** @see com.codenvy.ide.ext.java.jdt.codeassistant.MemberProposalInfo#getURL() */
    @Override
    protected String getURL() {
        if (fProposal.getDeclarationSignature() == null)
            return null;
        return docContext + Signature.toString(new String(fProposal.getDeclarationSignature())) + "%23"
               + new String(fProposal.getName()) + "&projectid=" + projectId + "&vfsid=" + vfsId + "&isclass=false";
    }
}
