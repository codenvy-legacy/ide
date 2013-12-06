/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.codeassistant.api.ContentAssistInvocationContext;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.templates.TemplateEngine;
import com.codenvy.ide.ext.java.jdt.templates.TemplateProposal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An template completion proposal computer can generate template completion proposals from a given TemplateEngine.
 * <p/>
 * Subclasses must implement {@link #computeCompletionEngine(JavaContentAssistInvocationContext)}
 */
public abstract class AbstractTemplateCompletionProposalComputer {

    /** The engine for the current session, if any */
    private TemplateEngine fEngine;

    /*
     * @see
     * org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeCompletionProposals(org.eclipse.jface.text.contentassist
     * .TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<JavaCompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context) {
        if (!(context instanceof JavaContentAssistInvocationContext))
            return Collections.emptyList();

        JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext)context;
        CompilationUnit unit = javaContext.getCompilationUnit();
        if (unit == null)
            return Collections.emptyList();

        fEngine = computeCompletionEngine(javaContext);
        if (fEngine == null)
            return Collections.emptyList();

        fEngine.reset();
        fEngine.complete(javaContext.getDocument(), javaContext.getInvocationOffset(), unit);

        TemplateProposal[] templateProposals = fEngine.getResults();
        List<JavaCompletionProposal> result = new ArrayList<JavaCompletionProposal>(Arrays.asList(templateProposals));

        JavaCompletionProposal[] keyWordResults = javaContext.getKeywordProposals();
        if (keyWordResults.length == 0)
            return result;

      /*
       * Update relevance of template proposals that match with a keyword give those templates slightly more relevance than the
       * keyword to sort them first.
       */
        for (int k = 0; k < templateProposals.length; k++) {
            TemplateProposal curr = templateProposals[k];
            String name = curr.getTemplate().getPattern();
            for (int i = 0; i < keyWordResults.length; i++) {
                String keyword = keyWordResults[i].getDisplayString();
                if (name.startsWith(keyword)) {
                    String content = curr.getTemplate().getPattern();
                    if (content.startsWith(keyword)) {
                        curr.setRelevance(keyWordResults[i].getRelevance() + 1);
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Compute the engine used to retrieve completion proposals in the given context
     *
     * @param context
     *         the context where proposals will be made
     * @return the engine or <code>null</code> if no engine available in the context
     */
    protected abstract TemplateEngine computeCompletionEngine(JavaContentAssistInvocationContext context);

//   /*
//    * @see
//    * org.eclipse.jface.text.contentassist.ICompletionProposalComputer#computeContextInformation(org.eclipse.jface.text.contentassist
//    * .TextContentAssistInvocationContext, org.eclipse.core.runtime.IProgressMonitor)
//    */
//   public List<ContextInformation> computeContextInformation(ContentAssistInvocationContext context,
//      IProgressMonitor monitor)
//   {
//      return Collections.emptyList();
//   }

    /*
     * @see org.eclipse.jface.text.contentassist.ICompletionProposalComputer#getErrorMessage()
     */
    public String getErrorMessage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer#sessionEnded()
     */
    public void sessionEnded() {
        if (fEngine != null) {
            fEngine.reset();
            fEngine = null;
        }
    }

}
