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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.core.compiler.IProblem;
import org.exoplatform.ide.editor.client.marking.Marker;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:04:46 AM 34360 2009-07-22 23:58:59Z evgen $
 */
public class ProblemImpl implements Marker {

    private IProblem problem;

    /** @param problem */
    public ProblemImpl(IProblem problem) {
        super();
        this.problem = problem;
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#getID() */
    @Override
    public int getID() {
        return problem.getID();
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#getMessage() */
    @Override
    public String getMessage() {
        return problem.getMessage();
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#getLineNumber() */
    @Override
    public int getLineNumber() {
        if (problem.getSourceLineNumber() == 0)
            return 1;
        return problem.getSourceLineNumber();
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#getEnd() */
    @Override
    public int getEnd() {
        return problem.getSourceEnd();
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#getStart() */
    @Override
    public int getStart() {
        return problem.getSourceStart();
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#isError() */
    @Override
    public boolean isError() {
        return problem.isError();
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#isWarning() */
    @Override
    public boolean isWarning() {
        if (getID() == IProblem.Task)
            return false;
        return problem.isWarning();
    }

    public IProblem getOriginalProblem() {
        return problem;
    }

    /** @see org.exoplatform.ide.editor.problem.Problem#isBreakpoint() */
    @Override
    public boolean isBreakpoint() {
        return false;
    }

    @Override
    public boolean isCurrentBreakPoint() {
        return false;
    }

}
