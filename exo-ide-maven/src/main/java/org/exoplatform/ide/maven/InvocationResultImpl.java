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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.InvocationResult;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InvocationResultImpl.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class InvocationResultImpl implements InvocationResult {
    private final int                  exitCode;
    private final CommandLineException cle;
    private final File                 projectDirectory;
    private final ResultGetter         resultGetter;

    public InvocationResultImpl(int exitCode,
                                CommandLineException cle,
                                File projectDirectory,
                                ResultGetter resultGetter) {
        this.exitCode = exitCode;
        this.cle = cle;
        this.projectDirectory = projectDirectory;
        this.resultGetter = resultGetter;
    }

    /** @see org.apache.maven.shared.invoker.InvocationResult#getExitCode() */
    public int getExitCode() {
        return exitCode;
    }

    /** @see org.apache.maven.shared.invoker.InvocationResult#getExecutionException() */
    public CommandLineException getExecutionException() {
        return cle;
    }

    /**
     * Result of maven build.
     *
     * @return maven build result (typically artifact). May be <code>null</code> if build is failed. If build is
     *         successful (<code>exitCode == 0</code>) then may not be <code>null</code>.
     */
    public Result getResult() throws IOException {
        return 0 == exitCode ? resultGetter.getResult(projectDirectory) : null;
    }

}