/*
 * Copyright (C) 2013 eXo Platform SAS.
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
