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

import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter.ExplicitAction;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.code.autocomplete.codegraph.ExplicitAutocompleter;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * Implementation that adds PHP-specific cases.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpExplicitAutocompleter.java Apr 17, 2013 1:37:18 PM azatsarynnyy $
 *
 */
class PhpExplicitAutocompleter extends ExplicitAutocompleter {

    public PhpExplicitAutocompleter() {
    }

    @Override
    protected ExplicitAction getExplicitAction(SelectionModel selectionModel, SignalEventEssence signal,
                                               boolean popupIsShown, DocumentParser parser) {

//        if (checkEnterTrigger(signal)) {
//            if (checkCursorBetweenCurlyBraces(selectionModel)) {
//                String text = selectionModel.getCursorPosition().getLine().getText();
//                int indent = StringUtils.lengthOfStartingWhitespace(text);
//                String newLine = "\n" + StringUtils.getSpaces(indent);
//                String emptyLine = newLine + "  ";
//                return new ExplicitAction(new DefaultAutocompleteResult(emptyLine + newLine, "", emptyLine.length()));
//            }
//            if (checkCommentOpen(selectionModel)) {
//                StringBuilder text = new StringBuilder("\n");
//                String lineText = selectionModel.getCursorLine().getText();
//                String spaces = StringUtils.getSpaces(lineText.lastIndexOf("/*") + 1);
//                text.append(spaces).append("* \n").append(spaces).append("*/");
//                return new ExplicitAction(new DefaultAutocompleteResult(text.toString(), "", spaces.length() + 3));
//            }
//
//            if (checkCursorInComment(selectionModel, parser)) {
//                StringBuilder text = new StringBuilder("\n");
//                String lineText = selectionModel.getCursorLine().getText();
//                String spaces = StringUtils.getSpaces(lineText.lastIndexOf("*"));
//                text.append(spaces).append("* ");
//                return new ExplicitAction(new DefaultAutocompleteResult(text.toString(), "", spaces.length() + 3));
//            }
//        }

        // 'auto-complete as you type' feature
        final char signalChar = signal.getChar();
        if (signalChar != '{' && 
            signalChar != ';' && 
            signalChar != ' ' && 
            signalChar != '(' && 
            signalChar != ')' && 
            signalChar != '\'' && 
            signalChar != '"') {
            
            if (!popupIsShown && signalChar != 0 && KeyCodes.KEY_ENTER != signalChar) {
                return ExplicitAction.DEFERRED_COMPLETE;
            }
            
            return ExplicitAction.DEFAULT;
        }
        
        return super.getExplicitAction(selectionModel, signal, popupIsShown, parser);
    }
}
