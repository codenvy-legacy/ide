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
package org.eclipse.che.ide.statepersisting;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.AsyncAction;
import org.eclipse.che.ide.api.controlflow.Task;

/**
 * {@link Task} that can perform an action.
 *
 * @author Artem Zatsarynnyy
 */
public class PerformActionTask implements Task {

    private final Action      action;
    private final ActionEvent actionEvent;

    public PerformActionTask(Action action, ActionEvent actionEvent) {
        this.action = action;
        this.actionEvent = actionEvent;
    }

    @Override
    public void run(final TaskCallback callback) {
        if (action instanceof AsyncAction) {
            final AsyncAction asyncAction = (AsyncAction)action;

            asyncAction.actionPerformed(actionEvent, new AsyncAction.Callback() {
                @Override
                public void onPerformed() {
                    callback.onPerformed();
                }
            });
        } else {
            action.actionPerformed(actionEvent);
            callback.onPerformed();
        }
    }
}
