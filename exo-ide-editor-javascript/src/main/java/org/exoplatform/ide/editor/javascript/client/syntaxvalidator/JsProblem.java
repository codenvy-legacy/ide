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
package org.exoplatform.ide.editor.javascript.client.syntaxvalidator;

import org.exoplatform.ide.editor.client.marking.Marker;

/**
 * Class represents JavaScript problem, as detected by the parser.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: JsProblem.java Sep 18, 2012 5:01:17 PM azatsarynnyy $
 */
public class JsProblem implements Marker {

    /** Native JavaScript error. */
    private JsError jsError;

    /** The start position of the problem. */
    private int start;

    /** The end position of the problem. */
    private int end;

    public JsProblem(JsError jsError, int start, int end) {
        this.jsError = jsError;
        this.start = start;
        this.end = end;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#getID() */
    @Override
    public int getID() {
        return 0;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#getMessage() */
    @Override
    public String getMessage() {
        // Original message has the follow form: "Line 45: Error message".
        // Cut the line number prefix.
        String errorMessage = jsError.getMessage();
        int colonIndex = errorMessage.indexOf(":");
        return errorMessage.substring(colonIndex + 2);
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#getLineNumber() */
    @Override
    public int getLineNumber() {
        return jsError.getLineNumber();
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#getEnd() */
    @Override
    public int getEnd() {
        return end;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#getStart() */
    @Override
    public int getStart() {
        return start;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#isError() */
    @Override
    public boolean isError() {
        return true;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#isWarning() */
    @Override
    public boolean isWarning() {
        return false;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#isBreakpoint() */
    @Override
    public boolean isBreakpoint() {
        return false;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Marker#isCurrentBreakPoint() */
    @Override
    public boolean isCurrentBreakPoint() {
        return false;
    }

}
