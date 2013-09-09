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
package org.exoplatform.ide.editor.python.client;

import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.SyntaxType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class PyAutocompliter extends LanguageSpecificAutocompleter {

    private static final PyExplicitAutocompleter EXPLICIT_AUTOCOMPLETER = new PyExplicitAutocompleter();

    protected PyAutocompliter() {
        super(SyntaxType.PY);
    }

    @Override
    protected ExplicitAction getExplicitAction(SelectionModel selectionModel, SignalEventEssence signal,
                                               boolean popupIsShown) {
        return EXPLICIT_AUTOCOMPLETER.getExplicitAction(selectionModel, signal, popupIsShown, getParser());
    }

    @Override
    public AutocompleteResult computeAutocompletionResult(ProposalWithContext proposal) {
        return null;
    }

    @Override
    public AutocompleteProposals findAutocompletions(SelectionModel selection, SignalEventEssence trigger) {
        return null;
    }

    @Override
    public void cleanup() {
    }
}
