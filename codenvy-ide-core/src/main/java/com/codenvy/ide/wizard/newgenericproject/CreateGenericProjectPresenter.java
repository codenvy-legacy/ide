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
package com.codenvy.ide.wizard.newgenericproject;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.wizard.newproject.AbstractCreateProjectPresenter;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Create generic project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateGenericProjectPresenter extends AbstractCreateProjectPresenter {
    private ResourceProvider resourceProvider;

    /**
     * Create new generic project presenter.
     *
     * @param resourceProvider
     */
    @Inject
    public CreateGenericProjectPresenter(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void create(final AsyncCallback<Project> callback) {
        final String projectName = getProjectName();

        resourceProvider.createProject(projectName, JsonCollections.<Property>createArray(),
                                       new AsyncCallback<Project>() {
                                           public void onSuccess(final Project project) {
                                               project.createFile(project, "Readme.txt",
                                                                  "This file was auto created when you created this project.",
                                                                  MimeType.TEXT_PLAIN, new AsyncCallback<File>() {
                                                   public void onFailure(Throwable caught) {
                                                       Log.error(NewGenericProjectPagePresenter.class, caught);
                                                   }

                                                   public void onSuccess(File result) {
                                                       callback.onSuccess(project);
                                                   }
                                               });
                                           }

                                           public void onFailure(Throwable caught) {
                                               Log.error(NewGenericProjectPagePresenter.class, caught);
                                           }
                                       });
    }
}