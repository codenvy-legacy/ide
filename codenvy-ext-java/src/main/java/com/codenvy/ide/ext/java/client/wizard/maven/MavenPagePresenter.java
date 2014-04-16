/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.client.wizard.maven;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.java.shared.Constants;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class MavenPagePresenter extends AbstractWizardPage implements MavenPageView.ActionDelegate {

    private MavenPageView        view;
    private ProjectServiceClient projectServiceClient;
    private ResourceProvider     resourceProvider;

    @Inject
    public MavenPagePresenter(MavenPageView view, ProjectServiceClient projectServiceClient, ResourceProvider resourceProvider) {
        super("Maven project settings", null);
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.resourceProvider = resourceProvider;
        view.setDelegate(this);
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return !view.getArtifactId().equals("") && !view.getGroupId().equals("") && !view.getVersion().equals("");
    }

    @Override
    public void focusComponent() {

    }

    @Override
    public void removeOptions() {

    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void commit(@NotNull final CommitCallback callback) {
        StringMap<String> options = Collections.createStringMap();
        options.put("artifactId", view.getArtifactId());
        options.put("groupId", view.getGroupId());
        options.put("version", view.getVersion());
        options.put("package", view.getPackage());
        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        projectServiceClient
                .generateProject(projectName, Constants.MAVEN_SIMPLE_PROJECT_GENERATOR, options,
                                 new AsyncRequestCallback<ProjectDescriptor>() {


                                     @Override
                                     protected void onSuccess(ProjectDescriptor result) {
                                         resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                                             @Override
                                             public void onSuccess(Project project) {
                                                 callback.onSuccess();
                                             }

                                             @Override
                                             public void onFailure(Throwable caught) {
                                                 callback.onFailure(caught);
                                             }
                                         });
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         callback.onFailure(exception);
                                     }
                                 }
                                );
    }

    @Override
    public void onTextsChange() {
        delegate.updateControls();
    }
}
