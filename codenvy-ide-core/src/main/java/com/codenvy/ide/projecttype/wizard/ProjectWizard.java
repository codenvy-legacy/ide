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

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode;
import com.codenvy.ide.api.wizard.AbstractWizard;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.CREATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.CREATE_MODULE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.IMPORT;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.UPDATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.PROJECT_NAME_KEY;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.PROJECT_PATH_KEY;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.WIZARD_MODE_KEY;

/**
 * Project wizard used for creating new a project or updating an existing one.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectWizard extends AbstractWizard<ImportProject> {

    private final ProjectWizardMode        mode;
    private final int                      totalMemory;
    private final CoreLocalizationConstant localizationConstants;
    private final ProjectServiceClient     projectServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final DtoFactory               dtoFactory;
    private final DialogFactory            dialogFactory;
    private final EventBus                 eventBus;
    private final AppContext appContext;

    /**
     * Creates project wizard.
     *
     * @param dataObject
     *         wizard's data-object
     * @param mode
     *         mode of project wizard
     * @param totalMemory
     *         available memory for runner
     * @param projectPath
     *         path to the project to update if wizard created in {@link ProjectWizardMode#UPDATE} mode
     *         or path to the folder to convert it to module if wizard created in {@link ProjectWizardMode#CREATE_MODULE} mode
     * @param localizationConstants
     *         localization constants
     * @param projectServiceClient
     *         GWT-client for Project service
     * @param dtoUnmarshallerFactory
     *         {@link com.codenvy.ide.rest.DtoUnmarshallerFactory} instance
     * @param dialogFactory
     *         {@link com.codenvy.ide.ui.dialogs.DialogFactory} instance
     * @param eventBus
     *         {@link com.google.web.bindery.event.shared.EventBus} instance
     * @param appContext
     *         {@link com.codenvy.ide.api.app.AppContext} instance
     */
    @Inject
    public ProjectWizard(@Assisted ImportProject dataObject,
                         @Assisted ProjectWizardMode mode,
                         @Assisted int totalMemory,
                         @Assisted String projectPath,
                         CoreLocalizationConstant localizationConstants,
                         ProjectServiceClient projectServiceClient,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         DtoFactory dtoFactory,
                         DialogFactory dialogFactory,
                         EventBus eventBus,
                         AppContext appContext) {
        super(dataObject);
        this.mode = mode;
        this.totalMemory = totalMemory;
        this.localizationConstants = localizationConstants;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.dialogFactory = dialogFactory;
        this.eventBus = eventBus;
        this.appContext = appContext;

        context.put(WIZARD_MODE_KEY, mode.toString());
        context.put(PROJECT_NAME_KEY, dataObject.getProject().getName());
        if (mode == UPDATE || mode == CREATE_MODULE) {
            context.put(PROJECT_PATH_KEY, projectPath);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void complete(@Nonnull final CompleteCallback callback) {
        if (mode == CREATE) {
            createProject(callback);
        } else if (mode == CREATE_MODULE) {
            createModule(callback);
        } else if (mode == UPDATE) {
            updateProject(callback);
        } else if (mode == IMPORT) {
            final int requiredMemory = getRequiredMemory();
            if (requiredMemory > totalMemory) {
                final ConfirmCallback confirmCallback = new ConfirmCallback() {
                    @Override
                    public void accepted() {
                        importProject(callback);
                    }
                };
                dialogFactory.createMessageDialog(localizationConstants.createProjectWarningTitle(),
                                                  localizationConstants.getMoreRam(requiredMemory, totalMemory),
                                                  confirmCallback).show();
            } else {
                importProject(callback);
            }
        }
    }

    private int getRequiredMemory() {
        RunnersDescriptor runners = dataObject.getProject().getRunners();
        if (runners != null) {
            RunnerConfiguration configuration = runners.getConfigs().get(runners.getDefault());
            if (configuration != null) {
                return configuration.getRam();
            }
        }
        return 0;
    }

    private void createProject(final CompleteCallback callback) {
        final NewProject project = dataObject.getProject();
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.createProject(project.getName(), project, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                callback.onCompleted();
            }

            @Override
            protected void onFailure(Throwable exception) {
                final String message = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class).getMessage();
                callback.onFailure(new Exception(message));
            }
        });
    }

    private void createModule(final CompleteCallback callback) {
        final String parentPath = appContext.getCurrentProject().getRootProject().getPath();
        final String modulePath = context.get(PROJECT_PATH_KEY);
        final NewProject project = dataObject.getProject();
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.createModule(
                parentPath, modulePath, project, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                    @Override
                    protected void onSuccess(ProjectDescriptor result) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent());
                        callback.onCompleted();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
    }

    private void importProject(final CompleteCallback callback) {
        final NewProject project = dataObject.getProject();
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.importProject(
                project.getName(), true, dataObject, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                    @Override
                    protected void onSuccess(ProjectDescriptor result) {
                        eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                        callback.onCompleted();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        final String message = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class).getMessage();
                        callback.onFailure(new Exception(message));
                    }
                });
    }

    private void updateProject(final CompleteCallback callback) {
        final NewProject project = dataObject.getProject();
        final String currentName = context.get(PROJECT_NAME_KEY);
        if (currentName.equals(project.getName())) {
            doUpdateProject(callback);
        } else {
            renameProject(new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    doUpdateProject(callback);
                }

                @Override
                public void onFailure(Throwable caught) {
                    final String message = dtoFactory.createDtoFromJson(caught.getMessage(), ServiceError.class).getMessage();
                    callback.onFailure(new Exception(message));
                }
            });
        }
    }

    private void doUpdateProject(final CompleteCallback callback) {
        final NewProject project = dataObject.getProject();
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.updateProject(project.getName(), project, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                // just re-open project if it's already opened
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                callback.onCompleted();
            }

            @Override
            protected void onFailure(Throwable exception) {
                final String message = dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class).getMessage();
                callback.onFailure(new Exception(message));
            }
        });
    }

    private void renameProject(final AsyncCallback<Void> callback) {
        final String path = context.get(PROJECT_PATH_KEY);
        projectServiceClient.rename(path, dataObject.getProject().getName(), null, new AsyncRequestCallback<Void>() {
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
