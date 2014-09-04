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
package com.codenvy.ide.wizard.project.importproject;

import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttype.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter of the import project wizard's main page.
 * 
 * @author Ann Shumilova
 */
public class MainPagePresenter extends AbstractWizardPage implements MainPageView.ActionDelegate {

    private static final RegExp                 HTTPS_URL_PATTERN =
                                                                    RegExp.compile("((https|http)://)((([^\\\\\\\\@:;, (//)])+/){2,})[^\\\\\\\\@:; ,]+");
    private static final RegExp                 SSH_URL_PATTERN   = RegExp.compile("((((git|ssh)://)(([^\\\\/@:]+@)??)[^\\\\/@:]+)(:|/)|" +
                                                                                   "([^\\\\/@:]+@[^\\\\/@:]+):)[^\\\\@:]+");
    private static final RegExp                 NAME_PATTERN      = RegExp.compile("^[A-Za-z0-9_-]*$");
    private final MainPageView                  view;
    private final ProjectImportersServiceClient projectImportersService;
    private final NotificationManager           notificationManager;
    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private final CoreLocalizationConstant      locale;
    private ProjectImporterDescriptor           projectImporter;

    @Inject
    public MainPagePresenter(ProjectImportersServiceClient projectImportersService,
                             MainPageView view,
                             NotificationManager notificationManager,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory, CoreLocalizationConstant locale) {
        super("Choose Project", null);
        this.view = view;
        view.setDelegate(this);
        this.projectImportersService = projectImportersService;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.locale = locale;
    }


    @Override
    public void projectNameChanged(String name) {
        if (name == null || name.isEmpty()) {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
        }
        else if (NAME_PATTERN.test(name)) {
            wizardContext.putData(ProjectWizard.PROJECT_NAME, name);
            view.hideNameError();
        } else {
            wizardContext.removeData(ProjectWizard.PROJECT_NAME);
            view.showNameError();
        }
        delegate.updateControls();
    }

    @Override
    public void projectDescriptionChanged(String projectDescriptionValue) {
        wizardContext.putData(ProjectWizard.PROJECT_DESCRIPTION, projectDescriptionValue);
    }

    @Override
    public void projectVisibilityChanged(Boolean aPublic) {
        wizardContext.putData(ProjectWizard.PROJECT_VISIBILITY, aPublic);
    }

    @Override
    public ProjectWizardView.ActionDelegate getProjectWizardDelegate() {
        return (ProjectWizardView.ActionDelegate)delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void projectImporterSelected(ProjectImporterDescriptor importer) {
        this.projectImporter = importer;
        wizardContext.putData(ImportProjectWizard.PROJECT_IMPORTER, importer);
        view.setImporterDescription(importer.getDescription());
        delegate.updateControls();
    }


    /** {@inheritDoc} */
    @Override
    public void projectUrlChanged(String url) {
        if (!(SSH_URL_PATTERN.test(url) || HTTPS_URL_PATTERN.test(url))) {
            wizardContext.removeData(ImportProjectWizard.PROJECT_URL);
            view.showUrlError();
        } else {
            wizardContext.putData(ImportProjectWizard.PROJECT_URL, url);
            view.hideUrlError();

            String projectName = view.getProjectName();
            if (projectName.isEmpty()) {
                projectName = parseUri(url);
                view.setProjectName(projectName);
                projectNameChanged(projectName);
            }
        }
        delegate.updateControls();
    }

    /** Gets project name from uri. */
    private String parseUri(String uri) {
        String result;
        int indexStartProjectName = uri.lastIndexOf("/") + 1;
        int indexFinishProjectName = uri.indexOf(".", indexStartProjectName);
        if (indexStartProjectName != 0 && indexFinishProjectName != (-1)) {
            result = uri.substring(indexStartProjectName, indexFinishProjectName);
        } else if (indexStartProjectName != 0) {
            result = uri.substring(indexStartProjectName);
        } else {
            result = "";
        }
        return result;
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        return projectImporter != null && wizardContext.getData(ProjectWizard.PROJECT_NAME) != null
               && wizardContext.getData(ImportProjectWizard.PROJECT_URL) != null;
    }

    @Override
    public void focusComponent() {

    }

    @Override
    public void removeOptions() {

    }

    @Override
    public void go(final AcceptsOneWidget container) {
        projectImporter = null;
        view.reset();
        container.setWidget(view);

        final Set<ProjectImporterDescriptor> importersSet = new HashSet<ProjectImporterDescriptor>();
        projectImportersService.getProjectImporters(new AsyncRequestCallback<Array<ProjectImporterDescriptor>>(
                                                                                                               dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectImporterDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectImporterDescriptor> result) {
                for (int i = 0; i < result.size(); i++) {
                    importersSet.add(result.get(i));
                }
                Map<String, Set<ProjectImporterDescriptor>> importers = new HashMap<>();
                importers.put("Importers", importersSet);

                view.setImporters(importers);
                if (importersSet.iterator().hasNext()) {
                    view.selectImporter(importersSet.iterator().next().getId());
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(MainPagePresenter.class, locale.importProjectError() + exception);
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }
}
