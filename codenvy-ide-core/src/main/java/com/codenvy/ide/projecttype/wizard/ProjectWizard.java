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
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.wizard1.AbstractWizard;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Project wizard implementation that used for creating new project or updating existing one.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectWizard extends AbstractWizard<NewProject> {

    private final boolean                isCreatingNewProject;
    private final ProjectServiceClient   projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final DialogFactory          dialogFactory;
    private final DtoFactory             dtoFactory;
    private final EventBus               eventBus;

    /**
     * Creates project wizard.
     *
     * @param dataObject
     *         data-object
     * @param isCreatingNewProject
     *         {@code true} if wizard will be used for creating new project, {@code false} - for editing existed one
     * @param projectServiceClient
     *         GWT-client for Project service
     * @param dtoUnmarshallerFactory
     *         {@link DtoUnmarshallerFactory} instance
     * @param dialogFactory
     *         {@link DialogFactory} instance
     * @param dtoFactory
     *         {@link DtoFactory} instance
     * @param eventBus
     *         {@link EventBus} instance
     */
    @Inject
    public ProjectWizard(@Assisted NewProject dataObject,
                         @Assisted boolean isCreatingNewProject,
                         ProjectServiceClient projectServiceClient,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         DialogFactory dialogFactory,
                         DtoFactory dtoFactory,
                         EventBus eventBus) {
        super(dataObject);
        this.isCreatingNewProject = isCreatingNewProject;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;

        // TODO: add constants for wizard context
        context.put("isCreatingNewProject", String.valueOf(isCreatingNewProject));
        if (!isCreatingNewProject) {
            context.put("currentProjectName", dataObject.getName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void complete() {
        if (isCreatingNewProject) {
            createProject();
        } else {
            boolean importFromTemplate = false; // TODO
            if (importFromTemplate) {
                importProject();
            } else {
                renameAndUpdateProject();
            }
        }
    }

    private void createProject() {
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.createProject(dataObject.getName(), dataObject, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
            }
        });
    }

    // TODO: consider to use ImportProject DTO as data-object
    private void importProject() {
        final ImportProject importProject = dtoFactory.createDto(ImportProject.class)
                                                      .withProject(dataObject);

        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.importProject(
                dataObject.getName(), true, importProject, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                    @Override
                    protected void onSuccess(ProjectDescriptor result) {
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
                    }
                });
    }

    private void renameAndUpdateProject() {
        // TODO: sub-project also may be updated (not only root-project) so should be project's path instead of name
        final String path = dataObject.getName();

        final String currentName = context.get("currentProjectName");
        if (currentName.equals(dataObject.getName())) {
            updateProject(path);
        } else {
            renameProject(path, dataObject.getName(), new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    updateProject(path);
                }

                @Override
                public void onFailure(Throwable caught) {
                    dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(caught.getMessage()), null).show();
                }
            });
        }
    }

    private void updateProject(String path) {
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.updateProject(path, dataObject, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
            }

            @Override
            protected void onFailure(Throwable exception) {
                dialogFactory.createMessageDialog("", JsonHelper.parseJsonMessage(exception.getMessage()), null).show();
            }
        });
    }

    private void renameProject(String path, String newName, final AsyncCallback<Void> callback) {
        projectServiceClient.rename(path, newName, null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }
}
