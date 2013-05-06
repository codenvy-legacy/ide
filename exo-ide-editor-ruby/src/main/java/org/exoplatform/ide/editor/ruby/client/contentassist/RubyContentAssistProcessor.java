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
package org.exoplatform.ide.editor.ruby.client.contentassist;

import com.codenvy.ide.client.util.logging.Log;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.Position;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;
import org.exoplatform.ide.json.shared.JsonArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A {@link RubyContentAssistProcessor} proposes completions and computes context information for Ruby content.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RubyContentAssistProcessor.java Apr 30, 2013 4:30:39 PM azatsarynnyy $
 */
public class RubyContentAssistProcessor implements ContentAssistProcessor {

    public interface RubyBundle extends ClientBundle {
        @Source("org/exoplatform/ide/editor/ruby/client/tokens/ruby_tokens.js")
        ExternalTextResource rubyKeyWords();
    }

    private Comparator<Token> tokenComparator = new Comparator<Token>() {
          @Override
          public int compare(Token o1, Token o2) {

              if (o1.getType() != org.exoplatform.ide.editor.api.codeassitant.TokenType.KEYWORD
                  && o2.getType() == org.exoplatform.ide.editor.api.codeassitant.TokenType.KEYWORD) {
                  return -1;
              } else if (o1.getType() == org.exoplatform.ide.editor.api.codeassitant.TokenType.KEYWORD
                         && o2.getType() != org.exoplatform.ide.editor.api.codeassitant.TokenType.KEYWORD) {
                  return 1;
              } else {
                  return o1.getName().compareTo(o2.getName());
              }
          }
      };

    /** Bean that holds {@link #findToken} results. */
    private static class FindTokenResult {
        /** Token that "covers" the cursor. */
        com.google.collide.codemirror2.Token inToken;

        /** Number of characters between "inToken" start and the cursor position. */
        int                                  cut;
    }

    /** List of Ruby language keywords. */
    private static List<Token> keyWords;

    public RubyContentAssistProcessor() {
        init();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeCompletionProposals(org.exoplatform.ide.editor.client.api.Editor,
     *      int)
     */
    @Override
    public CompletionProposal[] computeCompletionProposals(Editor editor, int offset) {
        CollabEditor collabEditor = (CollabEditor)editor;
        SelectionModel selection = collabEditor.getEditor().getSelection();
        DocumentParser parser = collabEditor.getEditorBundle().getParser();

        Position cursor = selection.getCursorPosition();
        final Line line = cursor.getLine();
        final int column = cursor.getColumn();

        JsonArray<com.google.collide.codemirror2.Token> tokens = parser.parseLineSync(line);
        if (tokens == null) {
            // This line has never been parsed yet. No variants.
            return null;
        }

        FindTokenResult findTokenResult = getTriggeringString(tokens, column);
        String prefix = findTokenResult.inToken.getValue();

        switch (findTokenResult.inToken.getType()) {
            case WHITESPACE:
            case NULL:
                prefix = "";
            default:
                prefix = prefix.substring(0, prefix.length() - findTokenResult.cut);
        }

        List<Token> filteredTokens = getTokensFilteredByPrefix(prefix);
        CompletionProposal[] proposals = new CompletionProposal[filteredTokens.size()];
        int i = 0;
        for (Token token : filteredTokens) {
            proposals[i++] = new RubyProposal(token.getName(), prefix, offset, token);
        }
        return proposals;
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#computeContextInformation(org.exoplatform.ide.editor.client.api.Editor,
     *      int)
     */
    @Override
    public ContextInformation[] computeContextInformation(Editor viewer, int offset) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
     */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor#getErrorMessage()
     */
    @Override
    public String getErrorMessage() {
        return null;
    }

    private void init() {
        if(keyWords != null) {
            return;
        }

        RubyBundle bundle = GWT.create(RubyBundle.class);
        try {
            bundle.rubyKeyWords().getText(new ResourceCallback<TextResource>() {
                @Override
                public void onSuccess(TextResource resource) {
                    JSONValue parseLenient = JSONParser.parseLenient(resource.getText());
                    JSONTokenParser parser = new JSONTokenParser();
                    keyWords = parser.getTokens(parseLenient.isArray());
                    Collections.sort(keyWords, tokenComparator);
                }

                @Override
                public void onError(ResourceException e) {
                    Log.error(getClass(), e.getMessage());
                }
            });
        } catch (ResourceException e) {
            Log.error(getClass(), e.getMessage());
        }
    }

    /** Finds token at cursor position. */
    private static FindTokenResult getTriggeringString(JsonArray<com.google.collide.codemirror2.Token> tokens, int column) {
        FindTokenResult result = new FindTokenResult();

        // Number of tokens in line.
        final int size = tokens.size();

        // Sum of lengths of processed tokens.
        int colCount = 0;

        // Index of next token.
        int index = 0;

        while (index < size) {
            com.google.collide.codemirror2.Token token = tokens.get(index);
            colCount += token.getValue().length();
            index++;
            if (result.inToken == null) {
                if (colCount >= column) {
                    result.inToken = token;
                    result.cut = colCount - column;
                    return result;
                }
            }
        }
        return result;
    }

    private List<Token> getTokensFilteredByPrefix(String prefix) {
        List<Token> filteredTokens = new ArrayList<Token>();
        for (Token token : keyWords) {
            if (token.getName().startsWith(prefix.toLowerCase())) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }
}
