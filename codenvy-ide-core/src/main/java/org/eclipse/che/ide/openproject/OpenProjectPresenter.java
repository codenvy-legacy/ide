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
package org.eclipse.che.ide.openproject;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectReference;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Provides opening project.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class OpenProjectPresenter implements OpenProjectView.ActionDelegate {
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private ProjectServiceClient   projectServiceClient;
    private EventBus               eventBus;
    private OpenProjectView        view;
    private ProjectReference       selectedProject;

    /** Create presenter. */
    @Inject
    protected OpenProjectPresenter(OpenProjectView view,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                   ProjectServiceClient projectServiceClient,
                                   EventBus eventBus) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
        this.view.setDelegate(this);

        updateComponents();
    }

    /** Updates change workspace view components. */
    private void updateComponents() {
        view.setOpenButtonEnabled(selectedProject != null);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        eventBus.fireEvent(new OpenProjectEvent(selectedProject.getName()));
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void selectedProject(ProjectReference projectName) {
        this.selectedProject = projectName;
        updateComponents();
    }

    /** Show dialog. */
    public void showDialog() {
        projectServiceClient.getProjects(
                new AsyncRequestCallback<Array<ProjectReference>>(dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class)) {
                    @Override
                    protected void onSuccess(Array<ProjectReference> result) {
                        view.setProjects(result);
                        view.showDialog();
                    }

                    @Override
                    protected void onFailure(Throwable throwable) {
                        Log.error(OpenProjectPresenter.class, "Can not get list of projects", throwable);
                    }
                });
    }
}