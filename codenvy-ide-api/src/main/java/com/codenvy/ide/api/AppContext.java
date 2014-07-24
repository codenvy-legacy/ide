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
package com.codenvy.ide.api;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.workspace.shared.dto.WorkspaceDescriptor;
import com.codenvy.ide.api.event.CloseCurrentProjectEvent;
import com.codenvy.ide.api.event.CloseCurrentProjectHandler;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.OpenProjectHandler;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import javax.inject.Singleton;

/**
 * Describe current state of application.
 * E.g. current project, current process
 *
 * @author Vitaly Parfonov
 */
@Singleton
public class AppContext {

    private CurrentProject currentProject;

    private WorkspaceDescriptor workspace;

    @Inject
    public AppContext(final EventBus eventBus, final ProjectServiceClient projectServiceClient,
                      final DtoUnmarshallerFactory dtoUnmarshallerFactory) {

        eventBus.addHandler(OpenProjectEvent.TYPE, new OpenProjectHandler() {
            @Override
            public void onOpenProject(OpenProjectEvent event) {
                // previously opened project should be correctly closed
                if (currentProject != null) {
                    eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(currentProject.getProjectDescription()));
                }

                Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
                projectServiceClient.getProject(event.getProject().getName(), new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                    @Override
                    protected void onSuccess(ProjectDescriptor projectDescriptor) {
                        currentProject = new CurrentProject(projectDescriptor);
                        eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(projectDescriptor));
                    }

                    @Override
                    protected void onFailure(Throwable throwable) {
                        Log.error(AppContext.class, throwable);
                    }
                });
            }
        });

        eventBus.addHandler(CloseCurrentProjectEvent.TYPE, new CloseCurrentProjectHandler() {
            @Override
            public void onClose(CloseCurrentProjectEvent event) {
                ProjectDescriptor closedProject = currentProject.getProjectDescription();
                // Important: currentProject must be null BEFORE firing ProjectClosedEvent
                currentProject = null;
                eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(closedProject));
            }
        });
    }

    public WorkspaceDescriptor getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceDescriptor workspace) {
        this.workspace = workspace;
    }

    /**
     * Returns the project that is currently opened or <code>null</code> if none opened.
     *
     * @return opened project or <code>null</code> if none opened
     */
    @Nullable
    public CurrentProject getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(CurrentProject currentProject) {
        this.currentProject = currentProject;
    }

}
