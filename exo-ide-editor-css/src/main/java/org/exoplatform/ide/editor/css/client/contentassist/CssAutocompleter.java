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

package org.exoplatform.ide.editor.css.client.contentassist;

import com.codenvy.ide.client.util.Preconditions;
import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.code.autocomplete.AbstractTrie;
import com.google.collide.client.code.autocomplete.AutocompleteProposal;
import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.DefaultAutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.documentparser.ParseResult;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.CssState;
import com.google.collide.codemirror2.CssToken;
import com.google.collide.codemirror2.SyntaxType;
import com.google.collide.codemirror2.Token;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.Position;
import com.google.gwt.event.dom.client.KeyCodes;

import static com.google.collide.client.code.autocomplete.AutocompleteResult.PopupAction.CLOSE;
import static com.google.collide.client.code.autocomplete.AutocompleteResult.PopupAction.OPEN;
import static com.google.collide.codemirror2.TokenType.NULL;


/**
 * Autocompleter for CSS. Currently, this only supports CSS2.
 * <p/>
 * TODO: Support CSS3.
 */
public class CssAutocompleter extends LanguageSpecificAutocompleter {

    private static final String                             PROPERTY_TERMINATOR = ";";

    private static final String                             PROPERTY_SEPARATOR  = ": ";

    private static final String                             CLASS_SEPARATOR     = "{\n  \n}";

    private static final int                                CLASS_JUMPLENGTH    = 4;

    private static final AbstractTrie<AutocompleteProposal> cssTrie             = CssTrie.createTrie();

    public static CssAutocompleter create() {
        return new CssAutocompleter();
    }

    private static AutocompleteResult constructResult(String rawResult, String triggeringString) {
        int start = rawResult.indexOf('<');
        int end = rawResult.indexOf('>');
        if ((start >= 0) && (start < end)) {
            return new DefaultAutocompleteResult(
                                                 rawResult, (end + 1), 0, (end + 1) - start, 0, CLOSE, triggeringString);
        }
        return new DefaultAutocompleteResult(rawResult, triggeringString, rawResult.length());
    }

    private CssCompletionQuery completionQuery;

    private CssAutocompleter() {
        super(SyntaxType.CSS);
    }

    @Override
    public void attach(DocumentParser parser) {
        super.attach(parser);
        completionQuery = null;
    }

    @Override
    public AutocompleteResult computeAutocompletionResult(ProposalWithContext proposal) {
        AutocompleteProposal selectedProposal = proposal.getItem();
        String triggeringString = proposal.getContext().getTriggeringString();
        String name = selectedProposal.getName();
        CompletionType completionType = completionQuery.getCompletionType();
        if (CompletionType.CLASS == completionType) {
            // In this case implicit autocompletion workflow should trigger,
            // and so execution should never reach this point.
            Log.warn(getClass(), "Invocation of this method in not allowed for type CLASS");
            return DefaultAutocompleteResult.EMPTY;
        } else if (CompletionType.PROPERTY == completionType) {
            String addend = name + PROPERTY_SEPARATOR + PROPERTY_TERMINATOR;
            int jumpLength = addend.length() - PROPERTY_TERMINATOR.length();
            return new DefaultAutocompleteResult(
                                                 addend, jumpLength, 0, 0, 0, OPEN, triggeringString);
        } else if (CompletionType.VALUE == completionType) {
            return constructResult(name, triggeringString);
        }
        Log.warn(getClass(), "Invocation of this method in not allowed for type NONE");
        return DefaultAutocompleteResult.EMPTY;
    }

    /**
     * Creates a completion query from the position of the caret and the editor. The completion query contains the string to complete and
     * the type of autocompletion.
     * <p/>
     * TODO: take care of quoted '{' and '}'
     */
    CssCompletionQuery updateOrCreateQuery(CssCompletionQuery completionQuery, Position cursor) {
        Line line = cursor.getLine();
        int column = cursor.getColumn();
        Line lineWithCursor = line;
        boolean parsingLineWithCursor = true;

        /*
         * textSoFar will contain the text of the CSS rule (only the stuff within the curly braces). If we are not in an open rule, return
         * false
         */
        String textBefore = "";
        while ((line != null) && (!textBefore.contains("{"))) {
            int lastOpen;
            int lastClose;

            String text;
            if (parsingLineWithCursor) {
                text = line.getText().substring(0, column);
                parsingLineWithCursor = false;
            } else {
                /*
                 * Don't include the newline character; it is irrelevant for autocompletion.
                 */
                text = line.getText();// .trim();
            }

            textBefore = text + textBefore;
            lastOpen = text.lastIndexOf('{');
            lastClose = text.lastIndexOf('}');

            // Either we have only a } or the } appears after {
            if (lastOpen < lastClose) {
                // return completionQuery;
                return new CssCompletionQuery(textBefore, "");
            } else if ((lastOpen == -1) && (lastClose == -1)) {
                line = line.getPreviousLine();
            } else {
                if (textBefore.endsWith("{")) {
                    // opening a new css class, no text after to consider
                    return new CssCompletionQuery(textBefore, "");
                } else if (textBefore.endsWith(";") && completionQuery != null) {
                    // we don't want to create a new query, otherwise we lose the
                    // completed proposals
                    completionQuery.setCompletionType(CompletionType.NONE);
                    return completionQuery;
                }
            }
        }

        parsingLineWithCursor = true;
        String textAfter = "";
        line = lineWithCursor;
        while ((line != null) && (!textAfter.contains("}"))) {
            int lastOpen;
            int lastClose;

            String text;
            if (parsingLineWithCursor) {
                text = line.getText().substring(column);
                parsingLineWithCursor = false;
            } else {
                /*
                 * Don't include the newline character; it is irrelevant for autocompletion.
                 */
                text = line.getText().trim();
            }

            textAfter = textAfter + text;
            lastOpen = text.lastIndexOf('{');
            lastClose = text.lastIndexOf('}');

            // Either we have only a } or the } appears after {
            if (lastClose < lastOpen) {
//                return completionQuery;
                return new CssCompletionQuery(textBefore, textAfter);
            } else if ((lastOpen == -1) && (lastClose == -1)) {
                line = line.getNextLine();
            } else {
                if ((!textAfter.isEmpty()) && (textAfter.charAt(textAfter.length() - 1) == ';')) {
                    return completionQuery;
                }
            }
        }

        if (textBefore.contains("{")) {
            textBefore = textBefore.substring(textBefore.indexOf('{') + 1);
        }
        if (textAfter.contains("}")) {
            textAfter = textAfter.substring(0, textAfter.indexOf('}'));
        }

        return new CssCompletionQuery(textBefore, textAfter);
    }

