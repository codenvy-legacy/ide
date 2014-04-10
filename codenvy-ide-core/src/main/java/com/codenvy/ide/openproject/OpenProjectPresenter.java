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
package com.codenvy.ide.openproject;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.api.resources.model.Project;
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
    private       OpenProjectView        view;
    private       ResourceProvider       resourceProvider;
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
    protected OpenProjectPresenter(OpenProjectView view, ResourceProvider resourceProvider, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                   ProjectServiceClient projectServiceClient) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
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