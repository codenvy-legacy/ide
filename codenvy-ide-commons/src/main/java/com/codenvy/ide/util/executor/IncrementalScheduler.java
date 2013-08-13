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

/** A scheduler that can incrementally run a task. */
public interface IncrementalScheduler {

    public interface Task {
        /** @return true if the task needs to continue */
        boolean run(int workAmount);
    }

    public void schedule(Task worker);

    public void cancel();

    public void pause();

    public void resume();

    public boolean isPaused();

    public boolean isBusy();

    public void teardown();
}
