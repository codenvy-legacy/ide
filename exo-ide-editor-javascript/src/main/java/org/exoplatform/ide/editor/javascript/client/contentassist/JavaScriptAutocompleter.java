/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
