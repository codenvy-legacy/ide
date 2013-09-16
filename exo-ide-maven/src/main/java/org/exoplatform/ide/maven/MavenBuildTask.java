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

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Maven build task.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MavenBuildTask {
    private final String                       id;
    private final Future<InvocationResultImpl> f;
    private final File                         projectDirectory;
    private final TaskLogger                   logger;

    public MavenBuildTask(String id, Future<InvocationResultImpl> f, File projectDirectory, TaskLogger logger) {
        this.id = id;
        this.f = f;
        this.projectDirectory = projectDirectory;
        this.logger = logger;
    }

    /**
     * Get build unique ID.
     *
     * @return build ID
     */
    public String getId() {
        return id;
    }

    /**
     * Get build logger.
     *
     * @return build logger
     */
    public TaskLogger getLogger() {
        return logger;
    }

    /**
     * Check is build done or not. Note build may be successful or failed.
     *
     * @return <code>true</code> if build is done and <code>false</code> otherwise
     */
    public boolean isDone() {
        return f.isDone();
    }

    /** Cancel maven build. */
    public void cancel() {
        f.cancel(true);
    }

    /**
     * Get result of maven build.
     *
     * @return result of maven build. <b>NOTE</b> If build is not finished yet this method returns <code>null</code>
     * @throws MavenInvocationException
     *         if maven task cannot be run because to incorrect input parameters
     */
    public InvocationResultImpl getInvocationResult() throws MavenInvocationException {
        if (f.isDone()) {
            try {
                return f.get();
            } catch (InterruptedException e) {
                // Should not happen since we checked is task done or not.
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw (MavenInvocationException)cause;
            } catch (CancellationException ce) {
                throw new MavenInvocationException("Job " + id + " was cancelled. ");
            }
        }
        return null;
    }

    /**
     * Get the maven project directory.
     *
     * @return the maven project directory
     */
    public File getProjectDirectory() {
        return projectDirectory;
    }
}
