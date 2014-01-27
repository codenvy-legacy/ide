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
package com.codenvy.ide.ext.java.client.projecttemplate.ant;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.ext.java.client.projecttemplate.CreateProjectClientService;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.ATTRIBUTE_SOURCE_FOLDERS;

/**
 * The wizard page for creating a Spring project from a projecttemplate.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateAntSpringProjectPage extends AbstractTemplatePage {
    private CreateProjectClientService service;
    private ResourceProvider           resourceProvider;

    /**
     * Create page.
     *
     * @param service
     *         service that makes it possible to create this kind of project
     * @param resourceProvider
     */
    @Inject
    public CreateAntSpringProjectPage(CreateProjectClientService service, ResourceProvider resourceProvider) {
        super(null, null, JavaExtension.ANT_SPRING_TEMPLATE_ID);
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void commit(final CommitCallback callback) {
        Map<String, List<String>> attributes = new HashMap<String, List<String>>(1);
        List<String> language = new ArrayList<String>(1);
        language.add("java");

        // TODO: make it as calculated attributes
        List<String> sourceFolders = new ArrayList<String>(1);
        sourceFolders.add("src");

        attributes.put(ATTRIBUTE_SOURCE_FOLDERS, sourceFolders);
        attributes.put("language", language);

        final String projectName = wizardContext.getData(PROJECT_NAME);
        try {
            service.createSpringProject(projectName, attributes, new AsyncRequestCallback<Void>() {
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
            service.unzipAntSpringTemplate(projectName, new AsyncRequestCallback<Void>() {
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