/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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