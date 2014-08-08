/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Doug Satchwell <doug.satchwell@ymail.com> - [implementation] Performance issue with jface text WordRule - http://bugs.eclipse
 *     .org/277299
 *******************************************************************************/
package com.codenvy.ide.api.text.rules;

import com.codenvy.ide.runtime.Assert;

import java.util.HashMap;
import java.util.Map;


/**
 * An implementation of {@link Rule} capable of detecting words. A word rule also allows to
 * associate a token to a word. That is, not only can the rule be used to provide tokens for exact
 * matches, but also for the generalized notion of a word in the context in which it is used. A word
 * rule uses a word detector to determine what a word is.
 *
 * @see WordDetector
 */
public class WordRule implements Rule {

    /** Internal setting for the un-initialized column constraint. */
    protected static final int UNDEFINED = -1;
    /** The word detector used by this rule. */
    protected WordDetector fDetector;
    /** The default token to be returned on success and if nothing else has been specified. */
    protected Token        fDefaultToken;
    /** The column constraint. */
    protected int          fColumn     = UNDEFINED;
    /** The table of predefined words and token for this rule. */
    protected Map          fWords      = new HashMap();
    /** Buffer used for pattern detection. */
    private   StringBuffer fBuffer     = new StringBuffer();
    /**
     * Tells whether this rule is case sensitive.
     *
     * @since 3.3
     */
    private   boolean      fIgnoreCase = false;


    /**
     * Creates a rule which, with the help of an word detector, will return the token
     * associated with the detected word. If no token has been associated, the scanner
     * will be rolled back and an undefined token will be returned in order to allow
     * any subsequent rules to analyze the characters.
     *
     * @param detector
     *         the word detector to be used by this rule, may not be <code>null</code>
     * @see #addWord(String, Token)
     */
    public WordRule(WordDetector detector) {
        this(detector, TokenImpl.UNDEFINED, false);
    }

    /**
     * Creates a rule which, with the help of a word detector, will return the token
     * associated with the detected word. If no token has been associated, the
     * specified default token will be returned.
     *
     * @param detector
     *         the word detector to be used by this rule, may not be <code>null</code>
     * @param defaultToken
     *         the default token to be returned on success
     *         if nothing else is specified, may not be <code>null</code>
     * @see #addWord(String, Token)
     */
    public WordRule(WordDetector detector, Token defaultToken) {
        this(detector, defaultToken, false);
    }

    /**
     * Creates a rule which, with the help of a word detector, will return the token
     * associated with the detected word. If no token has been associated, the
     * specified default token will be returned.
     *
     * @param detector
     *         the word detector to be used by this rule, may not be <code>null</code>
     * @param defaultToken
     *         the default token to be returned on success
     *         if nothing else is specified, may not be <code>null</code>
     * @param ignoreCase
     *         the case sensitivity associated with this rule
     * @see #addWord(String, Token)
     */
    public WordRule(WordDetector detector, Token defaultToken, boolean ignoreCase) {
        Assert.isNotNull(detector);
        Assert.isNotNull(defaultToken);

        fDetector = detector;
        fDefaultToken = defaultToken;
        fIgnoreCase = ignoreCase;
    }

    /**
     * Adds a word and the token to be returned if it is detected.
     *
     * @param word
     *         the word this rule will search for, may not be <code>null</code>
     * @param token
     *         the token to be returned if the word has been found, may not be <code>null</code>
     */
    public void addWord(String word, Token token) {
        Assert.isNotNull(word);
        Assert.isNotNull(token);

        // If case-insensitive, convert to lower case before adding to the map
        if (fIgnoreCase)
            word = word.toLowerCase();
        fWords.put(word, token);
    }

    /**
     * Sets a column constraint for this rule. If set, the rule's token
     * will only be returned if the pattern is detected starting at the
     * specified column. If the column is smaller then 0, the column
     * constraint is considered removed.
     *
     * @param column
     *         the column in which the pattern starts
     */
    public void setColumnConstraint(int column) {
        if (column < 0)
            column = UNDEFINED;
        fColumn = column;
    }

    /*
     * @see IRule#evaluate(ICharacterScanner)
     */
    public Token evaluate(CharacterScanner scanner) {
        int c = scanner.read();
        if (c != CharacterScanner.EOF && fDetector.isWordStart((char)c)) {
            if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {

                fBuffer.setLength(0);
                do {
                    fBuffer.append((char)c);
                    c = scanner.read();
                } while (c != CharacterScanner.EOF && fDetector.isWordPart((char)c));
                scanner.unread();

                String buffer = fBuffer.toString();
                // If case-insensitive, convert to lower case before accessing the map
                if (fIgnoreCase)
                    buffer = buffer.toLowerCase();

                Token token = (Token)fWords.get(buffer);

                if (token != null)
                    return token;

                if (fDefaultToken.isUndefined())
                    unreadBuffer(scanner);

                return fDefaultToken;
            }
        }

        scanner.unread();
        return TokenImpl.UNDEFINED;
    }

    /**
     * Returns the characters in the buffer to the scanner.
     *
     * @param scanner
     *         the scanner to be used
     */
    protected void unreadBuffer(CharacterScanner scanner) {
        for (int i = fBuffer.length() - 1; i >= 0; i--)
            scanner.unread();
    }

}
