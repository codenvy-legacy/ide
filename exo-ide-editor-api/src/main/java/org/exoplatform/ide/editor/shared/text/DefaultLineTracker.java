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
package org.exoplatform.ide.editor.shared.text;

/**
 * Standard implementation of {@link org.eclipse.jface.text.ILineTracker}.
 * <p>
 * The line tracker considers the three common line delimiters which are '\n', '\r', '\r\n'.
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DefaultLineTracker extends AbstractLineTracker {

    /** The predefined delimiters of this tracker */
    public final static String[] DELIMITERS = {"\r", "\n", "\r\n"}; //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-2$

    /** A predefined delimiter information which is always reused as return value */
    private DelimiterInfo fDelimiterInfo = new DelimiterInfo();

    /** Creates a standard line tracker. */
    public DefaultLineTracker() {
    }

    /* @see org.eclipse.jface.text.ILineTracker#getLegalLineDelimiters() */
    public String[] getLegalLineDelimiters() {
        return new String[]{"\n"};
    }

    /*
     * @see org.eclipse.jface.text.AbstractLineTracker#nextDelimiterInfo(java.lang .String, int)
     */
    protected DelimiterInfo nextDelimiterInfo(String text, int offset) {

        char ch;
        int length = text.length();
        for (int i = offset; i < length; i++) {

            ch = text.charAt(i);
            if (ch == '\r') {

                if (i + 1 < length) {
                    if (text.charAt(i + 1) == '\n') {
                        fDelimiterInfo.delimiter = DELIMITERS[2];
                        fDelimiterInfo.delimiterIndex = i;
                        fDelimiterInfo.delimiterLength = 2;
                        return fDelimiterInfo;
                    }
                }

                fDelimiterInfo.delimiter = DELIMITERS[0];
                fDelimiterInfo.delimiterIndex = i;
                fDelimiterInfo.delimiterLength = 1;
                return fDelimiterInfo;

            } else if (ch == '\n') {

                fDelimiterInfo.delimiter = DELIMITERS[1];
                fDelimiterInfo.delimiterIndex = i;
                fDelimiterInfo.delimiterLength = 1;
                return fDelimiterInfo;
            }
        }

        return null;
    }
}
