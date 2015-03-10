/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.debug;

import java.util.List;

import org.eclipse.che.ide.collections.Array;

/** interface for breakpoints managers. */
public interface BreakpointManager {

    /**
     * Change state of the breakpoint in active editor at the specified line.
     * @param lineNumber active editor's line number where breakpoint is
     */
    void changeBreakPointState(int lineNumber);

    /** Removes all breakpoints. */
    void removeAllBreakpoints();

    /**
     * Tells if there is a breakpoint on the given line of the active editor.
     * @param lineNumber the line number
     * @return true iff there is a breakpoint on this line
     */
    boolean breakpointExists(int lineNumber);

    /**
     * Tells if there is a breakpoint on the given line of the active editor.
     * @param lineNumber the line number
     * @return true iff there is a breakpoint on this line
     * @deprecated use {@link #breakpointExists(int)}
     */
    @Deprecated
    boolean isBreakPointExist(int lineNumber);

    /**
     * Returns all breakpoints.
     * @return all breakpoints
     */
    List<Breakpoint> getBreakpointList();

    /**
     * Returns all breakpoints.
     * @return all breakpoints
     * @deprecated use {@link #getBreakpointList()}
     */
    @Deprecated
    Array<Breakpoint> getBreakpoints();

    void markCurrentBreakpoint(int lineNumber);

    void unmarkCurrentBreakpoint();

    /**
     * Check whether line has the current breakpoint.
     *
     * @param lineNumber line number
     * @return <code>true</code> if the line is marked, and <code>false</code> otherwise
     */
    boolean isCurrentBreakpoint(int lineNumber);

    /**
     * @see #isCurrentBreakpoint(int)
     */
    @Deprecated
    boolean isMarkedLine(int lineNumber);
}
