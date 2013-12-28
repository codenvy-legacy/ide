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
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.Document;


/** Types directly completed to &#x7b;&#x40;link Type&#x7d;. See {@link CompletionProposal#JAVADOC_TYPE_REF}. */
public final class JavadocLinkTypeCompletionProposal extends LazyJavaTypeCompletionProposal {

    public JavadocLinkTypeCompletionProposal(CompletionProposal proposal, JavaContentAssistInvocationContext context) {
        super(proposal, context);
        Assert.isTrue(isInJavadoc());
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal#computeReplacementString()
     */
    @Override
    protected String computeReplacementString() {
        String typeReplacement = super.computeReplacementString();
        // XXX: respect the auto-close preference, but do so consistently with method completions
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=113544
        // if (autocloseBrackets())
        return "{@link " + typeReplacement + "}"; //$NON-NLS-1$ //$NON-NLS-2$
        // else
        //         return "{@link " + typeReplacement; //$NON-NLS-1$
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal#apply(org.eclipse.jface.text.IDocument, char, int)
     */
    @Override
    public void apply(Document document, char trigger, int offset) {
        // convert . to #
        if (trigger == '.')
            trigger = '#';
        // XXX: respect the auto-close preference, but do so consistently with method completions
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=113544
        // boolean continueWithMember= trigger == '#' && autocloseBrackets();
        boolean continueWithMember = trigger == '#';
        if (continueWithMember)
            setCursorPosition(getCursorPosition() - 1); // before the closing curly brace

        super.apply(document, trigger, offset);

        if (continueWithMember)
            setUpLinkedMode(document, '}');
    }
}
