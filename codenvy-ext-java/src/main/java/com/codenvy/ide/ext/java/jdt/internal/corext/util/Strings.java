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
package com.codenvy.ide.ext.java.jdt.internal.corext.util;

import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.core.formatter.IndentManipulation;

import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.DefaultLineTracker;
import com.codenvy.ide.text.LineTracker;
import com.codenvy.ide.text.Region;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Strings {

    /**
     * Returns that part of the indentation of <code>line</code> that makes up
     * a multiple of indentation units.
     *
     * @param line
     *         the line to scan
     * @param project
     *         the java project from which to get the formatter
     *         preferences, or <code>null</code> for global preferences
     * @return the indent part of <code>line</code>, but no odd spaces
     * @since 3.1
     */
    public static String getIndentString(String line) {
        return IndentManipulation.extractIndentString(line, CodeFormatterUtil.getTabWidth(),
                                                      CodeFormatterUtil.getIndentWidth());
    }

    /**
     * Change the indent of, possible multi-line, code range. The current indent is removed, a new indent added.
     * The first line of the code will not be changed. (It is considered to have no indent as it might start in
     * the middle of a line)
     *
     * @param code
     *         the code
     * @param codeIndentLevel
     *         level of indentation
     * @param project
     *         the java project from which to get the formatter
     *         preferences, or <code>null</code> for global preferences
     * @param newIndent
     *         new indent
     * @param lineDelim
     *         line delimiter
     * @return the changed code
     * @since 3.1
     */
    public static String changeIndent(String code, int codeIndentLevel, String newIndent, String lineDelim) {
        return IndentManipulation.changeIndent(code, codeIndentLevel, CodeFormatterUtil.getTabWidth(),
                                               CodeFormatterUtil.getIndentWidth(), newIndent, lineDelim);
    }

    /**
     * Change the indent of, possible muti-line, code range. The current indent is removed, a new indent added.
     * The first line of the code will not be changed. (It is considered to have no indent as it might start in
     * the middle of a line)
     *
     * @param code
     *         the code
     * @param codeIndentLevel
     *         indent level
     * @param tabWidth
     *         the size of one tab in space equivalents
     * @param indentWidth
     *         the size of the indent in space equivalents
     * @param newIndent
     *         new indent
     * @param lineDelim
     *         line delimiter
     * @return the changed code
     * @since 3.1
     */
    public static String changeIndent(String code, int codeIndentLevel, int tabWidth, int indentWidth, String newIndent,
                                      String lineDelim) {
        return IndentManipulation.changeIndent(code, codeIndentLevel, tabWidth, indentWidth, newIndent, lineDelim);
    }

    public static String trimIndentation(String source, boolean considerFirstLine) {
        return trimIndentation(source, CodeFormatterUtil.getTabWidth(), CodeFormatterUtil.getIndentWidth(),
                               considerFirstLine);
    }

    public static String trimIndentation(String source, int tabWidth, int indentWidth, boolean considerFirstLine) {
        try {
            LineTracker tracker = new DefaultLineTracker();
            tracker.set(source);
            int size = tracker.getNumberOfLines();
            if (size == 1)
                return source;
            String lines[] = new String[size];
            for (int i = 0; i < size; i++) {
                Region region = tracker.getLineInformation(i);
                int offset = region.getOffset();
                lines[i] = source.substring(offset, offset + region.getLength());
            }
            Strings.trimIndentation(lines, tabWidth, indentWidth, considerFirstLine);
            StringBuffer result = new StringBuffer();
            int last = size - 1;
            for (int i = 0; i < size; i++) {
                result.append(lines[i]);
                if (i < last)
                    result.append(tracker.getLineDelimiter(i));
            }
            return result.toString();
        } catch (BadLocationException e) {
            Assert.isTrue(false, "Can not happend"); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Removes the common number of indents from all lines. If a line
     * only consists out of white space it is ignored. If <code>
     * considerFirstLine</code> is false the first line will be ignored.
     *
     * @param lines
     *         the lines
     * @param tabWidth
     *         the size of one tab in space equivalents
     * @param indentWidth
     *         the size of the indent in space equivalents
     * @param considerFirstLine
     *         If <code> considerFirstLine</code> is false the first line will be ignored.
     * @since 3.1
     */
    public static void trimIndentation(String[] lines, int tabWidth, int indentWidth, boolean considerFirstLine) {
        String[] toDo = new String[lines.length];
        // find indentation common to all lines
        int minIndent = Integer.MAX_VALUE; // very large
        for (int i = considerFirstLine ? 0 : 1; i < lines.length; i++) {
            String line = lines[i];
            if (containsOnlyWhitespaces(line))
                continue;
            toDo[i] = line;
            int indent = computeIndentUnits(line, tabWidth, indentWidth);
            if (indent < minIndent) {
                minIndent = indent;
            }
        }

        if (minIndent > 0) {
            // remove this indent from all lines
            for (int i = considerFirstLine ? 0 : 1; i < toDo.length; i++) {
                String s = toDo[i];
                if (s != null)
                    lines[i] = trimIndent(s, minIndent, tabWidth, indentWidth);
                else {
                    String line = lines[i];
                    int indent = computeIndentUnits(line, tabWidth, indentWidth);
                    if (indent > minIndent)
                        lines[i] = trimIndent(line, minIndent, tabWidth, indentWidth);
                    else
                        lines[i] = trimLeadingTabsAndSpaces(line);
                }
            }
        }
    }

    /**
     * Removes leading tabs and spaces from the given string. If the string
     * doesn't contain any leading tabs or spaces then the string itself is
     * returned.
     *
     * @param line
     *         the line
     * @return the trimmed line
     */
    public static String trimLeadingTabsAndSpaces(String line) {
        int size = line.length();
        int start = size;
        for (int i = 0; i < size; i++) {
            char c = line.charAt(i);
            if (!IndentManipulation.isIndentChar(c)) {
                start = i;
                break;
            }
        }
        if (start == 0)
            return line;
        else if (start == size)
            return ""; //$NON-NLS-1$
        else
            return line.substring(start);
    }

    /**
     * Returns the indent of the given string in indentation units. Odd spaces
     * are not counted.
     *
     * @param line
     *         the text line
     * @param tabWidth
     *         the width of the '\t' character in space equivalents
     * @param indentWidth
     *         the width of one indentation unit in space equivalents
     * @return the indent of the given string in indentation units
     * @since 3.1
     */
    public static int computeIndentUnits(String line, int tabWidth, int indentWidth) {
        return IndentManipulation.measureIndentUnits(line, tabWidth, indentWidth);
    }

    /**
     * Removes the given number of indents from the line. Asserts that the given line
     * has the requested number of indents. If <code>indentsToRemove <= 0</code>
     * the line is returned.
     *
     * @param line
     *         the line
     * @param indentsToRemove
     *         the indents to remove
     * @param tabWidth
     *         the tab width
     * @param indentWidth
     *         the indent width
     * @return the trimmed line
     * @since 3.1
     */
    public static String trimIndent(String line, int indentsToRemove, int tabWidth, int indentWidth) {
        return IndentManipulation.trimIndent(line, indentsToRemove, tabWidth, indentWidth);
    }

    /**
     * Returns <code>true</code> if the given string only consists of
     * white spaces according to Java. If the string is empty, <code>true
     * </code> is returned.
     *
     * @param s
     *         the string to test
     * @return <code>true</code> if the string only consists of white
     *         spaces; otherwise <code>false</code> is returned
     * @see java.lang.Character#isWhitespace(char)
     */
    public static boolean containsOnlyWhitespaces(String s) {
        int size = s.length();
        for (int i = 0; i < size; i++) {
            if (!CharOperation.isWhitespace(s.charAt(i)))
                return false;
        }
        return true;
    }

    public static String trimTrailingTabsAndSpaces(String line) {
        int size = line.length();
        int end = size;
        for (int i = size - 1; i >= 0; i--) {
            char c = line.charAt(i);
            if (IndentManipulation.isIndentChar(c)) {
                end = i;
            } else {
                break;
            }
        }
        if (end == size)
            return line;
        else if (end == 0)
            return ""; //$NON-NLS-1$
        else
            return line.substring(0, end);
    }

    public static boolean startsWithIgnoreCase(String text, String prefix) {
        int textLength = text.length();
        int prefixLength = prefix.length();
        if (textLength < prefixLength)
            return false;
        for (int i = prefixLength - 1; i >= 0; i--) {
            if (Character.toLowerCase(prefix.charAt(i)) != Character.toLowerCase(text.charAt(i)))
                return false;
        }
        return true;
    }

}
