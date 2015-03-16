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
package org.eclipse.che.ide.restore.components;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.parts.OutlinePart;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.restore.AppState;
import org.eclipse.che.ide.restore.Callback;
import org.eclipse.che.ide.restore.StateComponent;
import org.eclipse.che.ide.util.loging.Log;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class WorkspaceStateComponent implements StateComponent {

    private final WorkspaceAgent workspaceAgent;
    private final OutlinePart    outlinePart;

    @Inject
    public WorkspaceStateComponent(WorkspaceAgent workspaceAgent, OutlinePart outlinePart) {
        this.workspaceAgent = workspaceAgent;
        this.outlinePart = outlinePart;
    }

    @Override
    public void save(AppState appState, final Callback callback) {
        Log.info(this.getClass(), "has started saving");

        callback.onPerformed();

        Log.info(this.getClass(), "has finished saving");
    }

    @Override
    public void restore(AppState appState, final Callback callback) {
        Log.info(this.getClass(), "has started restoring");

        if (appState.isOutlineShown()) {
            workspaceAgent.setActivePart(outlinePart);
        }

        callback.onPerformed();
    }
}
