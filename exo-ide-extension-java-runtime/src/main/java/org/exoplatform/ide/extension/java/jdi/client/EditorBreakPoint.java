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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.editor.java.Breakpoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:39:59 AM Mar 28, 2012 evgen $
 */
public class EditorBreakPoint extends Breakpoint {

    private String message;

    private final BreakPoint breakPoint;

    /**
     * @param lineNumber
     * @param message
     */

    public EditorBreakPoint(BreakPoint breakPoint, String message) {
        super(Type.BREAKPOINT, breakPoint.getLocation().getLineNumber(), message);
        this.breakPoint = breakPoint;

    }

    /** @see org.exoplatform.ide.editor.problem.Problem#getLineNumber() */
    @Override
    public int getLineNumber() {
        return breakPoint.getLocation().getLineNumber();
    }

    /** @return the breakPoint */
    public BreakPoint getBreakPoint() {
        return breakPoint;
    }

}
