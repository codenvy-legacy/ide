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
package com.codenvy.ide.openproject;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.projecttype.SelectProjectTypePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provides opening project.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class OpenProjectPresenter implements OpenProjectView.ActionDelegate {
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final ProjectServiceClient   projectServiceClient;
    private SelectProjectTypePresenter projectTypePresenter;
    private OpenProjectView  view;
    private ResourceProvider resourceProvider;
    private String selectedProject = null;

    /**
     * Create presenter.
     *
     * @param view
     * @param resourceProvider
     * @param dtoUnmarshallerFactory
     * @param projectServiceClient
     */
    @Inject
    protected OpenProjectPresenter(OpenProjectView view,
                                   ResourceProvider resourceProvider,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                   ProjectServiceClient projectServiceClient,
                                   SelectProjectTypePresenter projectTypePresenter) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
        this.projectTypePresenter = projectTypePresenter;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;

        updateComponents();
    }

    /** Updates change workspace view components. */
    private void updateComponents() {
        view.setOpenButtonEnabled(selectedProject != null);
    }

    /** {@inheritDoc} */
    @Override
    public void onOpenClicked() {
        resourceProvider.getProject(selectedProject, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                view.close();
                if (result.getDescription().getProjectTypeId().equals(Constants.NAMELESS_ID)) {
                    projectTypePresenter.showDialog(result, new AsyncCallback<Project>() {
                        @Override
                        public void onFailure(Throwable caught) {

                        }

                        @Override
                        public void onSuccess(Project result) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(OpenProjectPresenter.class, "Can't open project", caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void selectedProject(String projectName) {
        this.selectedProject = projectName;

        updateComponents();
    }

    /** Show dialog. */
    public void showDialog() {
        projectServiceClient.getProjects(
                new AsyncRequestCallback<Array<ProjectReference>>(dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class)) {
                    @Override
                    protected void onSuccess(Array<ProjectReference> result) {
                        Array<String> array = Collections.createArray();
                        for (ProjectReference projectReference : result.asIterable()) {
                            array.add(projectReference.getName());
                        }
                        view.setProjects(array);
                        view.showDialog();
                    }

                    @Override
                    protected void onFailure(Throwable throwable) {
                        Log.error(OpenProjectPresenter.class, "Can not get list of projects", throwable);
                    }
                });
    }
}