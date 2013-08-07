/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.client.template;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeClientService;
import com.codenvy.ide.ext.extruntime.client.wizard.ExtensionPageView;
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
import static com.codenvy.ide.json.JsonCollections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_MIXIN_NATURES;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/**
 * The implementation of {@link CreateProjectProvider} for creating Codenvy extension project.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateCodenvyExtensionProjectPresenter.java Jul 4, 2013 10:54:05 AM azatsarynnyy $
 */
@Singleton
public class CreateCodenvyExtensionProjectPresenter implements CreateProjectProvider {
    private String                  projectName;
    private ExtRuntimeClientService service;
    private ResourceProvider        resourceProvider;
    private ExtensionPageView       view;

    /**
     * Create controller.
     * 
     * @param service
     * @param resourceProvider
     */
    @Inject
    protected CreateCodenvyExtensionProjectPresenter(ExtRuntimeClientService service,
                                                     ResourceProvider resourceProvider,
                                                     ExtensionPageView view) {
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.view = view;
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
                                                     new Property(PROPERTY_MIXIN_NATURES, CODENVY_EXTENSION_PROJECT_TYPE),
                                                     new Property(PROPERTY_SOURCE_FOLDERS, createArray("src/main/java")));
        final String groupId = view.getGroupId() == null ? "" : view.getGroupId();
        final String artifactId = view.getArtifactId() == null ? "" : view.getArtifactId();
        final String version = view.getVersion() == null ? "" : view.getVersion();
        try {
            service.createCodenvyExtensionProject(projectName, properties, groupId, artifactId, version, new AsyncRequestCallback<Void>() {
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
