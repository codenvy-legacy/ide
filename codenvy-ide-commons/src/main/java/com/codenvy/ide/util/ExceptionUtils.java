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

package com.codenvy.ide.util;

/** Utility class for common Exception related operations. */
public class ExceptionUtils {
    public static final int MAX_CAUSE = 10;

    public static String getStackTraceAsString(Throwable e) {
        return getThrowableAsString(e, "\n", "\t");
    }

    public static String getThrowableAsString(Throwable e, String newline, String indent) {
        if (e == null) {
            return "";
        }
        // For each cause, print the requested number of entries of its stack
        // trace, being careful to avoid getting stuck in an infinite loop.
        StringBuffer s = new StringBuffer(newline);
        Throwable currentCause = e;
        String causedBy = "";

        int causeCounter = 0;
        for (; causeCounter < MAX_CAUSE && currentCause != null; causeCounter++) {
            s.append(causedBy);
            causedBy = newline + "Caused by: "; // after 1st, all say "caused by"
            s.append(currentCause.getClass().getName());
            s.append(": ");
            s.append(currentCause.getMessage());
            StackTraceElement[] stackElems = currentCause.getStackTrace();
            if (stackElems != null) {
                for (int i = 0; i < stackElems.length; ++i) {
                    s.append(newline);
                    s.append(indent);
                    s.append("at ");
                    s.append(stackElems[i].toString());
                }
            }

            currentCause = currentCause.getCause();
        }
        if (causeCounter >= MAX_CAUSE) {
            s.append(newline);
            s.append(newline);
            s.append("Exceeded the maximum number of causes.");
        }

        return s.toString();
    }
}
