// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.editor.html.client.contentassist;

import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.SyntaxType;

import org.exoplatform.ide.editor.css.client.contentassist.CssAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptAutocompleter;

/**
 * Autocompleter for HTML content.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlAutocompleter.java Jun 3, 2013 4:14:01 PM azatsarynnyy $
 */
public class HtmlAutocompleter extends LanguageSpecificAutocompleter {

    private static HtmlExplicitAutocompleter explicitAutocompleter;

    /** Static factory method for obtaining an instance of {@link HtmlAutocompleter}. */
    public static HtmlAutocompleter create(CssAutocompleter cssAutocompleter,
                                           JavaScriptAutocompleter jsAutocompleter) {
        return new HtmlAutocompleter(cssAutocompleter, jsAutocompleter);
    }

    private CssAutocompleter        cssAutocompleter;

    private JavaScriptAutocompleter jsAutocompleter;

    private HtmlAutocompleter(CssAutocompleter cssAutocompleter,
                              JavaScriptAutocompleter jsAutocompleter) {
        super(SyntaxType.HTML);
        this.cssAutocompleter = cssAutocompleter;
        this.jsAutocompleter = jsAutocompleter;
        explicitAutocompleter = new HtmlExplicitAutocompleter(cssAutocompleter, jsAutocompleter);
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#attach(com.google.collide.client.documentparser.DocumentParser)
     */
    @Override
    public void attach(DocumentParser parser) {
        super.attach(parser);
        if (cssAutocompleter != null) {
            cssAutocompleter.attach(parser);
        }
        if (jsAutocompleter != null) {
            jsAutocompleter.attach(parser);
        }
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#computeAutocompletionResult(com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext)
     */
    @Override
    public AutocompleteResult computeAutocompletionResult(ProposalWithContext proposal) {
        return null;
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#getExplicitAction(com.google.collide.client.editor.selection.SelectionModel,
     *      com.google.collide.client.code.autocomplete.SignalEventEssence, boolean)
     */
    @Override
    public ExplicitAction getExplicitAction(SelectionModel selectionModel,
                                            SignalEventEssence signal, boolean popupIsShown) {
        return explicitAutocompleter.getExplicitAction(selectionModel, signal, popupIsShown, getParser());
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#findAutocompletions(com.google.collide.client.editor.selection.SelectionModel,
     *      com.google.collide.client.code.autocomplete.SignalEventEssence)
     */
    @Override
    public AutocompleteProposals findAutocompletions(SelectionModel selection, SignalEventEssence trigger) {
        return null;
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#cleanup()
     */
    @Override
    public void cleanup() {
    }

    /** @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#getParser() */
    @Override
    public DocumentParser getParser() {
        return super.getParser();
    }
}
