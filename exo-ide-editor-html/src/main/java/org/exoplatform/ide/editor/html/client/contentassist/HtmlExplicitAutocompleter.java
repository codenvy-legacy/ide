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

package org.exoplatform.ide.editor.html.client.contentassist;

import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.code.autocomplete.DefaultAutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter.ExplicitAction;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.code.autocomplete.codegraph.ExplicitAutocompleter;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.documentparser.ParseResult;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.CodeMirror2;
import com.google.collide.codemirror2.HtmlState;
import com.google.collide.codemirror2.XmlContext;
import com.google.collide.codemirror2.XmlState;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.Position;
import com.google.collide.shared.document.anchor.Anchor;
import com.google.collide.shared.document.anchor.AnchorManager;
import com.google.collide.shared.document.anchor.AnchorType;
import com.google.gwt.event.dom.client.KeyCodes;

import org.exoplatform.ide.editor.css.client.contentassist.CssAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptAutocompleter;

/**
 * Implementation that adds HTML-specific cases.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlExplicitAutocompleter.java Apr 17, 2013 1:37:18 PM azatsarynnyy $
 */
class HtmlExplicitAutocompleter extends ExplicitAutocompleter {

    private static final String           ELEMENT_SEPARATOR_CLOSE = ">";

    private static final AnchorType       MODE_ANCHOR_TYPE        = AnchorType.create(HtmlAutocompleter.class, "mode");

    private final CssAutocompleter        cssAutocompleter;

    private final JavaScriptAutocompleter jsAutocompleter;

    public HtmlExplicitAutocompleter(CssAutocompleter cssAutocompleter, JavaScriptAutocompleter jsAutocompleter) {
        this.cssAutocompleter = cssAutocompleter;
        this.jsAutocompleter = jsAutocompleter;
    }

    @Override
    protected ExplicitAction getExplicitAction(SelectionModel selectionModel, SignalEventEssence signal,
                                               boolean popupIsShown, DocumentParser parser) {
        Position cursor = selectionModel.getCursorPosition();
        final int cursorColumn = cursor.getColumn();
        final Line cursorLine = cursor.getLine();
        final String mode = getModeForColumn(cursorLine, cursorColumn, parser.getInitialMode(cursorLine));

        if (cssAutocompleter != null && CodeMirror2.CSS.equals(mode)) {
            return cssAutocompleter.getExplicitAction(selectionModel, signal, popupIsShown);
        } else if (jsAutocompleter != null && CodeMirror2.JAVASCRIPT.equals(mode)) {
            return jsAutocompleter.getExplicitAction(selectionModel, signal, popupIsShown);
        } else if (mode == null) {
            // This is possible if line is new and hasn't been processed yet.
            // We prefer to avoid annoying autocompletions.
            return ExplicitAction.DEFAULT;
        }

        char signalChar = signal.getChar();
        if (signalChar == '/') {
            if (selectionModel.hasSelection()) {
                return ExplicitAction.DEFAULT;
            }
            if (cursorColumn == 0 || '<' != cursorLine.getText().charAt(cursorColumn - 1)) {
                return ExplicitAction.DEFAULT;
            }
            ParseResult<HtmlState> parseResult = parser.getState(HtmlState.class, cursor, null);
            if (parseResult != null) {
                XmlState xmlState = parseResult.getState().getXmlState();
                if (xmlState != null) {
                    XmlContext xmlContext = xmlState.getContext();
                    if (xmlContext != null) {
                        String tagName = xmlContext.getTagName();
                        if (tagName != null) {
                            String addend = "/" + tagName + ELEMENT_SEPARATOR_CLOSE;
                            return new ExplicitAction(new DefaultAutocompleteResult(addend, "", addend.length()));
                        }
                    }
                }
            }
            return ExplicitAction.DEFAULT;
        }

        // auto-complete as you type feature
        if (!popupIsShown && (signalChar != 0)
            && (KeyCodes.KEY_ENTER != signalChar)
            && ('>' != signalChar)) {
            return ExplicitAction.DEFERRED_COMPLETE;
        }
        return ExplicitAction.DEFAULT;

        // return super.getExplicitAction(selectionModel, signal, popupIsShown, parser);
    }

    private String getModeForColumn(Line line, int column, String initialMode) {
        JsonArray<Anchor> anchors = AnchorManager.getAnchorsByTypeOrNull(line, MODE_ANCHOR_TYPE);
        if (anchors != null) {
            for (Anchor anchor : anchors.asIterable()) {
                if (anchor.getColumn() >= column) {
                    // We'll use the previous mode.
                    break;
                }
                initialMode = anchor.getValue();
            }
        }
        return initialMode;
    }

}
