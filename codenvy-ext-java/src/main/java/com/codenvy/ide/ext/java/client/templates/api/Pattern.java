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

package com.codenvy.ide.ext.java.client.templates.api;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * GWT adaptation of {@link java.util.regex.Pattern}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Pattern {

    /** Declares that regular expressions should be matched across line borders. */
    public final static int MULTILINE = 1;

    /** Declares that characters are matched reglardless of case. */
    public final static int CASE_INSENSITIVE = 2;

    private static RegExp createExpression(String pattern, int flags) {
        String sFlags = "";
        if ((flags & MULTILINE) != 0)
            sFlags += "m";
        if ((flags & CASE_INSENSITIVE) != 0)
            sFlags += "i";
        sFlags += "g";
        return RegExp.compile(pattern, sFlags);
    }

    private RegExp regExp;

    private final String pattern;

    private final int flags;

    public static Pattern compile(String pattern) {
        return new Pattern(pattern);
    }

    public static Pattern compile(String pattern, int flags) {
        return new Pattern(pattern, flags);
    }

    /**
     * Escape a provided string so that it will be interpreted as a literal in regular expressions. The current implementation does
     * escape each character even if not neccessary, generating verbose literals.
     *
     * @param input
     * @return
     */
    public static String quote(String input) {
        String output = "";
        for (int i = 0; i < input.length(); i++) {
            output += "\\" + input.charAt(i);
        }
        return output;
    }

    /**
     * Class constructor
     *
     * @param pattern
     *         Regular expression
     */
    public Pattern(String pattern) {
        this(pattern, 0);
    }

    /**
     * Class constructor
     *
     * @param pattern
     *         Regular expression
     * @param flags
     */
    public Pattern(String pattern, int flags) {
        this.pattern = pattern;
        this.flags = flags;
        regExp = createExpression(pattern, flags);
    }

    /**
     * Create a matcher for this pattern and a given input character sequence
     *
     * @param cs
     *         The input character sequence
     * @return A new matcher
     */
    public Matcher matcher(CharSequence cs) {
        // recreate regExp
        regExp = createExpression(pattern, flags);
        return new Matcher(this, cs);
    }

    private void _match(String text, List<String> matches) {
        MatchResult result = regExp.exec(text);
        if (result == null)
            return;
        for (int i = 0; i < result.getGroupCount(); i++)
            matches.add(result.getGroup(i));
    }

    ;

    private void _split(String input, List<String> results) {
        SplitResult parts = regExp.split(input);
        for (int i = 0; i < parts.length(); i++)
            results.add(parts.get(i));
    }

    ;

    /**
     * Split an input string by the pattern's regular expression
     *
     * @param input
     * @return Array of strings
     */
    public String[] split(String input) {
        List<String> results = new ArrayList<String>();
        _split(input, results);
        String[] parts = new String[results.size()];
        for (int i = 0; i < results.size(); i++)
            parts[i] = (String)results.get(i);
        return parts;
    }

    /**
     * This method is borrowed from the JavaScript RegExp object. It parses a string and returns as an array any assignments to
     * parenthesis groups in the pattern's regular expression
     *
     * @param text
     * @return Array of strings following java's Pattern convention for groups: Group 0 is the entire input string and the
     *         remaining groups are the matched parenthesis. In case nothing was matched an empty array is returned.
     */
    public String[] match(String text) {
        List<String> matches = new ArrayList<String>();
        _match(text, matches);
        String arr[] = new String[matches.size()];
        for (int i = 0; i < matches.size(); i++)
            arr[i] = matches.get(i);
        return arr;
    }

    /**
     * Determines wether a provided text matches the regular expression
     *
     * @param text
     * @return
     */
    public MatchResult matches(String text) {
        return regExp.exec(text);
    }
}