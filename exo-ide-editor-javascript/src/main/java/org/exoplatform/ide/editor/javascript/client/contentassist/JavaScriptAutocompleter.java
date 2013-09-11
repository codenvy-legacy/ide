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
package org.exoplatform.ide.editor.javascript.client.contentassist;

import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.SyntaxType;

/**
 * JavaScript-specific autocompleter.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JavaScriptAutocompleter.java Aug 28, 2012 9:37:43 AM azatsarynnyy $
 */
public class JavaScriptAutocompleter extends LanguageSpecificAutocompleter {

    private static final JavaScriptExplicitAutocompleter autocompleter = new JavaScriptExplicitAutocompleter();

    /** @param mode */
    public JavaScriptAutocompleter() {
        super(SyntaxType.JS);
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#getExplicitAction(com.google.collide.client.editor
     * .selection.SelectionModel,
     *      com.google.collide.client.code.autocomplete.SignalEventEssence, boolean)
     */
    @Override
    public ExplicitAction getExplicitAction(SelectionModel selectionModel, SignalEventEssence signal,
                                            boolean popupIsShown) {
        return autocompleter.getExplicitAction(selectionModel, signal, popupIsShown, getParser());
    }

    /** @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#computeAutocompletionResult(com.google.collide
     * .client.code.autocomplete.AutocompleteProposals.ProposalWithContext) */
    @Override
    public AutocompleteResult computeAutocompletionResult(ProposalWithContext proposal) {
        return null;
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#findAutocompletions(com.google.collide.client.editor
     * .selection.SelectionModel,
     *      com.google.collide.client.code.autocomplete.SignalEventEssence)
     */
    @Override
    public AutocompleteProposals findAutocompletions(SelectionModel selection, SignalEventEssence trigger) {
        return null;
    }

    /** @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#cleanup() */
    @Override
    public void cleanup() {
    }

    /** @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#attach(com.google.collide.client.documentparser
     * .DocumentParser) */
    @Override
    public void attach(DocumentParser parser) {
        super.attach(parser);
    }
}
