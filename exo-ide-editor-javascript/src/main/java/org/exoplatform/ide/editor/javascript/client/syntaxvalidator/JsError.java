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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * JavaScript error that contains information about line number where error occurred and detailed text message.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: JsError.java Sep 18, 2012 12:36:09 PM azatsarynnyy $
 */
final class JsError extends JavaScriptObject {
    /** Default constructor. */
    protected JsError() {
    }

    /**
     * Returns the line number in source where the problem begins.
     *
     * @return the line number in source where the problem begins
     */
    public native int getLineNumber()/*-{
        return this.lineNumber;
    }-*/;

    /**
     * Returns a localized, human-readable message string which describes the problem.
     *
     * @return a localized, human-readable message string which describes the problem
     */
    public native String getMessage()/*-{
        return this.message;
    }-*/;
}
