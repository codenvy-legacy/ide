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

import com.codenvy.api.project.client.dto.DtoClientImpls;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides importing project.
 *
 * @author Roman Nikitenko
 */
public class ImportProjectPresenter implements ImportProjectView.ActionDelegate {

    private final ProjectServiceClient projectServiceClient;
    private       DtoFactory           dtoFactory;
    private       ImportProjectView    view;

    @Inject
    public ImportProjectPresenter(ProjectServiceClient projectServiceClient,
                                  DtoFactory dtoFactory,
                                  ImportProjectView view) {
        this.projectServiceClient = projectServiceClient;
        this.dtoFactory = dtoFactory;
        this.view = view;
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
        String projectName = view.getProjectName();
        ImportSourceDescriptor importSourceDescriptor =
                dtoFactory.createDto(DtoClientImpls.ImportSourceDescriptorImpl.class).withType(importer).withLocation(url);
        projectServiceClient.importProject(projectName, importSourceDescriptor, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                view.close();
            }

            @Override
            protected void onFailure(Throwable exception) {
                view.close();
                Log.error(ImportProjectPresenter.class, "can not import project: " + exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String uri = view.getUri();
        String projectName = view.getProjectName();
        boolean enable = !projectName.isEmpty() && !uri.isEmpty();

        view.setEnabledImportButton(enable);
    }
}
