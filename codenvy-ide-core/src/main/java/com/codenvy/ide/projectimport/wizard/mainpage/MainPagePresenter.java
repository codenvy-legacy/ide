/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projectimport.wizard.mainpage;

import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projectimport.wizard.ImportWizardRegistry;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.projectimport.wizard.ImportProjectWizardView.EnterPressedDelegate;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.NameUtils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

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
public class MainPagePresenter extends AbstractWizardPage<ImportProject> implements MainPageView.ActionDelegate {

    private static final String PUBLIC_VISIBILITY = "public";

    private final MainPageView                  view;
    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private final NotificationManager           notificationManager;
    private final CoreLocalizationConstant      locale;
    private final ImportWizardRegistry          importWizardRegistry;
    private       ImporterSelectionListener     importerSelectionListener;
    private       ProjectImportersServiceClient projectImportersService;
    private       ProjectImporterDescriptor     selectedProjectImporter;
    private       EnterPressedDelegate          enterPressedDelegate;

    @Inject
    public MainPagePresenter(ProjectImportersServiceClient projectImportersService,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             NotificationManager notificationManager,
                             CoreLocalizationConstant locale,
                             MainPageView view,
                             ImportWizardRegistry importWizardRegistry) {
        super();
        this.view = view;
        this.projectImportersService = projectImportersService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.importWizardRegistry = importWizardRegistry;

        view.setDelegate(this);
    }

    @Override
    public void init(ImportProject dataObject) {
        super.init(dataObject);

        dataObject.getProject().setVisibility(PUBLIC_VISIBILITY);
    }

    public void setEnterPressedDelegate(EnterPressedDelegate enterPressedDelegate) {
        this.enterPressedDelegate = enterPressedDelegate;
    }

    /** {@inheritDoc} */
    @Override
    public void projectImporterSelected(ProjectImporterDescriptor importer) {
        selectedProjectImporter = importer;
        view.setImporterDescription(importer.getDescription());

        if (importerSelectionListener != null) {
            importerSelectionListener.onImporterSelected(importer);
        }

        updateDelegate.updateControls();
    }

    public AcceptsOneWidget getImporterPanel() {
        return view.getImporterPanel();
    }

    @Override
    public boolean isCompleted() {
        final String projectName = dataObject.getProject().getName();
        return selectedProjectImporter != null && projectName != null && NameUtils.checkProjectName(projectName);
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        selectedProjectImporter = null;

        view.reset();
        container.setWidget(view);

        loadImporters();
    }

    private void loadImporters() {
        final Map<String, Set<ProjectImporterDescriptor>> importersByCategory = new HashMap<>();

        final Unmarshallable<Array<ProjectImporterDescriptor>> unmarshaller =
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectImporterDescriptor.class);
        projectImportersService.getProjectImporters(new AsyncRequestCallback<Array<ProjectImporterDescriptor>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ProjectImporterDescriptor> result) {
                for (ProjectImporterDescriptor importer : result.asIterable()) {
                    if (importer.isInternal() ||
                        importer.getCategory() == null ||
                        importWizardRegistry.getWizardRegistrar(importer.getId()) == null) {
                        continue;
                    }

                    if (importersByCategory.containsKey(importer.getCategory())) {
                        importersByCategory.get(importer.getCategory()).add(importer);
                    } else {
                        Set<ProjectImporterDescriptor> importersSet = new HashSet<>();
                        importersSet.add(importer);
                        importersByCategory.put(importer.getCategory(), importersSet);
                    }
                }

                new Timer() {
                    @Override
                    public void run() {
                        view.setImporters(importersByCategory);
                        if (importersByCategory.keySet().iterator().hasNext()) {
                            view.selectImporter(importersByCategory.get(importersByCategory.keySet().iterator().next()).iterator().next());
                        }
                    }
                }.schedule(300);
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(MainPagePresenter.class, locale.importProjectError() + exception);
                notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
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

    public void setImporterSelectionListener(ImporterSelectionListener listener) {
        importerSelectionListener = listener;
    }

    public interface ImporterSelectionListener {
        /** Called when importer selected. */
        void onImporterSelected(ProjectImporterDescriptor importer);
    }
}
