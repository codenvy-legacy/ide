/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.codeassistant.ContentAssistHistory.RHSHistory;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.ContentAssistInvocationContext;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.CompletionContext;
import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.IType;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.SignatureUtil;
import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.ext.java.worker.WorkerTypeInfoStorage;
import com.codenvy.ide.text.Document;


/**
 * Describes the context of a content assist invocation in a Java editor.
 * <p>
 * Clients may use but not subclass this class.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class JavaContentAssistInvocationContext extends ContentAssistInvocationContext {

    private RHSHistory fRHSHistory;

    private CompletionProposalLabelProvider fLabelProvider;

    private CompletionProposalCollector fCollector;

    private JavaCompletionProposal[] fKeywordProposals = null;

    private CompletionContext fCoreContext = null;

    private CompilationUnit compilationUnit;

    private String docContext;

    private String projectId;

    private IType fType;

    private String vfsId;

    /**
     * Creates a new context.
     *
     * @param unit
     *         the compilation unit in <code>document</code>
     */
    public JavaContentAssistInvocationContext(Document document, int offset) {
        super(document, offset);
    }

    /** @param compilationUnit */
    public JavaContentAssistInvocationContext(CompilationUnit compilationUnit, Document document, int offset,
                                              String projectId, String docContext, String vfsId) {
        this(document, offset);
        this.compilationUnit = compilationUnit;
        this.projectId = projectId;
        this.docContext = docContext;
        this.vfsId = vfsId;
    }

    /**
     * Returns the keyword proposals that are available in this context, possibly none.
     * <p>
     * <strong>Note:</strong> This method may run
     * {@linkplain ICodeAssist#codeComplete(int, org.eclipse.jdt.core.CompletionRequestor) codeComplete} on the compilation unit.
     * </p>
     *
     * @return the available keyword proposals
     */
    public JavaCompletionProposal[] getKeywordProposals() {
        if (fKeywordProposals == null) {
            if (fCollector != null && !fCollector.isIgnored(CompletionProposal.KEYWORD) && fCollector.getContext() != null) {
                // use the existing collector if it exists, collects keywords, and has already been invoked
                fKeywordProposals = fCollector.getKeywordCompletionProposals();
            }
            // else
            // {
            // // otherwise, retrieve keywords ourselves
            // computeKeywordsAndContext();
            // }
        }

        return fKeywordProposals;
    }

    /**
     * Returns the {@link CompletionContext core completion context} if available, <code>null</code> otherwise.
     * <p>
     * <strong>Note:</strong> This method may run
     * {@linkplain ICodeAssist#codeComplete(int, org.eclipse.jdt.core.CompletionRequestor) codeComplete} on the compilation unit.
     * </p>
     *
     * @return the core completion context if available, <code>null</code> otherwise
     */
    public CompletionContext getCoreContext() {
        if (fCollector != null) {
            CompletionContext context = fCollector.getContext();
            if (context != null) {
                if (fCoreContext == null)
                    fCoreContext = context;
                return context;
            }
        }

        // if (fCoreContext == null)
        // computeKeywordsAndContext(); // Retrieve the context ourselves

        return fCoreContext;
    }

    /**
     * Returns an float in [0.0,&nbsp;1.0] based on whether the type has been recently used as a right hand side for the type
     * expected in the current context. 0 signals that the <code>qualifiedTypeName</code> does not match the expected type, while
     * 1.0 signals that <code>qualifiedTypeName</code> has most recently been used in a similar context.
     * <p>
     * <strong>Note:</strong> This method may run
     * {@linkplain ICodeAssist#codeComplete(int, org.eclipse.jdt.core.CompletionRequestor) codeComplete} on the compilation unit.
     * </p>
     *
     * @param qualifiedTypeName
     *         the type name of the type of interest
     * @return a relevance in [0.0,&nbsp;1.0] based on previous content assist invocations
     */
    public float getHistoryRelevance(String qualifiedTypeName) {
        return getRHSHistory().getRank(qualifiedTypeName);
    }

    /**
     * Returns the content assist type history for the expected type.
     *
     * @return the content assist type history for the expected type
     */
    private RHSHistory getRHSHistory() {
        if (fRHSHistory == null) {
            CompletionContext context = getCoreContext();
            if (context != null) {
                char[][] expectedTypes = context.getExpectedTypesSignatures();
                if (expectedTypes != null && expectedTypes.length > 0) {
                    String expected = SignatureUtil.stripSignatureToFQN(String.valueOf(expectedTypes[0]));
                    fRHSHistory = WorkerMessageHandler.get().getContentAssistHistory().getHistory(expected);
                }
            }
            if (fRHSHistory == null)
                fRHSHistory = WorkerMessageHandler.get().getContentAssistHistory().getHistory(null);
        }
        return fRHSHistory;
    }

    /**
     * Returns the expected type if any, <code>null</code> otherwise.
     * <p>
     * <strong>Note:</strong> This method may run
     * {@linkplain ICodeAssist#codeComplete(int, org.eclipse.jdt.core.CompletionRequestor) codeComplete} on the compilation unit.
     * </p>
     *
     * @return the expected type if any, <code>null</code> otherwise
     */
    public IType getExpectedType() {
        if (fType == null && getCompilationUnit() != null) {
            CompletionContext context = getCoreContext();
            if (context != null) {
                char[][] expectedTypes = context.getExpectedTypesSignatures();
                if (expectedTypes != null && expectedTypes.length > 0) {
                    fType =
                            WorkerTypeInfoStorage.get().getTypeByFqn(
                                    SignatureUtil.stripSignatureToFQN(String.valueOf(expectedTypes[0])));
                }
            }
        }
        return fType;
    }

    /**
     * Returns a label provider that can be used to compute proposal labels.
     *
     * @return a label provider that can be used to compute proposal labels
     */
    public CompletionProposalLabelProvider getLabelProvider() {
        if (fLabelProvider == null) {
            if (fCollector != null)
                fLabelProvider = fCollector.getLabelProvider();
            else
                fLabelProvider = new CompletionProposalLabelProvider();
        }

        return fLabelProvider;
    }

    /**
     * Sets the collector, which is used to access the compilation unit, the core context and the label provider. This is a
     * performance optimization: {@link IJavaCompletionProposalComputer}s may instantiate a {@link CompletionProposalCollector} and
     * set this invocation context via {@link CompletionProposalCollector#setInvocationContext(JavaContentAssistInvocationContext)}
     * , which in turn calls this method. This allows the invocation context to retrieve the core context and keyword proposals
     * from the existing collector, instead of computing theses values itself via {@link #computeKeywordsAndContext()}.
     *
     * @param collector
     *         the collector
     */
    void setCollector(CompletionProposalCollector collector) {
        fCollector = collector;
    }

    /** @return the compilationUnit */
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /**
     * @param compilationUnit
     *         the compilationUnit to set
     */
    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    /** @return the docContext */
    public String getDocContext() {
        return docContext;
    }

    /** @return the projectId */
    public String getProjectId() {
        return projectId;
    }

    /** @return the vfsId */
    public String getVfsId() {
        return vfsId;
    }

    // /**
    // * Fallback to retrieve a core context and keyword proposals when no collector is available. Runs code completion on the cu
    // and
    // * collects keyword proposals. {@link #fKeywordProposals} is non-<code>null</code> after this call.
    // *
    // * @since 3.3
    // */
    // private void computeKeywordsAndContext()
    // {
    // CompilationUnit cu = getCompilationUnit();
    // if (cu == null)
    // {
    // if (fKeywordProposals == null)
    // fKeywordProposals = new IJavaCompletionProposal[0];
    // return;
    // }
    //
    // CompletionProposalCollector collector = new CompletionProposalCollector(cu, true);
    // collector.setIgnored(CompletionProposal.KEYWORD, false);
    //
    // try
    // {
    // cu.codeComplete(getInvocationOffset(), collector);
    // if (fCoreContext == null)
    // fCoreContext = collector.getContext();
    // if (fKeywordProposals == null)
    // fKeywordProposals = collector.getKeywordCompletionProposals();
    // if (fLabelProvider == null)
    // fLabelProvider = collector.getLabelProvider();
    // }
    // catch (JavaModelException x)
    // {
    // if (!x.isDoesNotExist() || cu.getJavaProject() == null || cu.getJavaProject().isOnClasspath(cu))
    // JavaPlugin.log(x);
    // if (fKeywordProposals == null)
    // fKeywordProposals = new IJavaCompletionProposal[0];
    // }
    // }

   /*
    * Implementation note: There is no need to override hashCode and equals, as we only add cached values shared across one assist
    * invocation.
    */
}
