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
package org.eclipse.che.ide.projectimport.wizard;

import org.eclipse.che.api.core.rest.shared.dto.ServiceError;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ImportProject;
import org.eclipse.che.api.project.shared.dto.ImportResponse;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.vfs.gwt.client.VfsServiceClient;
import org.eclipse.che.api.vfs.shared.dto.Item;

import org.eclipse.che.ide.CoreLocalizationConstant;

import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.commons.exception.JobNotFoundException;
import org.eclipse.che.ide.commons.exception.UnauthorizedException;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.util.loging.Log;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Project import wizard used for importing a project.
 *
 * @author Artem Zatsarynnyy
 */
public class ImportWizard extends AbstractWizard<ImportProject> {

    private final ProjectServiceClient                projectServiceClient;
    private final VfsServiceClient                    vfsServiceClient;
    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private final DtoFactory                          dtoFactory;
    private final EventBus                            eventBus;
    private final CoreLocalizationConstant            localizationConstant;
    private final ImportProjectNotificationSubscriber importProjectNotificationSubscriber;

    /**
     * Creates project wizard.
     *
     * @param dataObject
     *         wizard's data-object
     * @param projectServiceClient
     *         GWT-client for Project service
     * @param vfsServiceClient
     *         GWT-client for VFS service
     * @param dtoUnmarshallerFactory
     *         {@link DtoUnmarshallerFactory} instance
     * @param dtoFactory
     *         {@link DtoFactory} instance
     * @param eventBus
     *         {@link EventBus} instance
     * @param localizationConstant
     *         {@link CoreLocalizationConstant} instance
     */
    @Inject
    public ImportWizard(@Assisted ImportProject dataObject,
                        ProjectServiceClient projectServiceClient,
                        VfsServiceClient vfsServiceClient,
                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                        DtoFactory dtoFactory,
                        EventBus eventBus,
                        CoreLocalizationConstant localizationConstant,
                        ImportProjectNotificationSubscriber importProjectNotificationSubscriber) {
        super(dataObject);
        this.projectServiceClient = projectServiceClient;
        this.vfsServiceClient = vfsServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        this.localizationConstant = localizationConstant;
        this.importProjectNotificationSubscriber = importProjectNotificationSubscriber;
    }

    /** {@inheritDoc} */
    @Override
    public void complete(@Nonnull CompleteCallback callback) {
        checkFolderExistenceAndImport(callback);
    }

    private void checkFolderExistenceAndImport(final CompleteCallback callback) {
        // check on VFS because need to check whether the folder with the same name already exists in the root of workspace
        final String projectName = dataObject.getProject().getName();
        vfsServiceClient.getItemByPath(projectName, new AsyncRequestCallback<Item>() {
            @Override
            protected void onSuccess(Item result) {
                callback.onFailure(new Exception(localizationConstant.createProjectFromTemplateProjectExists(projectName)));
            }

            @Override
            protected void onFailure(Throwable exception) {
                importProject(callback);
            }
        });
    }

    private void importProject(final CompleteCallback callback) {
        final String projectName = dataObject.getProject().getName();
        importProjectNotificationSubscriber.subscribe(projectName);
        final Unmarshallable<ImportResponse> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ImportResponse.class);

        projectServiceClient.importProject(projectName, false, dataObject, new AsyncRequestCallback<ImportResponse>(unmarshaller) {
            @Override
            protected void onSuccess(final ImportResponse result) {
                importProjectNotificationSubscriber.onSuccess();
                callback.onCompleted();
                openProject(result.getProjectDescriptor());
            }

            @Override
            protected void onFailure(Throwable exception) {
                importProjectNotificationSubscriber.onFailure(exception.getMessage());
                deleteProject(projectName);

                String errorMessage = getImportErrorMessage(exception);
                if (errorMessage.equals("Unable get private ssh key")) {
                    callback.onFailure(new Exception(localizationConstant.importProjectMessageUnableGetSshKey()));
                    return;
                }
                callback.onFailure(new Exception(errorMessage));
            }
        });
    }

    private void openProject(ProjectDescriptor project) {
        eventBus.fireEvent(new OpenProjectEvent(project.getName()));
        //if (!project.getProblems().isEmpty()) {
        //    eventBus.fireEvent(new ConfigureProjectEvent(project));
        //}
    }

    private String getImportErrorMessage(Throwable exception) {
        if (exception instanceof JobNotFoundException) {
            return "Project import failed";
        } else if (exception instanceof UnauthorizedException) {
            UnauthorizedException unauthorizedException = (UnauthorizedException)exception;
            ServiceError serverError = dtoFactory.createDtoFromJson(unauthorizedException.getResponse().getText(),
                                                                    ServiceError.class);
            return serverError.getMessage();
        } else {
            return dtoFactory.createDtoFromJson(exception.getMessage(), ServiceError.class).getMessage();
        }
    }

    private void deleteProject(final String name) {
        projectServiceClient.delete(name, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Log.info(ImportWizard.class, "Project " + name + " deleted.");
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(ImportWizard.class, exception);
            }
        });
    }
}
