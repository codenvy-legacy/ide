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
import com.codenvy.ide.runtime.Assert;


/** Proposal info that computes the javadoc lazily when it is queried. */
public abstract class MemberProposalInfo extends ProposalInfo {

    protected CompletionProposal fProposal;

    protected final String projectId;

    protected final String docContext;

    private boolean isResolved = false;

//    private Frame frame;

    protected final String vfsId;

    /**
     * Creates a new proposal info.
     *
     * @param project
     *         the java project to reference when resolving types
     * @param proposal
     *         the proposal to generate information for
     */
    public MemberProposalInfo(CompletionProposal proposal, String projectId, String docContext, String vfsId) {
        this.projectId = projectId;
        this.docContext = docContext;
        this.vfsId = vfsId;
        Assert.isNotNull(proposal);
        this.fProposal = proposal;
    }

//    /** @see com.codenvy.ide.ext.java.jdt.codeassistant.ProposalInfo#getInfo() */
//    @Override
//    public Widget getInfo() {
//        if (isResolved)
//            return frame;
//
//        String url = getURL();
//        isResolved = true;
//        if (url != null) {
//            frame = new Frame(url);
//            frame.setSize("100%", "100%");
//            frame.removeStyleName("gwt-Frame");
//            frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
//        }
//        return frame;
//    }

    @Override
    public IJavaElement getJavaElement() {
        return null;
    }

    protected abstract String getURL();

}
