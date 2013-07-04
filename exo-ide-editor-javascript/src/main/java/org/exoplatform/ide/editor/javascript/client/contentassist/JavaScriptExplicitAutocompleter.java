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

package org.exoplatform.ide.editor.javascript.client.contentassist;

import com.codenvy.ide.client.util.SignalEvent.KeySignalType;
import com.codenvy.ide.commons.shared.StringUtils;
import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.code.autocomplete.DefaultAutocompleteResult;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter.ExplicitAction;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.code.autocomplete.codegraph.ExplicitAutocompleter;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.codemirror2.Token;
import com.google.collide.codemirror2.TokenType;
import com.google.collide.shared.document.anchor.ReadOnlyAnchor;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.regexp.shared.RegExp;

/** Implementation that adds JavaScript-specific cases. */
class JavaScriptExplicitAutocompleter extends ExplicitAutocompleter {

    private RegExp regExp         = RegExp.compile("/(\\*)+\n$", "g");

    private RegExp commentEndMach = RegExp.compile("(\\*)+[^/]");

    @Override
    protected ExplicitAction getExplicitAction(SelectionModel selectionModel, SignalEventEssence signal,
                                               boolean popupIsShown, DocumentParser parser) {
        if (checkEnterTrigger(signal)) {
            if (checkCursorBetweenCurlyBraces(selectionModel)) {
                String text = selectionModel.getCursorPosition().getLine().getText();
                int indent = StringUtils.lengthOfStartingWhitespace(text);
                String newLine = "\n" + StringUtils.getSpaces(indent);
                String emptyLine = newLine + "  ";
                return new ExplicitAction(new DefaultAutocompleteResult(emptyLine + newLine, "", emptyLine.length()));
            }
            if (checkCommentOpen(selectionModel)) {
                StringBuilder text = new StringBuilder("\n");
                String lineText = selectionModel.getCursorLine().getText();
                String spaces = StringUtils.getSpaces(lineText.lastIndexOf("/*") + 1);
                text.append(spaces).append("* \n").append(spaces).append("*/");
                return new ExplicitAction(new DefaultAutocompleteResult(text.toString(), "", spaces.length() + 3));
            }

            if (checkCursorInComment(selectionModel, parser)) {
                StringBuilder text = new StringBuilder("\n");
                String lineText = selectionModel.getCursorLine().getText();
                String spaces = StringUtils.getSpaces(lineText.lastIndexOf("*"));
                text.append(spaces).append("* ");
                return new ExplicitAction(new DefaultAutocompleteResult(text.toString(), "", spaces.length() + 3));
            }
        }

        // 'auto-complete as you type' feature
        final char signalChar = signal.getChar();
        if (signalChar != '{' && signalChar != ';' && signalChar != ' ' && signalChar != '(' && signalChar != ')' && signalChar != '\''
            && signalChar != '"' && signalChar != '\t') {
            if (!popupIsShown && signalChar != 0 && KeyCodes.KEY_ENTER != signalChar) {
                return ExplicitAction.DEFERRED_COMPLETE;
            }
            return ExplicitAction.DEFAULT;
        }

        return super.getExplicitAction(selectionModel, signal, popupIsShown, parser);
    }

    /**
     * @param selectionModel
     * @param parser
     * @return
     */
    private boolean checkCursorInComment(SelectionModel selectionModel, DocumentParser parser) {
        JsonArray<Token> tokens = parser.parseLineSync(selectionModel.getCursorLine());
        if (tokens.isEmpty()) {
            return false;
        }
        for (Token t = tokens.pop(); !tokens.isEmpty(); t = tokens.pop()) {
            if (t.getType() == TokenType.COMMENT && commentEndMach.test(t.getValue()))
                return true;
        }
        return false;
    }

    /**
     * @param selectionModel
     * @return
     */
    private boolean checkCommentOpen(SelectionModel selectionModel) {
        return regExp.test(selectionModel.getCursorLine().getText());
    }

    /**
     * Checks trigger to be plain "Enter" key press.
     * <p/>
     * <p>
     * "Shift-Enter" also works to avoid "sticky-shift" issue: when someone quickly types "Shift-[" (-> "{") and then press "Enter" while
     * "Shift" is not depressed.
     */
    private static boolean checkEnterTrigger(SignalEventEssence trigger) {
        return KeySignalType.INPUT == trigger.type && KeyCodes.KEY_ENTER == trigger.keyCode && !trigger.altKey
               && !trigger.ctrlKey && !trigger.metaKey;
    }

    /** Checks that cursor is situated between curly braces. */
    private static boolean checkCursorBetweenCurlyBraces(SelectionModel selectionModel) {
        if (!selectionModel.hasSelection()) {
            ReadOnlyAnchor cursor = selectionModel.getCursorAnchor();
            String text = cursor.getLine().getText();
            int column = cursor.getColumn();
            if (column > 0 && column < text.length()) {
                if (text.charAt(column - 1) == '{' && text.charAt(column) == '}') {
                    return true;
                }
            }
        }
        return false;
    }
}
