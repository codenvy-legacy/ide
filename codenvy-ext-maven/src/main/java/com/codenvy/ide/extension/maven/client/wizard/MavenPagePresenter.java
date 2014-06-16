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
package com.codenvy.ide.extension.maven.client.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.shared.Constants;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
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
    private DtoFactory           factory;
    private MavenPomReaderClient pomReaderClient;

    @Inject
    public MavenPagePresenter(MavenPageView view, ProjectServiceClient projectServiceClient, ResourceProvider resourceProvider,
                              DtoFactory factory, MavenPomReaderClient pomReaderClient) {
        super("Maven project settings", null);
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.resourceProvider = resourceProvider;
        this.factory = factory;
        this.pomReaderClient = pomReaderClient;
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
        view.reset();
        Project project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            if (project.hasAttribute(MavenAttributes.MAVEN_ARTIFACT_ID)) {
                view.setArtifactId(project.getAttributeValue(MavenAttributes.MAVEN_ARTIFACT_ID));
                view.setGroupId(project.getAttributeValue(MavenAttributes.MAVEN_GROUP_ID));
                view.setVersion(project.getAttributeValue(MavenAttributes.MAVEN_VERSION));
                view.setPackaging(project.getAttributeValue(MavenAttributes.MAVEN_PACKAGING));
                scheduleTextChanges();
            } else {
                pomReaderClient.readPomAttributes(project.getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(String result) {
                        Jso jso = Jso.deserialize(result);
                        view.setArtifactId(jso.getStringField(MavenAttributes.MAVEN_ARTIFACT_ID));
                        view.setGroupId(jso.getStringField(MavenAttributes.MAVEN_GROUP_ID));
                        view.setVersion(jso.getStringField(MavenAttributes.MAVEN_VERSION));
                        view.setPackaging(jso.getStringField(MavenAttributes.MAVEN_PACKAGING));
                        scheduleTextChanges();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(MavenPagePresenter.class, exception);
                    }
                });
            }
        }
    }

    private void scheduleTextChanges() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onTextsChange();
            }
        });
    }

    @Override
    public void commit(@NotNull final CommitCallback callback) {
        Map<String, List<String>> options = new HashMap<>();
        options.put(MavenAttributes.MAVEN_ARTIFACT_ID, Arrays.asList(view.getArtifactId()));
        options.put(MavenAttributes.MAVEN_GROUP_ID, Arrays.asList(view.getGroupId()));
        options.put(MavenAttributes.MAVEN_VERSION, Arrays.asList(view.getVersion()));
        options.put(MavenAttributes.MAVEN_PACKAGING, Arrays.asList(view.getPackaging()));
        if ("war".equals(view.getPackaging())) {
            options.put(Constants.RUNNER_NAME, Arrays.asList("JavaWeb"));
        }
        if ("jar".equals(view.getPackaging())) {
            options.put(Constants.RUNNER_NAME, Arrays.asList("JavaStandalone"));
        }
        final ProjectDescriptor projectDescriptor = factory.createDto(ProjectDescriptor.class);
        projectDescriptor.withProjectTypeId(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getProjectTypeId());
        projectDescriptor.setAttributes(options);
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptor.setVisibility(visibility ? "public" : "private");
        projectDescriptor.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        final Project project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            if (project.getName().equals(name)) {
                updateProject(project, projectDescriptor, callback);
            } else {
                projectServiceClient.rename(project.getPath(), name, null, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        project.setName(name);

                        updateProject(project, projectDescriptor, callback);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            }

        } else {
            createProject(callback, projectDescriptor, name);
        }
    }

    private void updateProject(final Project project, ProjectDescriptor projectDescriptor, final CommitCallback callback) {
        projectServiceClient.updateProject(project.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Project result) {
                        callback.onSuccess();
                    }
                });
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void createProject(final CommitCallback callback, ProjectDescriptor projectDescriptor, final String name) {
        projectServiceClient
                .createProject(name, projectDescriptor,
                               new AsyncRequestCallback<ProjectDescriptor>() {


                                   @Override
                                   protected void onSuccess(ProjectDescriptor result) {
                                       resourceProvider.getProject(name, new AsyncCallback<Project>() {
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