    /**
     * Finds autocompletions for a given completion query.
     * 
     * @return an array of autocompletion proposals
     */
    @Override
    public AutocompleteProposals findAutocompletions(SelectionModel selection, SignalEventEssence trigger) {
        if (selection.hasSelection()) {
            // Doesn't make much sense to autocomplete CSS when something is selected.
            return AutocompleteProposals.EMPTY;
        }

        completionQuery = updateOrCreateQuery(completionQuery, selection.getCursorPosition());
        if (completionQuery == null) {
            return AutocompleteProposals.EMPTY;
        }

        String triggeringString = completionQuery.getTriggeringString();
        if (triggeringString == null) {
            return AutocompleteProposals.EMPTY;
        }

        switch (completionQuery.getCompletionType()) {
            case PROPERTY:
                return new AutocompleteProposals(SyntaxType.CSS, triggeringString,
                                                 CssTrie.findAndFilterAutocompletions(
                                                                                      cssTrie, triggeringString,
                                                                                      completionQuery.getCompletedProperties()));

            case VALUE:
                return new AutocompleteProposals(SyntaxType.CSS, triggeringString,
                                                 CssPartialParser.getInstance().getAutocompletions(
                                                                                                   completionQuery.getProperty(),
                                                                                                   completionQuery.getValuesBefore(),
                                                                                                   triggeringString,
                                                                                                   completionQuery.getValuesAfter()));

            case CLASS:
                // TODO: Implement CSS-pseudoclass and HTML element autocompletions.
                String documentText = selection.getCursorLine().getDocument().asText();
                return new AutocompleteProposals(SyntaxType.CSS, triggeringString, CssClassNameFinder.findAutocompletions(triggeringString,
                                                                                                                          documentText));

            default:
                return AutocompleteProposals.EMPTY;
        }
    }

    @Override
    public ExplicitAction getExplicitAction(SelectionModel selectionModel,
                                            SignalEventEssence signal, boolean popupIsShown) {
        if (selectionModel.hasSelection()) {
            return ExplicitAction.DEFAULT;
        }

        char signalChar = signal.getChar();

        if (signalChar != '{') {
            // auto-complete as you type feature
            if (!popupIsShown && (signalChar != 0) && (KeyCodes.KEY_ENTER != signalChar)) {
                return ExplicitAction.DEFERRED_COMPLETE;
            }
            return ExplicitAction.DEFAULT;
        }

        DocumentParser parser = getParser();

        // 1) Check we are not in block already.
        ParseResult<CssState> parseResult = null;
        try {
            parseResult = parser.getState(CssState.class, selectionModel.getCursorPosition(), " ");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if (parseResult == null) {
            return ExplicitAction.DEFAULT;
        }
        
        JsonArray<Token> tokens = parseResult.getTokens();
        Preconditions.checkNotNull(tokens, "");
        Preconditions.checkState(tokens.size() > 0, "");
        CssToken lastToken = (CssToken)tokens.peek();
        if ("{".equals(lastToken.getContext())) {
            return ExplicitAction.DEFAULT;
        }

        // 2) Check we will enter block.
        parseResult = parser.getState(CssState.class, selectionModel.getCursorPosition(), "{");
        if (parseResult == null) {
            return ExplicitAction.DEFAULT;
        }
        tokens = parseResult.getTokens();
        Preconditions.checkNotNull(tokens, "");
        Preconditions.checkState(tokens.size() > 0, "");
        lastToken = (CssToken)tokens.peek();
        String context = lastToken.getContext();
        boolean inBlock = context != null && context.endsWith("{");
        if (inBlock && NULL == lastToken.getType()) {
            return new ExplicitAction(
                                      new DefaultAutocompleteResult(CLASS_SEPARATOR, "", CLASS_JUMPLENGTH));
        }
        return ExplicitAction.DEFAULT;
    }

    @Override
    protected void pause() {
        super.pause();
        completionQuery = null;
    }

    @Override
    public void cleanup() {
    }

    CssCompletionQuery getCompletionQuery() {
        return completionQuery;
    }
}
