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
import com.codenvy.ide.api.projectimporter.ImporterPagePresenter;
import com.codenvy.ide.api.projectimporter.ProjectImporterRegistry;
import com.codenvy.ide.api.projecttype.wizard.ImportProjectWizard;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.ProjectWizardView;
import com.codenvy.ide.wizard.project.importproject.ImportProjectWizardView.EnterPressedDelegate;
import com.google.gwt.user.client.Timer;
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

    private final MainPageView                  view;
    private final ProjectImporterRegistry       projectImporterRegistry;
    private       ProjectImportersServiceClient projectImportersService;
    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private final NotificationManager           notificationManager;
    private       ProjectImporterDescriptor     projectImporter;
    private       ImporterPagePresenter         importerPage;
    private final CoreLocalizationConstant      locale;
    private       EnterPressedDelegate          enterPressedDelegate;

    @Inject
    public MainPagePresenter(ProjectImporterRegistry projectImporterRegistry,
                             ProjectImportersServiceClient projectImportersService,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             NotificationManager notificationManager,
                             CoreLocalizationConstant locale,
                             MainPageView view) {
        super("Choose Project", null);
        this.view = view;
        view.setDelegate(this);
        this.projectImporterRegistry = projectImporterRegistry;
        this.projectImportersService = projectImportersService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
    }

    public void setEnterPressedDelegate(EnterPressedDelegate enterPressedDelegate) {
        this.enterPressedDelegate = enterPressedDelegate;
    }

    /**
     * Disable all page inputs.
     */
    public void disableInputs() {
        importerPage.disableInputs();
    }

    /**
     * Enable all page inputs.
     */
    public void enableInputs() {
        importerPage.enableInputs();
    }

    @Override
    public ProjectWizardView.ActionDelegate getProjectWizardDelegate() {
        return (ProjectWizardView.ActionDelegate)delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void projectImporterSelected(ProjectImporterDescriptor importer) {
        this.projectImporter = importer;
        wizardContext.putData(ImportProjectWizard.PROJECT_IMPORTER, projectImporter);
        setImporterPage();
    }

    private void setImporterPage() {
        this.importerPage = projectImporterRegistry.getImporterPage(projectImporter.getId());
        importerPage.setContext(wizardContext);
        importerPage.setProjectWizardDelegate(delegate);
        importerPage.go(view.getImporterPanel());
        delegate.updateControls();
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
        if (importerPage != null) {
            importerPage.clear();
            importerPage = null;
        }
        view.reset();
        container.setWidget(view);
        setImporters();
    }

    private void setImporters() {
        final Map<String, Set<ProjectImporterDescriptor>> importers = new HashMap<>();
        projectImportersService.getProjectImporters(new AsyncRequestCallback<Array<ProjectImporterDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectImporterDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectImporterDescriptor> result) {
                for (int i = 0; i < result.size(); i++) {
                    ProjectImporterDescriptor importer = result.get(i);
                    // do not show internal importers:
                    if (!importer.isInternal()) {
                        if (importer.getCategory() == null) {
                            break;
                        }
                        if (importers.containsKey(importer.getCategory())) {
                            importers.get(importer.getCategory()).add(importer);
                        } else {
                            Set<ProjectImporterDescriptor> importersSet = new HashSet<>();
                            importersSet.add(importer);
                            importers.put(importer.getCategory(), importersSet);
                        }
                    }
                }
                new Timer() {
                    @Override
                    public void run() {
                        view.setImporters(importers);
                        if (importers.keySet().iterator().hasNext()) {
                            view.selectImporter(importers.get(importers.keySet().iterator().next()).iterator().next());
                        }
                    }
                }.schedule(300);

            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(MainPagePresenter.class, locale.importProjectError() + exception);
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onEnterClicked() {
        if (enterPressedDelegate != null) {
            enterPressedDelegate.onEnterKeyPressed();
        }
    }
}
