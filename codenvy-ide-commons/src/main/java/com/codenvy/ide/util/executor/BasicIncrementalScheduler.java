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

package com.codenvy.ide.util.executor;

import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.codenvy.ide.util.executor.UserActivityManager.UserActivityListener;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Duration;


/** A scheduler that can incrementally run a task. */
public class BasicIncrementalScheduler implements IncrementalScheduler {

    private final AsyncRunner runner = new AsyncRunner() {
        @Override
        public void run() {
            if (isPaused) {
                return;
            }

            try {
                double start = Duration.currentTimeMillis();
                boolean keepRunning = worker.run(currentWorkAmount);
                updateWorkAmount(Duration.currentTimeMillis() - start);
                if (keepRunning) {
                    schedule();
                } else {
                    clearWorker();
                }
            } catch (Throwable t) {
                Log.error(getClass(), "Could not run worker", t);
            }
        }
    };

    private Task worker;

    private boolean isPaused;

    private int currentWorkAmount;

    private final int targetExecutionMs;

    private int completedWorkAmount;

    private double totalTimeTaken;

    public Remover userActivityRemover;

    public BasicIncrementalScheduler(int targetExecutionMs, int workGuess) {
        this.targetExecutionMs = targetExecutionMs;
        currentWorkAmount = workGuess;
    }

    public BasicIncrementalScheduler(UserActivityManager userActivityManager, int targetExecutionMs, int workGuess) {
        this(targetExecutionMs, workGuess);

        userActivityRemover = userActivityManager.getUserActivityListenerRegistrar().add(new UserActivityListener() {
            @Override
            public void onUserActive() {
                pause();
            }

            @Override
            public void onUserIdle() {
                resume();
            }
        });
    }

    @Override
    public void schedule(Task worker) {
        cancel();
        this.worker = worker;

        if (!isPaused) {
            runner.run();
        }
    }

    @Override
    public void cancel() {
        runner.cancel();
        worker = null;
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    /** Schedules the worker to resume.  This will run asychronously. */
    @Override
    public void resume() {
        isPaused = false;

        if (worker != null) {
            launch();
        }
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void teardown() {
        cancel();

        if (userActivityRemover != null) {
            userActivityRemover.remove();
        }
    }

    /**
     * Update the currentWorkAmount based upon the workTime it took to run the
     * last command so running the worker will take ~targetExecutionMs.
     *
     * @param workTime
     *         ms the last run took
     */
    private void updateWorkAmount(double workTime) {
        if (workTime <= 0) {
            currentWorkAmount *= 2;
        } else {
            totalTimeTaken += workTime;
            completedWorkAmount += currentWorkAmount;
            currentWorkAmount = (int)Math.ceil(targetExecutionMs * completedWorkAmount / totalTimeTaken);
        }
    }

    private void clearWorker() {
        worker = null;
    }

    @Override
    public boolean isBusy() {
        return worker != null;
    }

    /** Queues the worker launch. */
    private void launch() {
        runner.schedule();
    }
}
