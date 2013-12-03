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
package com.codenvy.ide.debug;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_MIXIN_NATURES;

/**
 * The manager provides to return debugger for current project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class DebuggerManager {
    private Debugger                currentDebugger;
    private JsonStringMap<Debugger> debuggers;

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
                Project project = event.getProject();
                String projectType = (String)project.getPropertyValue(PROPERTY_MIXIN_NATURES);
                currentDebugger = debuggers.get(projectType);
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
     * Register new debugger for some project type.
     *
     * @param projectType
     * @param debugger
     */
    public void registeredDebugger(String projectType, Debugger debugger) {
        debuggers.put(projectType, debugger);
    }

    /** @return debugger for project type */
    public Debugger getDebugger() {
        return currentDebugger;
    }
}