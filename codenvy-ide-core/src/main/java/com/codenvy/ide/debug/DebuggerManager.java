/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.debug;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The manager provides to return debugger for current project.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class DebuggerManager {
    private Debugger            currentDebugger;
    private StringMap<Debugger> debuggers;

    /**
     * Create manager.
     *
     * @param eventBus
     */
    @Inject
    protected DebuggerManager(EventBus eventBus) {
        this.debuggers = Collections.createStringMap();
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                currentDebugger = debuggers.get(event.getProject().getProjectTypeId());
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                currentDebugger = null;
            }
        });
    }

    /**
     * Register new debugger for the specified project type ID.
     *
     * @param projectTypeId
     * @param debugger
     */
    public void registeredDebugger(String projectTypeId, Debugger debugger) {
        debuggers.put(projectTypeId, debugger);
    }

    /** @return debugger for project type */
    public Debugger getDebugger() {
        return currentDebugger;
    }
}