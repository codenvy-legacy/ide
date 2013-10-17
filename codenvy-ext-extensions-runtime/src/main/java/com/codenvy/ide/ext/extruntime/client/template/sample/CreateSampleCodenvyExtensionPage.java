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
package com.codenvy.ide.ext.extruntime.client.template.sample;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeClientService;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeResources;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.wizard.WizardKeys.PROJECT_NAME;
import static com.codenvy.ide.ext.extruntime.client.ExtRuntimeExtension.CODENVY_EXTENSION_PROJECT_TYPE;
import static com.codenvy.ide.ext.extruntime.client.ExtRuntimeExtension.EMPTY_EXTENSION_ID;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS;
import static com.codenvy.ide.json.JsonCollections.createArray;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_MIXIN_NATURES;
import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PRIMARY_NATURE;

/**
 * Presenter for creating Codenvy extension project from 'Create project wizard'.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateSampleCodenvyExtensionPage.java Jul 8, 2013 4:24:17 PM azatsarynnyy $
 */
@Singleton
public class CreateSampleCodenvyExtensionPage extends AbstractTemplatePage implements CreateSampleCodenvyExtensionPageView.ActionDelegate {
    public static final String DEFAULT_VERSION = "1.0-SNAPSHOT";
    private CreateSampleCodenvyExtensionPageView view;
    private ExtRuntimeClientService              service;
    private ResourceProvider                     resourceProvider;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param resources
     */
    @Inject
    public CreateSampleCodenvyExtensionPage(CreateSampleCodenvyExtensionPageView view,
                                            ExtRuntimeClientService service,
                                            ResourceProvider resourceProvider,
                                            ExtRuntimeResources resources) {
        super("Define the properties of a new Maven module", resources.codenvyExtensionTemplate(), EMPTY_EXTENSION_ID);
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        delegate.updateControls();
    }

    /** Validate entered information on view. */
    public boolean validate() {
        return !view.getGroupId().isEmpty() && !view.getArtifactId().isEmpty() && !view.getVersion().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getGroupId().isEmpty()) {
            return "Please, specify groupId.";
        } else if (view.getArtifactId().isEmpty()) {
            return "Please, specify artifactId.";
        } else if (view.getVersion().isEmpty()) {
            return "Please, specify version.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        final String projectName = wizardContext.getData(PROJECT_NAME);

        view.setGroupId(projectName);
        view.setArtifactId(projectName);
        view.setVersion(DEFAULT_VERSION);

        container.setWidget(view);
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull final CommitCallback callback) {
        JsonArray<Property> properties = createArray(new Property(PROPERTY_PRIMARY_NATURE, PRIMARY_NATURE),
                                                     new Property(PROPERTY_MIXIN_NATURES, CODENVY_EXTENSION_PROJECT_TYPE),
                                                     new Property(PROPERTY_SOURCE_FOLDERS,
                                                                  createArray("src/main/java", "src/main/resources")));
        final String projectName = wizardContext.getData(PROJECT_NAME);
        final String groupId = view.getGroupId();
        final String artifactId = view.getArtifactId();
        final String version = view.getVersion();
        try {
            service.createSampleCodenvyExtensionProject(projectName, properties, groupId, artifactId, version,
                                                        new AsyncRequestCallback<Void>() {
                                                            @Override
                                                            protected void onSuccess(Void result) {
                                                                resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                                                                    @Override
                                                                    public void
                                                                    onSuccess(Project result) {
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