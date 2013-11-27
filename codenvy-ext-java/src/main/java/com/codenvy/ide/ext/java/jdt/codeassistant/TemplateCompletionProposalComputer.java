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

import com.codenvy.ide.ext.java.jdt.JavaPartitions;
import com.codenvy.ide.ext.java.jdt.core.CompletionContext;
import com.codenvy.ide.ext.java.jdt.templates.ContextTypeRegistry;
import com.codenvy.ide.ext.java.jdt.templates.JavaContextType;
import com.codenvy.ide.ext.java.jdt.templates.JavaDocContextType;
import com.codenvy.ide.ext.java.jdt.templates.TemplateEngine;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateContextType;

import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.TextUtilities;


/** Computer computing template proposals for Java and Javadoc context type. */
public class TemplateCompletionProposalComputer extends AbstractTemplateCompletionProposalComputer {

    private final TemplateEngine fJavaTemplateEngine;

    private final TemplateEngine fJavaStatementsTemplateEngine;

    private final TemplateEngine fJavaMembersTemplateEngine;

    private final TemplateEngine fJavadocTemplateEngine;

    public TemplateCompletionProposalComputer() {
        ContextTypeRegistry templateContextRegistry = WorkerMessageHandler.get().getTemplateContextRegistry();
        fJavaTemplateEngine = createTemplateEngine(templateContextRegistry, JavaContextType.ID_ALL);
        fJavaMembersTemplateEngine = createTemplateEngine(templateContextRegistry, JavaContextType.ID_MEMBERS);
        fJavaStatementsTemplateEngine = createTemplateEngine(templateContextRegistry, JavaContextType.ID_STATEMENTS);
        fJavadocTemplateEngine = createTemplateEngine(templateContextRegistry, JavaDocContextType.ID);
    }

    private static TemplateEngine createTemplateEngine(ContextTypeRegistry templateContextRegistry, String contextTypeId) {
        TemplateContextType contextType = templateContextRegistry.getContextType(contextTypeId);
        Assert.isNotNull(contextType);
        return new TemplateEngine(contextType);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.eclipse.jdt.internal.ui.text.java.TemplateCompletionProposalComputer#computeCompletionEngine(org.eclipse.jdt.ui.text
     * .java.JavaContentAssistInvocationContext)
     */
    @Override
    protected TemplateEngine computeCompletionEngine(JavaContentAssistInvocationContext context) {
        try {
            String partition =
                    TextUtilities.getContentType(context.getDocument(), JavaPartitions.JAVA_PARTITIONING,
                                                 context.getInvocationOffset(), true);
            if (partition.equals(JavaPartitions.JAVA_DOC))
                return fJavadocTemplateEngine;
            else {
                CompletionContext coreContext = context.getCoreContext();
                if (coreContext != null) {
                    int tokenLocation = coreContext.getTokenLocation();
                    if ((tokenLocation & CompletionContext.TL_MEMBER_START) != 0) {
                        return fJavaMembersTemplateEngine;
                    }
                    if ((tokenLocation & CompletionContext.TL_STATEMENT_START) != 0) {
                        return fJavaStatementsTemplateEngine;
                    }
                }
                return fJavaTemplateEngine;
            }
        } catch (BadLocationException x) {
            return null;
        }
    }

}
