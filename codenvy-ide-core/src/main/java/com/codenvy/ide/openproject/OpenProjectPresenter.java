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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Provides opening project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class OpenProjectPresenter implements OpenProjectView.ActionDelegate {
    private OpenProjectView  view;
    private ResourceProvider resourceProvider;
    private String selectedProject = null;

    /**
     * Create OpenProjectPresenter.
     *
     * @param view
     * @param resourceProvider
     */
    @Inject
    protected OpenProjectPresenter(OpenProjectView view, ResourceProvider resourceProvider) {
        this.view = view;
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
        resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>() {
            @Override
            public void onSuccess(JsonArray<String> result) {
                view.setProjects(result);
                view.showDialog();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(OpenProjectPresenter.class, "Can not get list of projects", caught);
            }
        });
    }
}