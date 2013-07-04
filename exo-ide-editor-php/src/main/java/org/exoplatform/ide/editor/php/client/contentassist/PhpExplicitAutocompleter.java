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
