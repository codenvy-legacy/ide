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
package org.exoplatform.ide.editor.ruby.client.contentassist;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.json.shared.JsonArray;
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

import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.codeassistant.JSONTokenParser;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.Metaclass;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.MethodInfo;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.ModuleMetaclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                                  public int compare(Token t1, Token t2) {
                                                      if (t1.getType() == t2.getType()) {
                                                          return t1.getName().compareTo(t2.getName());
                                                      }

                                                      if ((t1.getType() == TokenType.PARAMETER && t2.getType() == TokenType.LOCAL_VARIABLE)
                                                          || (t1.getType() == TokenType.LOCAL_VARIABLE && t2.getType() == TokenType.PARAMETER)) {
                                                          return t1.getName().compareTo(t2.getName());
                                                      }

                                                      if (t2.getType() == TokenType.PARAMETER) {
                                                          return 1;
                                                      }
                                                      if (t1.getType() == TokenType.PARAMETER) {
                                                          return -1;
                                                      }

                                                      if (t2.getType() == TokenType.LOCAL_VARIABLE) {
                                                          return 1;
                                                      }
                                                      if (t1.getType() == TokenType.LOCAL_VARIABLE) {
                                                          return -1;
                                                      }

                                                      if (t1.getType() == TokenType.CLASS_VARIABLE) {
                                                          return -1;
                                                      }

                                                      if (t2.getType() == TokenType.CLASS_VARIABLE) {
                                                          return 1;
                                                      }

                                                      if (t1.getType() == TokenType.INSTANCE_VARIABLE) {
                                                          return -1;
                                                      }

                                                      if (t2.getType() == TokenType.INSTANCE_VARIABLE) {
                                                          return 1;
                                                      }

                                                      if (t1.getType() == TokenType.GLOBAL_VARIABLE) {
                                                          return -1;
                                                      }

                                                      if (t2.getType() == TokenType.GLOBAL_VARIABLE) {
                                                          return 1;
                                                      }

                                                      if (t1.getType() == TokenType.METHOD || t1.getType() == TokenType.PROPERTY) {
                                                          return -1;
                                                      }

                                                      if (t2.getType() == TokenType.METHOD || t2.getType() == TokenType.PROPERTY) {
                                                          return 1;
                                                      }

                                                      return t1.getName().compareTo(t2.getName());
                                                  }
                                              };

    /** Bean that holds {@link #findToken} results. */
    private static class FindTokenResult {
        com.google.collide.codemirror2.Token previousToken;

        /** Token that "covers" the cursor. */
        com.google.collide.codemirror2.Token inToken;

        /** Number of characters between "inToken" start and the cursor position. */
        int                                  cut;
    }

    /** List of Ruby language keywords. */
    private static List<Token> keyWords;

    private static List<Token> metaclasses;

    private static List<Token> objectMethods;

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

        FindTokenResult findTokenResult = findToken(tokens, column);
        String prefix = findTokenResult.inToken.getValue();
        String previousTokenValue = findTokenResult.previousToken == null ? "" : findTokenResult.previousToken.getValue();

        switch (findTokenResult.inToken.getType()) {
            case WHITESPACE:
            case NULL:
                prefix = "";
            default:
                prefix = prefix.substring(0, prefix.length() - findTokenResult.cut);
        }

        List<Token> filteredTokens = new ArrayList<Token>();
        if (prefix.trim().equals(".") || previousTokenValue.equals(".")) {
            if (prefix.trim().equals(".")) {
                prefix = "";
            }
            filteredTokens.addAll(getTokensFilteredByPrefix(objectMethods, prefix));
        }
        else {
            filteredTokens.addAll(getTokensFilteredByPrefix(keyWords, prefix));
            filteredTokens.addAll(getTokensFilteredByPrefix(metaclasses, prefix));
        }

        CompletionProposal[] proposals = new CompletionProposal[filteredTokens.size()];
        int i = 0;
        for (Token token : filteredTokens) {
            String additionalString = "";
            String proposalLabel = token.getName();
            switch (token.getType()) {
                case METHOD:
                    additionalString = token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue();
                    proposalLabel += token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
                    break;
                case CLASS:
                    additionalString = token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
                    break;
                default:
                    additionalString = "";
                    break;
            }
            proposals[i++] = new RubyProposal(proposalLabel, additionalString, prefix, offset, token);
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
        if (keyWords == null) {
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

        if (metaclasses == null) {
            metaclasses = new ArrayList<Token>();
            for (Object className : BuiltinMethodsDatabase.metaclasses.keySet()) {
                metaclasses.add(getClassToken((Metaclass)BuiltinMethodsDatabase.metaclasses.get(className)));
            }
            Collections.sort(metaclasses, tokenComparator);
        }

        if (objectMethods == null) {
            Map<String, Token> objectMethodsMap = new HashMap<String, Token>();
            Metaclass metaclass = BuiltinMethodsDatabase.get("Object");
            objectMethodsMap.putAll(getTokenFromModules(metaclass.getIncludedModules()));
            convertMethodToToken(metaclass.getMetaClass().getSuperClass().getName(), metaclass.getMetaClass().getSuperClass().getMethods(),
                                 objectMethodsMap);
            objectMethods = new ArrayList<Token>();
            for (Token token : objectMethodsMap.values()) {
                objectMethods.add(token);
            }
            Collections.sort(objectMethods, tokenComparator);
        }
    }

    /** Finds token at cursor position. */
    private static FindTokenResult findToken(JsonArray<com.google.collide.codemirror2.Token> tokens, int column) {
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
            if (colCount >= column) {
                if (index > 0) {
                    result.previousToken = tokens.get(index - 1);
                }
                result.inToken = token;
                result.cut = colCount - column;
                return result;
            }
            index++;
        }
        return result;
    }

    private List<Token> getTokensFilteredByPrefix(List<Token> tokenList, String prefix) {
        List<Token> filteredTokens = new ArrayList<Token>();
        for (Token token : tokenList) {
            if (token.getName().startsWith(prefix)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    /**
     * Convert {@link Metaclass} to {@link Token} with type CLASS
     * 
     * @param metaclass
     * @return token
     */
    private Token getClassToken(Metaclass metaclass) {
        Token clazz = new TokenImpl(metaclass.getName(), TokenType.CLASS);
        clazz.setProperty(TokenProperties.FQN, new StringProperty(metaclass.getName() + ".rb"));
        return clazz;
    }

    /**
     * Get {@link Token}s from {@link ModuleMetaclass} array
     * 
     * @param includedModules
     * @return Map of converted tokens
     */
    protected Map<String, Token> getTokenFromModules(ModuleMetaclass[] includedModules) {
        Map<String, Token> tokenMap = new HashMap<String, Token>();
        for (ModuleMetaclass module : includedModules) {
            convertMethodToToken(module.getName(), module.getMethods(), tokenMap);
            tokenMap.putAll(getTokenFromModules(module.getIncludedModules()));
            tokenMap.putAll(getTokenFromModules(module.getMetaClass().getSuperClass().getIncludedModules()));
            convertMethodToToken(module.getMetaClass().getSuperClass().getName(), module.getMetaClass().getSuperClass()
                                                                                        .getMethods(), tokenMap);
            convertMethodToToken(module.getMetaClass().getName(), module.getMetaClass().getMethods(), tokenMap);
        }

        return tokenMap;
    }

    /**
     * Convert {@link MethodInfo} array to {@link Token}s and put its to tokens map
     * 
     * @param container Name of Class
     * @param methods array
     * @param tokens Map that store converted tokens
     */
    private void convertMethodToToken(String container, MethodInfo[] methods, Map<String, Token> tokens) {
        for (MethodInfo method : methods) {
            Token m = new TokenImpl(method.getName(), TokenType.METHOD);
            String param = getParameters(method.getArity());
            m.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(container));
            m.setProperty(TokenProperties.MODIFIERS, new NumericProperty(method.getFlags()));
            m.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(param));
            m.setProperty(TokenProperties.CODE, new StringProperty(method.getName() + param));

            // to avoid overriding methods from Kernel
            if (tokens.containsKey(m.getName() + param)
                && tokens.get(m.getName() + param).getProperty(TokenProperties.DECLARING_CLASS).equals("Kernel"))
                continue;

            tokens.put(m.getName() + param, m);
        }
    }

    /**
     * @param arity
     * @return
     */
    private String getParameters(int arity) {
        String par = "(";

        if (arity < 0) {
            par += "*args";
        } else if (arity > 0) {
            for (int i = 0; i <= arity; i++) {
                par += "arg" + (i + 1) + ", ";
            }
            if (par.endsWith(", ")) {
                par = par.substring(0, par.lastIndexOf(", "));
            }
        }

        par += ")";
        return par;
    }

}
