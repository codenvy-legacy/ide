/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.importproject;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Provides importing project.
 *
 * @author Roman Nikitenko
 */
public class ImportProjectPresenter implements ImportProjectView.ActionDelegate {

    private final ProjectServiceClient                   projectServiceClient;
    private       ResourceProvider                       resourceProvider;
    private       NotificationManager                    notificationManager;
    private       CoreLocalizationConstant               locale;
    private       DtoFactory                             dtoFactory;
    private       ImportProjectView                      view;
    private       ProjectImportersServiceClient          projectImportersService;
    private       DtoUnmarshallerFactory                 dtoUnmarshallerFactory;
    private       NewProjectWizardPresenter              wizardPresenter;
    private       Map<String, ProjectImporterDescriptor> importers;

    @Inject
    public ImportProjectPresenter(ProjectServiceClient projectServiceClient,
                                  NotificationManager notificationManager,
                                  ResourceProvider resourceProvider,
                                  CoreLocalizationConstant locale,
                                  DtoFactory dtoFactory,
                                  ImportProjectView view,
                                  ProjectImportersServiceClient projectImportersService,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  NewProjectWizardPresenter wizardPresenter) {
        this.projectServiceClient = projectServiceClient;
        this.notificationManager = notificationManager;
        this.resourceProvider = resourceProvider;
        this.locale = locale;
        this.dtoFactory = dtoFactory;
        this.view = view;
        this.projectImportersService = projectImportersService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.wizardPresenter = wizardPresenter;

        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        importers = new HashMap<>();
        view.setUri("");
        view.setProjectName("");
        final List<String> importersList = new ArrayList<>();
        //TODO: need add test on this
        projectImportersService.getProjectImporters(new AsyncRequestCallback<Array<ProjectImporterDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectImporterDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectImporterDescriptor> result) {
                for (int i = 0; i < result.size(); i++) {
                    importers.put(result.get(i).getId(), result.get(i));
                    importersList.add(result.get(i).getId());
                }
                view.setImporters(importersList);
                view.setEnabledImportButton(false);
                onImporterSelected();
                view.showDialog();
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(ImportProjectPresenter.class, "can not get project importers");
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
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
    public void onImportClicked() {
        String url = view.getUri();
        String importer = view.getImporter();
        final String projectName = view.getProjectName();
        view.close();
        ImportSourceDescriptor importSourceDescriptor =
                dtoFactory.createDto(ImportSourceDescriptor.class).withType(importer).withLocation(url);
        projectServiceClient.importProject(projectName, importSourceDescriptor, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        Notification notification = new Notification(locale.importProjectMessageSuccess(), INFO);
                        notificationManager.showNotification(notification);
                        WizardContext context = new WizardContext();
                        context.putData(ProjectWizard.PROJECT, result);
                        wizardPresenter.show(context);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error(ImportProjectPresenter.class, "can not get project " + projectName);

                        Notification notification = new Notification(caught.getMessage(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                });
            }

            @Override
            protected void onFailure(Throwable exception) {
                if (exception instanceof UnauthorizedException) {
                    ServiceError serverError =
                            dtoFactory.createDtoFromJson(((UnauthorizedException)exception).getResponse().getText(), ServiceError.class);
                    Notification notification = new Notification(serverError.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                } else {
                    Log.error(ImportProjectPresenter.class, "can not import project: " + exception);
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                }
                deleteFolder(projectName);
            }
        });
    }

    private void deleteFolder(String name) {
        resourceProvider.getProject(name, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                resourceProvider.delete(result, new AsyncCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                });
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String projectName = view.getProjectName();
        String uri = view.getUri();
        if (projectName.isEmpty() && !uri.isEmpty()) {
            projectName = parseUri(uri);
            view.setProjectName(projectName);
        }
        boolean enable = !uri.isEmpty() && !projectName.isEmpty();

        view.setEnabledImportButton(enable);
    }

    @Override
    public void onImporterSelected() {
        String importer = view.getImporter();
        view.setDescription(importers.get(importer).getDescription());

    }

    /** Gets project name from uri. */
    private String parseUri(String uri) {
        String result;
        int indexStartProjectName = uri.lastIndexOf("/") + 1;
        int indexFinishProjectName = uri.indexOf(".", indexStartProjectName);
        if (indexStartProjectName != 0 && indexFinishProjectName != (-1)) {
            result = uri.substring(indexStartProjectName, indexFinishProjectName);
        } else if (indexStartProjectName != 0) {
            result = uri.substring(indexStartProjectName);
        } else {
            result = "";
        }
        return result;
    }
}
