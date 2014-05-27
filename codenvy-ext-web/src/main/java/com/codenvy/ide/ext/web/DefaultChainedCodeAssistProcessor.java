/*
 * CODENVY CONFIDENTIAL
 *  __________________
 *
 *   [2014] Codenvy, S.A.
 *   All Rights Reserved.
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of Codenvy S.A. and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to Codenvy S.A.
 *  and its suppliers and may be covered by U.S. and Foreign Patents,
 *  patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from Codenvy S.A..
 */

package com.codenvy.ide.ext.web;

import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Allows to chain code assist processor for the default given content type.
 * It will delegate to sub processors.
 *
 * @author Florent Benoit
 */
public abstract class DefaultChainedCodeAssistProcessor implements CodeAssistProcessor {

    /**
     * HTML code assist processors.
     */
    private Set<? extends CodeAssistProcessor> codeAssistProcessors;

    /**
     * Allow to set processors.
     *
     * @param codeAssistProcessors
     *         the expected processors
     */
    protected void setProcessors(Set<? extends CodeAssistProcessor> codeAssistProcessors) {
        this.codeAssistProcessors = codeAssistProcessors;
    }


    /** {@inheritDoc} */
    @Override
    public void computeCompletionProposals(TextEditorPartView view, int offset, CodeAssistCallback callback) {
        if (codeAssistProcessors.size() > 0) {
            final List<CompletionProposal> proposalList = new ArrayList<>();
            for (CodeAssistProcessor processor : codeAssistProcessors) {
                processor.computeCompletionProposals(view, offset, new CodeAssistCallback() {
                    @Override
                    public void proposalComputed(CompletionProposal[] processorProposals) {
                        if (processorProposals == null || processorProposals.length == 0) {
                            return;
                        }

                        proposalList.addAll(Arrays.asList(processorProposals));

                    }
                });
            }
            callback.proposalComputed(proposalList.toArray(new CompletionProposal[proposalList.size()]));
        }
    }

    /** {@inheritDoc} */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        Set<Character> characters = new HashSet<>();
        if (codeAssistProcessors.size() > 0) {
            for (CodeAssistProcessor processor : codeAssistProcessors) {
                char[] found = processor.getCompletionProposalAutoActivationCharacters();
                if (found != null) {
                    for (char c : found) {
                        characters.add(c);
                    }
                }
            }
        }
        char[] chars = new char[characters.size()];
        int c = 0;
        for (Character character : characters) {
            chars[c] = character.charValue();
            c++;
        }
        return chars;
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        String errorMessage = null;
        if (codeAssistProcessors.size() > 0) {
            for (CodeAssistProcessor processor : codeAssistProcessors) {
                String processorErrorMessage = processor.getErrorMessage();
                if (processorErrorMessage != null) {
                    if (errorMessage == null) {
                        errorMessage = processorErrorMessage;
                    } else {
                        errorMessage.concat(processorErrorMessage);
                    }
                }
            }
        }
        return errorMessage;
    }

    /**
     * @return injected processors.
     */
    public Set<? extends CodeAssistProcessor> getProcessors() {
        return codeAssistProcessors;
    }
}
