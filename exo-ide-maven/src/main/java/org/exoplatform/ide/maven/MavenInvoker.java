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

import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamPumper;

import java.util.LinkedList;
import java.util.Queue;

import static org.codehaus.plexus.util.cli.CommandLineUtils.isAlive;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MavenInvoker.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class MavenInvoker extends DefaultInvoker {
    private final Queue<Runnable> preBuildTasks;
    private final Queue<Runnable> postBuildTasks;

    private       long         timeout;
    private final ResultGetter resultGetter;

    public MavenInvoker(ResultGetter resultGetter) {
        if (resultGetter == null) {
            throw new IllegalArgumentException("ResultGetter may not be null. ");
        }
        this.resultGetter = resultGetter;
        this.preBuildTasks = new LinkedList<Runnable>();
        this.postBuildTasks = new LinkedList<Runnable>();
    }

    /** @see org.apache.maven.shared.invoker.DefaultInvoker#execute(org.apache.maven.shared.invoker.InvocationRequest) */
    @Override
    public InvocationResultImpl execute(InvocationRequest request) throws MavenInvocationException {
        MavenCommandLineBuilder clBuilder = new MavenCommandLineBuilder();
        if (getLogger() != null) {
            clBuilder.setLogger(getLogger());
        }
        if (getLocalRepositoryDirectory() != null) {
            clBuilder.setLocalRepositoryDirectory(getLocalRepositoryDirectory());
        }
        if (getMavenHome() != null) {
            clBuilder.setMavenHome(getMavenHome());
        }
        if (getWorkingDirectory() != null) {
            clBuilder.setWorkingDirectory(getWorkingDirectory());
        }

        Commandline cl;
        try {
            cl = clBuilder.build(request);
        } catch (CommandLineConfigurationException e) {
            throw new MavenInvocationException("Error configuring command-line. Reason: " + e.getMessage(), e);
        }

        InvocationOutputHandler out = request.getOutputHandler(null);
        InvocationOutputHandler err = request.getErrorHandler(null);

        while (!preBuildTasks.isEmpty()) {
            preBuildTasks.poll().run();
        }

        int exitCode = -1;
        CommandLineException cle = null;
        try {
            exitCode = executeCommandLine(cl, out, err);
        } catch (CommandLineException e) {
            cle = e;
        }

        while (!postBuildTasks.isEmpty()) {
            postBuildTasks.poll().run();
        }

        return new InvocationResultImpl(exitCode, cle, request.getBaseDirectory(), resultGetter);
    }

    private int executeCommandLine(Commandline cl, StreamConsumer out, StreamConsumer err) throws CommandLineException {
        Process process = cl.execute();

        Watcher watcher = null;
        if (timeout > 0) {
            watcher = new Watcher(timeout);
            watcher.start(process);
        }

        StreamPumper outPipe = new StreamPumper(process.getInputStream(), out);
        StreamPumper errPipe = new StreamPumper(process.getErrorStream(), err);

        int exitCode = -1;

        try {
            outPipe.start();
            errPipe.start();

            try {
                exitCode = process.waitFor();
                synchronized (outPipe) {
                    while (!outPipe.isDone()) {
                        outPipe.wait();
                    }
                }

                synchronized (errPipe) {
                    while (!errPipe.isDone()) {
                        errPipe.wait();
                    }
                }

            } catch (InterruptedException e) {
                Thread.interrupted();
                kill(process);
            }
        } finally {
            outPipe.close();
            errPipe.close();

            if (out instanceof TaskLogger) {
                ((TaskLogger)out).close();
            }

            if (err instanceof TaskLogger) {
                ((TaskLogger)err).close();
            }

            if (watcher != null) {
                watcher.stop();
            }
        }

        return exitCode;
    }

    /**
     * Add task that should be invoked before start the maven build. All tasks should be added before call method {@link
     * #execute(org.apache.maven.shared.invoker.InvocationRequest)}.
     *
     * @param task
     *         the pre build task
     * @return this instance
     */
    public MavenInvoker addPreBuildTask(Runnable task) {
        preBuildTasks.add(task);
        return this;
    }

    /**
     * Add task that should be invoked after the maven build. All tasks should be added before call method {@link
     * #execute(org.apache.maven.shared.invoker.InvocationRequest)}.
     *
     * @param task
     *         the post build task
     * @return this instance
     */
    public MavenInvoker addPostBuildTask(Runnable task) {
        postBuildTasks.add(task);
        return this;
    }

    /**
     * Set build timeout in milliseconds.  It should be setup before call method {@link
     * #execute(org.apache.maven.shared.invoker.InvocationRequest)}.
     *
     * @param timeout
     *         the timeout in milliseconds
     * @return this instance
     */
    public MavenInvoker setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    private static void kill(Process process) {
        if (isAlive(process)) {
            process.destroy();
            try {
                process.waitFor(); // wait for process death
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
    }

    /**
     * Maven build watcher. It controls the time of build and if time if greater than timeout
     * (see {@link MavenInvoker#setTimeout(long)}) Watcher terminate such build. Watcher should not be used for
     * terminate
     * tasks that runs separate java processes, e.g. <code>mvn jetty:run</code>. Process that runs Jetty server may not
     * be terminated
     * by this Watcher. Such process must be terminated by correspond command, e.g. <code>mvn jetty:stop</code>
     */
    private static final class Watcher implements Runnable {
        private final long timeout;

        private boolean watch;
        private Process process;

        private Watcher(long timeout) {
            this.timeout = timeout;
        }

        public synchronized void run() {
            final long end = System.currentTimeMillis() + timeout;
            long now;
            while (watch && (end > (now = System.currentTimeMillis()))) {
                try {
                    wait(end - now);
                } catch (InterruptedException ignored) {
                }
            }
            if (watch) // If Watcher not stopped but timeout reached.
            {
                kill(process);
            }
        }

        public synchronized void start(Process process) {
            this.process = process;
            this.watch = true;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        public synchronized void stop() {
            watch = false;
            notify();
        }
    }
}
