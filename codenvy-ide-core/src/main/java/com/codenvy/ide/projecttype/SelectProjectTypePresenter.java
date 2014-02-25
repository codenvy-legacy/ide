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
package com.codenvy.ide.projecttype;

import com.codenvy.api.project.gwt.client.ProjectClientService;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.resources.model.ProjectDescription.PROPERTY_PROJECT_TYPE;

/** @author Ann Shumilova */
@Singleton
public class SelectProjectTypePresenter implements SelectProjectTypeView.ActionDelegate {

    private final DtoFactory                    dtoFactory;
    private       SelectProjectTypeView         view;
    private       CoreLocalizationConstant      localizationConstant;
    private       Project                       project;
    private       AsyncCallback<Project>        callback;
    private       ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;
    private       ProjectClientService          projectClientService;

    @Inject
    public SelectProjectTypePresenter(SelectProjectTypeView view, CoreLocalizationConstant localizationConstant,
                                      ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                                      ProjectClientService projectClientService, DtoFactory dtoFactory) {
        this.view = view;
        this.localizationConstant = localizationConstant;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.projectClientService = projectClientService;
        this.dtoFactory = dtoFactory;
        view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog(@NotNull Project project, @NotNull AsyncCallback<Project> callback) {
        this.project = project;
        this.callback = callback;
        view.setLabel(localizationConstant.selectProjectType(project.getName()));
        view.clearTypes();
        view.setTypes(projectTypeDescriptorRegistry.getDescriptors());
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        final String selectedProjectTypeId = view.getSelectedProjectTypeId();
        final ProjectTypeDescriptor descriptor = projectTypeDescriptorRegistry.getDescriptor(selectedProjectTypeId);

        Property projectProperty = project.getProperty(PROPERTY_PROJECT_TYPE);
        if (projectProperty != null) {
            projectProperty.setValue(Collections.createArray(descriptor.getProjectTypeId()));
        } else {
            project.getProperties().add(new Property(PROPERTY_PROJECT_TYPE, descriptor.getProjectTypeId()));
        }

        project.flushProjectProperties(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                updateProject(descriptor);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(SelectProjectTypePresenter.class, "Can not save project properties.", caught);
                callback.onFailure(caught);
            }
        });
    }

    private void updateProject(ProjectTypeDescriptor descriptor) {
        ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class)
                                                        .withProjectTypeId(descriptor.getProjectTypeId())
                                                        .withProjectTypeName(descriptor.getProjectTypeName());
        projectClientService.updateProject(project.getPath(), projectDescriptor, new AsyncRequestCallback<String>() {
            @Override
            protected void onSuccess(String result) {
                view.close();
                callback.onSuccess(project);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(SelectProjectTypePresenter.class, "Can't update project.", throwable);
                callback.onFailure(throwable);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}
