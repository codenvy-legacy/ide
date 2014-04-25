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
package com.codenvy.ide.extension.maven.client.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class MavenPagePresenter extends AbstractWizardPage implements MavenPageView.ActionDelegate {

    private MavenPageView        view;
    private ProjectServiceClient projectServiceClient;
    private ResourceProvider     resourceProvider;
    private DtoFactory factory;

    @Inject
    public MavenPagePresenter(MavenPageView view, ProjectServiceClient projectServiceClient, ResourceProvider resourceProvider,
                              DtoFactory factory) {
        super("Maven project settings", null);
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.resourceProvider = resourceProvider;
        this.factory = factory;
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
        Map<String, List<String>> options = new HashMap<>();
        options.put("maven.artifactId", Arrays.asList(view.getArtifactId()));
        options.put("maven.groupId", Arrays.asList(view.getGroupId()));
        options.put("maven.version", Arrays.asList(view.getVersion()));
//        options.put("package", view.getPackage());
        ProjectDescriptor projectDescriptor = factory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        projectDescriptor.setAttributes(options);
        final String projectName = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        projectServiceClient
                .createProject(projectName, projectDescriptor,
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
