/*
 * Copyright (C) 2012 eXo Platform SAS.
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
