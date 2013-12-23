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

import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.templates.TemplateProposal;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;

import java.util.Comparator;

/**
 * Comparator for java completion proposals. Completion proposals can be sorted by relevance or alphabetically.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * </p>
 *
 * @since 3.1
 */
public final class CompletionProposalComparator implements Comparator<CompletionProposal> {

    private boolean fOrderAlphabetically;

    /** Creates a comparator that sorts by relevance. */
    public CompletionProposalComparator() {
        fOrderAlphabetically = false;
    }

    /**
     * Sets the sort order. Default is <code>false</code>, i.e. order by relevance.
     *
     * @param orderAlphabetically
     *         <code>true</code> to order alphabetically, <code>false</code> to order by relevance
     */
    public void setOrderAlphabetically(boolean orderAlphabetically) {
        fOrderAlphabetically = orderAlphabetically;
    }

    /** {@inheritDoc} */
    public int compare(CompletionProposal p1, CompletionProposal p2) {
        if (!fOrderAlphabetically) {
            int r1 = getRelevance(p1);
            int r2 = getRelevance(p2);
            int relevanceDif = r2 - r1;
            if (relevanceDif != 0) {
                return relevanceDif;
            }
        }
      /*
       * TODO the correct (but possibly much slower) sorting would use a collator.
       */
        // fix for bug 67468
        return getSortKey(p1).compareToIgnoreCase(getSortKey(p2));
    }

    private String getSortKey(CompletionProposal p) {
        if (p instanceof AbstractJavaCompletionProposal)
            return ((AbstractJavaCompletionProposal)p).getSortString();
        return p.getDisplayString();
    }

    private int getRelevance(CompletionProposal obj) {
        if (obj instanceof JavaCompletionProposal) {
            JavaCompletionProposal jcp = (JavaCompletionProposal)obj;
            return jcp.getRelevance();
        }
        else if (obj instanceof TemplateProposal) {
        TemplateProposal tp= (TemplateProposal) obj;
        return tp.getRelevance();
        }
        // catch all
        return 0;
    }

}
