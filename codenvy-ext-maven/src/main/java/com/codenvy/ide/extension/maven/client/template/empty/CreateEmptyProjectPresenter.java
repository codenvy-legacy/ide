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
package com.codenvy.ide.extension.maven.client.template.empty;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.extension.maven.client.template.CreateProjectClientService;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.json.JsonCollections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/**
 * The implementation of {@link CreateProjectProvider}. Provides create empty
 * project nto which will be pulled sources from OpenShift application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
// TODO may be nned to remove
@Singleton
public class CreateEmptyProjectPresenter implements CreateProjectProvider {
    private String                     projectName;
    private CreateProjectClientService service;
    private ResourceProvider           resourceProvider;

    /**
     * Create Presenter.
     *
     * @param service
     * @param resourceProvider
     */
    @Inject
    protected CreateEmptyProjectPresenter(CreateProjectClientService service, ResourceProvider resourceProvider) {
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void create(final AsyncCallback<Project> callback) {
        JsonArray<Property> properties = createArray(new Property(PROPERTY_PRIMARY_NATURE, PRIMARY_NATURE));
        try {
            service.createEmptyProject(projectName, properties, new AsyncRequestCallback<Void>() {
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
}
