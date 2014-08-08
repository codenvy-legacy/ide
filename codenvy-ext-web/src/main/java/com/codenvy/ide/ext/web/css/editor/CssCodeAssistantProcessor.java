/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.web.css.editor;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.api.text.BadLocationException;
import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.text.Position;
import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.texteditor.CodeAssistCallback;
import com.codenvy.ide.api.texteditor.TextEditorPartView;
import com.codenvy.ide.api.texteditor.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.util.AbstractTrie;
import com.codenvy.ide.util.loging.Log;


/**
 * Implementation of {@link CodeAssistProcessor} for Css files.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CssCodeAssistantProcessor implements CodeAssistProcessor {

    private static final AbstractTrie<CssCompletionProposal> cssTrie = CssTrie.createTrie();
    private CssCompletionQuery completionQuery;
    private CssResources       resources;

    /** @param resources */
    public CssCodeAssistantProcessor(CssResources resources) {
        super();
        this.resources = resources;
    }

    /**
     * Creates a completion query from the position of the caret and the editor.
     * The completion query contains the string to complete and the type of
     * code assistant.
     */
    CssCompletionQuery updateOrCreateQuery(CssCompletionQuery completionQuery, Position cursor, Document document) {
        try {
            int line = document.getLineOfOffset(cursor.offset);
            Region region = document.getLineInformation(line);
            int column = cursor.getOffset() - region.getOffset();
            int lineWithCursor = line;

            boolean parsingLineWithCursor = true;
            //      Line line = cursor.getLine();

      /*
       * textSoFar will contain the text of the CSS rule (only the stuff within
       * the curly braces). If we are not in an open rule, return false
       */
            String textBefore = "";
            while ((line >= 0) && (!textBefore.contains("{"))) {
                int lastOpen;
                int lastClose;

                String text;
                if (parsingLineWithCursor) {
                    Region information = document.getLineInformation(line);
                    text = document.get(information.getOffset(), column);
                    parsingLineWithCursor = false;
                } else {
            /*
             * Don't include the newline character; it is irrelevant for
             * autocompletion.
             */
                    Region information = document.getLineInformation(line);
                    text = document.get(information.getOffset(), information.getLength());//.trim();
                }

                textBefore = text + textBefore;
                lastOpen = text.lastIndexOf('{');
                lastClose = text.lastIndexOf('}');

                // Either we have only a } or the } appears after {
                if (lastOpen < lastClose) {
                    return new CssCompletionQuery(textBefore, "");
                } else if ((lastOpen == -1) && (lastClose == -1)) {
                    line--;
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
            while ((line < document.getNumberOfLines()) && (!textAfter.contains("}"))) {
                int lastOpen;
                int lastClose;

                String text;
                Region information = document.getLineInformation(line);
                if (parsingLineWithCursor) {

                    text = document.get(information.getOffset(), information.getLength()).substring(column);
                    parsingLineWithCursor = false;
                } else {
            /*
             * Don't include the newline character; it is irrelevant for
             * autocompletion.
             */

                    text = document.get(information.getOffset(), information.getLength()).trim();
                }

                textAfter = textAfter + text;
                lastOpen = text.lastIndexOf('{');
                lastClose = text.lastIndexOf('}');

                // Either we have only a } or the } appears after {
                if (lastClose < lastOpen) {
                    return completionQuery;
                } else if ((lastOpen == -1) && (lastClose == -1)) {
                    line++;
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
        } catch (BadLocationException e) {
            Log.error(CssCodeAssistantProcessor.class, e);
            return null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void computeCompletionProposals(TextEditorPartView view, int offset, CodeAssistCallback callback) {
        if (view.getSelection().hasSelection()) {
            // Doesn't make much sense to autocomplete CSS when something is selected.
            callback.proposalComputed(null);
            return;
        }

        completionQuery = updateOrCreateQuery(completionQuery, view.getSelection().getCursorPosition(),
                                              view.getDocument());
        if (completionQuery == null) {
            callback.proposalComputed(null);
            return;
        }

        String triggeringString = completionQuery.getTriggeringString();
        if (triggeringString == null) {
            callback.proposalComputed(null);
            return;
        }
        InvocationContext context = new InvocationContext(triggeringString, offset, resources, view);
        switch (completionQuery.getCompletionType()) {
            case PROPERTY:
                Array<CssCompletionProposal> autocompletions = CssTrie.findAndFilterAutocompletions(cssTrie,
                                                                                                        triggeringString, completionQuery
                        .getCompletedProperties());
                callback.proposalComputed(jsToArray(autocompletions, context));
                return;

            case VALUE:
                JsoArray<CssCompletionProposal> jsoArray = CssPartialParser.getInstance().getAutocompletions(
                        completionQuery.getProperty(), completionQuery.getValuesBefore(), triggeringString,
                        completionQuery.getValuesAfter());
                callback.proposalComputed(jsToArray(jsoArray, context));
                return;

            case CLASS:
                // TODO: Implement css-class autocompletions (pseudoclasses
                //               and HTML elements).
                callback.proposalComputed(null);
                return;

            default:
                callback.proposalComputed(null);
        }
    }

    private CssCompletionProposal[] jsToArray(Array<CssCompletionProposal> autocompletions,
                                              InvocationContext context) {
        if (autocompletions == null) {
            return null;
        }
        CssCompletionProposal[] proposals = new CssCompletionProposal[autocompletions.size()];
        for (int i = 0; i < autocompletions.size(); i++) {
            proposals[i] = autocompletions.get(i);
            proposals[i].setContext(context);
        }
        return proposals;
    }

    /** {@inheritDoc} */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return new char[]{':'};
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        return null;
    }
}
