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

import com.codenvy.api.project.server.ProjectService;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides importing project.
 *
 * @author Roman Nikitenko
 */
public class ImportProjectPresenter implements ImportProjectView.ActionDelegate{

    private ImportProjectView      view;
    private ProjectService         projectService;
    private String                 workspaceId;
    private ImportSourceDescriptor importSourceDescriptor;
    private List<String>           importersList;

    @Inject
    public ImportProjectPresenter(ImportProjectView view,
                                  ProjectService projectService,
                                  @Named("workspaceId") String workspaceId) {
        this.view = view;
        this.projectService = projectService;
        this.workspaceId = workspaceId;
        this.view.setDelegate(this);
        getSupportedImporters();
    }

    /** Show dialog. */
    public void showDialog() {
        view.setEnabledImportButton(false);
        view.setImporters(importersList);
        view.setUri("");
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
        try {
//            projectService.importProject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String uri = view.getUri();
        boolean enable = !uri.isEmpty();
        view.setEnabledImportButton(enable);
    }

    /** Gets supported importers. */
    private void getSupportedImporters(){
        importersList = new ArrayList<>();
        importersList.add("Git");
    }

}
