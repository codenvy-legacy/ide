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

/**
 * Defines the requirements for task that may be run by {@link TaskFlow}.
 *
 * @author Artem Zatsarynnyy
 */
public interface Task {

    /**
     * Provides the way to run this task.
     *
     * @param callback
     *         callback that should be called when task completed
     */
    void run(TaskCallback callback);


    /** Task completion callback. */
    interface TaskCallback {

        /** Called when task has completed successfully. */
        void onPerformed();

        /** Called when task has failed to complete. */
        void onError(Throwable exception);
    }
}
