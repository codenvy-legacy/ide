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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * Helps to update project type.
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@Singleton
public class SelectProjectTypePresenter implements SelectProjectTypeView.ActionDelegate {

    private final DtoFactory                    dtoFactory;
    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private       SelectProjectTypeView         view;
    private       CoreLocalizationConstant      localizationConstant;
    private       Project                       project;
    private       AsyncCallback<Project>        callback;
    private       ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;
    private       ProjectServiceClient          projectServiceClient;

    @Inject
    public SelectProjectTypePresenter(SelectProjectTypeView view, CoreLocalizationConstant localizationConstant,
                                      ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                                      ProjectServiceClient projectServiceClient, DtoFactory dtoFactory,
                                      DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.localizationConstant = localizationConstant;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.projectServiceClient = projectServiceClient;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
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
        updateProjectWithDescriptor(descriptor);
    }

    private void updateProjectWithDescriptor(ProjectTypeDescriptor descriptor) {
        ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class)
                                                        .withProjectTypeId(descriptor.getProjectTypeId())
                                                        .withProjectTypeName(descriptor.getProjectTypeName())
                                                        .withAttributes(project.getAttributes());
        projectServiceClient.updateProject(project.getPath(), projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                project.setId(result.getId());
                project.setProjectType(result.getProjectTypeId());
                project.setAttributes(result.getAttributes());
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
