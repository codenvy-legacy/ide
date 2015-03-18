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
package org.eclipse.che.ide.api.controlflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Invokes the list of {@link Task}s in parallel or in series.
 *
 * @author Artem Zatsarynnyy
 */
public class TaskFlow {

    private final List<Task> tasks;
    private       int        pos;

    /**
     * Creates new flow for running the given {@code tasks}.
     *
     * @param tasks
     *         list containing tasks to run
     */
    public TaskFlow(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Run the tasks in parallel, without waiting until the previous task has completed.
     *
     * @param callback
     *         optional callback to call once all the tasks have completed
     *         or if any tasks in the series pass an error to its callback
     */
    public void runInParallel(final FlowCallback callback) {
    }

    /**
     * Run the tasks in series, each one running once the previous task has completed.
     *
     * @param callback
     *         optional callback to call once all the tasks have completed
     *         or if any tasks in the series pass an error to its callback
     */
    public void runInSeries(final FlowCallback callback) {
        final Task.TaskCallback internalCallback = new Task.TaskCallback() {
            @Override
            public void onPerformed() {
                if (pos < tasks.size()) {
                    runNextTask(this);
                } else {
                    pos = 0;
                    callback.onDone();
                }
            }

            @Override
            public void onError(Throwable exception) {
                pos = 0;
                callback.onError(exception);
            }
        };

        runNextTask(internalCallback);
    }

    private void runNextTask(Task.TaskCallback callback) {
        final Task nextTask = tasks.get(pos++);
        if (nextTask != null) {
            nextTask.run(callback);
        }
    }


    /** Flow completion callback. */
    public interface FlowCallback {

        /** Called when all the tasks have completed. */
        void onDone();

        /** Called when any tasks pass an error to its callback. */
        void onError(Throwable exception);
    }
}
