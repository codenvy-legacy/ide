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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.ext.tutorials.client.TutorialsClientService;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.extruntime.client.ExtRuntimeExtension.CODENVY_EXTENSION_PROJECT_TYPE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS;
import static com.codenvy.ide.ext.tutorials.client.TutorialsExtension.TUTORIAL_PROJECT_TYPE;
import static com.codenvy.ide.json.JsonCollections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_MIXIN_NATURES;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/**
 * The implementation of {@link CreateProjectProvider} to create a project that contains action tutorial.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateActionTutorialProjectPresenter implements CreateProjectProvider {
    private String                 projectName;
    private TutorialsClientService service;
    private ResourceProvider       resourceProvider;

    /**
     * Create Presenter.
     *
     * @param service
     * @param resourceProvider
     */
    @Inject
    protected CreateActionTutorialProjectPresenter(TutorialsClientService service, ResourceProvider resourceProvider) {
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public String getProjectName() {
        return projectName;
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /** {@inheritDoc} */
    @Override
    public void create(final AsyncCallback<Project> callback) {
        JsonArray<Property> properties = createArray(new Property(PROPERTY_PRIMARY_NATURE, PRIMARY_NATURE),
                                                     new Property(PROPERTY_MIXIN_NATURES,
                                                                  createArray(TUTORIAL_PROJECT_TYPE, CODENVY_EXTENSION_PROJECT_TYPE)),
                                                     new Property(PROPERTY_SOURCE_FOLDERS,
                                                                  createArray("src/main/java", "src/main/resources")));
        try {
            service.createActionTutorialProject(projectName, properties, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            callback.onSuccess(result);
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