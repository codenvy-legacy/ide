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

import com.google.gwt.core.client.Scheduler;

/** Executor of a cancellable scheduled command. */
public abstract class ScheduledCommandExecutor {

    private boolean scheduled;
    private boolean cancelled;

    private final Scheduler.ScheduledCommand scheduledCommand = new Scheduler.ScheduledCommand() {

        @Override
        public void execute() {
            scheduled = false;

            if (cancelled) {
                return;
            }

            ScheduledCommandExecutor.this.execute();
        }
    };

    protected abstract void execute();

    public void scheduleFinally() {
        cancelled = false;

        if (!scheduled) {
            scheduled = true;
            Scheduler.get().scheduleFinally(scheduledCommand);
        }
    }

    public void scheduleDeferred() {
        cancelled = false;

        if (!scheduled) {
            scheduled = true;
            Scheduler.get().scheduleDeferred(scheduledCommand);
        }
    }

    public void cancel() {
        cancelled = true;
    }
}
