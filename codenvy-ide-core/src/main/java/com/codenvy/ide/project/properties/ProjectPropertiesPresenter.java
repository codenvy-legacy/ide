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
package com.codenvy.ide.project.properties;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.project.properties.edit.EditPropertyPresenter;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for managing project's properties (view, delete, edit).
 * 
 * @author Ann Shumilova
 */
@Singleton
public class ProjectPropertiesPresenter implements ProjectPropertiesView.ActionDelegate {

    private ProjectPropertiesView                 view;
    private ResourceProvider                      resourceProvider;
    private Property                              selectedProperty;
    private ProjectPropertiesLocalizationConstant localization;

    private Array<Property>                       properties;
    private NotificationManager                   notificationManager;
    private EditPropertyPresenter                 editPropertyPresenter;

    @Inject
    public ProjectPropertiesPresenter(ProjectPropertiesView view,
                                      ResourceProvider resourceProvider,
                                      ProjectPropertiesLocalizationConstant localization,
                                      NotificationManager notificationManager,
                                      EditPropertyPresenter editPropertyPresenter) {
        this.view = view;
        view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.localization = localization;
        this.notificationManager = notificationManager;
        this.editPropertyPresenter = editPropertyPresenter;
    }

    /**
     * Shows properties of the active project.
     */
    public void showProperties() {
        if (resourceProvider.getActiveProject() != null) {
            resourceProvider.getActiveProject().refreshProperties(new AsyncCallback<Project>() {

                @Override
                public void onSuccess(Project result) {
                    properties = Collections.createArray(result.getProperties().asIterable());
                    view.setSaveButtonEnabled(false);
                    view.setDeleteButtonEnabled(false);
                    view.setEditButtonEnabled(false);
                    view.setProperties(properties);
                    view.showDialog();
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error(ProjectPropertiesPresenter.class, localization.getProjectPropertiesFailed(), caught);
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onEditClicked() {
        editPropertyPresenter.editProperty(selectedProperty, new AsyncCallback<Property>() {

            @Override
            public void onFailure(Throwable caught) {

            }

            @Override
            public void onSuccess(Property result) {
                for (Property property : properties.asIterable()) {
                    if (property.getName().equals(result.getName())) {
                        property.setValue(result.getValue());
                        break;
                    }
                }
                view.setSaveButtonEnabled(true);
                view.setProperties(properties);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        if (selectedProperty == null)
            return;
        boolean isDelete =
                           Window.confirm(localization.deletePropertyQuestion(PropertyUtil.getHumanReadableName(selectedProperty.getName())));

        if (isDelete) {
            view.setSaveButtonEnabled(true);
            selectedProperty.setValue(null);
            view.setProperties(properties);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onSaveClicked() {
        resourceProvider.getActiveProject().getProperties().clear();
        resourceProvider.getActiveProject().getProperties().addAll(properties);
        resourceProvider.getActiveProject().flushProjectProperties(new AsyncCallback<Project>() {

            @Override
            public void onFailure(Throwable caught) {
                view.close();
                Notification notification = new Notification(localization.saveProjectPropertiesFailed(), ERROR);
                notificationManager.showNotification(notification);
            }

            @Override
            public void onSuccess(Project result) {
                view.close();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void selectedProperty(Property property) {
        this.selectedProperty = property;

        boolean enable = selectedProperty != null;
        view.setDeleteButtonEnabled(enable);
        view.setEditButtonEnabled(enable);
    }

}
