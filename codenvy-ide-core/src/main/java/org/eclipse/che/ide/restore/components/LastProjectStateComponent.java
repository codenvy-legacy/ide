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
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.restore.AppState;
import org.eclipse.che.ide.restore.Callback;
import org.eclipse.che.ide.restore.AppStateComponent;
import org.eclipse.che.ide.restore.ProjectState;

/**
 * {@link AppStateComponent} responsible for saving/restoring last opened project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class LastProjectStateComponent implements AppStateComponent {

    private final EventBus             eventBus;
    private final ProjectServiceClient projectServiceClient;

    private HandlerRegistration handlerRegistration;

    @Inject
    public LastProjectStateComponent(EventBus eventBus, ProjectServiceClient projectServiceClient) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
    }

    /** {@inheritDoc} */
    @Override
    public void save(ProjectState appState, Callback callback) {
        callback.onPerformed();
    }

    /** {@inheritDoc} */
    @Override
    public void restore(ProjectState appState, final Callback callback) {
        final String lastProjectPath = "" /*appState.getLastProjectPath()*/;

        projectServiceClient.getProject(lastProjectPath, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                handlerRegistration = eventBus.addHandler(ProjectActionEvent.TYPE, getProjectOpenedHandler(callback));
                eventBus.fireEvent(new OpenProjectEvent(lastProjectPath));
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onPerformed();
            }
        });
    }

    private ProjectActionHandler getProjectOpenedHandler(final Callback callback) {
        return new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                if (handlerRegistration != null) {
                    handlerRegistration.removeHandler();
                }
                callback.onPerformed();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
            }
        };
    }
}
