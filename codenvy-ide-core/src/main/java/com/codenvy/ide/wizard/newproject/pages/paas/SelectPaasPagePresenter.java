/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.wizard.newproject.pages.paas;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.wizard.newproject.PaaSAgentImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;

/** @author Evgen Vidolob */
public class SelectPaasPagePresenter extends AbstractWizardPage implements SelectPaasPageView.ActionDelegate {

    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private       SelectPaasPageView     view;
    private       ProjectServiceClient   projectService;
    private       ResourceProvider       resourceProvider;
    private       PaaSAgentImpl          paasAgent;
    private       Array<PaaS>            paases;

    @Inject
    public SelectPaasPagePresenter(SelectPaasPageView view,
                                   ResourceProvider resourceProvider,
                                   ProjectServiceClient projectServiceClient,
                                   PaaSAgentImpl paasAgent,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super("Select PaaS", null);
        this.view = view;
        this.projectService = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.paasAgent = paasAgent;
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return wizardContext.getData(PAAS) != null;
    }

    @Override
    public void focusComponent() {
        this.paases = paasAgent.getPaaSes();
        this.view.setPaases(paases);
        ProjectTypeDescriptor projectType = wizardContext.getData(PROJECT_TYPE);
        boolean isFirst = true;
        for (int i = 0; i < paases.size(); i++) {
            PaaS paas = paases.get(i);
            boolean isAvailable = paas.isAvailable(projectType.getProjectTypeId());
            view.setEnablePaas(i, isAvailable);
            if (isAvailable && isFirst) {
                onPaaSSelected(i);
                isFirst = false;
            }
        }
    }

    @Override
    public void removeOptions() {
        // nothing to do
    }

    /** {@inheritDoc} */
    @Override
    public void onPaaSSelected(int id) {
        PaaS paas = paases.get(id);
        wizardContext.putData(PAAS, paas);

        view.selectPaas(id);

        delegate.updateControls();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void commit(final CommitCallback callback) {
        final String projectName = wizardContext.getData(PROJECT_NAME);
        final ProjectTemplateDescriptor templateDescriptor = wizardContext.getData(TEMPLATE);
        projectService.importProject(projectName, templateDescriptor.getSources(),
                                     new AsyncRequestCallback<ProjectDescriptor>(
                                             dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class)) {
                                         @Override
                                         protected void onSuccess(final ProjectDescriptor result) {
                                             resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                                                 @Override
                                                 public void onSuccess(Project project) {
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
    }
}
