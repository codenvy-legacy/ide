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
package com.codenvy.ide.extension.maven.client.template.java;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.extension.maven.client.template.AbstractCreateProjectPage;
import com.codenvy.ide.extension.maven.client.template.CreateProjectClientService;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.wizard.WizardKeys.PROJECT_NAME;
import static com.codenvy.ide.ext.java.client.JavaExtension.JAVA_APPLICATION_PROJECT_TYPE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS;
import static com.codenvy.ide.json.JsonCollections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_MIXIN_NATURES;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class CreateJavaProjectPage extends AbstractCreateProjectPage {

    @Inject
    public CreateJavaProjectPage(CreateProjectClientService service, ResourceProvider resourceProvider) {
        super(service, resourceProvider);
    }

    /** {@inheritDoc} */
    @Override
    public void commit(final WizardPage.CommitCallback callback) {
        JsonArray<Property> properties =
                createArray(new Property(PROPERTY_PRIMARY_NATURE, PRIMARY_NATURE),
                            new Property(PROPERTY_MIXIN_NATURES, createArray(JAVA_APPLICATION_PROJECT_TYPE)),
                            new Property(PROPERTY_SOURCE_FOLDERS, createArray("src/main/java", "src/test/java")));
        final String projectName = wizardContext.getData(PROJECT_NAME);
        try {
            service.createJavaProject(projectName, properties, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            callback.onSuccessful();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            // TODO exception
                            callback.onFailed();
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    // TODO exception
                    callback.onFailed();
                }
            });
        } catch (RequestException e) {
            // TODO exception
            callback.onFailed();
        }
    }
}