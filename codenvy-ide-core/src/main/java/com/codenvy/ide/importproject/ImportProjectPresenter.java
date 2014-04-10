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
package com.codenvy.ide.importproject;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.Constants;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.projecttype.SelectProjectTypePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Provides importing project.
 *
 * @author Roman Nikitenko
 */
public class ImportProjectPresenter implements ImportProjectView.ActionDelegate {

    private final ProjectServiceClient     projectServiceClient;
    private       ResourceProvider         resourceProvider;
    private       NotificationManager      notificationManager;
    private       CoreLocalizationConstant locale;
    private       DtoFactory               dtoFactory;
    private       ImportProjectView        view;
    private SelectProjectTypePresenter projectTypePresenter;

    @Inject
    public ImportProjectPresenter(ProjectServiceClient projectServiceClient,
                                  NotificationManager notificationManager,
                                  ResourceProvider resourceProvider,
                                  CoreLocalizationConstant locale,
                                  DtoFactory dtoFactory,
                                  ImportProjectView view,
                                  SelectProjectTypePresenter projectTypePresenter) {
        this.projectServiceClient = projectServiceClient;
        this.notificationManager = notificationManager;
        this.resourceProvider = resourceProvider;
        this.locale = locale;
        this.dtoFactory = dtoFactory;
        this.view = view;
        this.projectTypePresenter = projectTypePresenter;
        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        view.setUri("");
        view.setProjectName("");
        List<String> importersList = new ArrayList<>();
        importersList.add("git");
        view.setImporters(importersList);
        view.setEnabledImportButton(false);

        view.showDialog();
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
                        if (result.getDescription().getProjectTypeId().equals(Constants.NAMELESS_ID)) {
                            projectTypePresenter.showDialog(result, new AsyncCallback<Project>() {
                                @Override
                                public void onFailure(Throwable caught) {

                                }

                                @Override
                                public void onSuccess(Project result) {

                                }
                            });
                        }
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
                Log.error(ImportProjectPresenter.class, "can not import project: " + exception);
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
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
