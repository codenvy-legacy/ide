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
package com.codenvy.ide.wizard.project.my_wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.GeneratorDescription;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.wizard.DefaultWizard;
import com.codenvy.ide.api.wizard.WizardContext.Key;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.inject.Inject;

/**
 * Wizard that used in creating projects from scratch.
 *
 * @author Evgen Vidolob
 */
public class ProjectWizard extends DefaultWizard {

    public static final Key<ProjectTypeDefinition>     PROJECT_TYPE        = new Key<>("Project type");
    public static final Key<ProjectTemplateDescriptor> PROJECT_TEMPLATE    = new Key<>("Project template");
    public static final Key<String>                    PROJECT_NAME        = new Key<>("Project name");
    public static final Key<String>                    PROJECT_DESCRIPTION = new Key<>("Project description");

    /**
     * Value of this key is project description that will used for create or update project.
     * So if you wont to change some project attributes or settings (like type or builder/runner) apply your changes to this key value
     */
    public static final Key<ProjectDescriptor> PROJECT = new Key<>("Project");

    /**
     * Value of this key is original {@code ProjectDescriptor}, appears only when project wizard used for update project.
     * All attributes and other properties will be copied to {@link ProjectWizard#PROJECT} value, you don't need to change this key value.
     */
    public static final Key<ProjectDescriptor>    PROJECT_FOR_UPDATE = new Key<>("Project for update");
    public static final Key<Boolean>              PROJECT_VISIBILITY = new Key<>("Project Visibility");
    public static final Key<GeneratorDescription> GENERATOR          = new Key<>("Project Generator");

    private final DtoFactory             dtoFactory;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final ProjectServiceClient   projectServiceClient;

    /**
     * Create default wizard.
     *
     * @param notificationManager
     *         manager of notification
     */
    @Inject
    public ProjectWizard(NotificationManager notificationManager,
                         DtoFactory dtoFactory,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         ProjectServiceClient projectServiceClient) {
        super(notificationManager, "New project");
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;
    }

    /**
     * Get wizard pages.
     *
     * @return the array
     */
    public Array<WizardPage> getPages() {
        return wizardPages;
    }

    @Override
    public void onFinish() {
        super.onFinish();

        // TODO: create project or import from template
    }

    private void createProject() {
        final NewProject newProject = dtoFactory.createDto(NewProject.class);

        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);

        GeneratorDescription generatorDescription = wizardContext.getData(ProjectWizard.GENERATOR);
        if (generatorDescription != null) {
            newProject.setGeneratorDescription(generatorDescription);
        }

        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.createProject(projectName, newProject, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
//                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
//                callback.onFailure(exception);
            }
        });
    }
}
