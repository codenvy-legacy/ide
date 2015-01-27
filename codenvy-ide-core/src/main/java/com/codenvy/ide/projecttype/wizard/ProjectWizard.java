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
package com.codenvy.ide.projecttype.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.wizard1.AbstractWizard;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Project wizard implementation that used for creating new project or updating existing one.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectWizard extends AbstractWizard<NewProject> {
    private final boolean                editMode;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final DialogFactory          dialogFactory;

    /**
     * Creates project wizard.
     *
     * @param data
     *         data object
     * @param editMode
     *         {@code true} if wizard will be used for editing project, {@code false} - for creating new one
     * @param projectServiceClient
     *         GWT-client for Project service
     * @param dtoUnmarshallerFactory
     *         {@link DtoUnmarshallerFactory} instance
     * @param dialogFactory
     *         {@link DialogFactory} instance
     */
    @Inject
    public ProjectWizard(@Assisted NewProject data,
                         @Assisted boolean editMode,
                         ProjectServiceClient projectServiceClient,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         DialogFactory dialogFactory) {
        super(data);
        this.editMode = editMode;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
    }

    @Override
    public void onFinish() {
        if (editMode) {
            updateProject();
        } else {
            // TODO
            boolean importFromTemplate = false; //data.isImporting();
            if (importFromTemplate) {
                importProject();
            } else {
                createProject();
            }
        }
    }

    private void updateProject() {
//        projectServiceClient.updateProject();
    }

    private void createProject() {
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.createProject(data.getName(), data, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog("", exception.getMessage(), null).show();
            }
        });
    }

    private void importProject() {
//        projectServiceClient.importProject();
    }
}
