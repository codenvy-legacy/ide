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
import org.eclipse.che.ide.restore.AppStateComponent;
import org.eclipse.che.ide.restore.ProjectState;
import org.eclipse.che.ide.util.loging.Log;

/**
 * {@link AppStateComponent} responsible for saving/restoring workspace state, like opening/closing parts.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class WorkspaceStateComponent implements AppStateComponent {

    private final WorkspaceAgent workspaceAgent;
    private final OutlinePart    outlinePart;

    @Inject
    public WorkspaceStateComponent(WorkspaceAgent workspaceAgent, OutlinePart outlinePart) {
        this.workspaceAgent = workspaceAgent;
        this.outlinePart = outlinePart;
    }

    /** {@inheritDoc} */
    @Override
    public void save(ProjectState appState, final Callback callback) {
        callback.onPerformed();
    }

    /** {@inheritDoc} */
    @Override
    public void restore(ProjectState appState, final Callback callback) {
//        if (appState.isOutlineShown()) {
            workspaceAgent.setActivePart(outlinePart);
//        }

        callback.onPerformed();
    }
}
