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
import com.codenvy.api.project.shared.dto.RunnerConfiguration;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode;
import com.codenvy.ide.api.wizard.AbstractWizard;
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
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.IMPORT;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.UPDATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.CURRENT_NAME_KEY;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.WIZARD_MODE_KEY;

/**
 * Project wizard implementation that used for creating new project or updating existing one.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectWizard extends AbstractWizard<ImportProject> {

    private final ProjectWizardMode        mode;
    private final int                      totalMemory;
    private final CoreLocalizationConstant localizationConstants;
    private final ProjectServiceClient     projectServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final DialogFactory            dialogFactory;
    private final EventBus                 eventBus;

    /**
     * Creates project wizard.
     *
     * @param dataObject
     *         wizard's data-object
     * @param mode
     *         mode of project wizard
     * @param totalMemory
     *         available memory for runner
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
     */
    @Inject
    public ProjectWizard(@Assisted ImportProject dataObject,
                         @Assisted ProjectWizardMode mode,
                         @Assisted int totalMemory,
                         CoreLocalizationConstant localizationConstants,
                         ProjectServiceClient projectServiceClient,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory,
                         DialogFactory dialogFactory,
                         EventBus eventBus) {
        super(dataObject);
        this.mode = mode;
        this.totalMemory = totalMemory;
        this.localizationConstants = localizationConstants;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
        this.eventBus = eventBus;

        context.put(WIZARD_MODE_KEY, mode.toString());
        if (mode == UPDATE) {
            context.put(CURRENT_NAME_KEY, dataObject.getProject().getName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void complete(@Nonnull final CompleteCallback callback) {
        if (mode == CREATE) {
            createProject(callback);
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
                                                  localizationConstants.messagesWorkspaceRamLessRequiredRam(requiredMemory, totalMemory),
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
                        callback.onFailure(exception);
                    }
                });
    }

    private void updateProject(final CompleteCallback callback) {
        final NewProject project = dataObject.getProject();
        final String currentName = context.get(CURRENT_NAME_KEY);
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
                    callback.onFailure(caught);
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
                callback.onFailure(exception);
            }
        });
    }

    private void renameProject(final AsyncCallback<Void> callback) {
        final String path = context.get(CURRENT_NAME_KEY);
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
