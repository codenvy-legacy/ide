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
package org.exoplatform.ide.shell.client;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 2, 2011 12:57:09 PM anya $
 */
public interface ConsoleWriter {
    /**
     * Print string to console.
     *
     * @param str
     */
    void print(String str);

    /**
     * Print string to console and move cursor on new line.
     *
     * @param str
     */
    void println(String str);

    /**
     * Print to console buffer(without add prompt on each call this method).
     *
     * @param str
     */
    void printToBuffer(String str);

    /** Refresh console. */
    void flush();

    /** Print console's prompt. */
    void printPrompt();

    /** Clear console. */
    void clearConsole();

    int getLength();
}
