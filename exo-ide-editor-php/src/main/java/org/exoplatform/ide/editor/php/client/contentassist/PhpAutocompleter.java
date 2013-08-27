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
package org.exoplatform.ide.editor.php.client.contentassist;

import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.code.autocomplete.AutocompleteProposals;
import com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext;
import com.google.collide.client.code.autocomplete.AutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.CodeMirror2;
import com.google.collide.codemirror2.SyntaxType;
import com.google.collide.codemirror2.TokenUtil;
import com.google.collide.shared.Pair;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.Position;
import com.google.collide.shared.document.anchor.AnchorType;

import org.exoplatform.ide.editor.html.client.contentassist.HtmlAutocompleter;

/**
 * Autocompleter for PHP.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpAutocompleter.java Apr 17, 2013 1:01:44 PM azatsarynnyy $
 */
public class PhpAutocompleter extends LanguageSpecificAutocompleter {

    private static final PhpExplicitAutocompleter EXPLICIT_AUTOCOMPLETER = new PhpExplicitAutocompleter();

    static final AnchorType MODE_ANCHOR_TYPE = AnchorType.create(HtmlAutocompleter.class, "mode");

    private final HtmlAutocompleter htmlAutocompleter;

    public PhpAutocompleter(HtmlAutocompleter htmlAutocompleter) {
        super(SyntaxType.PHP);
        this.htmlAutocompleter = htmlAutocompleter;
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#attach(com.google.collide.client.documentparser.DocumentParser)
     */
    @Override
    protected void attach(DocumentParser parser) {
        super.attach(parser);
        if (htmlAutocompleter != null) {
            htmlAutocompleter.attach(parser);
        }
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#getExplicitAction(com.google.collide.client.editor.selection.SelectionModel,
     *      com.google.collide.client.code.autocomplete.SignalEventEssence, boolean)
     */
    @Override
    protected ExplicitAction getExplicitAction(SelectionModel selectionModel,
                                               SignalEventEssence signal, boolean popupIsShown) {
        Position cursor = selectionModel.getCursorPosition();
        int cursorColumn = cursor.getColumn();
        Line cursorLine = cursor.getLine();
        final String mode = getModeForColumn(cursorLine, cursorColumn);

        if (CodeMirror2.CSS.equals(mode) || CodeMirror2.JAVASCRIPT.equals(mode) || CodeMirror2.HTML.equals(mode)) {
            if (htmlAutocompleter != null) {
                return htmlAutocompleter.getExplicitAction(selectionModel, signal, popupIsShown);
            }
        }

        return EXPLICIT_AUTOCOMPLETER.getExplicitAction(selectionModel, signal, popupIsShown, getParser());
    }

    private String getModeForColumn(Line line, int column) {
        DocumentParser parser = getParser();
        final String initialMode = parser.getInitialMode(line);
        JsonArray<com.google.collide.codemirror2.Token> tokens = parser.parseLineSync(line);
        if (tokens == null) {
            // This line has never been parsed yet. No variants.
            return initialMode;
        }
        JsonArray<Pair<Integer, String>> modes = TokenUtil.buildModes(initialMode, tokens);
        final String mode = TokenUtil.findModeForColumn(initialMode, modes, column);
        return mode;
    }

    /**
     * @see com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter#computeAutocompletionResult(com.google.collide.client.code.autocomplete.AutocompleteProposals.ProposalWithContext)
     */
    @Override
    public AutocompleteResult computeAutocompletionResult(ProposalWithContext proposal) {
        return null;
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

}
