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
package com.codenvy.ide.ext.tutorials.client.template;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.resources.CreateProjectClientService;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.tutorials.client.TutorialsClientService;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDescription.ATTRIBUTE_SOURCE_FOLDERS;
import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.GIN_TUTORIAL_ID;
import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.TUTORIAL_PROJECT_TYPE_ID;

/**
 * The wizard page for creating gin tutorial template.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class CreateGinTutorialPage extends AbstractTemplatePage {
    private CreateProjectClientService    createProjectClientService;
    private ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;
    private TutorialsClientService        unzipTemplateClientService;
    private ResourceProvider              resourceProvider;

    /**
     * Create page.
     *
     * @param service
     *         service that provides create this kind of project
     * @param resourceProvider
     */
    @Inject
    public CreateGinTutorialPage(CreateProjectClientService createProjectClientService,
                                 ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                                 TutorialsClientService unzipTemplateClientService,
                                 ResourceProvider resourceProvider) {
        super(null, null, GIN_TUTORIAL_ID);
        this.createProjectClientService = createProjectClientService;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.unzipTemplateClientService = unzipTemplateClientService;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void commit(final CommitCallback callback) {
        Map<String, List<String>> attributes = new HashMap<String, List<String>>(1);
        // TODO: make it as calculated attributes
        List<String> sourceFolders = new ArrayList<String>(2);
        sourceFolders.add("src/main/java");
        sourceFolders.add("src/test/java");
        attributes.put(ATTRIBUTE_SOURCE_FOLDERS, sourceFolders);

        final String projectName = wizardContext.getData(PROJECT_NAME);
        ProjectTypeDescriptor projectTypeDescriptor = projectTypeDescriptorRegistry.getDescriptor(TUTORIAL_PROJECT_TYPE_ID);
        try {
            createProjectClientService.createProject(projectName, projectTypeDescriptor, attributes, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            unzipTemplate(projectName, callback);
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
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    private void unzipTemplate(final String projectName, final CommitCallback callback) {
        try {
            unzipTemplateClientService.unzipGinTutorial(projectName, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            wizardContext.putData(PROJECT, result);
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
            });
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }
}