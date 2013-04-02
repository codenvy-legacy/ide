/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.wizard.newgenericproject;

import com.codenvy.ide.api.paas.AbstractPaasWizardPagePresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.api.wizard.newproject.AbstractNewProjectWizardPage;
import com.codenvy.ide.api.wizard.newproject.CreateProjectHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.StringUtils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectPageView.ActionDelegate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Provides creating new generic project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewGenericProjectPagePresenter extends AbstractNewProjectWizardPage implements ActionDelegate {
    private NewGenericProjectPageView view;

    private ResourceProvider resourceProvider;

    private boolean hasIncorrectSymbol;

    private boolean hasProjectList;

    private boolean hasSameProject;

    private JsonArray<String> projectList;

    /**
     * Create presenter
     *
     * @param resources
     * @param view
     * @param resourceProvider
     */
    @Inject
    protected NewGenericProjectPagePresenter(NewGenericProjectWizardResource resources, NewGenericProjectPageView view,
                                             ResourceProvider resourceProvider) {
        super("New generic project wizard", resources.genericProjectIcon());
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;

        this.resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>() {
            @Override
            public void onSuccess(JsonArray<String> result) {
                projectList = result;
                hasProjectList = true;
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(NewGenericProjectPagePresenter.class, caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        AbstractPaasWizardPagePresenter paasWizardPage = getPaaSWizardPage();
        CreateProjectHandler createProjectHandler = getCreateProjectHandler();
        createProjectHandler.setProjectName(view.getProjectName());
        paasWizardPage.setCreateProjectHandler(createProjectHandler);
        paasWizardPage.setPrevious(this);
        paasWizardPage.setUpdateDelegate(delegate);

        return paasWizardPage;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return isCompleted() && !hasNext();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return getPaaSWizardPage() != null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return !view.getProjectName().isEmpty() && !hasIncorrectSymbol && hasProjectList && !hasSameProject;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getProjectName().isEmpty()) {
            return "Please, enter a project name.";
        } else if (!hasProjectList) {
            return "Please wait, checking project list";
        } else if (hasSameProject) {
            return "Project with this name already exists.";
        } else if (hasIncorrectSymbol) {
            return "Incorrect project name.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void checkProjectName() {
        hasIncorrectSymbol = false;
        String projectName = view.getProjectName();
        for (int i = 0; i < projectName.length() && hasIncorrectSymbol == false; i++) {
            Character ch = projectName.charAt(i);
            hasIncorrectSymbol = !(StringUtils.isWhitespace(ch) || StringUtils.isAlphaNumOrUnderscore(ch));
        }

        hasSameProject = false;
        for (int i = 0; i < projectList.size() && hasSameProject == false; i++) {
            String name = projectList.get(i);
            hasSameProject = projectName.compareTo(name) == 0;
        }

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        CreateProjectHandler createProjectHandler = getCreateProjectHandler();
        createProjectHandler.setProjectName(view.getProjectName());
        createProjectHandler.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                // do nothing
            }

            @Override
            public void onFailure(Throwable caught) {
                // do nothing
            }
        });
    }
}