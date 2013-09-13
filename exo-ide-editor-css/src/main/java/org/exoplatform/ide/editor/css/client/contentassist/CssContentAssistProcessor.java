/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.editor.css.client.contentassist;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.editor.selection.SelectionModel;

import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;

/**
 * A {@link ContentAssistProcessor} proposes completions and
 * computes context information for CSS content.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CssContentAssistProcessor.java Feb 4, 2013 5:52:44 PM azatsarynnyy $
 */
public class CssContentAssistProcessor implements ContentAssistProcessor {

    /** The auto activation characters for completion proposal. */
    private static final char[] ACTIVATION_CHARACTERS = new char[]{':'};

    /** Autocompleter for CSS. */
    private CssAutocompleter autocompleter;

    /**
     * Creates new {@link CssContentAssistProcessor} instance.
     *
     * @param autocompleter
     *         {@link CssAutocompleter}
     */
    public CssContentAssistProcessor(CssAutocompleter cssAutocompleter) {
        autocompleter = cssAutocompleter;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide
     * .editor.client.api.Editor,
     *      int)
     */
    @Override
    public CompletionProposal[] computeCompletionProposals(Editor viewer, int offset) {
        SelectionModel selection = ((CollabEditor)viewer).getEditor().getSelection();
        AutocompleteProposals autocompletionProposals = autocompleter.findAutocompletions(selection, null);

        CssCompletionQuery completionQuery = autocompleter.getCompletionQuery();
        if (completionQuery == null) {
            return null;
        }
        CompletionType completionType = completionQuery.getCompletionType();

        CompletionProposal[] proposalArray = new CompletionProposal[autocompletionProposals.getItems().size()];
        for (int i = 0; i < autocompletionProposals.getItems().size(); i++) {
            ProposalWithContext proposal = autocompletionProposals.select(i);
            String triggeringString = proposal.getContext().getTriggeringString();
            proposalArray[i] = new CssProposal(proposal.getItem().getName(), completionType, triggeringString, offset);
        }
        return proposalArray;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide
     * .editor.client.api.Editor,
     *      int)
     */
    @Override
    public ContextInformation[] computeContextInformation(Editor viewer, int offset) {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters() */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return ACTIVATION_CHARACTERS;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters() */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getErrorMessage() */
    @Override
    public String getErrorMessage() {
        return null;
    }

}
