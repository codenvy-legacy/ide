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
package com.codenvy.ide.ext.extensions.client.template;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.extensions.client.ExtRuntimeClientService;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.ext.extensions.client.ExtRuntimeExtension.CODENVY_EXTENSION_PROJECT_TYPE;
import static com.codenvy.ide.ext.extensions.client.ExtRuntimeExtension.EMPTY_EXTENSION_ID;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS;
import static com.codenvy.ide.collections.Collections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_MIXIN_NATURES;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/**
 * The wizard page for creating empty Codenvy extension project.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateEmptyCodenvyExtensionPage.java Sep 2, 2013 10:54:05 AM azatsarynnyy $
 */
@Singleton
public class CreateEmptyCodenvyExtensionPage extends AbstractTemplatePage {
    private ExtRuntimeClientService service;
    private ResourceProvider        resourceProvider;

    /**
     * Create page.
     *
     * @param service
     * @param resourceProvider
     */
    @Inject
    public CreateEmptyCodenvyExtensionPage(ExtRuntimeClientService service, ResourceProvider resourceProvider) {
        super(null, null, EMPTY_EXTENSION_ID);
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull final CommitCallback callback) {
        Array<Property> properties = createArray(new Property(PROPERTY_PRIMARY_NATURE, PRIMARY_NATURE),
                                                     new Property(PROPERTY_MIXIN_NATURES, CODENVY_EXTENSION_PROJECT_TYPE),
                                                     new Property(PROPERTY_SOURCE_FOLDERS,
                                                                  createArray("src/main/java", "src/main/resources")));
        final String projectName = wizardContext.getData(PROJECT_NAME);
        try {
            service.createEmptyCodenvyExtensionProject(projectName, properties, new AsyncRequestCallback<Void>() {
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